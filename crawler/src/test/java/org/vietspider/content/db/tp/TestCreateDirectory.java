/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 4, 2011  
 */
public class TestCreateDirectory {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\Temp\\tp\\", "tdtv2.txt"); 
    String content = new String(RWData.getInstance().load(file), Application.CHARSET);
    String [] elements = content.split("\n");
    
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < elements.length; i++) {
      if(elements[i].charAt(0) != '\"') continue;
      int idx = elements[i].indexOf('\"', 2);
     
      if(idx < 0) continue;
      String value = elements[i].substring(1, idx);
      if(builder.length() > 0) builder.append('\n');
      builder.append(value);
    }
    
    file = new File("D:\\Temp\\tp\\", "tdtv3.txt"); 
    RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
  }
  
}
