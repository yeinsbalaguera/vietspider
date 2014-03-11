/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 14, 2008  
 */
public class Pinging {
  
  static int pinging(String ip) throws Exception {
    String cmd = "ping " + ip;
    Runtime runtime = Runtime.getRuntime();
    Process p = runtime.exec(cmd);
    InputStreamReader isr=new InputStreamReader(p.getInputStream());
    BufferedReader brd = new BufferedReader(isr);
    String line=null;
    int flag=0;
    while(true) {
      line= brd.readLine();
      if(line == null) break;
      line  = line.toLowerCase();
//      System.out.println(line);
      if(line.indexOf("ttl=") > -1 && line.indexOf("time=") > -1) {
//      if(line.startsWith("Reply")) {
        flag=1;
        break;
      }
    }
    return flag;
  }
  
  public static void main(String[] args) throws Exception  {
    URL url = new URL("http://www.daniweb.com/forums/thread120734.html");
    System.out.println(pinging(url.getHost()));
    try {
      InetAddress.getByName(url.getHost());
      System.out.println(" xong roi ");
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}
