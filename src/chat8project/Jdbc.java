package chat8project;

import java.sql.SQLException;

import java.util.Scanner;

public class Jdbc extends IConnectImpl{

	Scanner scan = new Scanner(System.in);
	public Jdbc() {
		super("kosmo","1234");
	}
	
//	public void printMenu() {
//		System.out.print("1.데이터 입력");
//		System.out.print("\t2.데이터 검색");
//		System.out.print("\t3.데이터 삭제");
//		System.out.println("\t4.프로그램 종료");
//		System.out.print("선택 : ");
//		
//	}

	public void dataInput(String name,String contents) {
		try {
		String query = " INSERT INTO chat_talking VALUES (chat_seq.nextval ,?, ?, sysdate) ";
		
		psmt = con.prepareStatement(query);
		
		System.out.println("DB에 채팅 내용을 저장합니다.");

		psmt.setString(1, name);
		psmt.setString(2, contents);

		
		int affected = psmt.executeUpdate();
		System.out.println(affected +"행이 입력되었습니다.");
		
	}catch(SQLException e) {
		System.out.println("sql오류");
		
	}catch(Exception e) {
		e.printStackTrace();
	}
		
	}
	
	public void dataSearch() {
		try {
			
			stmt = con.createStatement();

			System.out.println("데이터 검색을 시작합니다.");
			System.out.print("검색할 이름 :");
			String searchName = scan.nextLine();
	
			String query = "SELECT id, name, phoneNum, "
					+ " to_char(birthDay, 'yyyy-mm-dd') bir "
					+ " FROM chat_talking "
					+ " Where name like '%"+searchName+"%'";
			
			rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String phone = rs.getString("phoneNum");			
				String birth = rs.getString("bir");

				System.out.printf("%s %s %s %s\n",
						id,name, phone, birth);
			}
		}
		catch(SQLException e) {
			System.out.println("쿼리오류발생");
			e.printStackTrace();
		}catch(Exception e) {
			System.out.println("에러발생");
		}
		
	}
	
	public void dataDelete() {
		
		try {	
			String query = "DELETE FROM chat_talking WHERE name like ?";
			psmt = con.prepareStatement(query);
			System.out.println("삭제할 이름을 입력하세요.");
			String deleteName = scan.nextLine();
			psmt.setString(1, deleteName);
			System.out.println(psmt.executeUpdate()+"행이 삭제되었습니다");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
	}
		
	@Override
	public void close() {
		super.close();
	}

}
	
