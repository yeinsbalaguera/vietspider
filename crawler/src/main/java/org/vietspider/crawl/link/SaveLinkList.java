/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import org.vietspider.crawl.link.queue.LinkQueue;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 25, 2008  
 */
public class SaveLinkList {

  private Source source;
  private LinkQueue links;
  
  public SaveLinkList(Source source, LinkQueue links) {
    this.source = source;
    this.links = links;
  }

  public Source getSource() { return source; }

  public LinkQueue getLinkQueue() { return links; }
  
}
