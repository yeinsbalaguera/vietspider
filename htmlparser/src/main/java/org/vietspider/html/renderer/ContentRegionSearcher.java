/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.document.util.OtherLinkRemover2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLParentUtils;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2009  
 */
public class ContentRegionSearcher {

  private NodeHandler nodeHandler = new NodeHandler();
  private HTMLParentUtils htmlUtil = new HTMLParentUtils();
  
  private Name [] ignores = {Name.A, Name.MARQUEE};

  public void searchNodes(NodeIterator iterator, List<HTMLNode> nodes, Name name) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) nodes.add(n);
    }
  }  

  public List<HTMLNode> searchNodes(HTMLNode node, Name name) {
    List<HTMLNode> refsNode = new ArrayList<HTMLNode>();
    searchNodes(node.iterator(), refsNode, name);

    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();

    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);

    return contents;
  }

  public String searchMaxSequence(String text, char c) {
    int index = text.indexOf(c);
    if(index < 0) return null;

    int counter = 1;
    while(index > -1) {
      counter = searchMaxSequence(text, c, index, counter);
      index = text.indexOf(c, index+counter);
    }

    StringBuilder builder = new StringBuilder(counter);
    for(int i = 0; i < counter; i++) {
      builder.append(c); 
    }
    return builder.toString();
  }

  public int searchMaxSequence(String text, char c, int index, int counter) {
    int k = index + 1;
    for(; k < index + counter; k++) {
      if(k >= text.length() || text.charAt(k) != c) return counter;
    }

    k = index + counter ;
    while(k  < text.length()) {
      if(text.charAt(k) != c) {
        return k - index;
      }
      k++;
    }

    return counter;
  }

  public NodeRenderer filte(NodeRenderer [] nodeRenders) {
    NodeRenderer renderer = null;
    int max = 0;
    for(int i = 0; i < nodeRenders.length; i++) {
      int counter = countWord(nodeRenders[i].getNodes());
      if(renderer == null) {
        max = counter;
        renderer = nodeRenders[i];
      } else if (counter >= max) {
        max = counter;
        renderer = nodeRenders[i];
      }
    }
    return renderer;
  }

  public int countWord(List<HTMLNode> list) {
    int counter = 0;
    for(HTMLNode node : list) {
      counter += countWord(node);
    }
    return counter;
  }

  public int countWord(HTMLNode node) {
    if(node.isNode(Name.CONTENT)) {
      if(isIgnoreTag(node)) {
        return 0;
      }
      int c = nodeHandler.count(new String(node.getValue()));
      return c;
    }
    return 0;
  }

  public int countWordA(List<HTMLNode> list) {
    int counter = 0;
    for(HTMLNode node : list) {
      counter += countWordA(node);
    }
    return counter;
  }

  public int countWordA(HTMLNode node) {
    if(node.isNode(Name.CONTENT)) {
      if(isIgnoreTag(node)) {
        int c = nodeHandler.count(new String(node.getValue()));
        return c;
      }
      return 0;
    }
    return 0;
  }

  public boolean isIgnoreTag(HTMLNode node) {
    return isIgnoreTag(node, 0);
  }

  public boolean isIgnoreTag(HTMLNode node, int level) {
    if(node == null || level >= 5) return false;
    for(int i = 0; i < ignores.length; i++) {
      if(node.isNode(ignores[i])) return true;
    }
    return isIgnoreTag(node.getParent(), level+1);
  }

  public HTMLNode searchParent(HTMLDocument document, int level) throws Exception  {
    HTMLNode  body = searchBody(document);
    ContentRenderer renderer = createContentRenderer(body);

    int length = renderer.getTextValue().length();
    List<HTMLNode> renderNodes = renderer.getNodePositions(0, length);
    NodeRenderer nodeRenderer = new NodeRenderer(renderer, renderNodes, 0, length);

    NodeRenderer [] nodeRenderers = {nodeRenderer};
    while(true) {
      nodeRenderer = filte(nodeRenderers);

      if(nodeRenderer == null) break;

      String pattern = searchMaxSequence(nodeRenderer.getText(), '\\');
      if(pattern == null || pattern.length() < level) break;
      nodeRenderers = nodeRenderer.split(pattern);
    }
    nodeRenderer = filte(nodeRenderers);
    nodeRenderers = new NodeRenderer[] {nodeRenderer};


    int start  = nodeRenderer.getStart();
    int end = nodeRenderer.getEnd();
    return htmlUtil.getUpParent(renderer.getNodePositions(start, end));
  }
  
  public List<HTMLNode> searchNodes(HTMLDocument document, int level, int step) throws Exception  {
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    
    HTMLNode body = searchBody(document);
    ContentRenderer renderer = createContentRenderer(body);

    int length = renderer.getTextValue().length();
    List<HTMLNode> renderNodes = renderer.getNodePositions(0, length);
    NodeRenderer nodeRenderer = new NodeRenderer(renderer, renderNodes, 0, length);

    NodeRenderer [] nodeRenderers = {nodeRenderer};
    while(true) {
      nodeRenderer = filte(nodeRenderers);

      if(nodeRenderer == null) break;

      String pattern = searchMaxSequence(nodeRenderer.getText(), '\\');
      if(pattern == null || pattern.length() < level) break;
      
      if(pattern.length()%step == 0) {
        int start  = nodeRenderer.getStart();
        int end = nodeRenderer.getEnd();
        List<HTMLNode> subNodes = renderer.getNodePositions(start, end);
        values.add(htmlUtil.getUpParent(subNodes));
      }
      nodeRenderers = nodeRenderer.split(pattern);
    }
    
    nodeRenderer = filte(nodeRenderers);
    nodeRenderers = new NodeRenderer[] {nodeRenderer};


    int start  = nodeRenderer.getStart();
    int end = nodeRenderer.getEnd();
    List<HTMLNode> subNodes = renderer.getNodePositions(start, end);
    values.add(htmlUtil.getUpParent(subNodes));
    
    return values;
  }
  
  protected HTMLNode searchBody(HTMLDocument document) throws Exception {
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      node.setValue(chars);              
    }  

    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    NodePath nodePath  = pathParser.toPath("BODY");
    return extractor.lookNode(document.getRoot(), nodePath);
  }
  
  private ContentRenderer createContentRenderer(HTMLNode body) {
    List<HTMLNode> contents = searchNodes(body, Name.A);
    
    OtherLinkRemover2 linkRemover = new OtherLinkRemover2();
    List<HTMLNode> nodes = linkRemover.removeLinks(body, null);
    for(int i = 0; i < nodes.size(); i++) {
      contents.remove(nodes.get(i));
    }
    return ContentRendererFactory.createContentRenderer(body, null);
  }
}
