/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import org.vietspider.crawl.link.ILink;
import org.vietspider.html.HTMLDocument;
import org.vietspider.net.client.WebClient;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 15, 2009  
 */
public interface UpdateDocument {
  
  public void generate(ILink link, WebClient webClient, HTMLDocument document);
}
