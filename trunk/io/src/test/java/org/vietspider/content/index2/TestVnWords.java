/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index2;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2009  
 */
public class TestVnWords {
  
  private static Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
  
  public static int count(CharSequence charSeq){
    int start = 0;
    int counter = 0;
    Matcher matcher = wordPattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }
  
  public static void main(String[] args)  throws Throwable {
    File file  = new File("D:\\Temp\\full.vn.dict.cfg");
    byte [] bytes = RWData.getInstance().load(file);
    String data = new String(bytes, "utf-8");
    String [] elements = data.split(";");
    
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < elements.length; i++) {
      if(builder.length() > 0) builder.append(';');
      builder.append(elements[i].trim());
    }
    
    file  = new File("D:\\Temp\\full.vn.dict.cfg");
    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes("utf-8"));
  }
}
