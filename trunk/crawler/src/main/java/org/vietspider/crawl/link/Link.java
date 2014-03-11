/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.browser.form.Param;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.URLCodeGenerator;
import org.vietspider.link.V_URL;
import org.vietspider.model.Source;
import org.vietspider.pool.Status;
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
@NodeMap("link")
@SuppressWarnings("serial")
public class Link implements Status<String>, Serializable, ILink {
  
  @NodeMap("address")
  private String address;
  @NodeMap("url")
  private String url;
  @NodeMap("normalize-url")
  private String normalizeURL;
  @NodeMap("ref")
  private String ref;
  @NodeMap("baseHref")
  private String baseHref;
  @NodeMap("referer")
  private String referer;
  @NodeMap("sessionParam")
  private String sessionParam;
//  @NodeMap("text-referer")
//  private String textReferer;
  @NodesMap(value = "params", item = "param")
  private List<Param> params  = new ArrayList<Param>();
  
  @NodeMap(value = "post-content")
  private String postContent; 
  
  @NodeMap("level")
  private int level;
  @NodeMap("code")
  private int code = -1;
//  private int titleCode = -1;
//  @NodeMap("root-code")
//  private int rootCode = -1;
  
  private transient MD5Hash urlId; 
  private transient MD5Hash titleId; 
  
  private transient long size = -1; 
  
  private transient long totalOfBytes = 0; 

  @NodeMap("isRss")
  private boolean isRss = false;
  
  @NodeMap("isData")
  private boolean isData = false;
  @NodeMap("isLink")
  private boolean isLink = false;
  
  private volatile Object document;
  private volatile List<NodeImpl> tokens;
  
  @NodeMap("source-full-name")
  private String sourceFullName;
  private int redirectCode;
  private boolean fromCached = false;
//  private Calendar calendar;
  
  private URL baseUrl = null;
  
  public Link() {
  }
  
  public Link(String address, String sessionParam) {
    this.address = address;
    this.sessionParam = sessionParam;
  }
  
  public Link(String address, int level, String sessionParam, String sourceFullName/*, int rootCode*/){
    this.address = address;
    this.level = level;
    this.sourceFullName = sourceFullName;
    this.url = address;
//    this.rootCode = rootCode;
    this.sessionParam = sessionParam;
  }
  
  public Link(String address, String url, 
      String ref, int level, String sessionParam, String sourceFullName/*, int rootCode*/){
    this.address = address;
    this.level = level;
    this.sourceFullName = sourceFullName;
    this.url = url;
    this.ref = ref;
//    this.rootCode = rootCode;
    this.sessionParam = sessionParam;
  }

  public int getLevel() { return level; }
  public void setLevel(int lvl) { this.level = lvl; }
  
  public String getUrl() { return url; }

  @SuppressWarnings("unchecked")
  public <T> T getDocument() { 
    if(document == null) createDocument();
    return (T)document; 
  }
  public void setDocument(Object document) { this.document = document; }
  
  public int getRedirectCode() { return redirectCode; }
  public void setRedirect(int redirect) { this.redirectCode = redirect; }

  public List<NodeImpl> getTokens() { return tokens;  }
  public void setTokens(List<NodeImpl> tokens) { this.tokens = tokens; }
  
  public String getSourceFullName() { return sourceFullName; }
  public void setSourceFullName(String source) { this.sourceFullName = source; }
  
  public String getBaseHref() { return baseHref; }
  public void setBaseHref(String baseHref) { this.baseHref = baseHref; }

//  public int getTitleCode() { return titleCode; }
//  public void setTitleCode(int codeTitle) { this.titleCode = codeTitle; }

  public String getRef() { return ref; }
  
  public String getReferer() { return referer; }
  public void setReferer(String referer) { this.referer = referer; }
  
//  public String getTextReferer() { return textReferer; }
//  public void setTextReferer(String textReferer) { this.textReferer = textReferer; }
  
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
    this.urlId = null;
  }
  
  public int getCode() {  
    if(code != -1) return code;
    try {
      URLCodeGenerator codeGenerator = new URLCodeGenerator();
      code = codeGenerator.hashCode(new URL(address), sessionParam);
    } catch (Exception e) {
    }
    return code; 
  }
  
//  public void setAddressCode(int addressCode) { this.addressCode = addressCode; }
  
  @GetterMap("isRss")
  public boolean isRss() { return isRss; }
  @SetterMap("isRss")
  public void setRss(boolean isRss) { this.isRss = isRss; }
//  public void setRss(Boolean isRss) { this.isRss = isRss; }

  public boolean isData() { return isData; }
  public void setIsData(boolean isData) { this.isData = isData; }
  
  public boolean isLink() { return isLink; }
  public void setIsLink(boolean isLink) { this.isLink = isLink; }
  
  public boolean isFromCached() { return fromCached; }
  public void setFromCached(boolean fromCached) { this.fromCached = fromCached;  }
  
  public String getSessionParam() { return sessionParam; }
  
  public String buildStatus(int id) {
    StringBuilder builder = new StringBuilder("Executor ");
    builder.append(id);
    builder.append(' ').append('[');
    builder.append(sourceFullName);
    builder.append(']').append(' ');
    builder.append('-').append(' ').append(address);
    
    return builder.toString();
  }
  
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(address).append("]\n[Link is: ");
    builder.append(isRss ?  " rss link, " : " html link, ");
    builder.append(isData ?  " data link, " : " not data link, ");
    builder.append(isLink ?  " is visit link" : " not visit link");
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    builder.append(", level " + level + " in "+ source.getDepth());
    return builder.toString();
  }

  private void createDocument() {
    if(tokens == null) return ;
    try {
      document = new HTMLParser2().createDocument(tokens);
    } catch (Exception e) {
      Source source = CrawlingSources.getInstance().getSource(sourceFullName);
      LogService.getInstance().setThrowable(source, e, address);
    }    
  }
  
  public void clearContent() {
    tokens = null;
    document = null;
  }

  public String getNormalizeURL() {
    if(normalizeURL != null) return normalizeURL;
    StringBuilder builder = new StringBuilder(address);
    if(params != null) {
      for(int i = 0; i < params.size(); i++) {
        builder.append('&').append(params.get(i).toString());
      }
    }
    normalizeURL = new V_URL(builder.toString(), sessionParam).toNormalize();
    return normalizeURL; 
  }
  
  public String buildURL() {
    StringBuilder builder = new StringBuilder(address);
    if(params != null) {
      for(int i = 0; i < params.size(); i++) {
        builder.append('&').append(params.get(i).toString());
      }
    }
    if(builder.length() >= 2000) {
      builder.delete(2000, builder.length());
    }
    return builder.toString();
  }
  
  public MD5Hash getUrlId() {
    if(urlId != null) return urlId;
    urlId = MD5Hash.digest(getNormalizeURL().toLowerCase());
    return urlId;
  }

  public MD5Hash getTitleId() { return titleId; }
  public void setTitleId(MD5Hash titleId) { this.titleId = titleId; }

//  public int getRootCode() { return rootCode; }
//  public void setRootCode(int rootCode) { this.rootCode = rootCode; }
  
  public long getSize() { return size; }
  public void setSize(long size) { this.size = size; }

  public long getTotalOfBytes() { return totalOfBytes; }
  public void setTotalOfBytes(long totalOfBytes) { this.totalOfBytes = totalOfBytes; }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setNormalizeURL(String normalizeURL) {
    this.normalizeURL = normalizeURL;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public void setSessionParam(String sessionParam) {
    this.sessionParam = sessionParam;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setUrlId(MD5Hash urlId) {
    this.urlId = urlId;
  }

  public void setData(boolean isData) {
    this.isData = isData;
  }

  public void setLink(boolean isLink) {
    this.isLink = isLink;
  }

  public void setRedirectCode(int redirectCode) {
    this.redirectCode = redirectCode;
  }
  
  public URL getBaseURL() {
    if(baseUrl == null) {
      baseUrl = HTMLLinkExtractorUtils.getBaseURL(this);
    }
    return baseUrl;
  }
}
