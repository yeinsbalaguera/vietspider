/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.vietspider.client.common.CrawlerClientHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 16, 2009  
 */
public class ClearPoolQueue extends BackgroupLoader {

  public ClearPoolQueue(Crawler crawler) {
    super(crawler, crawler);
  }

  public void finish() {
  }

  public void load() throws Exception {
    new CrawlerClientHandler().clearPoolQueue();
  }
}
