/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.post;

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
import org.vietspider.common.io.RWData;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestPostMethod3 {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address, List<NameValuePair> nvpList) throws Exception {
    HttpPost httpPost = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpPost = webClient.createPostMethod(address, "", nvpList, "utf-8");
      
      System.out.println(httpPost.getEntity().toString());

      if(httpPost == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);
      
      System.out.println(httpResponse.getStatusLine().getStatusCode());
      

//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      ResponseReader httpResponseReader = new ResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
//    WebRedirectHandler redirectHandler = new WebRedirectHandler();
//    webClient.setRedirectHandler(redirectHandler);
    webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
    webClient.setLog(true);
    
    String home = "http://www2.guidestar.org/SearchResults.aspx";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
    
    File file = new File(TestPostMethod3.class.getResource("PostData2").toURI());
    String data  = new String(RWData.getInstance().load(file), "utf-8");
    String [] elements  = data.split("\n");
    
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      int index = elements[i].indexOf('=');
      if(index < 0) continue;
      String name  = elements[i].substring(0, index);
      String value  = elements[i].substring(index+1, elements[i].length());
      nvpList.add(new BasicNameValuePair(name, value));
    }    
    
    byte [] bytes = loadContent(home, nvpList);
    System.out.println(" da xong " + file.getAbsolutePath()+ " : "+ bytes.length);
    file  = new File(file.getParentFile(), "b.html");
    writer.save(file, bytes);
    System.exit(0);
  }
  
}
