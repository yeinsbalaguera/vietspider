/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import static org.vietspider.net.client.HttpClientFactory.CONTENT_TYPE_NAME;
import static org.vietspider.net.client.HttpClientFactory.CONTENT_TYPE_VALUE_FORM;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestPostMethod3 {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
    try {
      int idx = address.indexOf('?');
      String query = address.substring(idx+1);
      String url = address.substring(0, idx);
      
      System.out.println(url);
      System.out.println(query);
      
      HttpPost httpPost = new HttpPost(url);
      httpPost.addHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_FORM);
      StringEntity  entity = new StringEntity(query);
      httpPost.setEntity(entity);


      HttpHost httpHost = webClient.createHttpHost(url);
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
//    String home = "http://www.schoolinbeeld.nl/scholen.php?e=Basisonderwijs&p=Noord-Brabant&g=Dongen";
    String home = "http://www.schoolinbeeld.nl/onderwijsdetail.php?un=8945";
//    String home = "http://www.schoolinbeeld.nl/scholen.php?s=Basisonderwijs&pr=Noord-Brabant&ge=Dongen&en=Anspach%2C+J.J.&un=8939";
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    byte [] bytes = loadContent(home);
    writer.save(new File("D:\\java\\test\\html\\TestPostMethod3.html"), bytes);
    System.exit(0);
    
  }
  
}
