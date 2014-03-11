/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import org.apache.http.HttpResponse;
import org.vietspider.chars.URLEncoder;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2008  
 */
public class TestGetProxies {
  
  public static WebClient webClient = new WebClient();
  
  private static HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
  
//  static {
//    methodHandler.setTimeout(30);
//  }
  
  public static byte[] loadContent(String address) throws Exception {
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      
      HttpResponse httpResponse = methodHandler.execute(address, "");
      return methodHandler.readBody();
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    new Site1GetProxy().load();
    new Site2GetProxy().load();
    System.exit(0);
  }
  
  
}
