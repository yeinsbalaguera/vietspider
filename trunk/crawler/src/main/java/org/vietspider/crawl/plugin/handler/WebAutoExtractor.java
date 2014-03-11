/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.checker.NodeChecker;
import org.vietspider.html.renderer.content.AnalyticsRenderer;
import org.vietspider.html.renderer.content.TextAnalyticsRenderer;
import org.vietspider.html.renderer.extractor.WebPageExtractor;
import org.vietspider.html.renderer.extractor.WebPageFilter;
import org.vietspider.html.renderer.extractor.WebPageSearcher;
import org.vietspider.locale.DetachDate;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 19, 2009  
 */
public class WebAutoExtractor {
  
  private String title;
  private String desc;
  private String textContent;
  
  private DetachDate detachDate;
  
  private ViDateTimeExtractor timeExtractor;
  
  public WebAutoExtractor(ViDateTimeExtractor timeExtractor) {
    this.timeExtractor = timeExtractor;
  } 
  
  public HTMLNode extractData(HTMLDocument document, String url) throws Exception  {
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    NodePath nodePath  = pathParser.toPath("BODY");
    HTMLNode body = extractor.lookNode(document.getRoot(), nodePath);
    
    HTMLNode docTitleNode = null; 
    nodePath  = pathParser.toPath("HEAD.TITLE");
    try {
      docTitleNode = extractor.lookNode(document.getRoot(), nodePath);
    } catch (Exception e) {
    }
    
    HTMLNode cloneBody = null;
    try {
      cloneBody = clone(body, 0);
    } catch (Throwable e) {
      Exception exception = new Exception("Stack over flow error. ");
      LogService.getInstance().setMessage(exception, url);
      return body;
    }
    
    if(cloneBody == null)  return body;
    
    WebPageFilter webPageFilter = new WebPageFilter();  
    webPageFilter.filter(body, NodeChecker.createDefaultCheckers(url));
    
    WebPageExtractor webPageExtractor = new WebPageExtractor();
    body = webPageExtractor.extract(body);
    
    AnalyticsRenderer renderer = new AnalyticsRenderer(body, true);
    this.textContent = renderer.getTextValue();
    
    if(textContent.length() < 100) {
      body = cloneBody;
      renderer = new AnalyticsRenderer(body, false);
      this.textContent = renderer.getTextValue();
    }
    
    WebPageSearcher webPageSearcher = new WebPageSearcher();
    HTMLNode node = webPageSearcher.searchContentNode(body);

    HTMLNode descNode = extractDesc(node);
    HTMLNode titleNode = extractTitle(node.getParent(), descNode);

    if(descNode != null) webPageFilter.removeNode(descNode);
    if(titleNode != null) webPageFilter.removeNode(titleNode);
    
    if(title == null) {
      if(docTitleNode != null) {
        if(docTitleNode.getChildren() != null &&
            docTitleNode.getChildren().size() > 0) {
         title = docTitleNode.getChild(0).getTextValue();
        }
      }
    }
    if(title == null) title  = "...";
    if(desc == null) desc  = "...";
    
    return body;
  }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDesc() { return desc; }
  public void setDesc(String desc) { this.desc = desc;  }

  public String getTextContent() { return textContent; }
  
  private HTMLNode extractTitle(HTMLNode node, HTMLNode descNode) {
    if(node == null) return null;
    List<HTMLNode> textNodes = new ArrayList<HTMLNode>();
    searchTextNode(textNodes, node);
    
    int i = 0;
    for(;i < textNodes.size(); i++) {
      if(textNodes.get(i) == descNode) break;
    }
    
    i--;
    
    for(; i > -1 ; i--) {
      HTMLNode txtNode = textNodes.get(i);
      AnalyticsRenderer renderer = new AnalyticsRenderer(txtNode, false);
      
      String text = renderer.getTextValue();
      
      detachDate = timeExtractor.detect(text);
      if(detachDate != null) continue;
      
      if(isSourceName(renderer)) continue;
      
      if(isImageNode(renderer, txtNode)) continue;
      
      if(isTitle(renderer)) {
        title = text;
        return txtNode;
      }
    }
    return extractTitle(node.getParent(), descNode);
  }
  
  private HTMLNode extractDesc(HTMLNode node) {
    if(node == null) return null;
    
    List<HTMLNode> textNodes = new ArrayList<HTMLNode>();
    searchTextNode(textNodes, node);
    
    for(int i = 0; i < textNodes.size(); i++) {
      HTMLNode txtNode = textNodes.get(i);
      AnalyticsRenderer renderer = new AnalyticsRenderer(txtNode, false);
      String text = renderer.getTextValue();
      
      detachDate = timeExtractor.detect(text);
      if(detachDate != null) continue;
      
      if(isSourceName(renderer)) continue;
      if(isImageNode(renderer, txtNode)) continue;
      
      if(isDesc(renderer)) {
        desc = text;
        return txtNode;
      } 
    }
    return extractDesc(node.getParent());
  }
  
  private boolean isTitle(AnalyticsRenderer renderer) {
    if(title != null) return false;
    if(renderer.getSentence() > 2) return false;
    if(renderer.getSentence() == 2) {
      if(renderer.getTextValue().indexOf("...") > -1) return true;
      return false;
    }
    if(renderer.getWord() < 1) return false;
    return renderer.getWord() < 25;
  }
  
  private boolean isDesc(AnalyticsRenderer renderer) {
    if(renderer.getSentence() > 1) return true;
    if(renderer.getWord() > 25) return true;
    return title != null;
  }
  
  private void searchTextNode(List<HTMLNode> nodes, HTMLNode node) {
    switch (node.getName()) {
    case P:
    case CONTENT:
//      if(isBadNode(node)) {
//        System.out.println(" ================================= ");
//        System.out.println(node.getTextValue());
//        return;
//      }
      nodes.add(node);
      return;
    case SPAN:
      List<HTMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) return;
      if(children.size() < 2) {
        nodes.add(node);
        return;
      }
    default:
      break;
    }
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      searchTextNode(nodes, children.get(i));
    }
  }
  
  /*private boolean isBadNode(HTMLNode node) {
    if(badNodes.contains(node)) return true;
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(badNodes.contains(n)) return true;
    }
    return false;
  }*/
  
  private boolean isSourceName(AnalyticsRenderer renderer) {
    if (renderer.getWord() > 5) return false;  
    String text = renderer.getTextValue();
    if(text == null) return true;
    text = text.trim();
    if(text.isEmpty()) return true;
    return text.charAt(0) == '(' || text.charAt(text.length() - 1) == ')';
  }
  
  private boolean isImageNode(AnalyticsRenderer renderer, HTMLNode node) {
    if(renderer.getSentence() > 3) return false;
    HTMLNode table = searchUpper(node, 0, 10, Name.TABLE);
    if(table != null) {
      TextAnalyticsRenderer textRenderer = new TextAnalyticsRenderer(table);
      if(textRenderer.getParagraph() > 1) return false;
      if(textRenderer.getSentence() > 3) return false;
      return textRenderer.getWord() < 50;
    }
    
    HTMLNode container = searchUpper(node, 0, 5, Name.DIV, Name.CENTER);
    if(container != null) {
      TextAnalyticsRenderer textRenderer = new TextAnalyticsRenderer(container);
      if(textRenderer.getParagraph() > 1) return false;
      if(textRenderer.getSentence() > 3) return false;
      return textRenderer.getWord() < 50;
    }
    return false;
  }
  
  public HTMLNode searchUpper(HTMLNode node, int level, int max, Name...names) {
    if(level > max) return null;
    HTMLNode parent  = node.getParent();
    if(parent == null) return null;
    for(Name name  : names) {
      if(parent.isNode(name)) return parent;
    }
    return searchUpper(parent, level + 1, max, names);
  }
  
  private HTMLNode clone(HTMLNode node, int stack) throws Exception {
    if(stack >= 5000)  {
      throw new Exception("Stack over flow error.");
    }
    
    HTMLNode cloneNode = HTMLParser2.clone(node);
    List<HTMLNode> children  = node.getChildren();
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        cloneNode.addChild(clone(children.get(i), stack+1));
      }
    }
    return cloneNode;
  }
  
}
