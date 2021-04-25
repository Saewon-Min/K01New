package chat8project;
 

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class MultiClient {
     
	public static void main(String[] args) {
		
		System.out.println("이름을 입력하세요 :");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		int nameCnt = 0;
		
		try {
			
			// 서버로 접속 요청
			String ServerIP = "localhost";
			if(args.length>0) {
				ServerIP = args[0];
			}
			
			Socket socket = new Socket(ServerIP,9999);
			System.out.println("서버와 연결되었습니다.");
			
			
			// 서버가 Echo해준 메세지를 받기위한 리시버 쓰레드 객체 생성
		
				Thread receiver = new Receiver(socket);
				receiver.start();
				
			
			// 서버로 메세지를 전송할 Sender 쓰레드 객체 생성
		
				Thread sender = new Sender(socket,s_name);
				sender.start();
				
			
			
				
				
		} catch (Exception e) {
			System.out.println("예외발생[MultiClient]"+e);	
		}
	}
}
