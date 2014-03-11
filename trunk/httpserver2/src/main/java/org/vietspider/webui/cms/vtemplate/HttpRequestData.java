/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.httpserver.GZipEntity;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 28, 2011  
 */
public class HttpRequestData {
  
  public static final short DOMAIN = 0;
  public static final short DETAIL = 1;
  public static final short EVENT = 2;
  public static final short THREAD = 3;

  private String uriFolder;
  private String agent;
  private String [] cookies;
  private String [] params;
  private ByteArrayOutputStream output;
  private String contentType = "text/html";
  private boolean isZipResponse = false;
  private String referer;
  private boolean isMobile = false; 
  private String uri;
  
  private int pageType = DOMAIN;
  
  private String currentDateLink;
  
  private Properties properties = new Properties();
  
  public HttpRequestData() {

  }

  public HttpRequestData(String[] params) {
    this.params = params;
  }

  public HttpRequestData(String agent) {
    setAgent(agent);
  }

  public HttpRequestData(String agent, String[] params) {
    this.params = params;
    setAgent(agent);
  }

  public HttpRequestData(String agent, String uriFolder, String[] params) {
    this.uriFolder = uriFolder;
    this.params = params;
    setAgent(agent);
  }

  public String getUri() { return uri; }
  public void setUri(String uri) { this.uri = uri; }

  public String getUriFolder() { return uriFolder; }
  public void setUriFolder(String uriFolder) { this.uriFolder = uriFolder; }

  public String getAgent() { return agent; }
  public void setAgent(String agent) { 
    this.agent = agent;
    String text = this.agent.toLowerCase();
//    System.out.println("=====  >"+ text);
    for(int i = 0; i < HttpUtils.MOBILE_USER_AGENTS.length; i++) {
      if(text.indexOf(HttpUtils.MOBILE_USER_AGENTS[i]) > -1) {
//        LogService.getInstance().setMessage(null, 
//            text +  " : " + HttpUtils.MOBILE_USER_AGENTS[i] + " is mobile!");
        isMobile = true;
        break;
      }
    }
//    System.out.println(" =====  > "+ isMobile);
  }
  
  public boolean isMobile() { return isMobile; }

  public String[] getCookies() { return cookies; }
  public void setCookies(String[] cookies) { this.cookies = cookies; }

  public String[] getParams() { return params; }
  public void setParams(String[] params) { this.params = params; }

  public ByteArrayOutputStream getOutput() {
    if(output == null) output = new ByteArrayOutputStream();
    return output; 
  }
  public void setOutput(ByteArrayOutputStream output) { this.output = output; }
  
  public void write(byte[] bytes) throws Exception {
    getOutput().write(bytes);
  }
  
  public void write(String text) {
    try {
      getOutput().write(text.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
  public void write(Exception exp) {
    try {
      getOutput().write(exp.getMessage().getBytes());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }
  
  public boolean isZipResponse() { return isZipResponse; }
  public void setZipResponse(boolean isZipResponse) { this.isZipResponse = isZipResponse; }
  
  public String getReferer() { return referer; }
  public void setReferer(String referer) { this.referer = referer; }
  
  public int getPageType() { return pageType; }
  public void setPageType(int pageType) { this.pageType = pageType; }
  
  public Properties getProperties() { return properties; }

  public HttpEntity createEntity() {
    byte[] bytes = getOutput().toByteArray();
    if(isZipResponse) {
      GZipEntity byteArrayEntity = new GZipEntity(HttpUtils.gzipCompress(bytes));
      byteArrayEntity.setContentType(contentType+"; charset="+Application.CHARSET);
      return byteArrayEntity;
    } 
    ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
    byteArrayEntity.setContentType(contentType+"; charset="+Application.CHARSET);
    return byteArrayEntity;
  }
  
  public String getCurrentDateLink() {
    if(currentDateLink != null) return currentDateLink;
    StringBuilder hrefCate = new StringBuilder(uriFolder);
    hrefCate.append('/').append("domain").append("/1/");
    Calendar calendar = Calendar.getInstance();
    hrefCate.append(CalendarUtils.getParamFormat().format(calendar.getTime())).append('/');
    currentDateLink = hrefCate.toString();
    return currentDateLink;
  }
  
}
