/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2008  
 */
public abstract class TitleExtractor extends NodeUtils {
  
  protected HTMLExtractor extractor;
  
  public TitleExtractor(HTMLExtractor extractor, NodeHandler nodeHandler) {
    super(nodeHandler);
    this.extractor = extractor;
  }
  
  public abstract String extract(HTMLNode root, List<HTMLNode> contents) ;

}
