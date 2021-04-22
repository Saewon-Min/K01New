package chat8project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.lang.InterruptedException;

// 서버가 보내는 Echo메세지를 읽어오는 쓰레드 클래스
public class Receiver extends Thread {

	Socket socket;
	BufferedReader in = null;
	
	// 클라이언트가 접속할때 사용한 Socket객체를 기반으로 입력스트림 생성
	public Receiver(Socket socket) {
		this.socket = socket;
		
		try {
			in = new BufferedReader(
					new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
		} catch (Exception e) {
			System.out.println("예외>Receiver>생성자 : "+e);
		}
	}
	
	/*
	run()메소드에서는 서버가 보내는 Echo메세지를
	지속적으로 읽어오고, 예외발생시 while루프를 탈출한다.
	 */
	
	@Override
	public void run() {
		while(in != null) {
			try {
//				if(in.readLine()==null) {
//					Thread.sleep(1);
//					System.out.println("------Missed Connection-------");
//					break;
//				}else {
					System.out.println("Thread Receive : "+URLDecoder.decode(in.readLine(),"UTF-8"));
					Thread.sleep(5000);
//				}
				
			}catch (InterruptedException e){
				System.out.println("Thread interrupt");
				
			}catch (SocketException e) {
				
				System.out.println("SocketException 발생됨 . 루프탈출");
				break;
			} catch (NullPointerException e) {
				System.out.print("");
				break;
			
			} catch (Exception e) {
				System.out.println("예외>Receiver>run2 : "+e);
				
			}
		}
		try {
			in.close();
		
		} catch (Exception e) {
			System.out.println("예외>Receiver>run3 : " + e);
		}
	}
	
}
