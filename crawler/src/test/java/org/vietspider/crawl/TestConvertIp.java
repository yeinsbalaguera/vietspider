/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.io.File;
import java.net.InetAddress;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 11, 2009  
 */
public class TestConvertIp {
  
  private static void convert(String name, String ouput) throws Exception {
    StringBuilder builder = new StringBuilder();
    File file  = new File("D:\\Temp\\chan\\"+name);
    String text = new String(RWData.getInstance().load(file));
    
    String [] elements = text.split("\n");
    for(int i = 0; i < elements.length; i++) {
      InetAddress inetAddress = InetAddress.getByName(elements[i].trim());
      String ip = inetAddress.getHostAddress();
      
      if(builder.length() > 0) builder.append('\n');
      builder.append("ip route add ").append(ip).append("/32    via $VNPT  #  ").append(elements[i].trim());
    }
    
    file  = new File("D:\\Temp\\chan\\"+ouput);
    org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes());
  }
  
  public static void main(String[] args) throws Exception {
   convert("fpt.txt", "fpt2.txt");
   convert("vnpt.txt", "vnpt2.txt");
  }
}
