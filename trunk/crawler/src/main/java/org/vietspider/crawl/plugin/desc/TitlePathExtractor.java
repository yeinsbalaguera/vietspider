/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2008  
 */
public final class TitlePathExtractor extends TitleExtractor {
  
  private NodePath [] titlePaths = null;  
  
  public TitlePathExtractor(HTMLExtractor extractor, NodeHandler nodeHandler, NodePath[] paths) {
    super(extractor, nodeHandler);
    this.titlePaths = paths;
  }

  public String extract(HTMLNode root, List<HTMLNode> contents) {
    StringBuilder builder  = new StringBuilder();
    List<HTMLNode> titleNodes = extractor.matchNodes(root, titlePaths);
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    for(int i = 0; i < titleNodes.size(); i++) {
      HTMLNode titleNode = titleNodes.get(i);
      if(titleNode == null) continue;
      List<HTMLNode> nodes  = new ArrayList<HTMLNode>();
      htmlText.searchText(nodes, titleNode, verify);
//      nodeHandler.searchTextNode(titleNode, nodes);
      for(HTMLNode ele : nodes) {
        builder.append(ele.getValue()).append(' ');
        contents.remove(ele);
      }
    }
    
    for(int i = 0; i < titleNodes.size(); i++) {
      nodeHandler.removeNode(titleNodes.get(i));
    }
    return  builder.toString();
  }

}
