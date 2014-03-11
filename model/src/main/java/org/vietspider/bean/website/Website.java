/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean.website;

import java.net.URL;
import java.util.Calendar;

import org.vietspider.common.io.DataOfDay;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.locale.vn.DomainChecker;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 4, 2008  
 */
@NodeMap("website")
public class Website {
  
  public final static int NEW_ADDRESS = 0;
  public final static int UNAVAILABLE = -1;
  public final static int CONFIGURATION = 1;
  
  @NodeMap("address")
  private String address;
  @NodeMap("host")
  private String host;
  @NodeMap("desc")
  private String desc = "";
  @NodeMap("language")
  private String language = "*";
  @NodeMap("date")
  private String date;
  @NodeMap("status")
  private int status = NEW_ADDRESS;
  @NodeMap("ip")
  private String ip = "*";
  @NodeMap("config")
  private String config = "";
  @NodeMap("source")
  private String source = "";
  @NodeMap("charset")
  private String charset = "";
  @NodesMap(value = "homepages", item="url")
  private String [] homepages;
  @NodeMap("timedownload")
  private int timeDownload = 0;
  @NodeMap("path")
  private String path;
  @NodeMap("hash")
  private String hash;

  @NodeMap(value = "html", cdata = true)
  private String html = "";
  
  private double scoreHost = -1;
  
  private transient boolean isNew = false;
  
  public Website() {
    this.date = DataOfDay.getDateValue(); 
  }
  
  public Website(String n){
    host = n;
  }
  
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  
  public String getHash() { return hash;  }
  public void setHash(String hash) { this.hash = hash; }
  
  public int getStatus() {
    if(source != null && source.trim().length() > 0) {
      this.status = CONFIGURATION;
    }
    return status; 
  }
  public void setStatus(int status) { this.status = status; }
  
  public int getTimeDownload() { return timeDownload; }
  public void setTimeDownload(int timeDownload) { this.timeDownload = timeDownload; }
  
  public String getCharset() {
    if(charset == null) charset = "";
    return charset; 
  }
  public void setCharset(String charset) { this.charset = charset; }

  public String getDesc() { return desc; }
  public void setDesc(String desc) { this.desc = desc; }

  public String getDate() {
    if(date == null || date.trim().isEmpty()) {
      Calendar calendar = Calendar.getInstance();
      date = CalendarUtils.getDateTimeFormat().format(calendar.getTime());
    }
    return date; 
  }
  public void setDate(String date) { this.date = date; }
  
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }

  public String getHost() {
    if(host == null 
        || host.trim().length() < 1) {
      host = toHost(address);
    }
    return host; 
  }
  
  public double getScoreHost() {
    if(scoreHost > -1) return scoreHost;
    DomainChecker checker = DomainChecker.getDomainChecker();
    scoreHost = checker.calculate(getHost());
    return scoreHost;
  }
  
  public void setHost(String host) { this.host = host; }

  public String getLanguage() { return language; }
  public void setLanguage(String language) {
    if(language == null || language.trim().isEmpty()) {
      this.language = "*";
    } else {
      this.language = language;
    }
    
  }

  public String getIp() { return ip; }
  public void setIp(String ip) {this.ip = ip; }

  public boolean isNew() { return isNew; }
  public void setNew(boolean isNew) { this.isNew = isNew; }
  
  public String getConfig() { return config; }
  public void setConfig(String config) { this.config = config; }
  
  public String[] getHomepages() { 
    return homepages != null ? homepages : new String[0]; 
  }
  public void setHomepages(String[] homepages) { this.homepages = homepages; }
  
  public String getHtml() { return html; }
  public void setHtml(String html) { this.html = html; }
  
  public String getPath() { return path; }
  public void setPath(String path) { this.path = path; }
  
  public String toString(){
    return host +"("+ip+")|"+ status + "|"+language;
  }

  public boolean equals(Website w2){
    if(w2 == null )return false;
    if(!compareString(address, w2.getAddress())) return false;
    if(!compareString(ip, w2.getIp())) return false;
    if(!compareString(host, w2.getHost())) return false;
    if(!compareString(desc, w2.getDesc())) return false;
    if(!compareString(language, w2.getLanguage())) return false;
    if(status != w2.getStatus()) return false;
    return true;
  }
  
  private boolean compareString(String s1, String s2){
    if(s1 == null && s2 == null )return true;
    if(s1 == null  )return false;
    return s1.equals(s2);
  }
  
  public void update(Website website) {
    if(website.getDesc() != null 
        && website.getDesc().trim().length() > 0) {
      this.desc = website.getDesc();
    }
    
    if(website.getConfig() != null 
        && website.getConfig().trim().length() > 0) {
      this.config = website.getConfig();
    }
    
    if(website.getSource() != null 
        && website.getSource().trim().length() > 0) {
      this.source = website.getSource();
    }
    
    this.status = website.getStatus();
    this.language = website.getLanguage();
    this.ip = website.getIp();
  }
  
  public final static String toHost(String _address) {
    try {
      String _host = new URL(_address).getHost();
      if(_host == null || _host.isEmpty()) return null;
      if(_host.startsWith("www")) {
        int idx = _host.indexOf('.') + 1;
        if(idx > 0) _host = _host.substring(idx);
      }
      return _host.toLowerCase();
    } catch (Exception e) {
      return null;
    }
  }
  

}
