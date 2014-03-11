/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHost;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 24, 2008  
 */
public class ProxiesMonitor extends Thread {
  
  private final static ProxiesMonitor INSTANCE = new ProxiesMonitor();
  
  public final static ProxiesMonitor getInstance() { return INSTANCE; }
  
  private final ConcurrentHashMap<String, Proxies> maps = new ConcurrentHashMap<String, Proxies>();
  
  private ProxiesMonitor() {
    start();
  }
  
  public void run() {
    while(true) {
      Enumeration<String> enumeration = maps.keys();
      while(enumeration.hasMoreElements()) {
        String key  = enumeration.nextElement();
        Proxies proxies = maps.get(key);
        if(proxies.isTimeout()) maps.remove(key);
      }
      try {
        Thread.sleep(10*60*1000);
      } catch (Exception e) {
      }
    }
  }
  
  public Proxies getProxies(String key) {
    if(key == null) return null;
    return maps == null ? null : maps.get(key); 
  }
  
  public HttpHost nextProxy(String key) {
    Proxies proxies = maps.get(key);
    return proxies == null ? null : proxies.next();
  }
  
  public void put(String key, String blind) {
    Proxies proxies = new Proxies();
    setErrors(proxies, blind);
    maps.put(key, proxies);
  }
  
  public static final HttpHost createProxy(String proxy) {
    if(proxy == null 
        || (proxy = proxy.trim()).isEmpty()) return null;
    
    String [] elements = proxy.split(":");
    if(elements.length < 2)  return null;
    
    try {
      int port = Integer.parseInt(elements[1].trim());
      return new HttpHost(elements[0].trim(), port, "http");
    } catch (Exception e) {
      return null;
    }
  }
  
 /* public static final Proxies createProxies(int totalThread, String value) {
    if(value == null 
        || (value = value.trim()).isEmpty()) return null; 
    
    String [] elements = value.split("\n");
    HttpHost [] httpProxies = new HttpHost[elements.length];
    
    Proxies proxies = new Proxies();
    Pattern pattern = Pattern.compile("\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[:]\\p{Digit}+");
    
    for(int i = 0; i < elements.length; i++) {
      if(elements[i] == null 
          || (elements[i] = elements[i].trim()).isEmpty()) continue;
      
      if(elements[i].startsWith("#")) {
        setError(proxies, elements[i]);
        continue;
      }
      
      Matcher matcher = pattern.matcher(elements[i]);
      if(!matcher.find()) continue;

      String [] values = elements[i].split(":");
      if(values.length < 2) continue;

      try {
        int port = Integer.parseInt(values[1].trim());
        httpProxies[i] = new HttpHost(values[0].trim(), port, "http");
      } catch (Exception e) {
      }
    }
    proxies.setProxies(totalThread,httpProxies);
    return proxies;
  }
  */
  
  public static void setErrors(Proxies proxies, String value) {
    String [] elements = value.split("\n");
    for(int i = 0; i < elements.length; i++) {
      setError(proxies, elements[i]);
    }
  }
  
  public static void setError(Proxies proxies, String text) {
    text = text.trim();
    int idx = text.toLowerCase().indexOf("error.code");
    if(idx > -1) {
      try {
        text = text.substring(idx+10).trim();
        proxies.setErrorCode(Integer.parseInt(text));
      } catch (Exception e) {
      }
      return;
    }
    
    idx = text.toLowerCase().indexOf("error.size");
    if(idx > -1) {
      try {
        text = text.substring(idx+10).trim();
        proxies.setErrorSize(Integer.parseInt(text));
      } catch (Exception e) {
      }
      return;
    }
    
    idx = text.toLowerCase().indexOf("error.message");
    if(idx > -1) {
      proxies.setErrorMessage(text.substring(idx+13).trim());
      return;
    }
    
    idx = text.toLowerCase().indexOf("total.executor");
    if(idx > -1) {
      try {
        text = text.substring(idx+14).trim();
        proxies.setTotalExcutor(Integer.parseInt(text));
      } catch (Exception e) {
      }
      return;
    }
  }
  
}
