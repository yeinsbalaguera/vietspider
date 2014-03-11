/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.List;

import org.vietspider.chars.SpecChar;
import org.vietspider.html.Name;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 29, 2008  
 */
public class HTMLTokenUtils {
  
  public static String buildTextContent(List<NodeImpl> tokens) {
    StringBuilder buildContent = new StringBuilder();
    for(int i = 0; i < tokens.size(); i++) {
      Name name = tokens.get(i).getName();
      char [] value = tokens.get(i).getValue();
      
      if(name == Name.CONTENT) buildContent.append(value);
    }
    return buildContent.toString();
  }
  
  public static String buildContent(List<NodeImpl> tokens) {
    StringBuilder buildContent = new StringBuilder();
    for(int i = 0; i < tokens.size(); i++) {
      buildContent(buildContent, tokens.get(i));
    }
    return buildContent.toString();
  }
  
  public static void buildContent(StringBuilder builder, NodeImpl node){
    builder.append(SpecChar.n);

    Name name = node.getName();
    char [] value = node.getValue();

    if(name == Name.CONTENT || name == Name.COMMENT) {
      builder.append(value);
      return;
    }

    builder.append('<');
    if(node.getType() == TypeToken.CLOSE) builder.append('/');
    builder.append(value);
    builder.append('>');
  }
  
}
