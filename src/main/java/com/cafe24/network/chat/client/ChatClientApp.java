package com.cafe24.network.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_IP="127.0.0.1";  
	private static final int SERVER_PORT= 8888;
	

	public static void main(String[] args) {
		
		String name = null;
		Scanner scanner = new Scanner(System.in);
		BufferedReader br=null;
		PrintWriter pw=null;
		
		while( true ) {
			
			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();
			
			if (name.isEmpty() == false ) {
				break;
			}
			
			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		try {
		// 소켓 생성
		Socket sock = new Socket();
		
		//connect()
		sock.connect(new InetSocketAddress(SERVER_IP,SERVER_PORT));
		
		// iostream 초기화
		
			br = new BufferedReader(new InputStreamReader(sock.getInputStream(),"utf-8"));
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"utf-8"));
			
		// join
			String msg="join:"+name;
			//log(msg);
			pw.println(msg);
			pw.flush();
		//Scanner close
		scanner.close();

		new ChatWindow(name,br,pw,sock).show(); //생성자에 소켓도 넘겨야됨
		
		
		} 
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void log(String msg) {
		System.out.println(msg);
	}
}
