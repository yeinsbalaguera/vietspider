/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2007  
 */
import java.io.File;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 2, 2006  
 */
public class TestWebClientURLEncoder {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
//    URL url = new URL(address);
    
    HttpGet httpGet = null;
    try{
      httpGet = webClient.createGetMethod(address, "");

      if(httpGet == null) return null;
//      System.out.println(" an cut ");
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      System.out.println(" status code is "+ statusCode);
      
//      Header [] headers = httpResponse.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : " + header.getValue());
//      }
      
      System.out.println(" \n\n chuan bi read "+ address);
      
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      byte [] bytes = null; 
      
      long start = System.currentTimeMillis();
      
//      HttpEntity httpEntity = httpResponse.getEntity();
//      bytes = webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      
      bytes = httpResponseReader.readBody(httpResponse);
      
      long end = System.currentTimeMillis();
      System.out.println(" doc het "+ (end - start)+ " s");
      
      System.out.println(" bytes size "+ bytes.length);
      
      return bytes;
      
    } catch(Exception exp){
      exp.printStackTrace();
//      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
//    System.out.println(java.net.URLEncoder.encode("`", "utf-8"));
//    String home  = "http://abcnews.go.com/\"/International/story?id=5803011&page=1%252F%2522";
//    String home  = "http://my.opera.com/vietnq/blog/index.dml/tag/Ha~y co^' ga'ng le^n Vie^t nhe' !";
    String home  = "http://my.opera.com/chuonchuonlonely/blog/index.dml/tag/^^";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    byte[] obj = loadContent(home);
    writer.save(new File("F:\\webclient\\a.html"), obj);
    
    System.exit(1);
    
  }
}
