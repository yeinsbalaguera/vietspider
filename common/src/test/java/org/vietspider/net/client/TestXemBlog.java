/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestXemBlog {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
    HttpGet httpGet = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    
    URLEncoder urlEncoder = new URLEncoder();
    String home = urlEncoder.encode("http://xemblog.com/");
    
    System.out.println(home);
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
   
    webClient.setURL(null, new URL(home));
    
    String address = "http://xemblog.com/bloglist/";
    for(int i = 1; i <= 3728; i++) {
      try {
        String index = String.valueOf(i);
        File file = new File("D:\\Temp\\Xemblog\\"+index+".html");
        if(file.exists()) continue;
        byte [] bytes = loadContent(address + index);
        if(bytes == null) continue;
        writer.save(file, bytes);
        Thread.sleep(5*1000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
  }
  
}
