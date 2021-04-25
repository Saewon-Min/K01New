package chat8project;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

// 클라이언트가 입력한 메세지를 서버로 전송해주는 쓰레드 클래스
public class Sender extends Thread {

	Socket socket;
	PrintWriter out = null;
	String name;
	
	// 클라이언트가 접속시 사용했던 Socket객체를 기반으로 출력스트림 생성
	public Sender(Socket socket, String name) {
		this.socket = socket;
		
		try {
			out = new PrintWriter(this.socket.getOutputStream(),true);
			this.name = name;
			
			
		} catch (Exception e) {
			System.out.println("예외>Sender>생성자 : "+e);
		}
	}
	
	
	
	@Override
	public void run() {
		
		Scanner s = new Scanner(System.in);
		try {
			
			// 최초로 보내는 메세지는 대화명
			try {
				
//				HashSet<String> blacklist = new HashSet<String>(); 
//				blacklist.add("스팸");
//				blacklist.add("음란물");
//				blacklist.add("광고");
//
//				Iterator<String> it = blacklist.iterator();
//
//				if (blacklist.contains(name)) {
//					out.println("impossible");
//					interrupt();
//				}else {
					out.println(URLEncoder.encode(name,"UTF-8"));
//				}
				
				
			}catch(UnsupportedEncodingException en1) {}
			
			// 두번째부터는 q를 입력하기전까지는 입력한 메세지를 서버로 전송한다.
			while(out != null) {
				try {
					String s2 = s.nextLine();
					
					
					// 대소문자 구분없이
					if(s2.equalsIgnoreCase("Q")) {
						break;
					}
					else {
						
						
						try {	
							out.println(URLEncoder.encode(s2,"UTF-8"));
						}catch(UnsupportedEncodingException en2) {}
					
					
					}
					Thread.sleep(1);
				}catch (InterruptedException e){
					System.out.println("Thread interrupt");
					
				}catch (Exception e) {
					System.out.println("예외>Sender>run1 : "+e);
					}
				}
			
			out.close();
			socket.close();
			Thread.sleep(1);
		}catch (InterruptedException e){
			System.out.println("Thread interrupt");
		}catch (Exception e) {
			System.out.println("예외>Sender>run2 : " + e);
			
		}
	}
}
