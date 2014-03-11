/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestPostMethod2 {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address, List<NameValuePair> nvpList) throws Exception {
    HttpPost httpPost = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      
      httpPost = webClient.createPostMethod(address, "", nvpList, "utf-8");      

      if(httpPost == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);

//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    //?target=listcategory&page=5&cat=404&ord=0
    String home = "http://www.vinabook.com/tieng-anh-m13i404/";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
    nvpList.add(new BasicNameValuePair("target", "listcategory"));
    nvpList.add(new BasicNameValuePair("page", "7"));
    nvpList.add(new BasicNameValuePair("cat", "404"));
    nvpList.add(new BasicNameValuePair("ord", "0"));
    
    
    byte [] bytes = loadContent(home, nvpList);
    writer.save(new File("D:\\Temp\\b.html"), bytes);
    System.exit(0);
    
  }
  
}
