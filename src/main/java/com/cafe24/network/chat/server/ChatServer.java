package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	
	private static final int PORT = 8888;
	 
	
	public static void main(String[] args) {
		List<Writer> listWriters = new ArrayList<Writer>();
		ServerSocket serverSocket=null;
		try {
				//서버 소켓 생성
				serverSocket = new ServerSocket();
				//바인딩
				//String hostAddress = InetAddress.getLocalHost().getHostAddress();
				serverSocket.bind(new InetSocketAddress("0.0.0.0",PORT));
				log("연결 대기중 ...");
				//accept()
				while(true) {
					Socket socket=serverSocket.accept();
					new ChatServerThread(socket,listWriters).start();
				}
				//통신 스레드 생성 후 실행
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
				
				try {
					if(serverSocket!=null || serverSocket.isClosed()==false)
							serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}

	}
	public static void log(String msg) {
		System.out.println("[server] : "+msg);
	}

}
