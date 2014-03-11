/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.io.File;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class TestLinkChecker {
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\web\\stest\\testTextBlock.htm");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    HTMLNode node = document.getRoot().getChild(1).getChild(0);
    
    
    ContentChecker contentChecker = new ContentChecker();
//    System.out.println(checker.isTextBlock(node, true, 50, 5));
    LinkBlockChecker ulNodeChecker = new LinkBlockChecker(contentChecker);
    System.out.println(ulNodeChecker.isLink(new CheckModel(node)));
    
    
  }
}
