/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.util.List;

import org.apache.http.NameValuePair;
import org.vietspider.browser.form.Param;
import org.vietspider.html.parser.NodeImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 29, 2009  
 */
public class MockLink implements ILink {

  private String address;
  
  private String referer;
  
  public MockLink(String address, String referer) {
    this.address = address;
    this.referer = referer;
  }
  
  public String getAddress() {
    return address;
  }

  public String getBaseHref() {
    return null;
  }

  public <T> T getDocument() {
    return null;
  }

  public int getLevel() {
    return 0;
  }

  public List<Param> getParams() {
    return null;
  }

  public String getPostContent() {
    return null;
  }

  public int getRedirectCode() {
    return 0;
  }

  public String getRef() {
    return null;
  }

  public String getReferer() {
    return referer;
  }

  public List<NameValuePair> getRequestParams() {
    return null;
  }

  public String getSessionParam() {
    return null;
  }

  public String getSourceFullName() {
    return null;
  }

  public List<NodeImpl> getTokens() {
    return null;
  }

  public String getUrl() {
    return address;
  }

  public boolean isData() { return true; }

  public boolean isFromCached() { return false;  }

  public boolean isLink() { return false;  }

  public boolean isRss() { return false; }

  public void setAddress(String address) { this.address = address; }

  @SuppressWarnings("unused")
  public void setBaseHref(String baseHref) { }
  @SuppressWarnings("unused")
  public void setDocument(Object document) { }
  @SuppressWarnings("unused")
  public void setFromCached(boolean fromCached) {}

  @SuppressWarnings("unused")
  public void setIsData(boolean isData) {
  }

  @SuppressWarnings("unused")
  public void setIsLink(boolean isLink) {
  }

  @SuppressWarnings("unused")
  public void setLevel(int lvl) {
    
  }

  @SuppressWarnings("unused")
  public void setParams(List<Param> params) {
    // TODO Auto-generated method stub
    
  }

  @SuppressWarnings("unused")
  public void setPostContent(String postContent) {
  }

  @SuppressWarnings("unused")
  public void setRedirect(int redirect) {
    
  }

  public void setReferer(String referer) {
    this.referer = referer;
  }

  @SuppressWarnings("unused")
  public void setRss(boolean isRss) {
  }

  @SuppressWarnings("unused")
  public void setSourceFullName(String source) {
    
  }

  @SuppressWarnings("unused")
  public void setTokens(List<NodeImpl> tokens) {
  }

  
}
