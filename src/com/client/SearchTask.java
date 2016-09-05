package com.client;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.db.operation.CRUD;
import com.util.Config;
import com.util.Constance;
import com.util.Log;

/**
 * @author Boris
 * @description 查询任务
 * 2016年8月18日
 */
public class SearchTask implements Runnable{
	private String receiverIp;
	private int receiverPort;
	private DealTask dealTask = null;
	
	public SearchTask(String receiverIp, int receiverPort){
		this.receiverIp = receiverIp;
		this.receiverPort = receiverPort;
	}
	
	public void searchTask(String receiverIp, int receiverPort)  throws SQLException, InterruptedException{
		Log.out.debug("查询任务...");
		CRUD crud = new CRUD();
		updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.FREE); //初始化接收机的状态为空闲
		
		int receiverStatus = Constance.Reveiver.FREE;
		int currentTaskPri = Constance.FreqPri.NORMAL;
		
		while(true){
			//查询接收机状态
			String recveiverSql = "select r.status from tab_mam_receiver r where r.ip = '"+ receiverIp + "' and r.port = " + receiverPort;
			ResultSet receiverRS = crud.find(recveiverSql);
			
			while (receiverRS.next()){
				receiverStatus = receiverRS.getInt("status");
				break;
			}
			receiverRS.close();
			crud.close();
			
			//按优先级查询任务 优先级高的任务排在前面
			String taskSql = "select g.grap_id,g.task_id,f.freq_name,r.ip,r.port,to_char(g.start_time,'yyyy-mm-dd hh24:mi:ss') start_time,g.length,g.priorty,g.freq_id," +
						 "(select inner_url from tab_app_storage  where sto_id=2) path from tab_grap_task g " +
						 "left join tab_task t on g.task_id=t.task_id " +
						 "left join tab_mam_freq f on f.freq_id=g.freq_id " +
						 "left join tab_mam_receiver r on r.receiver_id=g.receiver_id " +
						 "where g.status=70 and g.start_time<=sysdate and r.ip='"+ receiverIp + "' and r.port=" + receiverPort + " " +
						 "order by g.priorty ASC";
			ResultSet taskRS = crud.find(taskSql);
			
//			System.out.println(taskSql);
			
//			printSearchContent(taskRS, "任务信息");
		
			while (taskRS.next()) {
				int grapId = taskRS.getInt("grap_id");
				int freqId = taskRS.getInt("freq_id");
				int taskId = taskRS.getInt("task_id");
				int fileTotalTime = taskRS.getInt("length") * 60;//取得分钟
				String frequence = String.format("%08d", Integer.parseInt(taskRS.getString("freq_name")) * 1000);//取得的是kHZ 乘1000变成HZ 长度8 不够填0
				String savePath = taskRS.getString("path");
				
				if (receiverStatus == Constance.Reveiver.BUSY) {
					if (taskRS.getInt("priorty") == Constance.FreqPri.URGENCY && currentTaskPri != Constance.FreqPri.URGENCY) {//加急任务 且正在做的不是加急任务 则停止当前任务 做加急任务
						currentTaskPri = Constance.FreqPri.URGENCY;
						//TOOD 停止当前任务，做新任务
						if (dealTask != null) {
							dealTask.stopCurrentTask();
						}
						
						dealTask = new DealTask(savePath, frequence, receiverIp, receiverPort, fileTotalTime, freqId, taskId, grapId);
						new Thread(dealTask).start();
						
					}
				}else if(receiverStatus == Constance.Reveiver.FREE){
					currentTaskPri = taskRS.getInt("priorty");
					//TODO 做新任务
					dealTask = new DealTask(savePath, frequence, receiverIp, receiverPort, fileTotalTime, freqId, taskId, grapId);
					new Thread(dealTask).start();
				}
				break;
			}
			taskRS.close();
			crud.close();
//			break;
			
			Thread.sleep(Config.SEARCH_TASK_TIME);
		}
	}
	
	public void updateReceiverStatus(String receiverIp, int receiverPort, int status){
		CRUD crud = new CRUD();
		String sql = "update tab_mam_receiver r set r.status = " + status + " where r.port = " + receiverPort + " and r.ip = '" + receiverIp + "'";
		crud.update(sql);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			searchTask(receiverIp, receiverPort);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
