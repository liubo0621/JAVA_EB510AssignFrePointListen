import com.client.SearchTask;
import com.util.Tools;


public class RunEAFPL{
	private static Tools tools = Tools.getTools();
	
	public static void main(String[] args) {
		String []receivers = tools.getProperty("receivers").split(",");
		for (String receiver : receivers) {
			String rPort = tools.getProperty(receiver + ".port");
			String rIp = tools.getProperty(receiver + ".ip");
			
			SearchTask searchTask = new SearchTask(rIp, Integer.parseInt(rPort));
			new Thread(searchTask).start();
		}
	}
}
/*
 *select g.grap_id,g.task_id,f.freq_name,r.ip,r.port,to_char(g.start_time,'yyyy-mm-dd hh24:mi:ss') start_time,g.length,g.priorty,g.freq_id,
(select inner_url from tab_app_storage  where sto_id=2) path from tab_grap_task g
 left join tab_task t on g.task_id=t.task_id
 left join tab_mam_freq f on f.freq_id=g.freq_id
 left join tab_mam_receiver r on r.receiver_id=g.receiver_id
 where g.status=70 and g.start_time<sysdate and r.ip='192.168.10.120' and r.port=4410 order by f.freq_pri DESC;
 
 insert into tab_file (file_id, file_name, start_time,end_time ,freq_id, sto_id, sto_path, score_status, task_id)
values(19, '20160812124030_20160812124032.wav', to_date('2016-08-12 12:40:30','yyyy-mm-dd hh24:mi:ss'),to_date('2016-08-12 12:40:32','yyyy-mm-dd hh24:mi:ss'),
1,2,'06210000\2016-08-12\20160812124030_20160812124032.wav',70,1);


 */