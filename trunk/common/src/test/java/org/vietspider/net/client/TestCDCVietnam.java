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
public class TestCDCVietnam {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
    URL url = new URL(address);

    
    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");
      HttpHost httpHost = webClient.createHttpHost(address);

      if(httpGet == null) return null;
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);


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
    String home  = "http://www.cdcvietnam.com/v2/";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    webClient.registryProxy("127.0.0.1", 9090, null, null);
    
    byte[] obj = loadContent(home);
    writer.save(new File("F:\\Temp2\\webclient\\a.html"), obj);
    
//    Cookie [] cookies = webClient.getHttpClient().getCookieStore().getCookies();
//    System.out.println(cookies);
//    System.out.println(cookies.length);
//    System.out.println(cookies[0].getName() +" : "+cookies[0].getValue());
    
    obj = loadContent(home);
    writer.save(new File("F:\\Temp2\\webclient\\b.html"), obj);
    
//    cookies = webClient.getHttpClient().getCookieStore().getCookies();
//    System.out.println(cookies);
//    System.out.println(cookies.length);
//    System.out.println(cookies[0].getName() +" : "+cookies[0].getValue());
    
//    home = "http://vietnamnet.vn/xahoi/2007/10/750823/";
//    home = "http://www.vinhphuc.gov.vn/tag.b7a4741664ea428d.render.userLayoutRootNode.target.107.uP?view=Home&newsID=10225&topicID=2&fromView=Home#107";
//    home  = "http://muabanraovat.com/main.php?cat_id=1";
//    obj = webClient.loadContent(home);
//    writer.save(new File("F:\\Temp2\\webclient\\b.html"), obj);
    
//    obj = client.loadContent(address);
//    writer.save(new File("C:\\Temp\\b.html"), obj);
//    
//    obj = client.loadContent(address);
//    writer.save(new File("C:\\Temp\\c.html"), obj);
//    
//    System.out.println(get.getFollowRedirects());
  }

}
