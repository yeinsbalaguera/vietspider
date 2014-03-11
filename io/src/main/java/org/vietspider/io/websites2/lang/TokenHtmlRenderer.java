/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

import java.util.List;

import org.vietspider.chars.SpecChar;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2009  
 */
public class TokenHtmlRenderer {
  
  public String buildHtml(List<NodeImpl> tokens) {
    StringBuilder builder = new StringBuilder();
    RefsDecoder decoder = new RefsDecoder();
    for(int i = 0; i < tokens.size(); i++) {
      buildHTMLNode(decoder, builder, tokens.get(i));
    }
    return builder.toString();
  }
  
  private void buildHTMLNode(RefsDecoder decoder, StringBuilder builder, NodeImpl node){
//  if(value.length < 1) return builder;
    if(builder.length() > 0) builder.append(SpecChar.n);
//    Name name  = node.getName();
    int type  = node.getType();
    boolean isTag = node.isTag();
    
    if(node.isNode(Name.META)) changeEncoding(node);
    
//    System.out.println(node.getName() + " - "  + isTag);
    
    if(isTag || node.getName() == Name.UNKNOWN) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    if(node.isNode(Name.CONTENT)) {
      builder.append(decoder.decode(node.getValue()));
    } else {
      builder.append(node.getValue());
    }
    if(isTag || node.getName() == Name.UNKNOWN) builder.append('>');
//    if(type == TypeToken.CLOSE || node.getConfig().hidden())  return;
//    
//    if(node.getConfig().end() != Tag.FORBIDDEN){
//      builder.append(SpecChar.n);
//      builder.append('<').append('/').append(name).append('>');
//    }
  }
  
  private void changeEncoding(NodeImpl node) {
    Attributes attributes = node.getAttributes();
    Attribute attribute = attributes.get("http-equiv");
    if(attribute == null || attribute.getValue() == null) return;

    if(!"content-type".equalsIgnoreCase(attribute.getValue().trim())) return;

    attribute = attributes.get("content");
    if(attribute == null) return;
    attribute.setValue("text/html; charset=utf-8");
    attributes.set(attribute);
  }
  
}
