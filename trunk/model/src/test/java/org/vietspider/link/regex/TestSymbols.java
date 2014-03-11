package org.vietspider.link.regex;


public class TestSymbols {
	public static void main(String[] args) {
		String template = "query=nha-**-cong-$-hi";
		String value = "query=nha-sdfsdfsdf-cong-23232-hi";
		
//		template = "*_html_5_and_javascript.html";
//		value = "benchmarking_html_5_and_javascript.html";
		
		Symbols symbols = new Symbols(template);
		System.out.println(symbols.match(value));
	}
}
