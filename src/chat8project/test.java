package chat8project;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class test {

	
	public static void main(String[] args) {
		HashSet<String> blacklist = new HashSet<String>(); 
		blacklist.add("스팸");
		blacklist.add("음란물");
		blacklist.add("광고");
		
		Scanner scan = new Scanner(System.in);
		System.out.println("이름을 입력하세요 :");
		String name = scan.nextLine();
		
		Iterator<String> it = blacklist.iterator();
		if(it.hasNext()) {
			
			if (blacklist.contains(name)) {
				System.out.println("접속 불가능");
			}else {
				System.out.println("접속 가능");
			}
					
		}
	}

}










