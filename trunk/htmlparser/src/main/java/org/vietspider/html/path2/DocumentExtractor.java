/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 6, 2007  
 */
public class DocumentExtractor {
  
  public HTMLDocument extractFirst(HTMLDocument document, NodePath [] nodePaths) {
    HTMLDocument [] documents = extractRow(document, nodePaths);
    if(documents.length < 1) return null;
    return documents[0];
  }
  
  public HTMLDocument extract(HTMLDocument document, NodePath... nodePaths) {
    HTMLNode root = document.getRoot();
//    CharsToken tokens = document.getTokens();
    
    HTMLNode newRoot = HTMLParser2.clone(root);
    HTMLDocument newDocument  = new HTMLDocument();
//    CharsToken newTokens = new CharsToken(newDocument);
//    newTokens.push((NodeImpl)newRoot);

    LookupNode lookupNode = new LookupNode();
    
    for(int i = 0; i < nodePaths.length; i++) {
      List<HTMLNode> htmlNodes = lookupNode.lookupNodes(root, nodePaths[i]);
      if(htmlNodes == null ) continue;
      for(HTMLNode htmlNode : htmlNodes) {
        if(htmlNode == null) continue;
//        lookupNode.extractTokens(tokens, newTokens, htmlNode);
        htmlNode.clone(newRoot);
      }
    }
    
    newDocument.setRoot(newRoot);
    return newDocument;
  }

  public HTMLDocument[] extractRow(HTMLDocument document, NodePath[] nodePaths) {
    List<List<HTMLNode>> nodeLists = new ArrayList<List<HTMLNode>>();
    HTMLNode root = document.getRoot();

    LookupNode lookupNode = new LookupNode();
    
    for(int i = 0; i < nodePaths.length; i++) {
      List<HTMLNode> matchValues = lookupNode.lookupNodes(root, nodePaths[i]);
      if(matchValues != null) nodeLists.add(matchValues);
    }

    if(nodeLists.size() == 0 || nodeLists.get(0) == null) return new HTMLDocument[0];

    List<HTMLNode> nodes = nodeLists.get(0);
    HTMLDocument [] newDocuments = new HTMLDocument[nodes.size()]; 

    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode newRoot = HTMLParser2.clone(root);
      if(nodes.get(i) == null) continue;
      nodes.get(i).clone(newRoot);
      for(int j = 1; j < nodeLists.size(); j++) {
        List<HTMLNode> nextNodes = nodeLists.get(j);
        if(i > nextNodes.size()) break;
        try {
          if(nextNodes.get(i) == null) continue;
          nextNodes.get(i).clone(newRoot);
        } catch (Exception e) {
          continue;
        }
      }
      newDocuments[i] = new HTMLDocument(newRoot);
    }
    return newDocuments;
  }
  

}
