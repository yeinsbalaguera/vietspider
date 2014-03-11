/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
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
 * Jun 5, 2008  
 */
public class TestRedirectHandler {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
    URL url = new URL(address);

    
    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      
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
    String home = "http://www.xahoithongtin.com.vn/index.asp?ID=52&subjectID=14820";
//    String home = "http://www.hanoi.gov.vn/hnportal/render.userLayoutRootNode.uP";
//    String home = "http://giadinh.net.vn/?direct=455c6d31e7e5e49f8dea243641ca29f2&column=95&nID=24283&lang=Vn";

    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);


    System.out.println(home);


   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();

    webClient.setURL(null, new URL(home));
//    webClient.getHttpClient().setRedirectHandler(new WebRedirectHandler());
//  webClient.registryProxy("127.0.0.1", 9090, null, null);

    byte[] obj = loadContent(home);
    writer.save(new File("F:\\Temp2\\webclient\\a.html"), obj);

//    obj = loadContent(home);
//    writer.save(new File("F:\\Temp2\\webclient\\b.html"), obj);

  }
}
