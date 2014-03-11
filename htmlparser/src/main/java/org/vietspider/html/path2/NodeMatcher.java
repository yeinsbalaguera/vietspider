/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 24, 2007  
 */
public class NodeMatcher {
  
  private final static char C_EXCL = '!';
  private final static char C_EQUA = '=';
  private final static char C_GREA = '>';
  private final static char C_LOW = '<';
  private final static char C_IDX = 'i';
  private final static char C_AND = '&';
  private final static char C_OR = '|';
  
//  private static ThreadSoftRef<ExpComputor> EXP_COMPUTOR = new ThreadSoftRef<ExpComputor>(ExpComputor.class);
//  
//  private static ThreadSoftRef<NodeMatcher> nodeMatcher = new ThreadSoftRef<NodeMatcher>(NodeMatcher.class);
  
//  public static NodeMatcher getInstance() { return nodeMatcher.getRef(); }
  
  public boolean match(String pattern, int nodeIndex) { 
    return match(pattern, nodeIndex, 0, -1); 
  }
  
  public boolean contains(Attributes attributes, Attribute [] attrs) {
    for(Attribute attr : attrs) {
      Attribute attr1 = attributes.get(attr.getName());
      if(attr1 == null || !attr.getValue().equalsIgnoreCase(attr1.getValue())) return false;
    }
    return true;
  }
  
  private boolean match(String pattern, int nodeIndex, int start, int logic) {
    int value1 = -1;
    int value2 = 0;
    
    int type = -5;
    
    int i = start;
    
    StringBuilder builder = new StringBuilder();
    ExpComputor computor = new ExpComputor();
    while(i < pattern.length()) {
      char c = pattern.charAt(i);
      if(c == C_EXCL) {
        type = -2;
      } else if(c == C_EQUA) {
        if(type == -2) {
          type = -1;
        } else if(type == 1)  {
          type = 2;  
        } else if(type == 3) {
          type = 4;
        } else {
          type = 0;
        }
      } else if(c == C_GREA) {
        type = 1; 
      } else if(c == C_LOW) {
        type = 3;
      } else if(Character.isDigit(c) || computor.isCharOperator(c)) {
        builder.append(c);
      } else if (c == C_IDX) {
        builder.append(nodeIndex);
      } else if (c == C_AND) {
        logic = 1;
        break;
      } else if (c == C_OR) {
        logic = 0;
        break;
      }
      if(type > -2 && value1 < 0) {
        value1 = computor.compute(builder);
        builder.setLength(0);
      }
      i++;
    }
    
    if(value1 < 0) {
      if(builder.length() < 1 || builder.charAt(0) == ExpComputor.C_ANY) return true;
      try {
        return Integer.parseInt(builder.toString()) == nodeIndex;
      }catch (Exception e) {
        return false;
      }
    }
    value2 = computor.compute(builder);
    
    boolean value = compare(value1, value2, type);
    if(logic < 0) return value;
    if(logic == 0) { 
      if(value) return true;
      return match(pattern, nodeIndex, i+1, -1);
    } 
    return value & match(pattern, nodeIndex, i+1, -1);
  }
  
  private boolean compare(int value1, int value2, int type) {
    switch(type) {
    case -1: return value1 != value2;
    case 0: return value1 == value2;
    case 1: return value1 > value2;
    case 2: return value1 >= value2;
    case 3: return value1 < value2;
    case 4: return value1 <= value2;
    }
    return false;
  }
}
