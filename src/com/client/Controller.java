package com.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

import com.tools.Tools;

/**
 * @author Boris
 * @description 控制EB510
 * 2016年8月18日
 */
public class Controller  implements Runnable{
	private Socket socket;
	private Socket socket2;
	private static DatagramSocket udpSocket = null;
	
	private OutputStream os;
	private InputStream is;
	
	private Tools tools;
	
	public final static String LISTEN_TCP = "listenTcp";
	public final static String LISTEN_UDP = "listenUdp";
	
	public Controller(String ip, int tcpPort, int udpPort){
		try {
			this.socket = new Socket(ip, tcpPort);
			this.socket2 = new Socket(ip, 5565);
			socket.setTcpNoDelay(true);//不延时拼包
			os = socket.getOutputStream();
			is = socket.getInputStream();
			
			udpSocket = new DatagramSocket(10110);
			
			tools = Tools.getTools();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendSCPI(String str){
		try {
			os.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void listenTcp() throws IOException{
		System.out.println("监听STPI");
		boolean flag = true;
		while(flag){
			byte[] buffer = new byte[1024];
			int length = is.read(buffer);
			String str = new String(buffer, 0, length);
	        System.out.println(str);
		}
	}
	
	
	public void listenTcp2() throws IOException{
		System.out.println("监听5565");
		boolean flag = true;
		while(flag){
			InputStream is =  socket2.getInputStream();
			byte[] buffer = new byte[1024];
			int length = is.read(buffer);
			String str = new String(buffer, 0, length);
	        System.out.println(str);
		}
	}
	
	public void listenUdp() throws IOException{
		System.out.println("监听UDP");
		byte[] buffer = new byte[1024];
		DatagramPacket dpReceived = new DatagramPacket(buffer, 1024);
		
		boolean f = true;
		while(f){
			udpSocket.receive(dpReceived);//接受来自工作站的数据
			byte[] receivedBuffer = Arrays.copyOfRange(dpReceived.getData(), dpReceived.getOffset(), 
					dpReceived.getOffset() + dpReceived.getLength()); 
			
//			String str = new String(receivedBuffer, "UTF-8");
//	        System.out.println(str);
			
			tools.printHexString(receivedBuffer);
			dpReceived.setLength(1024);
		}
		
		udpSocket.close();
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if (Thread.currentThread().getName().equals(LISTEN_TCP)) {
				listenTcp();
			}else if (Thread.currentThread().getName().equals(LISTEN_UDP)){
				listenUdp();
			}else{
				listenTcp2();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
