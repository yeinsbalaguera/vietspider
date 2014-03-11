/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 17, 2008  
 */
public class Proxies extends RandomAccess  {

  public final static int DEFAULT = 0;
  public final static int ERROR = 1;
  public final static int SIZE = 2;
  public final static int MESSAGE = 3;

  //  private volatile HttpHost [] proxies;
  private volatile long lastAceccess = 0;

  private volatile long position = 0;
  private volatile int index = 0;
  private volatile long fsize = 0;

  private final static long TIME_OUT = 15*60*1000;

  private volatile int errorCode = -1;
  private volatile int errorSize = -1;
  private volatile String errorMessage = null;
  
  private volatile int totalExcutor = 1; 
  
  private List<String> badProxies = new ArrayList<String>(); 
  
//  private volatile long currentStart = -1;
  
  public Proxies() {
    loadBadProxies();
  }

  //  public int size() {
  //    return proxies == null ? -1 : proxies.length;
  //  }
  
  public HttpHost next() {
    return next(0);
  }

  private HttpHost next(int time) {
    if(time  >= 5) return null;
//    currentStart = -1;
    lastAceccess = System.currentTimeMillis();

    if(index%2 == 1) {
      index++;
      return null;
    }

    index++;

    File file = UtilFile.getFile("system/proxy/", "proxies.txt");
    
    Preferences prefs = Preferences.userNodeForPackage(Proxies.class);
    try {
     String size = prefs.get("proxies.size", "");
      if(size == null || size.trim().isEmpty()) {
        size = String.valueOf(file.length());
      }

      fsize = Long.parseLong(size.trim());
    } catch (Exception e) {
    }
   
    try {
      String text = prefs.get("proxies.position", "");
      if(text != null) position = Long.parseLong(text);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if(file.length() != fsize) {
      index = 0;
      position = 0;
      fsize = file.length();
    }
    
    prefs.put("proxies.size", String.valueOf(fsize));
    prefs.put("proxies.position", String.valueOf(position));

    if(position >= file.length()-1) {
      index = 0;
      position = 0;
    }

    HttpHost proxy = null;

    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      random.seek(position);

      String line = readLine(random);
      position = random.getFilePointer();
      prefs.put("proxies.position", String.valueOf(position));
      
      if(badProxies.contains(line)) return next(time + 1);
      
//      System.out.println(" next proxy "+ line);
      
      if(line == null || line.isEmpty()) return null;

      Pattern pattern = Pattern.compile("\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[:]\\p{Digit}+");

      Matcher matcher = pattern.matcher(line);
      if(!matcher.find()) return null;

      String [] values = line.split(":");
      if(values.length < 2) return null;

      int port = Integer.parseInt(values[1].trim());
      proxy = new HttpHost(values[0].trim(), port, "http");

    } catch (IOException e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(e2);
      }
    }

    /*if(proxies == null || proxies.length < 1) return null;
    if(index >= proxies.length) {
      index = 0;
      return null;
    }
    if(proxies.length == 1) return proxies[0];
    HttpHost proxy = proxies[index];    
//    System.out.println(" ta thay co "+ proxy+ " : + index);
    index++;*/
    
//    currentStart = System.currentTimeMillis();
    return proxy;
  }

  public int getIndex() { return index; }

  public boolean isTimeout() {
    return System.currentTimeMillis() - lastAceccess > TIME_OUT;
  }
  
  public int getErrorCode() { return errorCode; }
  public void setErrorCode(int errorCode) { this.errorCode = errorCode;  }

  public int getErrorSize() { return errorSize; }
  public void setErrorSize(int errorSize) { this.errorSize = errorSize; }
  
  public String getErrorMessage() { return errorMessage; }
  public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
  
  public int getTotalExcutor() { return totalExcutor; }
  public void setTotalExcutor(int totalExcutor) { this.totalExcutor = totalExcutor; }

  public boolean isType(int type) {
    if(errorMessage != null 
        && !errorMessage.trim().isEmpty()) {
      if(type == MESSAGE) return true;
      return false;
    }
    
    if(errorSize > 0) {
      if(type == SIZE) return true;
      return false;
    }
    
    if(errorCode > -1) {
      if(type == ERROR) return true;
      return false;
    }
    
    if(type == DEFAULT) return true;
    return false; 
  }

  public void addBadProxy(String badProxy) {
//    System.out.println(" da thay 1 bad proxy "+ badProxy);
    this.badProxies.add(badProxy);
    this.saveBadProxies();
  }
  
  private void saveBadProxies() {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < badProxies.size(); i++) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(badProxies.get(i));
    }
    
    String text = builder.toString();
    if(text.isEmpty()) return;
    
    File file  = UtilFile.getFile("system/proxy/", "bad.proxies.txt");
    try {
      RWData.getInstance().save(file, text.getBytes("utf-8"));
    } catch (Exception e) {
    }
  }
  
  private void loadBadProxies() {
    File file  = UtilFile.getFile("system/proxy/", "bad.proxies.txt");
    String [] elements = null;
    try {
      String text = new String(RWData.getInstance().load(file), "utf-8");
      elements = text.split("\n");
    } catch (Exception e) {
    }
    if(elements == null || elements.length < 1) return;
    for(int i = 0; i < elements.length; i++) {
      if((elements[i] = elements[i].trim()).isEmpty()) continue;
      badProxies.add(elements[i]);
    }
    if(badProxies.size() > 100) badProxies.clear();
  }
  
  
  
//  public boolean isProxyTimeout() {
//    if(currentStart < 1) return false;
//    return System.currentTimeMillis() - currentStart  > 5*60*1000;
//  }

}
