/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public class DescPathExtractor extends DescExtractor {
  
  protected NodePath [] descPath = null;
  
  public DescPathExtractor(HTMLExtractor extractor, NodeHandler nodeHandler, NodePath [] path) {
    super(extractor, nodeHandler);
    this.descPath = path;
  }
  
  public String extract(HTMLNode root, List<HTMLNode> contents) {
    StringBuilder builder  = new StringBuilder();
    HTMLText htmlText = new HTMLText();
    for(NodePath path : descPath) {
      HTMLNode node = extractor.lookNode(root, path);
      if(node == null) continue;
      htmlText.buildText(builder, node);
//      nodeHandler.buildContent(builder, node);
      nodeHandler.removeContent(node.iterator(), contents);
      if(node.isNode(Name.CONTENT))  {
        nodeHandler.removeNode(node);
      } else {
        removeNode(node);
        nodeHandler.removeContent(node.iterator(), contents);
      }
    }
    return builder.toString();
  }
}
