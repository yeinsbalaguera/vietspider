/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.Arrays;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public final class NLPRenderer {

  private StringBuilder builder;
  
  public NLPRenderer(HTMLNode root) {
    builder = new StringBuilder();
    if(build(root) && isNewLine()) builder.append('\n');
  }

  public NLPRenderer(StringBuilder builder, HTMLNode root) {
    this.builder = builder;
    if(build(root) && isNewLine()) builder.append('\n');
  }

  public boolean build(HTMLNode node) {
    boolean newLine = false;
    
    if(isSingleNode(node)) return false;
    
    switch (node.getName()) {
    case CONTENT:
      if(builder.length() > 0 
          && Character.isLetterOrDigit(builder.charAt(builder.length() -1))) {
        builder.append(' ');
      }
      char [] chars =  trim(node.getValue());
//      System.out.println(new String(chars) + " : "+ isEmpty(chars));
      if(!isEmpty(chars)) {
        for(int k = 0; k < chars.length; k++) {
          builder.append(chars[k] == '\n' ? ' ' : chars[k]);
        }
        HTMLNode parent = node.getParent();
        if(parent != null && parent.isNode(Name.SPAN)) builder.append(' ');
      } 
      newLine = false;
      break;
    case IMG:
      newLine = false;
      break;
    case H1:
    case H2:
    case H3:
    case H4:
    case H5:
    case H6:
    case TR:
    case TABLE:
    case TD:
    case P:
    case DIV:
    case LI: 
      if(isNewLine()) builder.append('\n');
      newLine = true;
      break;
    case BR:
      if(isNewLine()) builder.append('\n');
      newLine = false;
      break;
    case SCRIPT:
    case STYLE:
      newLine = false;
      break;
    default:
      if(builder.length() > 0) {
        char c = builder.charAt(builder.length()-1);
        if(!(Character.isWhitespace(c) 
            || Character.isSpaceChar(c))) {
          builder.append(' ');
        }
      }
      newLine = false;
      break;
    }
    
    List<HTMLNode> children = node.getChildren();
    if(children == null) return newLine;
    for(int i = 0; i < children.size(); i++) {
      if(build(children.get(i))
          &&  isNewLine()) builder.append('\n');
    }
    
//    if(node.isNode(Name.A)) System.out.println(" == > "+ newLine);
    
    return newLine;
  }

  public StringBuilder getTextValue() { return builder; }

  private boolean isEmpty(char [] chars) {
    int i = 0;
    while(i < chars.length) {
      if(Character.isWhitespace(chars[i]) 
          || Character.isSpaceChar(chars[i])) {
        i++;
        continue;
      }
      return false;
    }
    return true;
  }
  
  private boolean isSingleNode(HTMLNode root) {
    if(root.getName() != Name.A) return false;
    
    if(builder.length() > 0 
        && builder.charAt(builder.length() -1) != '\n') return false;
    
//    return true;
    
    Attribute attr = root.getAttributes().get("href");
    if(attr != null && attr.getValue() != null 
        && attr.getValue().toLowerCase().startsWith("mailto:")) return false;
    
    List<HTMLNode> list = root.getChildren();
    StringBuilder _builder = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).isNode(Name.CONTENT)) {
        _builder.append(list.get(i).getValue());
        continue;
      }
      if(list.get(i).isNode(Name.COMMENT)) continue;
      return false;
    }
    return isAction(_builder.toString().trim().toLowerCase());
  }
  
  private boolean isAction(String text) {
    if(text.indexOf('\n') > -1 
        || text.indexOf("\r\n") > -1) return false;
    int index = text.indexOf("cho thuê");
    if(index > -1) return true;
    
    index = text.indexOf("bán ");
    if(index > -1) return true;
    
    index = text.indexOf("căn hộ");
    if(index > -1) return true;
    
    index = text.indexOf("nhượng ");
    if(index > -1) return true;
    
    index = text.indexOf("cần thuê");
    if(index > -1) return true;
    
    index = text.indexOf("cần mua");
    if(index > -1) return true;
    
    return false;
  }
  
  private boolean isNewLine() {
    if(builder.length() < 3) return false;
    return builder.charAt(builder.length() - 1) != '\n'
      || builder.charAt(builder.length() - 2) != '\n';
  }

  private char[] trim(char [] chars) {
    int i = 0;
    while(i < chars.length) {
      if(Character.isWhitespace(chars[i]) 
          || Character.isSpaceChar(chars[i]) ) {
        i++;
        continue;
      }
      break;
    }
    int start = i;
    
    i = chars.length - 1;
    while(i >= start) {
      if(Character.isWhitespace(chars[i]) 
          || Character.isSpaceChar(chars[i]) ) {
        i--;
        continue;
      }
      break;
    }
    
    return Arrays.copyOfRange(chars, start, i+1);
  }

}
