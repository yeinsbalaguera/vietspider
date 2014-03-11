/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 7, 2008  
 */
public class TestDantri {
  
  private static WebClient webClient = new WebClient();
  
  private static HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
  
//  static {
//    methodHandler.setTimeout(60);
//  }
//  
  public static byte[] loadContent(String address) throws Exception {
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      
      HttpResponse httpResponse = methodHandler.execute(address, "");
      
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp){
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home = "http://dantri.com.vn/";
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
//    webClient.registryProxy("216.194.70.3", 8118, null, null);
    
    String address = "http://ddth.com/images/avatars/Cartoon_Ummm.gif";
    byte[] obj = loadContent(address);
    System.out.println( "xong "+ address);
    writer.save(new File("F:\\Temp2\\webclient\\anh.jpg"), obj);
    System.exit(1);
  }
}
