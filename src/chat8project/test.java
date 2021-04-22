package chat8project;

import java.util.HashSet;
import java.util.Iterator;

public class test {
	HashSet<String> blacklist;
	String[] name;
	

	public String[] getName() {
		return name;
	}


	public test(String[] name) {
		blacklist = new HashSet<String>(); 
		blacklist.add("스팸");
		blacklist.add("음란물");
		blacklist.add("광고");

		Iterator<String> it = blacklist.iterator();
		System.out.println("");
		

		
		
		if(it.hasNext()) {
	
			if(name.equals(it)) {
				System.out.println("blacklist => do not access");
			}else {
				System.out.println("****");
			}
			
			
		}
		
		
	}
	
	public static void main(String[] args) {
		

		
	}

}
