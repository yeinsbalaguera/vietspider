/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public final class TextRenderer2 {

  public final static int RENDERER = 1;
  public final static int HANDLER = 0;

  private List<NodePosition> positions = new ArrayList<NodePosition>();

  private StringBuilder builder = new StringBuilder();

  public TextRenderer2 (HTMLNode root, List<HTMLNode> contents) {
    this(root, contents, HANDLER, true);
  }

  public TextRenderer2(HTMLNode root, int type) {
    this(root, null, type, false);
  }

  public TextRenderer2(HTMLNode root, List<HTMLNode> contents, int type, boolean constain) {
    if(build(root, contents, type, constain)) builder.append('\n');
  }

  public boolean build(HTMLNode node, List<HTMLNode> contents, int type, boolean constain) {
    boolean newLine = false;
    switch (node.getName()) {
    case CONTENT:
      if(builder.length() > 0 
          && Character.isLetterOrDigit(builder.charAt(builder.length() -1))) {
        builder.append(' ');
      }
      char [] chars = node.getValue();
      if(!isEmpty(chars)) {
        if(isValid(contents, node, constain)) {
          int start = builder.length();
          for(int k = 0; k < chars.length; k++) {
            builder.append(chars[k] == '\n' ? ' ' : chars[k]);
          }
          HTMLNode parent = node.getParent();
          if(parent != null && parent.isNode(Name.SPAN)) builder.append(' ');

          int end = builder.length();
          positions.add(new NodePosition(node, start, end));
        }
      } 
      newLine = false;
      break;
    case IMG:
      positions.add(new NodePosition(node, -1, -1));
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
      builder.append('\n');
      newLine = true;
      break;
    case BR:
      builder.append('\n');
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
      if(build(children.get(i), contents, type, constain)) {
        builder.append('\n');
      }
    }
    
    return newLine;
  }

  private boolean isValid(List<HTMLNode> list, HTMLNode node, boolean constain) {
    if(list == null) return true;
    boolean listConstain = list.contains(node);
    return constain ? listConstain : !listConstain; 
  }

  public StringBuilder getTextValue() { return builder; }

/*  private final boolean isEndWithNewLine(StringBuilder value) {
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
  }*/

  public List<HTMLNode> getNodePositions(int start, int end) {
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();
    for(int i = 0; i < positions.size(); i++) {
      NodePosition np = positions.get(i);
      if(np.getStart() < 0 
          || np.getStart() < start) continue;
      if(np.getEnd() > end + 1) break;
      nodes.add(np.getNode());
    }
    return nodes;
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

  public List<NodePosition> getPositions() { return positions; }

  public void setPositions(List<NodePosition> positions) { this.positions = positions; }

}
