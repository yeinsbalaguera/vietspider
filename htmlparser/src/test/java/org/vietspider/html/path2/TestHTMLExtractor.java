/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.io.File;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2007  
 */
public class TestHTMLExtractor {
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\Temp2\\README4.html");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    
    String [] paths = new String [2];
    paths[0] = "BODY[0].UL[*][class=a bc].LI[2]";
    paths[1] = "BODY[0].A[0].H2[0]";
    NodePath [] nodePaths = pathParser.toNodePath(paths);
    HTMLDocument newDocument = extractor.extract(document, nodePaths);
    
    HTMLNode ulNode = newDocument.getRoot().getChildren().get(0);
    
//    Iterator<NodeImpl> iterator = newDocument.getTokens().iterator();
   /* int counter = 0;
    while(iterator.hasNext()){
      NodeImpl node = iterator.next();
      if(counter == 1) {
        System.out.println(" =================== > "+ (node == ulNode ));
      }
//      if(!node.isNode(Name.A)) continue;
      String value = new String(node.getValue());
      System.out.println("----------------------");
      System.out.println(value +" : "+ node.getType() + " : " + node.isOpen());
      counter++;
    }*/
    
    System.out.println(newDocument.getTextValue());
//    System.out.println(newDocument.getTokens());
    
  }
}
