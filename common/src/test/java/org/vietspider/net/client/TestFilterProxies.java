/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2008  
 */
public class TestFilterProxies {
  
  
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\webclient\\proxies.all.txt") ;
    File newFile  = new File("F:\\Temp2\\webclient\\proxies3.txt") ;
    String textValue  = new String(RWData.getInstance().load(file), "utf-8");
    String [] proxies = textValue.split("\n");
    
    StringBuilder builder = new StringBuilder();
    Set<String> set = new HashSet<String>();
    for(int i = 0; i < proxies.length; i++) {
      if(proxies[i].trim().isEmpty()) continue;
      if(set.contains(proxies[i])) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(proxies[i]);
      RWData.getInstance().append(newFile, (proxies[i]+"\n").getBytes());
      set.add(proxies[i]);
    }
    System.out.println(builder);
   
  }
  
  synchronized public static void deleteFolder(File file){
    File[] list = file.listFiles();
    for(File ele : list){
      ele.delete();
    }
  }
}
