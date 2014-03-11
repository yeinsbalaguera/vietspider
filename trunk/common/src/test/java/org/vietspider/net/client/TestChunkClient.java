/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2008  
 */
public class TestChunkClient {
  
  public static void main(String[] args) throws Exception {
    HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, "UTF-8");
    HttpProtocolParams.setUserAgent(params, "Apache-HttpClient/4.0");
    HttpConnectionParams.setStaleCheckingEnabled(params, true);
   
    DefaultHttpClient  httpclient = new DefaultHttpClient(params);

    String url = "http://blog.360.yahoo.com/blog-o_2VJewgbqfL2HJEtXaTzW5_k8508jdY?tag=vietdart";
//    String url = "http://www.reuters.com/article/bankingfinancial-SP/idUSN2432828320070824";
    HttpGet httpget = new HttpGet(url);

    long start = System.currentTimeMillis();
    HttpResponse response = httpclient.execute(httpget);
    HttpEntity entity = response.getEntity();

    if (entity != null) {
        entity.consumeContent();
    }
    System.out.println(entity.getContentLength());
    long finish = System.currentTimeMillis();
   
    System.out.println(finish - start + " ms");   
  }
  
  
}
