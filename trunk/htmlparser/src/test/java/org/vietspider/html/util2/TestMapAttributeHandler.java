/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util2;

import java.io.File;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.LookupNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HyperLinkUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 15, 2008  
 */
public class TestMapAttributeHandler {
  
  public static void main(String[] args) throws Exception {
    HyperLinkUtil util = new HyperLinkUtil();
    
    File file  = new File("F:\\Temp2\\README4.html");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    NodePathParser pathParser = new NodePathParser();
    String [] paths = new String [1];
    paths[0] = "BODY[0].UL[0].LI[2]";
    NodePath [] nodePaths = pathParser.toNodePath(paths);
    
    LookupNode lookupNode = new LookupNode();
    HTMLNode node  = lookupNode.lookupNode(document.getRoot(), nodePaths[0]);
    
    List<String> values = util.scanSiteLink(node);
    System.out.println(values.size());
  }
  
}
