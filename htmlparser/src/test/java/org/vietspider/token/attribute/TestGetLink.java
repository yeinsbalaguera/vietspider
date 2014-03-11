/***************************************************************************
 * Copyright 2001-2007 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.token.attribute;

import java.net.URL;
import java.util.List;

import org.vietspider.chars.URLUtils;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HyperLinkUtil;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2007  
 */
public class TestGetLink {
  public static void main(String[] args) throws Exception {
    URL url = new URL("http://www.tuoitre.com.vn/Tianyon/Index.aspx");
    HTMLNode root = HTMLParser.createDocument(url.openStream(),"utf-8").getRoot();
//    System.out.println(root.getTextValue());
    HyperLinkUtil linkUtil = new HyperLinkUtil();
//    List<String> collection = handler.getSiteLink(root);
    
    URLUtils urlCreator = new URLUtils();
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    NodePath BASE_NODE = new NodePath(pathParser.toNodes("HEAD.BASE"));
    HTMLNode baseNode = extractor.lookNode(root, BASE_NODE);
    String baseHref = null;
    if(baseNode != null)  { 
      Attributes attrs = baseNode.getAttributes(); 
      int idx = attrs.indexOf("href");
      if(idx > -1) {
        baseHref  = attrs.get(idx).getValue();
        linkUtil.createFullImageLink(root, url);
      }
    }
    
    List<String> collection = linkUtil.scanImageLink(root);
    
//    System.out.println("total link :"+collection.size());
//    System.out.println(baseHref);
//    System.out.println("===================================");
//    for(String ele : collection) {
//      System.out.println(ele);
//    }
  }
}
