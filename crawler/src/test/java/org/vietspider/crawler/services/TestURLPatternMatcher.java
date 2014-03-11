/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 13, 2007  
 */
public class TestURLPatternMatcher {
  
  public static void testURL() {
//    String value  = "http://vietsonic.com.vn/Product_detail.asp?pID='UltraFlat'";
//    String txtPattern  = "http[:][/][/][[\\p{L}\\p{Digit}]*[.]]*";
    
//    String value  = "aaaaa www.vietsonic.com.vn/Product_detail.asp?pID='UltraFlat'";
//    String txtPattern  = "www[.] [[\\p{L}\\p{Digit}]*[.]]*";
    
//    String value  = "aaaaa www.vietsonic.org/Product_detail.asp?pID='UltraFlat'";
//    String txtPattern  = "[[\\p{L}\\p{Digit}]*[.]]*[.]com";
    
    StringBuilder buildValue = new StringBuilder();
    buildValue.append("aaaaa www.vietsonic.com.vn/Product_detail.asp?pID='UltraFlat'");
    buildValue.append(" dfsdfds www.aha.vn sfhdsfjsdfhds vnexpress.net dsfdsfdsfjk");
    buildValue.append("  sfsdf http://www.vnn.vn");
    buildValue.append("Website: www.abc-transportation.com hoặc www.thuexedulich.comhdfsd");
    String value = buildValue.toString();
    
   
//    String txtPattern = "[[\\p{L}\\p{Digit}]*[.]]*[.]vn[^a-z&&[^0-9]]";
    
    String [] txtPatterns = {
        "http[:][/][/][[\\p{L}\\p{Digit}]*[.]]*",
        "www[.] [[\\p{L}\\p{Digit}]*[.]]*",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]vn[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]com.vn[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]gov.vn[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]net.vn[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]org.vn[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]com[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]net[^a-z&&[^0-9]]",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]org[^a-z&&[^0-9]]",
    };
    
    Pattern [] patterns = new Pattern[txtPatterns.length];
    for(int i = 0; i< patterns.length; i++) {
      patterns[i] = Pattern.compile(txtPatterns[i], Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    }
    
    List<String> collection = new ArrayList<String>();
    for(int i = 0; i < patterns.length; i++) {
      StringBuilder builder = new StringBuilder();
      Matcher matcher = patterns[i].matcher(value);
      int lastStart = 0;
      while(matcher.find()) {
        int start = matcher.start();
        int end = matcher.end();
        if(start > -1 && end > start) {
          builder.append(value.substring(lastStart, start));
          collection.add(value.substring(start, end));
          lastStart = end+1; 
        }
      }
      if(lastStart > 0 && lastStart < value.length()) {
        builder.append(value.substring(lastStart, value.length()));
      }
      if(builder.length() > 0) value = builder.toString();
    }
    
    for(String ele : collection) {
      System.out.println("==== > "+ ele);
    }
   
  }
  
  public static void main(String[] args) {
    testURL();
    
  /*  String prefix  = "http://";
    String value = "c264/t40/lao-dong-tri-oc-ban-tho?i-gian.html";
    String address = value.toLowerCase().trim();
    if(!address.startsWith(prefix)) address = prefix +address;*/
    
//    try {
//      System.out.println(InetAddress.getByName("vnexpress.anet"));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
   /* System.out.println(address.indexOf('/'));
    int slashIndex = address.indexOf("?", prefix.length());
    System.out.println("hic "+slashIndex);
    if(slashIndex > 0) address = address.substring(0, slashIndex);
    
    int dotIndex = address.indexOf('.') ;
    System.out.println(dotIndex);
    System.out.println("thay la "+ address);*/
    
//    System.out.println("http://www.vietnamnet.net".substring(7));
    
    int style = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
    Pattern cssTitlePattern = Pattern.compile("[title]", style);
    String value  = "newstitle";
    Matcher matcher = cssTitlePattern.matcher(value);
    System.out.println(matcher.find());
  }
}
