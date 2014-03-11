/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

import java.util.List;

import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2009  
 */
public class TokenTextRenderer {
  
  String buildTextValue(List<NodeImpl> tokens) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0;  i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      switch (node.getName()) {
      case CONTENT:
        char [] chars = node.getValue();
        if(!isEmpty(chars)) {
          builder.append(' ').append(chars);
        } 
        break;
      case IMG:
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
      case BR:
      case LI:        
        if(!isEndWithNewLine(builder)) {
          builder.append('\n');
        }
        break;
      case SCRIPT:
      case STYLE:
        if(node.getType() == TypeToken.TAG) i++;
        break;
      default:
        if(builder.length() > 0) {
          char c = builder.charAt(builder.length()-1);
          if(!(Character.isWhitespace(c) 
              || Character.isSpaceChar(c))) {
            builder.append(' ');
          }
        }
      break;
      }
    }
    return builder.toString();
  }
  
  private final boolean isEmpty(char [] chars) {
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
  
  private final boolean isEndWithNewLine(StringBuilder value) {
    int i = value.length()-1;
    while(i > -1) {
      char c = value.charAt(i);
      if(c == '\n') {
        return true;
      } else if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        i--;
        continue;
      } 
      return false;
    }
    return true;
  }
}
