/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 10, 2008  
 */
public interface JsLinkPlugin {
  
  public List<Link> getLinks(String host, Link referer);

}
