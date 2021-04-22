package chat8project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;



public class MultiServer {

	// 멤버변수
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	
	// 클라이언트 정보저장을 위한 Map 컬렉션 생성
	Map<String, PrintWriter> clientMap;
	
	// 블랙리스트 처리를 위한 HashSet 컬렉션 생성
	HashSet<String> blacklist;
		
	

		
		
	
	public MultiServer() {
		// 클라이언트의 이름과 출력스트림을 저장할 HashMap 컬렉션 생성
		clientMap = new HashMap<String, PrintWriter>();
		// HashMap 동기화 설정. 쓰레드가 사용자 정보에 동시에 접근하는것을 차단함
		Collections.synchronizedMap(clientMap);
		
		blacklist = new HashSet<String>(); 
		blacklist.add("스팸");
		blacklist.add("음란물");
		blacklist.add("광고");
		
	}
	

	// 채팅 서버 초기화
	public void init() {
		try {
			// 서버 소켓 오픈
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			while(true) {
				

				
				
				// 클라이언트의 접속 허가
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + "(클라이언트)의 "
						+ socket.getPort() + "포트를 통해 "
						+ socket.getLocalAddress() + "(서버)의 "
						+ socket.getLocalPort() + "포트로 연결되었습니다.");
				
				
				
				// 쓰레드로 정의된 내부클래스 객체생성 및 시작
				// 클라이언트 한명당 하나씩의 쓰레드가 생성된다.
				Thread mst = new MultiServerT(socket);
				mst.start();
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally {
			
			try {
				serverSocket.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	public static void main(String[] args) {
		// static 타입이 아니라서 직접 호출 불가능
		// 참조변수를 통해서만 호출가능
		MultiServer ms = new MultiServer();
		ms.init();
		
	}

	// 접속된 모든 클라이언트 측으로 서버의 메세지를 Echo해주는 역할 담당
	public void sendAllMsg(String name, String msg , String flag) {
		
		// Map에 저장된 객체의 키값(대화명)을 먼저 얻어온다.
		Iterator<String> it = clientMap.keySet().iterator();
		
		// 저장된 객체(클라이언트)의 개수만큼 반복한다.
		while(it.hasNext()) {
			
			try {
				// 컬렉션의 key는 클라이언트의 대화명이다.
				String clientName = it.next();
				
				// 각 클라이언트의 PrintWriter객체를 얻어온다.
				try {
					PrintWriter it_out = (PrintWriter)clientMap.get(clientName);

				// flag가 One이면 해당 클라이언트 한명에게만 전송한다.(귓속말)
				if (flag.equals("One")) {
					// 컬렉션에 저장된 접속자명과 일치하는 경우에만 메세지를 전송한다.
					
					try {
						if(name.equals(clientName)) {
							it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
						}
					}catch(UnsupportedEncodingException e1){}
				
				// flag가 OneFix이면 해제 전까지 계속 전송한다.(귓속말)
				}else if(flag.equals("OneFix")) {
					
					while(true) {
						try {
							if(name.equals(clientName)) {
								it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
							
								if(flag.equals("FixOut")) {
									break;
								}
							
							}
							
						}catch(UnsupportedEncodingException e1){}
						
					}
					
				}else if(flag.equals("Block")) {
				
					while(true) {
						try {
							if(name.equals(clientName)) {
								it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
								
								if(flag.equals("UnBlock")) {
									break;
								}
							
							}
							
						}catch(UnsupportedEncodingException e1){}
						
					}
					
				}else if(flag.equals("List")) {
					
					while(true) {
						try {
							if(name.equals(clientName)) {
								it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
							}
							
						}catch(UnsupportedEncodingException e1){}
						
					}	
				
				// 그 외에는 모든 클라이언트에게 전송한다.
				} else {
					/*
					클라이언트에게 메세지를 전달할때 매개변수로 name이
					있는 경우와 없는 경우를 구분해서 전달하게 된다.
					 */
					try {
						if(name.equals("")) {
							// 입장, 퇴장에서 사용되는 부분
							it_out.println(URLEncoder.encode(msg, "UTF-8"));
						}else {
							// 메세지를 보낼때 사용되는 부분
							it_out.println("["+URLEncoder.encode(name, "UTF-8")+"] : "+URLEncoder.encode(msg, "UTF-8"));
						}
						
					}catch(UnsupportedEncodingException e1) {}
				}
				}catch(Exception e){
					System.out.println(e);
				}
					
			}catch (Exception e) {
				System.out.println("예외 : "+e);	
			}
		}
		
		
		
	}



	
	
	
	class MultiServerT extends Thread{
		// 멤버변수
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		Thread receiver; 
		//Sender sender; 

		
		public MultiServerT(Socket socket) {
			this.socket = socket;
			receiver = new Receiver(socket);
			try {
				out =  new PrintWriter(this.socket.getOutputStream(),true);
				in = new BufferedReader(
						new InputStreamReader(
								this.socket.getInputStream(), "UTF-8"));
				
				
			} catch (Exception e) {
				System.out.println("예외 : "+e);
			}
			
		}
		@Override
		public void run() {

			String name = "";
			String s = "";
			//sender = new Sender(socket,name);
			
			try {
				
				/*
				클라이언트가 보내는 최초메세지는 대화명이므로
				접속에 대한 부분을 출력하고 Echo한다.
				 */
				if (in != null) {
				
					// 클라이언트의 이름을 읽어온다.
					name = in.readLine(); 
					name = URLDecoder.decode(name,"UTF-8");
					
					// 방금 접속한 클라이언트를 제외한 나머지에게 입장을 알린다.
					sendAllMsg("",name + "님이 입장하셨습니다.","ALL"); 

					// 현재 접속한 클라이언트를 HashMap에 저장한다.
					//if(name == null) {
					//clientMap.put(name, out);
					//}
					
//					for (String b : blacklist) {
//						if(name.contentEquals(b)==true) {
//							System.out.println("blacklist => do not access");
//						}
//						return;
//						
//					}
				
					
					if(clientMap.containsKey(name)==true) {
						System.out.println("중복된 이름이 존재합니다.");
						receiver.interrupt();
						//return;
					}else if(clientMap.containsKey(name)==false) {
						clientMap.put(name, out);
						System.out.println(name + " 접속");
						System.out.println("현재 접속자 수는 "+clientMap.size()+"명 입니다.");
						
					}
					

					

					// 입력한 메세지는 모든 클라이언트에게 Echo된다.
					while(in != null) {
						s = in.readLine();
						s = URLDecoder.decode(s,"UTF-8");
						System.out.println(s);
						if(s==null) {
							break;
						}
						// 서버의 콘솔에 출력되고
						System.out.println(name + " >> "+s); 
						
						
						// 클라이언트 측으로 전송한다.
						if (s.charAt(0)=='/') {
							String[] strArr = s.split(" ");
							String msgContent = "";
							for (int i = 2; i < strArr.length; i++) {
								msgContent += strArr[i]+" ";
							}
							if (strArr[0].equals("/to")) {
								sendAllMsg(strArr[1], msgContent,"One");
							}else if(strArr[0].equals("/fixto")) {
								sendAllMsg(strArr[1], msgContent,"OneFix");	
							}else if(strArr[0].equals("/unfixto")) {
								sendAllMsg(strArr[1], msgContent,"FixOut");	
							}else if(strArr[0].equals("/block")) {
								sendAllMsg(strArr[1], msgContent,"Block");	
							}else if(strArr[0].equals("/unblock")) {
								sendAllMsg(strArr[1], msgContent,"UnBlock");	
							}else if(strArr[0].equals("/list")) {
								sendAllMsg(strArr[1], msgContent,"List");	
							}
							
							
						} else {
							sendAllMsg(name,s,"ALL"); 

						}
					}
				}
				
				
				
			} catch (Exception e) {
				System.out.println("예외 : "+e);	

			}finally {
				
				/*
				클라이언트가 접속을 종료하면 Socket예외가 발생하게 되어
				finally절로 진입하게 된다. 이때 "대화명"을 통해 정보를
				삭제한다.
				 */
				if (in!=null) {
					clientMap.remove(name);
					sendAllMsg("",name+"님이 퇴장하셨습니다.","ALL");
					System.out.println(name + "["+ Thread.currentThread().getName() +"] 퇴장");
					System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");
				}
				try {
					in.close();
					out.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
			}
			
		}
		
		
			
	}
	
}




