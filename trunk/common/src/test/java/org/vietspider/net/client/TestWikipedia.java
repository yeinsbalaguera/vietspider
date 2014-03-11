/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2007  
 */
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
public class TestWikipedia {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
    URL url = new URL(address);

    
    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");
      System.out.println(httpGet.getFirstHeader("User-Agent").getValue().trim());

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      System.out.println(httpGet.getFirstHeader("User-Agent").getValue().trim());


//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
//      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home  = "http://en.wikipedia.org/wiki/Special:AllPages/Ak";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    byte[] obj = loadContent(home);
//    writer.save(new File("F:\\Temp2\\webclient\\a.html"), obj);
    System.out.println(new String(obj));
  }

}
