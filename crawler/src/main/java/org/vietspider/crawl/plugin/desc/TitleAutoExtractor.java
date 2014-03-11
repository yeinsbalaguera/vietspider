/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.desc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.util.HTMLParentUtils;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2008  
 */
public class TitleAutoExtractor extends TitleExtractor {
  
  public TitleAutoExtractor(HTMLExtractor extractor, NodeHandler nodeHandler) {
    super(extractor, nodeHandler);
  }

  @Override
  public String extract(HTMLNode root, List<HTMLNode> contents) {
    TextRenderer renderer = new TextRenderer(root, contents, TextRenderer.RENDERER, true);

//    java.io.File cFile  = new java.io.File("F:\\Temp\\title_des\\a.txt");
//    org.vietspider.common.io.DataWriter buffer = org.vietspider.common.io.RWData.getInstance();
//    try {
//      buffer.save(cFile, renderer.getTextValue().toString().getBytes("utf-8"));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
    StringBuilder builderTitle = new StringBuilder();
    StringBuilder textValue = renderer.getTextValue();
    cutTitle(contents, builderTitle, renderer, textValue.toString(), 0, 0);
    return builderTitle.toString();
  }

  private void cutTitle(List<HTMLNode> contents, 
      StringBuilder builder, TextRenderer renderer, String value, int start, int level) {
    
    while(start < value.length()) {
      char c = value.charAt(start);
      if(Character.isLetterOrDigit(c)) break;
      start++;
    }
    
    int idx = value.indexOf('\n', start);
    
    if(idx < 0) {
      Pattern pattern = Pattern.compile("[.!?;]");
      Matcher matcher = pattern.matcher(value);
      if(matcher.find(start)) idx = matcher.start(); 
    }

//  System.out.println("============ >thay co "+ idx+ " : |"
//  + value.charAt(idx)+"|"+value.substring(idx-5, idx+5)+"|"+ " aaaaaaaa");

    if(idx < 1) {
      builder.append(value);
      List<HTMLNode> removes = renderer.getNodePositions(start, value.length());
      removeNodes(contents, removes);
      return;
    }
    
    builder.append(value.substring(start, idx));
    List<HTMLNode> removes = renderer.getNodePositions(start, idx);
    HTMLNode parent = new HTMLParentUtils().getUpParent(removes);
    
    removeNodes(contents, removes);
    if(parent == null) return;

//    System.out.println(" hihihi "+ builder+ " / "+ isSubTitle(builder));
    if(isSubTitle(builder)) {
      cutTitle(contents, builder, renderer, value, idx+1, level+1);
    } else if(isSubTitle(parent)) {
      builder.append(':').append(' ');
      cutTitle(contents, builder, renderer, value, idx+1, level+1);
    }
  }

  private boolean isSubTitle(StringBuilder builder) {
    int i = builder.length() - 1;
    while(i > -1) {
      char c = builder.charAt(i);
      if(Character.isWhitespace(c) 
          && Character.isSpaceChar(c)) {
        i--;
        continue;
      }
      if(c == ':') return true;
      break;
    }

    if(nodeHandler.count(builder) < 1) return true;
    return false;
  }
  
  private boolean isSubTitle(HTMLNode node) {
    if(node == null) return false;
    if(node.isNode(Name.CONTENT) 
             || node.isNode(Name.COMMENT)) {
      node = upParentContentNode(node);
    }
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("class");
    if(attribute != null) {
      String value = attribute.getValue().toLowerCase();
      if( (value.indexOf("sub") > -1 || value.indexOf("super") > -1)   && (
            value.indexOf("title") > -1 || value.indexOf("headline") > -1 )) return true;
    }
    return false;
  }
  
  protected HTMLNode upParentContentNode(HTMLNode node) {
    HTMLNode parent = node.getParent();
    if(parent == null) return node;
    return (isStyleNode(parent)) ? upParentContentNode(parent) : parent;
  }
  
  protected boolean isStyleNode(HTMLNode node) {
    switch(node.getName()) {
      case CONTENT:
      case COMMENT:
      case EM:
      case B : 
      case I:
      case U:
  
      case SUP:
      case SUB:
        
      case INS:
      case DEL:
        return true;
    }
    return false;
  }

}
