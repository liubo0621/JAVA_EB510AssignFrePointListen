import com.client.Controller;



/**
 * @author Boris
 * @description 
 * 2016年8月18日
 */
public class RunEAFPL{
	public static void main(String[] args) {
		Controller controller = new Controller("192.168.10.52", 5555, 5565);
		new Thread(controller,Controller.LISTEN_TCP).start();
		new Thread(controller,Controller.LISTEN_UDP).start();
		new Thread(controller).start();
		
		controller.sendSCPI("*CLS\n");/* clear status reporting */
		controller.sendSCPI("*RST\n");/* reset Receiver */
		controller.sendSCPI("DEM AM;:FREQ:FIXed 9585KHZ\n"); // 控制AM 和 频率
		controller.sendSCPI("SYSTem:AUDio:REMote:MODe 5\n");
		
//		controller.sendSCPI("UDP:FLAG 192.168.10.115 10110\n");
//		controller.sendSCPI("UDP:TAG 192.168.10.115 10110\n");
//		controller.sendSCPI("SYST:COMM:LAN:PING ON\n");
//		controller.sendSCPI("TRAC:UDP:TAG \"192.168.10.115\", 10110, FSC, MSC, DSC, AUD, IFP, CW\n");
//		controller.sendSCPI("TRAC:UDP:TAG \"192.168.10.115\", 10110\n");
//		controller.sendSCPI("TRAC:UDP:FLAG:OFF \"192.168.10.115\", 10110\n");//This command registers a flag for a specific UDP or TCP path.
	}
	
	/*TRAC:UDP:TAG \"192.168.10.115\", 10110, FSC, MSC, DSC, AUD, IFP, CW\n
	 * cBuffer = 0x0073f428 "TRAC:UDP:TAG \"192.168.10.115\", 59931, IF, VID, VDP, PSC\n"
	 * cBuffer = 0x0073f428 "TRAC:UDP:TAG \"192.168.10.115\", 59931, PIFP\n"
	 * 0x0073f428 "TRAC:UDP:FLAG \"192.168.10.115\", 59931, \"VOLT:AC\", \"FREQ:RX\", \"FREQ:OFFS\", \"OPT\"\n"
	 * 0x0073f428 "TRAC:UDP:FLAG \"192.168.10.115\", 59931, \"FSTRength\"\n"
	 * cBuffer = 0x0073f428 "SYST:AUD:REM:MODE 1\n"
	 * 0x0073f428 "SYST:IF:REM:MODE OFF\n"
	 */
}
