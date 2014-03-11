/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.browser.form.Param;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2006
 */
@NodeMap("link-explorer")
public class LinkExplorer  {
  
  @NodeMap("address")
  private String address;
  
  @NodeMap("url")
  private String url;
  
  @NodeMap("ref")
  private String ref;
  
  @NodeMap("base-url")
  private String baseHref;
  
  @NodeMap("referer")
  private String referer;
  
  @NodesMap(value = "params", item = "param")
  private List<Param> params  = new ArrayList<Param>();
  
  @NodeMap(value = "post-content")
  private String postContent; 
  
  @NodeMap(value = "level")
  private int level;
  
  @NodeMap(value = "is-rss")
  private boolean isRss = false;
  
  @NodeMap(value = "is-data")
  private boolean isData = false;
  
  @NodeMap(value = "is-link")
  private boolean isLink = false;
  
  private volatile Object document;
  private volatile List<NodeImpl> tokens;
  
  private int redirectCode;
  private boolean fromCached = false;
  private Calendar calendar;
  
  public LinkExplorer() {
  }
  
  public LinkExplorer(String address) {
    this.address = address;
  }
  
  public LinkExplorer(String address, int level){
    this.address = address;
    this.level = level;
    this.url = address;
  }
  
  public LinkExplorer(String address, String url, String ref, int level){
    this.address = address;
    this.level = level;
    this.url = url;
    this.ref = ref;
  }

  public int getLevel() { return level; }
  public void setLevel(int lvl) { this.level = lvl; }
  
  public String getUrl() { return url; }
  public void setUrl(String url) {
    this.url = url;
  }

  @SuppressWarnings("unchecked")
  public <T> T getDocument() throws Exception{ 
    if(document == null) createDocument();
    return (T)document; 
  }
  public void setDocument(Object document) { this.document = document; }
  
  public int getRedirectCode() { return redirectCode; }
  public void setRedirect(int redirect) { this.redirectCode = redirect; }

  public List<NodeImpl> getTokens() { return tokens;  }
  public void setTokens(List<NodeImpl> tokens) { this.tokens = tokens; }
  
  public String getBaseHref() { return baseHref; }
  public void setBaseHref(String baseHref) { this.baseHref = baseHref; }

  public String getRef() { return ref; }
  
  public Calendar getCalendar() {
    return calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public void setRedirectCode(int redirectCode) {
    this.redirectCode = redirectCode;
  }

  public String getReferer() { return referer; }
  public void setReferer(String referer) { this.referer = referer; }
  
  public List<Param> getParams() { return params; }
  public void setParams(List<Param> params) { this.params = params; }
  
  public List<NameValuePair> getRequestParams() {
    if(params == null || params.size() < 1) return null;
    List<NameValuePair> nvpList = new ArrayList <NameValuePair>();
    for(int i = 0; i < params.size(); i++) {
      Param param = params.get(i);
      nvpList.add(new BasicNameValuePair(param.getName(), param.getValue()));
    }
    return nvpList;
  }
  public String getPostContent() { return postContent; }
  public void setPostContent(String postContent) { this.postContent = postContent; }

  public String getAddress() { return address; }
  public void setAddress(String address) {
    this.address = address; 
  }
  
  @GetterMap("is-rss")
  public boolean isRss() { return isRss; }
  @SetterMap("is-rss")
  public void setRss(boolean isRss) { this.isRss = isRss; }

  @GetterMap("is-data")
  public boolean isData() { return isData; }
  @SetterMap("is-data")
  public void setIsData(boolean isData) { this.isData = isData; }
  
  @GetterMap("is-link")
  public boolean isLink() { return isLink; }
  @SetterMap("is-link")
  public void setIsLink(boolean isLink) { this.isLink = isLink; }
  
  public boolean isFromCached() { return fromCached; }
  public void setFromCached(boolean fromCached) { this.fromCached = fromCached;  }
  
  private void createDocument() throws Exception {
    if(tokens == null) return ;
    document = new HTMLParser2().createDocument(tokens);
  }
  
  public void clearContent() { 
    tokens = null; 
    document = null;
  }

  public Calendar getTime() {
    if(calendar == null) {
      calendar = Calendar.getInstance();
    }
    return calendar;
  }
  public void setTime(Calendar calendar) { this.calendar = calendar; }

}
