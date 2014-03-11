/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.parser.NodeImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class TestNextHTMLNode {
  
  public static void main(String[] args) throws Exception {
    File file  = new File("F:\\Temp2\\README4.html");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    HTMLNode node = document.getRoot();
    
    DocumentExtractor extractor  = new DocumentExtractor();
    NodePathParser pathParser = new NodePathParser();
    
    String [] paths = new String [2];
    paths[0] = "BODY[0].UL[0]";
    paths[1] = "BODY[0].A[0].H2[0]";
    NodePath [] nodePaths = pathParser.toNodePath(paths);
    
    HTMLDocument newDocument = extractor.extract(document, nodePaths);
    node = newDocument.getRoot();
    System.out.println(node.getTextValue());
    
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();
//    node.traverse(new SearchNode(nodes));
    System.out.println(nodes.size());
    for(HTMLNode n : nodes) {
      NodeImpl impl = (NodeImpl)n;
//      System.out.println(impl.level);
    }
    
  }
  
//  private static class SearchNode implements HTMLNodeHandler {
//    
//    private List<HTMLNode> nodes1;
//    
//    private SearchNode(List<HTMLNode> nodes1) {
//      this.nodes1 = nodes1;
//    }
//    
//    public void handle(HTMLNode node) {
//      if(!node.isNode(Name.A)) return;
////      System.out.println(node.getName() +  "  : " );
//      nodes1.add(node);
//    }
//  }
}
