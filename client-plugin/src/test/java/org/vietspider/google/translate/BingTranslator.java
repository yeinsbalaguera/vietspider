/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.google.translate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.BasicManagedEntity;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2011  
 */
public class BingTranslator {
  
  public static final long BIG_SIZE = 1024*1024l + 512*1024l; 
  
  public final static String CONTENT_LENGTH = "Content-Length";
  
  private static byte[] readBody(InputStream inputStream) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      int read = 0;
      byte [] bytes = new byte[8*1024];
//      managers.put(input, 0);
      
      while ((read = inputStream.read(bytes)) > -1) {
        output.write(bytes, 0, read);
      }
//      System.out.println(" mat het 2 "+ (System.currentTimeMillis() - start));
      
      if(output.size() >= BIG_SIZE) output.reset();

      inputStream.close();
    } catch (IllegalStateException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw e;
    } finally {
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
    }

    byte [] bytes  = output.toByteArray();
    output.flush();
    output.close();
    if(bytes.length > BIG_SIZE) return null;
    return bytes;
  }
  
  static byte[] readBody(HttpResponse httpResponse) throws Exception  {
    if(httpResponse == null) return null;
    HttpEntity httpEntity = httpResponse.getEntity();
    if(httpEntity == null) return null;
    
    
    Header zipHeader = httpResponse.getFirstHeader("zip");
    boolean zip = zipHeader != null && "true".equals(zipHeader.getValue());
    
    try {
      byte [] bytes = readBody(httpEntity.getContent());
      if(zip) bytes = new GZipIO().unzip(bytes);
      if(bytes == null || bytes.length < 1) return bytes;
      return bytes;
    } finally {
      if(httpEntity instanceof BasicManagedEntity) {
        BasicManagedEntity entity = (BasicManagedEntity) httpEntity;
//        System.out.println(" hihi  da thay roi 2 " + entity);
        entity.releaseConnection();
      } else {
        if(httpEntity.getContent() != null) httpEntity.getContent().close();
      }
    }
  }
  
  public static void main(String[] args)  throws Exception {
    
    String text = "Hơn 1 năm nay chị dầm mưa dãi nắng chạy xe ôm kiếm tiền thang thuốc cho chồng và con thơ.";
    String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?appId="

    + "A06CAD095F5CD226B6437F62669DBC9F42966F99" + "&text=" + URLEncoder.encode(text,"utf8") + /*"&from=vi" +*/ "&to=en";
    
    WebClient webClient = new WebClient();
    webClient.setURL(null, new URL("http://api.microsofttranslator.com/")); 
    
    HttpGet httpGet = webClient.createGetMethod(uri, null);
    

    HttpHost httpHost = webClient.createHttpHost("http://api.microsofttranslator.com/");
    HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
    
    System.out.println(httpResponse.getStatusLine().getStatusCode());
    

//    HttpEntity httpEntity = httpResponse.getEntity();
//    return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
    byte[] bytes = readBody(httpResponse);
    System.out.println(new String(bytes));
  }
}
