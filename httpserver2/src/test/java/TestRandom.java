/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 11, 2010  
 */
public class TestRandom {
  public static void main(String[] args) {
    for(int i = 0 ; i < 10; i++) {
      System.out.println((int)(Math.random()*2));
    }
    
    String text  = "thuê nhà tại tphcm";
    Pattern pattern = Pattern.compile("ở|tại");
    Matcher matcher = pattern.matcher(text);
    System.out.println(matcher.find());

//    int index = text.indexOf("ở|tại"); 
//    System.out.println(matcher.start());
  }
}
