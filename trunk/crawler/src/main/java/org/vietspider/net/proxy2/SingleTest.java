/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.net.client.HttpClientFactory;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2011  
 */
class SingleTest implements Runnable {
  
  protected final static long TOTAL_TIMEOUT = 10*1000l;
  
  protected WebClient webClient;
  protected DefaultHttpClient httpClient;
//  protected MethodHandler methodHandler;
  protected HttpHost httpHost;
  protected HttpResponseReader reader;
  protected String website = "http://vietspider.org/";
  
  
  private long start = -1;
  private Thread thread;
  private String proxy ;
  private String proxyHost;
  private int proxyPort;
  private boolean successfull = false;
  private long time;
  
  SingleTest() {
    httpClient = HttpClientFactory.createDefaultHttpClient(1000, 2*1000);
    webClient = new WebClient(httpClient);
    reader = new HttpResponseReader();
    webClient.setUserAgent("Mozilla/5.0 (Windows NT 5.1; rv:22.0) Gecko/20100101 Firefox/22.0");
    httpHost = webClient.createHttpHost(website);
    
    try {
      webClient.setURL(null, new URL(website), httpClient);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  void startProxy(String _proxy) {
    if(_proxy == null || _proxy.trim().isEmpty()) return; 
    this.proxy = _proxy;
   
//    System.out.println(" check === >" + proxy);
    int index = proxy.indexOf(':');
    if(index < 0) return;
//    String [] elements = proxy.split(":");
    proxyHost = proxy.substring(0, index).trim();
    
    proxyPort = Integer.parseInt(proxy.substring(index+1).trim()); 
    webClient.registryProxy(proxyHost, proxyPort, null, null);
    
    if(thread != null) {
      try {
        thread.interrupt();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    successfull = false;
    
    thread = new Thread(this);
    thread.start();
  }
  
  public void run() {
    time = -1;
    start = System.currentTimeMillis();
//    System.out.println(hashCode() + " test " + proxy );
//    long s = System.currentTimeMillis();
    HttpResponse httpResponse = null;
    try {
      HttpRequest httpRequest = webClient.createGetMethod(website, "");
      httpResponse = httpClient.execute(httpHost, httpRequest);
    } catch (Throwable e) {
//      long e1 = System.currentTimeMillis();
//      System.out.println(hashCode() + " : " + proxy  + " : "  + (e1 - s)  );
//      System.out.println(hashCode() + " : " + proxy + " step 2: " + e.toString());
      return ;
    }
    
    start = System.currentTimeMillis();
    byte[] bytes =  null;
    try {
      bytes = reader.readBody(httpResponse);
    } catch (Exception e) {
//      System.out.println(hashCode() + " : " + proxy + " step 3: read data fail!" + e.toString());
      return ;
    } finally {
      reader.abort();
    }
//    System.out.println(" ================== > "+ proxy + " : "+ bytes);
    if(bytes == null) {
//      System.out.println(hashCode() + " : " + proxy + " step 4: no data!");
      return ;
    }
    
    time  = System.currentTimeMillis() - start;
//    System.out.println(" ================== > "+ proxy + " : "+ bytes.length+ " : "+ time);
//    if(start < 0 || proxy == null) return;
    
//    System.out.println(new String(bytes));
    
    if(bytes.length <= 512 || time > 6000) {
//      System.out.println(hashCode() + " : " + proxy + " step 5: invalid time: " + time + " , bytes "+bytes.length);
      return ;
    }
    
    try {
      String text = new String(bytes, Application.CHARSET);
      successfull = text.indexOf("webextractor") > -1;
//      if(!successfull) System.out.println(hashCode() + " : " + proxy + " step 6: text fail!");
    } catch (Exception e) {
//      System.out.println(hashCode() + " : " + proxy + " step 7: character error "+ e.toString());
    }
  }
  
  boolean isTimeout() {
//    if(System.currentTimeMillis() - start >= TOTAL_TIMEOUT) {
//      System.out.println(hashCode() + " : " + proxy  + " :  "  
//          + (System.currentTimeMillis() - start) + " : "
//          + (2.60*1000l) + " : "
//          + (System.currentTimeMillis() - start >= TOTAL_TIMEOUT)
//          + " timeout!" );
//    }
    return System.currentTimeMillis() - start >= TOTAL_TIMEOUT;
  }
  
  long getTime() { return time; }
  
  void abort() { 
    reader.abort();
    thread.interrupt();
  }
  
  boolean isLive() { return thread != null && thread.isAlive();}
  
  boolean isSuccess() { return successfull; }
  
  String getProxy() { return proxy; }
  
  public static void main(String[] args) {
    SingleTest test = new SingleTest();
    test.startProxy("180.95.129.232:80");
  }
}
