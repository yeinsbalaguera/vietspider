/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.io.File;

import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.DocumentExtractor;
import org.vietspider.html.path2.LookupNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class TestHTMLNodeIterator {
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\html\\lookup.node.1.html");
    HTMLDocument document = new HTMLParser2().createDocument(file,"utf-8");
    
    HTMLNode node = document.getRoot();
//    System.out.println(node.getTextValue());
//    System.out.println("hi hi hi "+new String(node.getValue()));
    
    NodePathParser pathParser = new NodePathParser();
    String [] paths = new String [1];
    paths[0] = "BODY[0].DIV[0].DIV[1].CLASS";
    NodePath [] nodePaths = pathParser.toNodePath(paths);
    
    LookupNode lookupNode = new LookupNode();
    
    
    DocumentExtractor extractor  = new DocumentExtractor();
    HTMLDocument newDocument = extractor.extract(document, nodePaths);
    System.out.println(newDocument.getTextValue());
//    node = newDocument.getRoot();
    
//    NodeImpl.next((NodeImpl)node);
    
//    NodeIterator iterator = document.getRoot().iterator();
//    
//    HTMLNode n = null;
//    System.out.println(iterator.hasNext());
//    while(iterator.hasNext()) {
//      n = iterator.next();
////      if(n.isNode(Name.HTML)) {
////        if(counter < 1) counter++; else break;
////      }
////      System.out.println(n.hashCode());
//      System.out.println(n.getName());
////      System.out.println(new String(n.getValue()));
////      if(n.isNode(Name.A)) System.out.println(n.getName());
//    }
////    System.out.println(n.hashCode());
////    System.out.println("\n\n\n");
////    System.out.println(n.getTextValue());
    
  }
  
  
}

  