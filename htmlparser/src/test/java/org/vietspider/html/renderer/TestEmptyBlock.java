/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.renderer.checker.TABLENodeChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class TestEmptyBlock {
  public static void main(String[] args) throws Exception  {
    TABLENodeChecker checker = new TABLENodeChecker(0);
    File file  = new File("F:\\Temp2\\web\\stest\\ngoisaoblog8.htm");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    HTMLNode node = document.getRoot().getChild(1).getChild(0);
//    System.out.println(node.getTextValue());
//    System.out.println(checker.isEmptyBlock(node));
  }
}
