package org.vietspider.link.regex;



public class TestSingleURLElement {
	
	static void print(String template, String url) {
		URIElement urlTemplate = URIParser.parse(template);
		System.out.println(urlTemplate.match(url));	
	}
	
	public static void main(String[] args) throws Exception {
	  String pattern;
	  String value;
	  
	  pattern = "http://quangcaoso.vn/diendan/showthread.php?$-*";
	  value  = "http://quangcaoso.vn/diendan/showthread.php?917-Can-ho-Thao-Dien-Pear-goi-ngay-de-duoc-gia-tot-nhat-view-dep-tang-cao&s=8db94c1e980aa86cbf5c7c3055a0db40";
	  System.out.println("8db94c1e980aa86cbf5c7c3055a0db40".length());
		print(pattern, value);
	}
}
