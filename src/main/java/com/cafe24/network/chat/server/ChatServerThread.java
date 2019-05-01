package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ChatServerThread extends Thread{

	private Socket socket;
	private String nickname; //클라이언트의 닉네임
	private List<Writer> listWriters;
	private String remoteIp;
	private int remotePort;
	BufferedReader br = null;
	PrintWriter pw = null;
	
	public ChatServerThread(Socket socket,List<Writer> listWriters) {
		this.socket=socket;
		this.listWriters=listWriters;
	}
	
	public void run() {
		
		try {
		//리모트 host Information
		InetSocketAddress remoteInfo=(InetSocketAddress)socket.getRemoteSocketAddress();
		remoteIp = remoteInfo.getAddress().getHostAddress();
		remotePort = remoteInfo.getPort();
		ChatServer.log("connected by client["+remoteIp+" : "+
				remotePort+"]");
		
		//입출력 스트림 초기화
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
		pw =new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
		
		//요청 처리
		while(true) {
			String request = br.readLine();
			//ChatServer.log("received "+request);
			if(request==null) {
				ChatServer.log("client disconnect [ "+remoteIp+" : "+remotePort+"]");
				break;
			}
			//프로토콜 분석
			String[] tokens = request.split(":");
			
			if("join".equals(tokens[0])) {
				doJoin(tokens[1],pw); //닉네임->tokens[1]저장 , pw ->Writer pool에저장
			}
			else if("message".equals(tokens[0])) {
				doMessage(tokens[1]); //Writer pool에 존재하는 모든 pw에 데이터 write
			}
			else if("quit".equals(tokens[0])) {
				doQuit(pw); //소켓을 닫는 메소드
			}
			else {
				ChatServer.log("에러:알수 없는 요청("+tokens[0]+")");
			}
		}
		
		
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private void doJoin(String nickname,Writer writer) {
		this.nickname=nickname;
		
		String data = nickname+" 님이 참여하였습니다.";
		broadcast(data);
		//Writer pool에 저장
		addWriter(writer);
		ChatServer.log("join by client["+remoteIp+" : "+
				remotePort+"]"+listWriters.size());
		
		pw.println("join:ok");
		pw.flush();
		
	}
	 
	private void doMessage(String msg) {
		//메시지 브로드 캐스팅
		//ChatServer.log(nickname+":"+msg);
		broadcast(nickname+":"+msg);
		
	}
	private void doQuit(Writer writer) {
	
		removeWriter(writer);
		String data = nickname+"님이 퇴장 하였습니다.";
		broadcast(data);
		
	}
	private void removeWriter(Writer writer) {
		synchronized(listWriters) {
				pw.println("quit"); 
				pw.flush();
		       listWriters.remove(writer);
		      // ChatServer.log("delete 1 Writer");
		}
	}
	
	private void addWriter(Writer writer) {
		synchronized(listWriters) {
			listWriters.add(writer);
		}
	}
	private void broadcast(String data) {
		synchronized(listWriters) {
			for(Writer writer : listWriters) {
				PrintWriter printWriter = (PrintWriter)writer;
				if(pw.equals(printWriter))
						continue;
				printWriter.println(data);
				printWriter.flush();
			}
		}
	}
	
}
