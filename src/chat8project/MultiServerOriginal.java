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
import java.util.Scanner;



public class MultiServerOriginal implements Limit{

	// 멤버변수
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	
	// 클라이언트 정보저장을 위한 Map 컬렉션 생성
	Map<String, PrintWriter> clientMap;
	Map<String,String> nameMap = new HashMap<String,String>(); // 귓속말고정
	Map<String,String> blockMap = new HashMap<String,String>(); // block차단

	HashSet<String> pWords = new HashSet<String>(); //대화금칙어처리
	HashSet<String> blacklist = new HashSet<String>(); // 블랙리스트 처리 부분
	
	
	public MultiServerOriginal() {
		// 클라이언트의 이름과 출력스트림을 저장할 HashMap 컬렉션 생성
		clientMap = new HashMap<String, PrintWriter>();
		// HashMap 동기화 설정. 쓰레드가 사용자 정보에 동시에 접근하는것을 차단함
		Collections.synchronizedMap(clientMap);
		
		
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
		MultiServerOriginal ms = new MultiServerOriginal();
		ms.init();
	}

	// 접속된 모든 클라이언트 측으로 서버의 메세지를 Echo해주는 역할 담당
	public void sendAllMsg(String name, String msg , String flag) {
		
		// Map에 저장된 객체의 키값(대화명)을 먼저 얻어온다.
		Iterator<String> it = clientMap.keySet().iterator();
		//HashSet<String> set = new HashSet<String>();
		Scanner scan = new Scanner(System.in);
		
		
		// 저장된 객체(클라이언트)의 개수만큼 반복한다.
		while(it.hasNext()) {
			
			try {

				
				// 컬렉션의 key는 클라이언트의 대화명이다.
				String clientName = it.next();
				
				
				try {
					// 각 클라이언트의 PrintWriter객체를 얻어온다.
					PrintWriter it_out = (PrintWriter)clientMap.get(clientName); //clientName : key => key로 value값을 받아오는것
					nameMap.put(name, clientName); // name: 보내는사람이름 ,clientName : 받는사람이름
					System.out.println("보내는사람 : "+name+" 받는사람 : "+clientName);
					
					
					// flag가 One이면 해당 클라이언트 한명에게만 전송한다.(귓속말)
					if (flag.equals("One")) {
						//try {
						// 컬렉션에 저장된 접속자명과 일치하는 경우에만 메세지를 전송한다.
							if(name.equals(clientName)) {
								it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
								//it_out.println("[귓속말]"+name+": "+msg);
							}
						//}catch(UnsupportedEncodingException e1){}
						
					}else if(flag.equals("OneFix")) {
//						
//						Iterator<String> map = nameMap.keySet().iterator();
						//String key = map.next();
//						String msg2 = "";
						
						
						if(name.equals("stop")) {
							it_out.println("귓속말을 종료합니다.");
							break;
		 				}
						//if(name.equals(key) && clientName.equals(nameMap.get(key))) {

//							it_out.println(name+"에게 고정귓속말 시작");
//							boolean tf = true;
//							while(tf)
//							it_out.println("메세지를 작성하세요(종료는 exit입력) : ");
							//it_out.println("[귓속말]"+URLEncoder.encode(name, "UTF-8")+": "+URLEncoder.encode(msg, "UTF-8"));
//							return;
//							msg2= scan.nextLine();
						
//								if(msg2.equals("exit")) {
//									tf = false;
						
//								}
						//}
						//try {
						
//						it_out.println("귓속말을 종료하려면 /unfixto를 입력해주세요");
							
						//}catch(UnsupportedEncodingException e1){}
							
						
					
					}else if(flag.equals("Block")) {
						
						blockMap.put(name, clientName);
						Iterator<String> blockKeys = blockMap.keySet().iterator() ;
						while (blockKeys.hasNext()) {
							String key = blockKeys.next();
	//							try {
								if(key.contains(name) && blockMap.get(key).contains(clientName)) {
									
									break;
								}
//								else {
//									it_out.println("["+name+"] : "+msg);
//								}
									
						}
						it_out.println("블럭차단을 종료하려면 /unblock을 입력해주세요");
						if(name.equals("stop")) {
							it_out.println("차단을 해제합니다.");
							blockMap.remove(clientName);
							break;
		 				}
							
							
						
						
					}else if(flag.equals("List")) {
						
							it_out.print("[현재 접속자] :");
							for(String key : clientMap.keySet() ) {
								it_out.print(key + " ");
							}
							it_out.println();

						
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
					
				}catch(Exception e) {
					System.out.println("예외 : "+e);
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
		int nameCnt=0;
		
		public MultiServerT(Socket socket) {
			this.socket = socket;
			
			try {
				out =  new PrintWriter(this.socket.getOutputStream(),true);
				in = new BufferedReader(
						new InputStreamReader(
								this.socket.getInputStream()));
				
				
			} catch (Exception e) {
				System.out.println("예외 : "+e);
			}
			
		}
		@Override
		public void run() {
			Jdbc jdbc = new Jdbc();
			String name = "";
			String s = "";
			String s2= "";
			//Thread receiver = new Receiver(socket);
			
			blacklist.add("짱구");
			blacklist.add("흰둥이");
			blacklist.add("짱아");
			
			pWords.add("바보");
			pWords.add("메롱");
			pWords.add("대출");
			
			try {
				
				/*
				클라이언트가 보내는 최초메세지는 대화명이므로
				접속에 대한 부분을 출력하고 Echo한다.
				 */
				if (in != null) {
				
					// 클라이언트의 이름을 읽어온다.
					name = in.readLine(); 
					name = URLDecoder.decode(name,"UTF-8");
					
					

					Iterator<String> it = blacklist.iterator();

					if (blacklist.contains(name)) {
						out.println("차단된 회원 -> 접속 불가능");
						out.println("서버와 연결이 해제되었습니다.");
						return;
					}
					
					// 현재 접속한 클라이언트를 HashMap에 저장한다.
					if(clientMap.containsKey(name)) {
						out.println("이미 존재하는 이름 입니다.");
						
					}else {
						clientMap.put(name, out);
						
						// 방금 접속한 클라이언트를 제외한 나머지에게 입장을 알린다.
						sendAllMsg("",name + "님이 입장하셨습니다.","ALL"); 
						
						// 접속자의 이름을 서버의 콘솔에 띄워주고
						System.out.println(name + " 접속");
						// HashMap에 저장된 객체의 수로 현재 접속자를 파악할 수 있다.
						System.out.println("현재 접속자 수는 "+clientMap.size()+"명 입니다.");
					}

					
					
					if (clientMap.size()>MAX_ACCEPT) {
						System.out.println("최대 인원을 초과하여 접속이 불가능합니다..");
						out.println("최대 인원을 초과하여 접속이 불가능합니다.");
						return;
					}
					
					//HashSet<String> set = new HashSet<String>();
					
					// 입력한 메세지는 모든 클라이언트에게 Echo된다.
					while(in != null) {
						s = in.readLine();
						//set.add(s);
						s = URLDecoder.decode(s,"UTF-8");
						if(s==null) {
							break;
						} 
						
						for (String pw : pWords) {
							if(s.contains(pw)) {
								s = "금칙어 입력 -> 메세지 전송 불가능";
							}
						}
						
						// 서버의 콘솔에 출력되고
						System.out.println(name + " >> "+s); 
						//jdbc.dataInput(name,s);
						
						// 클라이언트 측으로 전송한다.
						if (s.charAt(0)=='/') {
							String[] strArr = s.split(" ");
							String msgContent = "";
							for (int i = 2; i < strArr.length; i++) {
								msgContent += strArr[i]+" ";
								
							}
							
							if (strArr[0].equals("/to")) {
								sendAllMsg(strArr[1], msgContent,"One");
							}
							if(strArr[0].equals("/list")) {
								sendAllMsg("", "","List");
							}
							if(strArr[0].equals("/fixto")) {
								out.println("고정귓속말을 시작합니다.\n"
										+ "귓속말을 종료하려면 /unfixto를 입력해주세요");
								
								while(true) {
									s2 = in.readLine();
									s2 = URLDecoder.decode(s2,"UTF-8");
									if (s2.charAt(0)=='/') {
										String[] strArr2 = s2.split(" ");
										String msgContent2 = "";
										for (int i = 2; i < strArr2.length; i++) {
											msgContent2 += strArr2[i]+" ";
											
											if(strArr2[0].contains("/unfixto")){
												sendAllMsg(strArr2[1],"", "OneFix");
												break;
												
											}
										}
									}else {
										sendAllMsg(strArr[1], s2,"One");
									}
								}
								
							}
//							if(strArr[0].equals("/unfixto")) {
//								sendAllMsg("stop","" ,"OneFix");
//							}
							if(strArr[0].equals("/block")) {
								sendAllMsg("","" ,"Block");
							}
							if(strArr[0].equals("/unblock")) {
								sendAllMsg("stop","" ,"Block");
							}
							
						}else {
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
				clientMap.remove(name);
				sendAllMsg("",name+"님이 퇴장하셨습니다.","ALL");
				System.out.println(name + "["+ Thread.currentThread().getName() +"] 퇴장");
				System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");
				
				try {
					in.close();
					out.close();
					jdbc.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
			}
			
		}
		
		
			
	}
	
}




