/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.queue;

import java.util.List;

import org.vietspider.crawl.link.Link;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 27, 2008  
 */
public class UrlList {
  
  private volatile String host;
  private volatile Link referer;

  private volatile List<Link> list;
//  private volatile boolean isWebsite = false;

  public UrlList(String host, Link referer) {
    this.host = host;
    this.referer = referer;
  }

  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }

  public Link getReferer() { return referer; }
  public void setReferer(Link referer) { this.referer = referer; }

  public List<Link> getList() { return list; }
  public void setList(List<Link> list) { this.list = list; }

//  public boolean isWebsite() { return isWebsite; }
//  public void setWebsite(boolean isWebsite) { this.isWebsite = isWebsite; }
}
