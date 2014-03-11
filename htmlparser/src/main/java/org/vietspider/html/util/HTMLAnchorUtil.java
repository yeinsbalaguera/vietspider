/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Oct 12, 2007
 */
public class HTMLAnchorUtil extends HTMLNodePath {

  public final void searchAnchors(HTMLNode node, List<HTMLNode> anchors) {   
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if (n.isNode(Name.A)) {
        Attributes attrs = n.getAttributes();
        Attribute attr = attrs.get("name");
        if (attr != null) anchors.add(n);
      }
    }
  }

  public HTMLDocument searchDocument(HTMLDocument document, String name) {
    HTMLNode root = document.getRoot();
    HTMLNode newRoot = searchDocument(root, name);
    if (root == newRoot) return document;
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    try {
      HTMLNode body = extractor.lookNode(root, pathParser.toPath("BODY"));
      body.clearChildren();
      body.addChild(newRoot);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return document;
  }

  public HTMLNode searchDocument(HTMLNode root, String name) {
    List<HTMLNode> anchors = new ArrayList<HTMLNode>();
    searchAnchors(root, anchors);

    int i = 0;
    for (; i < anchors.size(); i++) {
      Attributes attrs = anchors.get(i).getAttributes();
      Attribute attr = attrs.get("name");
      if (attr.getValue().equals(name)) break;
    }

    if (i < anchors.size() - 1) {
      return processNextNode(root, anchors, i);
    } else if (i > 0) {
      return processPrevNode(root, anchors, i);
    }
    return root;
  }

  private HTMLNode processNextNode(HTMLNode root, List<HTMLNode> anchors, int i) {
    HTMLNode node = anchors.get(i);
    HTMLNode nextNode = anchors.get(i + 1);
    String path = getIndexPath(node);
    String nextPath = getIndexPath(nextNode);
    String commonPath = getCommonIndexPath(path, nextPath);
    try {
      HTMLNode commonNode = getNodeByIndex(root, commonPath);
      path = path.substring(commonPath.length());
      path = path.substring(1, path.indexOf('.', 1));
      int idx = Integer.parseInt(path);

      nextPath = nextPath.substring(commonPath.length());
      nextPath = nextPath.substring(1, nextPath.indexOf('.', 1));
      int nextIdx = Integer.parseInt(nextPath);

      HTMLNode newCommonNode = 
        new NodeImpl(commonNode.getValue(), commonNode.getName(), TypeToken.TAG);
      List<HTMLNode> children = commonNode.getChildren();
      for (i = idx; i < nextIdx; i++) {
        newCommonNode.addChild(children.get(i));
      }
      return newCommonNode;
    } catch (Exception e) {
      // LogService.getInstance().setMessage(e.toString());
      return root;
    }
  }

  private HTMLNode processPrevNode(HTMLNode root, List<HTMLNode> anchors, int i) {
    if (i < 0 || i >= anchors.size())
      return root;
    HTMLNode node = anchors.get(i);
    HTMLNode prevNode = anchors.get(i - 1);
    String path = getIndexPath(node);
    String prevPath = getIndexPath(prevNode);
    String commonPath = getCommonIndexPath(path, prevPath);
    try {
      HTMLNode commonNode = getNodeByIndex(root, commonPath);
      path = path.substring(commonPath.length());
      int idx = path.indexOf('.', 1);
      // path = path.substring(0, idx);
      if (idx > 0) {
        path = path.substring(1, idx);
      } else {
        if (path.charAt(0) == '.')
          path = path.substring(1);
      }

      // if(idx > 1) path = path.substring(2, idx);
      // if(path.charAt(0) == '.') path = path.substring(1);
      // if(path.trim().isEmpty()) return root;
      idx = Integer.parseInt(path);

      HTMLNode newCommonNode = new NodeImpl(commonNode.getValue(), commonNode.getName(),
          TypeToken.TAG);
      List<HTMLNode> children = commonNode.getChildren();
      for (i = idx; i < children.size(); i++) {
        newCommonNode.addChild(children.get(i));
        // children.get(i).setParent(newCommonNode);
      }
      return newCommonNode;
    } catch (Exception e) {
      StringBuilder builder = new StringBuilder();
      builder.append("path value: ").append(path).append('\n');
      builder.append("commonpath: ").append(commonPath).append('\n');
      LogService.getInstance().setMessage(e, path + "<=" + getIndexPath(node));
      return root;
    }
  }

}
