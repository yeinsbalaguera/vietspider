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
public interface ILink {
  
  public int getLevel() ;
  public void setLevel(int lvl) ;
  
  public String getUrl();

  public <T> T getDocument();
  public void setDocument(Object document) ;
  
  public int getRedirectCode() ;
  public void setRedirect(int redirect) ;

  public List<NodeImpl> getTokens() ;
  public void setTokens(List<NodeImpl> tokens) ;
  
  public String getSourceFullName() ;
  public void setSourceFullName(String source) ;
  
  public String getBaseHref() ;
  public void setBaseHref(String baseHref) ;


  public String getRef() ;
  
  public String getReferer();
  public void setReferer(String referer) ;
  
  public List<Param> getParams() ;
  public void setParams(List<Param> params) ;
  public List<NameValuePair> getRequestParams() ;
  
  public String getPostContent() ;
  public void setPostContent(String postContent);

  public String getAddress() ;
  public void setAddress(String address) ;
  
  public boolean isRss() ;
  public void setRss(boolean isRss) ;

  public boolean isData() ;
  public void setIsData(boolean isData) ;
  
  public boolean isLink() ;
  public void setIsLink(boolean isLink) ;
  
  public boolean isFromCached() ;
  public void setFromCached(boolean fromCached) ;
  
  public String getSessionParam() ;

  
  
}
