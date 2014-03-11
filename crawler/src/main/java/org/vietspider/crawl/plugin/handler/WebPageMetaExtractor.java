/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.locale.DetachDate;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public final class WebPageMetaExtractor {
  
  private NodePath bodyPath = null;
  private NodePath titlePath = null;
  private NodePath headPath = null;
  
  private HTMLExtractor extractor;
  private ViDateTimeExtractor timeExtractor;
  
  private Pattern cssTitlePattern;
  private Pattern cssHeadlinePattern;
  
  private int descIndex = -1;
  
  public WebPageMetaExtractor(HTMLExtractor extractor, ViDateTimeExtractor timeExtractor) {
    NodePathParser pathParser = new NodePathParser();
    try {
      bodyPath = pathParser.toPath("BODY");
    } catch (Exception e) {
    }
    
    try {
      titlePath = pathParser.toPath("HEAD.TITLE");
    } catch (Exception e) {
    }
    
    try {
      headPath  = pathParser.toPath("HEAD");
    } catch (Exception e) {
      
    }
    
    int style = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
    cssTitlePattern = Pattern.compile("title", style);
    cssHeadlinePattern =  Pattern.compile("headline", style);
    
    this.extractor = extractor;
    this.timeExtractor = timeExtractor;
  }
  
  public String extractTitle(HTMLNode root, List<HTMLNode> nodes) {
    NodeHandler nodeHandler = new NodeHandler();
    while(descIndex > 0 && descIndex < nodes.size() - 1) {
      HTMLNode node = nodes.get(descIndex - 1);
      String title = node.getTextValue();
      int count = nodeHandler.count(title);
      if(count < 1 || count > 100) {
        descIndex--;
        continue; 
      }
      
      if(isCommentSource(nodeHandler, title)) {
        descIndex--;
        continue;
      }
      
      if(isAuthor(nodeHandler, node)) {
        descIndex--;
        continue;
      }
      
      DetachDate detach = timeExtractor.detect(title.toLowerCase());
      if(detach == null) return title;
      descIndex--;
    }
    
    String title = null;
    
    String headTitle = extractText(extractor.lookNode(root, titlePath));
    if(nodeHandler.count(headTitle) >= 10) return headTitle;
    
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      if(!isTitleNode(node, 0)) continue;
      String value = node.getTextValue();
      int count = nodeHandler.count(value);
      if(count < 1 || count >= 100) continue;
      DetachDate detach = timeExtractor.detect(value.toLowerCase());
      if(detach != null) continue;
      title = value;
      descIndex = i;
      break;
    }
    
    int count = title != null ? nodeHandler.count(title) : 0; 
    if(count < 1)  title = headTitle;
    if(title == null || (title = title.trim()).isEmpty()) title = "...";
    return title;
  }
  
  private boolean isCommentSource(NodeHandler nodeHandler, String value) {
    value = value.trim();
    if(nodeHandler.count(value) >= 5) return false;
    return value.indexOf(')') > -1 && value.indexOf(')') > -1; 
  }
  
  private boolean isAuthor(NodeHandler nodeHandler, HTMLNode node) {
    String value = node.getTextValue().trim();
    if(nodeHandler.count(value) >= 5) return false;
    HTMLNode parent = node.getParent();
    Attributes attributes = parent.getAttributes();
    Attribute attribute = attributes.get("class");
    if(attribute == null) return false;
    String attrValue = attribute.getValue();
    if(attrValue == null) return false;
    attrValue = attrValue.trim().toLowerCase();
    return attrValue.indexOf("author") > -1;
  }
  
  public HTMLNode extractImage(List<HTMLNode> nodes) {
    if(descIndex > 0 && descIndex < nodes.size() - 1) {
      HTMLNode node = nodes.get(descIndex - 1);
      while(node != null) {
        HTMLNode imgNode = searchImageNode(node.iterator());
        if(imgNode != null) return imgNode;
        node = node.getParent();
      }
    }
    return null;
  }
  
  private HTMLNode searchImageNode(NodeIterator iterator) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.IMG)) return n;
    }
    
//    List<HTMLNode> children  = node.getChildren();
//    if(children == null) return  null;
//    for (int i = 0; i < children.size(); i++) {
//      HTMLNode value = searchImageNode(children.get(i));
//      if(value != null) return value;
//    }
    return null;
  }
  
  public String extractDesc(List<HTMLNode> nodes){
    String desc = null;
    descIndex = -1;
    NodeHandler nodeHandler = new NodeHandler();
    desc = extractDesc(nodeHandler, nodes, 20, 200);
    if(desc == null) desc = extractDesc(nodeHandler, nodes, 5, 20);
    if(desc == null || (desc= desc.trim()).isEmpty()) desc = "...";
    return desc;
  }
  
  private String extractDesc(NodeHandler nodeHandler, List<HTMLNode> nodes, int  min, int max){
    String desc = null;
    for(int i = 0; i < nodes.size(); i++) {
      HTMLNode node = nodes.get(i);
      if(isLinkNode(node, 0)) continue;
      int count = nodeHandler.count(node.getTextValue());
      if(count < min || count > max) continue;
      desc = node.getTextValue();
      descIndex = i;
      break;
    }
    return desc;
  }
  
  public boolean isLinkNode(HTMLNode node, int time) {
    if(time  == 5 || node == null) return false;
    if(node.isNode(Name.A) || node.isNode(Name.MARQUEE)) return true;
    return isLinkNode(node.getParent(), time+1);
  }
  
  private String extractText(HTMLNode node) {
    StringBuilder builder = new StringBuilder();
    HTMLText textUtils = new HTMLText();
    textUtils.buildText(builder, node);
    return builder.toString();
  }
  
  public HTMLNode extractHeader(HTMLNode root) {
    return extractor.lookNode(root,  headPath);
  }
  
  public List<HTMLNode> searchContents(HTMLNode root) {
    if(bodyPath == null) {
      List<HTMLNode> list = new ArrayList<HTMLNode>();
      new HTMLText().searchText(list, root, new HTMLText.EmptyVerify());
//      nodeHandler.searchTextNode(root, list);
      return list;
    } 
    
    HTMLNode body = extractor.lookNode(root, bodyPath);
    List<HTMLNode> list =  new ArrayList<HTMLNode>();
    list = searchTextNode(body, list);
    return list;
  }
  
  private List<HTMLNode> searchTextNode(HTMLNode root, List<HTMLNode> contents){
    if(root == null) return contents;
    
    switch (root.getName()) {
    case SCRIPT:
      return contents;
    case STYLE:
      return contents;
    case UNKNOWN:
      return contents;
      
    case MARQUEE:
      return contents;

    case OPTION:
      return contents;
    case OPTGROUP:
      return contents;
    case SELECT:
      return contents;

    default:
      break;
    }
    
    if(root.isNode(Name.CONTENT)) {
      HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
      if(verify.isValid(root.getValue())) contents.add(root);
      return contents;
    }
    
    List<HTMLNode> childen = root.getChildren();
    if (childen == null)  return  contents;
    for(HTMLNode ele : childen) {
      searchTextNode(ele, contents);      
    }
    return contents;
  }
  
  private boolean isTitleNode(HTMLNode node, int time) {
    if(time == 4 || node == null) return false;
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("class");
    if(attribute != null) {
      String value = attribute.getValue();
      if(value != null) {
        value = value.trim().toLowerCase();
        if(cssTitlePattern.matcher(value).find()) return true;
        if(cssHeadlinePattern.matcher(value).find()) return true;
      }
    }
    return isTitleNode(node.getParent(), time+1);
  }
 
  
}
