/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.DataWriter;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 2, 2007  
 */
public class TestRemoveDescImage {
  public static void main(String[] args) throws Exception {
    NodeHandler nodeHandler  = new NodeHandler();
//    RemoveDescImage remover = new RemoveDescImage(nodeHandler);
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    File folder = new File("F:\\Temp\\html_parser_test");
    HTMLDocument doc = new HTMLParser2().createDocument(new File(folder, "anh 8.html"), "utf-8");
    
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(contents, doc.getRoot(), verify);
//    nodeHandler.searchTextNode(doc.getRoot(), contents);
    
//    remover.removeDescImageNode(doc.getRoot(), contents);
    
    StringBuilder builder = new StringBuilder();
    for(HTMLNode ele : contents) {
      builder.append(ele.getValue()).append('\n').append('\n');
    }
    
    File file = new File(folder, "text.txt");
    writer.save(file, builder.toString().getBytes("utf-8"));
    
  }
}
