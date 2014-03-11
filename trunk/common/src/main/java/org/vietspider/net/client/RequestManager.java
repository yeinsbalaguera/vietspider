/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 21, 2009  
 */
public class RequestManager extends Thread {
  
  private final static RequestManager instance = new RequestManager();
  
  public final static RequestManager getInstance() { return instance; }
  
  private ConcurrentHashMap<HttpRequest, Long> httpRequests;
  
  public RequestManager() {
    httpRequests = new ConcurrentHashMap<HttpRequest, Long>();
    this.start();
  }
  
  public HttpGet createGet(WebClient webClient, String referer, String address) {
    HttpGet httpGet = new HttpGet(address);
    webClient.setHeaderValue(httpGet, referer);
    httpRequests.put(httpGet, System.currentTimeMillis());
    return httpGet;
  }
  
  public HttpPost createPost(WebClient webClient, String referer, String address) {
    HttpPost httpPost = new HttpPost(address);
    webClient.setHeaderValue(httpPost, referer);
    httpRequests.put(httpPost, System.currentTimeMillis());
    return httpPost;
  }
  
  public void run() {
    while(true) {
      closeExpires(httpRequests, 30*60*1000);
      try {
        Thread.sleep(1*60*1000);
      } catch (Exception e) {
      }
    }
  }
  
  public void close(HttpRequest request) {
    httpRequests.remove(request);
  }
  
  public void closeExpires(ConcurrentHashMap<HttpRequest, Long> requests, long timeout) {
    Iterator<HttpRequest> iterator = requests.keySet().iterator();
    while(iterator.hasNext()) {
      HttpRequest request = iterator.next();
      if(requests == null) break;
      Long time = requests.get(request);
      if(time == null) continue;
      
      if(request instanceof HttpGet) {
        HttpGet get = (HttpGet)request;
        if(get.isAborted()) {
          iterator.remove();
        } else  if(System.currentTimeMillis() - time >= timeout) {
          get.abort();
          iterator.remove();
        }
        continue;
      } 
      
      if(request instanceof HttpPost) {
        HttpPost post = (HttpPost)request;
        if(post.isAborted()) {
          iterator.remove();
        } else  if(System.currentTimeMillis() - time >= timeout) {
          post.abort();
          iterator.remove();
        }
        continue;
      }
    }
  }
  
}
