/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 24, 2009  
 */
public class CityCodeDetector {

  final static char [] [] keys = {
    "địa chỉ".toCharArray(),
    "nơi rao".toCharArray(),
    "liên hệ".toCharArray(), 
    "địa điểm".toCharArray(),
    "liên lạc".toCharArray(),
    "tin rao vặt tại".toCharArray(), 
    "nơi đăng tin".toCharArray(), 
    "nơi đăng".toCharArray(),
    "region".toCharArray(),
    "phạm vi".toCharArray(),
    "address".toCharArray(),
    "khu vực".toCharArray(),
    "add".toCharArray(),
    "người đăng".toCharArray()
  };
  
  public String detect(String text) {
    char [] chars = text.toCharArray();
    List<char[]> trust = new ArrayList<char[]>();
    List<char[]> normal = new ArrayList<char[]>();
    CityCodeCharsUtil.split2List(keys, chars, trust, normal);
    for(int i = 0; i < trust.size(); i++) {
      String name = CityCodes.getInstance().match(trust.get(i));
      if(name != null) return name;
    }
    
    String name2 = null;
    for(int i = 0; i < normal.size(); i++) {
      char [] line = normal.get(i);
      String name = CityCodes.getInstance().match(line);
      if(name != null) return name;
      if(name2 == null) name2 = CityCodes.getInstance().match2(line);
    }
    
    return name2;
  }
  
 /* public String detect(String text) {
    //    System.out.println(text);
    char [] chars = text.toCharArray();
    for(int i = 0; i < keys.length; i++) {
     String name = match(chars, keys[i]);
     if(name != null) return name;
//      int index = CharsUtil.indexOfIgnoreCase(chars, keys[i], 0);
//      if(index < 0 ) continue;
//      System.out.println(new String(keys[i])+ " ==  > "+ index);
//      index += keys[i].length;
//      if(index >= chars.length-1) continue; 
//      if(Character.isLetterOrDigit(chars[index+1])) continue;
//      char [] region = new char[chars.length - index];
//      System.arraycopy(chars, index, region, 0, region.length);
//      String name  = CityCodes.getInstance().match(region);
//      System.out.println(new String(region));
//      if(name != null) return name;
    }

    //    System.out.println(new String(chars));

    String name2 = null;
    int start = 0;
    int index  = 0;
    while(index < chars.length) {
      char c = chars[index];
       if(!Character.isLetterOrDigit(c) && c != ' ') {
        System.out.println(" ===  >|"+ c+"|");
      }
      if(c == '\n') {
        char [] region = new char[index - start];
        System.arraycopy(chars, start, region, 0, region.length);
//                System.out.println(new String(region));
        String name  = CityCodes.getInstance().match(region, region.length);
        if(name != null) return name;
        if(name2 == null) name2 = CityCodes.getInstance().match2(region, region.length);
        start = index+1;
      }
      index++;
    }

    if(start < chars.length) {
      char [] region = new char[index - start];
      System.arraycopy(chars, start, region, 0, region.length);
      String name  = CityCodes.getInstance().match(region, region.length);
      if(name != null) return name;
      if(name2 == null) name2 = CityCodes.getInstance().match2(region, region.length);
      start = index+1;
    }
    
    return name2;
  }*/

 /* private String match(char [] chars) {
    int start  = 0;
    String name  = null;
    while(start < chars.length) {
      
      String name  = CityCodes.getInstance().match(chars, 150);
//      System.out.println(new String(region));
//      System.out.println("==================================================");
      if(name != null) return name;
    }
    return null;
  }*/

}
