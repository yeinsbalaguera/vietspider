/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2008  
 */
public class Site1GetProxy {
  
  public void load() throws Exception {
    String home  = "http://proxylist.sakura.ne.jp";
    String address = "http://proxylist.sakura.ne.jp/index.htm?pages=";
    
    System.out.println(home);
    
    TestGetProxies.webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
    
    final org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    File file  = new File("D:\\Temp\\proxies2.txt") ;
    
    TestGetProxies.webClient.setURL(null, new URL(home));
   
    Pattern pattern = Pattern.compile("proxy[(]\\d*[,][']\\d*['][,][']\\d*['][,][']\\d*['][,][']\\d*['][,]\\d*");
    for(int i = 0; i < 10; i++) {
      String newAddress = address + String.valueOf(i);
      byte[] bytes = TestGetProxies.loadContent(newAddress);
      
      System.out.println(bytes);
//      File file2  = new File("F:\\Temp2\\webclient\\b.html") ;
//      byte [] bytes = reader.load(file2);
      
      if(bytes == null) continue;
      String value = new String(bytes);
      
      Matcher matcher = pattern.matcher(value);
      while(matcher.find()) {
        int start = matcher.start();
        int end = matcher.end();
//        if(start < 0 || end < 0) break;
        String proxy = value.substring(start, end);
        int idx = proxy.indexOf('\'');
        if(idx < -1) continue;
        proxy = proxy.substring(idx);
        proxy = proxy.replaceAll("'", "");
        idx = proxy.lastIndexOf(',');
        proxy = proxy.substring(0, idx) +":"+ proxy.substring(idx+1);
        proxy = proxy.replace(',', '.');
        System.out.println(proxy);
        writer.append(file, (proxy+"\n").getBytes());
      }
    }
  }
}
