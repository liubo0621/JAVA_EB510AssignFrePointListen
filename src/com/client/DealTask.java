package com.client;

import com.db.operation.CRUD;
import com.util.Config;
import com.util.Constance;
import com.util.Tools;

/**
 * @author Boris
 * @description 
 * 2016��8��24��
 */
public class DealTask implements Runnable{
	private Tools tools = Tools.getTools();
	
	private String serverPath; //�������ϵĵ�ַ
	private String frequence;
	private String receiverIp;

	private int receiverPort;
	private int fileTotalTime;
	private int freqId;
	private int taskId; 
	private int grapId;
	
	private long udpExampleAddr;
	
	static{
		System.load("D://WorkSpace//VC//EB510AssignFrePointListen//x64//Debug//EB510AssignFrePointListen.dll");
	}
	
	/**
	 * @param serverPath
	 * @param frequence
	 * @param receiverIp
	 * @param receiverPort
	 * @param fileTotalTime
	 * @param freqId
	 * @param taskId
	 * @param grapId
	 */
	public DealTask(String serverPath, String frequence, String receiverIp,
			int receiverPort, int fileTotalTime, int freqId, int taskId,
			int grapId) {
		super();
		this.serverPath = serverPath;
		this.frequence = frequence;
		this.receiverIp = receiverIp;
		this.receiverPort = receiverPort;
		this.fileTotalTime = fileTotalTime;
		this.freqId = freqId;
		this.taskId = taskId;
		this.grapId = grapId;
		
		this.udpExampleAddr = getUdpExampleAddr();
	}

	public void doTask(){
		System.out.println("***********do task + "+ grapId + "***********");
		//�������ݿ���ջ�״̬
		updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.BUSY);
		updateGrapTaskStatus(grapId, Constance.Task.DOING);
		
		String beginTime = tools.getCurrentTime();
		String localFilePath = Config.LOCAL_SAVE_PATH  + frequence + "\\" + tools.getCurrentDay() +"\\" + beginTime + ".wav";
		// ��鱾�ش洢·���Ƿ���ڣ� �������򴴽�
		tools.makeDir(localFilePath);
		
		//��Ƶ ��Ƶ c++ʵ��
		doTask(udpExampleAddr, receiverIp, receiverPort, frequence, localFilePath, String.valueOf(fileTotalTime));
		
		//�������ݿ���ջ�״̬
		updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.FREE);
		updateGrapTaskStatus(grapId, Constance.Task.DONE);
		
		String endTime = tools.getCurrentTime();
		String fileName = beginTime + "_" + tools.getCurrentTime() + ".wav";
		
		String startT = tools.formatDate(beginTime);
		String endT = tools.formatDate(endTime);
		String path = frequence + "\\" + tools.getCurrentDay() +"\\";
		String remoteSavePath = serverPath + path;
		
		//�����ļ���Զ�� �����뵽�ļ�����
		if(tools.cpSrcFileToDestFile(localFilePath, remoteSavePath + beginTime + "_" + tools.getCurrentTime() + ".wav")){
			 String sql = "insert into tab_file (file_id,file_name, start_time,end_time ,freq_id, sto_id, sto_path, score_status, task_id, grap_id) " +
		              "values(seq_global.nextval,'"+fileName+"', to_date('"+startT+"','yyyy-mm-dd hh24:mi:ss'),to_date('"+endT+"','yyyy-mm-dd hh24:mi:ss'),"+freqId+",2,'"+path+"',70,"+taskId+","+grapId+")";
			 CRUD crud = new CRUD();
			 crud.instert(sql);
		}
	}
	
	public void stopCurrentTask(){
		stopCurrentTask(udpExampleAddr);
	}
	
	public void updateReceiverStatus(String receiverIp, int receiverPort, int status){
		CRUD crud = new CRUD();
		String sql = "update tab_mam_receiver r set r.status = " + status + " where r.port = " + receiverPort + " and r.ip = '" + receiverIp + "'";
		crud.update(sql);
	}
	
	public void updateGrapTaskStatus(int grapId, int status){
		CRUD crud = new CRUD();
		System.out.println("����grap id " + grapId + " status" + status);
		String sql = "update tab_grap_task set status = " + status + " where grap_id = " + grapId; 
		crud.update(sql);
	}
	
	private native long getUdpExampleAddr(); 
	private native void stopCurrentTask(long udpExampleAddr);
	private native void doTask(long udpExampleAddr, final String receiverIp, final int receiverPort, final String frequence, final String filePath, final String fileTotalTime);
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		doTask();
	}
	
	public static void main(String[] args) {
		DealTask dealTask = new DealTask("D:\\eb510file_server\\", "06210000", "192.168.10.52", 5555, 10, 0, 0, 0);
		dealTask.doTask();
		System.out.println("*******************");
	}

}