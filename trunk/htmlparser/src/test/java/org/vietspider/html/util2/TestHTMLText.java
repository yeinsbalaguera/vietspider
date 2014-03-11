/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.util.HTMLText;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2008  
 */
public class TestHTMLText {
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\vietnamnet.html");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, document.getRoot(), verify);
    
    System.out.println("\n===================================");
    for(HTMLNode content : contents) {
      System.out.println(content);
    }
    System.out.println("===================================");
  }
}
