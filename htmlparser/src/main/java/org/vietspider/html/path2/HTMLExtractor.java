/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 6, 2007  
 */
public class HTMLExtractor {
  
  public HTMLDocument extractFirst(HTMLDocument document, NodePath[] nodePaths) {
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

    for(int i = 0; i < nodePaths.length; i++) {
      List<HTMLNode> htmlNodes = matchNodes(root, nodePaths[i]);
//      System.out.println(" extract data === >"+ nodePaths[i].toString()+ " : "+ htmlNodes) ;
      if(htmlNodes == null ) continue;
      for(HTMLNode htmlNode : htmlNodes) {
        if(htmlNode == null) continue;
//        extractTokens(tokens, newTokens, htmlNode);
        newRoot.addChild(htmlNode);
//        htmlNode.setParent(newRoot);
      }
    }
    
    newDocument.setRoot(newRoot);
    return newDocument;
  }

  public HTMLDocument[] extractRow(HTMLDocument document, NodePath[] nodePaths) {
    List<List<HTMLNode>> listHtmlValues = new ArrayList<List<HTMLNode>>();
    HTMLNode root = document.getRoot();

    for(int i = 0; i < nodePaths.length; i++) {
      List<HTMLNode> matchValues = matchNodes(root, nodePaths[i]);
      if(matchValues != null) listHtmlValues.add(matchValues);
    }

    if(listHtmlValues.size() == 0 || listHtmlValues.get(0) == null) return new HTMLDocument[0];

    List<HTMLNode> htmlValues = listHtmlValues.get(0);
    HTMLDocument [] newDocuments = new HTMLDocument[htmlValues.size()]; 

    for(int i = 0; i < htmlValues.size(); i++) {
      HTMLNode html = HTMLParser2.clone(root);
      if(htmlValues.get(i) == null) continue;
      html.addChild(htmlValues.get(i));
//      htmlValues.get(i).setParent(html);
      for(int j = 1; j < listHtmlValues.size(); j++) {
        List<HTMLNode> newHtmlValues = listHtmlValues.get(j);
        if(i > newHtmlValues.size()) break;
        try {
          if(newHtmlValues.get(i) == null) continue;
          html.addChild(newHtmlValues.get(i)); 
//          newHtmlValues.get(i).setParent(html);
        } catch (Exception e) {
          continue;
        }
      }
      newDocuments[i] = new HTMLDocument(html);
    }
    return newDocuments;
  }
  
  public HTMLNode lookNode(HTMLNode htmlRoot, NodePath nodePath) {
    List<HTMLNode> list = matchNodes(htmlRoot, nodePath);
    return list == null || list.size() < 1 ? null : list.get(0); 
  }
  
  public List<HTMLNode> matchNodes(HTMLNode root, NodePath[] nodePaths) {
    List<HTMLNode> htmlValues = new ArrayList<HTMLNode>();
    for(NodePath nodePath : nodePaths) {
      List<HTMLNode> list = matchNodes(root, nodePath);
      if(list == null || list.size() < 1) continue;
      htmlValues.addAll(list);
    }
    return htmlValues;
  }

  public List<HTMLNode> matchNodes(HTMLNode htmlRoot, NodePath nodePath) {
    if(nodePath == null) return null;
    
//    System.out.println(" === > "+ nodePath);
    INode<?> [] inodes = nodePath.getNodes();
    if(inodes.length < 1) return null;
    List<HTMLNode> htmlValues = new ArrayList<HTMLNode>();
    if(inodes[0] instanceof NodeExp) {
      matchNodes(htmlRoot, (NodeExp)inodes[0], htmlValues);
    } else {
      htmlValues.add(lookNode(htmlRoot, (Node)inodes[0]));
    }

    for(int i = 1; i < inodes.length; i++) {
      HTMLNode [] htmlNodes = htmlValues.toArray(new HTMLNode[htmlValues.size()]);
      htmlValues.clear();
      
      if(inodes[i] instanceof AttrName) {
        AttrName attr = (AttrName) inodes[i];
        for(HTMLNode htmlNode : htmlNodes) {
          Attributes attributes = htmlNode.getAttributes();
          for(int j = 0; j < attributes.size(); j++) {
            Attribute attribute = attributes.get(j); 
            if(attribute.getName().equalsIgnoreCase(attr.getName())) {
              char [] chars = attribute.getValue().toCharArray();
              htmlValues.add(new NodeImpl(chars, Name.CONTENT));
            }
          }
        }
        continue;
      }

      if(inodes[i] instanceof NodeExp) {
        NodeExp nodeExp = (NodeExp)inodes[i];
        for(HTMLNode htmlNode : htmlNodes) {
          if(htmlNode == null) continue;
          matchNodes(htmlNode, nodeExp, htmlValues);
        }
        continue;
      }
      
      Node node = (Node)inodes[i];
      for(HTMLNode htmlNode : htmlNodes) {
        if(htmlNode == null) continue;
        HTMLNode test = lookNode(htmlNode, node);
        if(test == null) {
        	continue;
        }
        htmlValues.add(lookNode(htmlNode, node));
      }
    }
    if(htmlValues.size() < 1 ) return null;
    return htmlValues;
  }

  public List<HTMLNode> matchNodes(HTMLNode htmlNode, NodeExp nodeExp, List<HTMLNode> htmlValues) {
    List<HTMLNode> htmlChildren = htmlNode.getChildren();
//    List<HTMLNode> htmlValues = new ArrayList<HTMLNode>();  
//    System.out.println("node expresstion "+nodeExp.toString());
//    System.out.println("attributes length "+nodeExp.getAttributes().length);
    int counter  = 0;
    NodeMatcher matcher = new NodeMatcher();
    for(int i = 0; i < htmlChildren.size(); i++) {
      HTMLNode childNode =  htmlChildren.get(i);
      if(nodeExp.getName() != childNode.getName()) continue;
      if(matcher.match(nodeExp.getPattern(), counter)) { 
        Attribute [] attrs = nodeExp.getAttributes(); 
        if(attrs == null || attrs.length < 1) {
//          System.out.println(" da xay ra roi ");
          htmlValues.add(childNode);
        } else {
//          System.out.println(" xay ra ");
          Attributes nodeAttributes = childNode.getAttributes();
          if(matcher.contains(nodeAttributes, attrs)) htmlValues.add(childNode);
        }
      }
      counter++;
    }  
    return htmlValues;
  }

  public HTMLNode lookNode(HTMLNode htmlNode, Node inode) {
//    System.out.println(" da chuan bi vao day "+ htmlNode.getName()+ " : "+ inode);
    List<HTMLNode> htmlChildren = htmlNode.getChildren();

    int counter  = 0;
    for(int i = 0; i < htmlChildren.size(); i++) {
//      System.out.println(inode.getName() + " : " +htmlChildren.get(i).getName());
      if(inode.getName() != htmlChildren.get(i).getName()) continue;
//      System.out.println(inode.getIndex()+ " : " + counter);
      if(inode.getIndex() == counter) return htmlChildren.get(i);
      counter++;
    }  
//    System.out.println(" thay co null");

    return null;
  }
  
  public void remove(HTMLNode root, boolean isRemoveFrom, NodePath ... removePaths) {
    if(removePaths == null || removePaths.length < 1) return;
    if(isRemoveFrom) removeFrom(root, removePaths[removePaths.length-1]);
    remove(root, removePaths);
  }

  public void remove(HTMLNode root, NodePath ... nodePaths){
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();
    for(NodePath nodePath : nodePaths) {
      List<HTMLNode> matchValues = matchNodes(root, nodePath); 
//      System.out.println(root.getName() + " : " + nodePaths + " : "+ matchValues);
      if(matchValues != null) nodes.addAll(matchValues);
    }

    for(HTMLNode node : nodes) {
      if(node == null) continue;
      HTMLNode parent  = node.getParent();
      if(parent == null) continue;
//      System.out.println(" truoc " + parent.getChildren().size());
      parent.removeChild(node);
//      System.out.println(" sau " + parent.getChildren().size());
    }
  } 

  public void removeFrom(HTMLNode root, NodePath path){
    HTMLNode element = lookNode(root, path);
    if (element == null) return;
    java.util.Iterator<HTMLNode> iter =  element.getParent().getChildren().iterator();
    boolean remove = false;
    while(iter.hasNext()){
      HTMLNode ele = iter.next();
      if(!remove) remove = ele == element;
      if(remove) iter.remove();
    }
  } 
  
/*  private HTMLNode searchBody(HTMLNode root) {
    if(root.isNode(Name.BODY)) return root;
    List<HTMLNode> children = root.getChildren();
    if(children == null) return null;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode node = searchBody(children.get(i));
      if(node != null) return node;
    }
    return null;
  }
*/  
 /* private void extractTokens(CharsToken tokens, CharsToken newTokens, HTMLNode htmlNode) {
    HTMLNode breakNode = searchEndNode(htmlNode);
    Iterator<NodeImpl> iterator = tokens.iterator();
    boolean start = false;
    while(iterator.hasNext()) {
      NodeImpl nodeImpl = iterator.next();
      if(nodeImpl == breakNode) break;
      if(!start && nodeImpl == htmlNode) start = true;
      if(start) newTokens.push(nodeImpl);
    }
  }
  
  private HTMLNode searchEndNode(HTMLNode node) {
    HTMLNode parent  = node.getParent();
    if(parent == null) return null;
    List<HTMLNode> children = parent.getChildren();
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i) != node) continue;
      if(i == children.size() - 1) return searchEndNode(parent);
      return children.get(i+1);
    }
    return null;
  }*/

}
