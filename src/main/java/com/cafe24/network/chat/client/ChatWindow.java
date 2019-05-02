package com.cafe24.network.chat.client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private Socket sock = null;

	public ChatWindow(String name,BufferedReader br,PrintWriter pw,Socket sock) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		textArea.setFont(new Font("Default",Font.BOLD,14));
		this.sock=sock;
		this.br=br;
		this.pw=pw;
	}
	
	private void finish() {
		//socket 정리
		try {
			
			pw.println("quit");
			pw.flush();
			br.close();
			if(sock!=null || sock.isClosed()==false)
					sock.close();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() { //익명 클래스 , 옵저버 패턴의 종류중 하나
			
			public void actionPerformed(ActionEvent e) {
				sendMessage();
				
			}
			
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER)
					sendMessage();
			}

		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		//스레드 생성 ,  서버로부터 데이터 읽어오기
		new Thread(){ 
			public void run() {
			while(true) {
				try {
					
					String data=br.readLine();
					if("quit".equals(data)) 
							break;
					updateTextArea(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			}
		}.start();

		
	}
	//스레드에서 불러야됨
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
		
	}
	private void sendMessage() {
		String message=" ";
		if(!textField.getText().equals("")) {
			message=textField.getText();
			String[] str=message.split(":");
			StringBuilder remessage = new StringBuilder("");
			for(String token:str)
				remessage.append(token);
			pw.println("message:"+remessage);
		}
		else {
		pw.println("message:"+message);
		}
		pw.flush();
		
		textField.setText("");
		textField.requestFocus();
		
		updateTextArea(message);
	}
	
}
