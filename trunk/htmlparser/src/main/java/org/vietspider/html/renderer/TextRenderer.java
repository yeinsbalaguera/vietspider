/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public final class TextRenderer {

  public final static int RENDERER = 1;
  public final static int HANDLER = 0;

  private List<NodePosition> positions = new ArrayList<NodePosition>();

  private StringBuilder builder = new StringBuilder();

  public TextRenderer (HTMLNode root, List<HTMLNode> contents) {
    this(root, contents, HANDLER, true);
  }

  public TextRenderer (HTMLNode root, int type) {
    this(root, null, type, false);
  }

  public TextRenderer (HTMLNode root, 
      final List<HTMLNode> contents, final int type, final boolean constain) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      switch (node.getName()) {
      case CONTENT:
        if(builder.length() > 0 
            /*&& Character.isLetterOrDigit(builder.charAt(builder.length() -1))*/) {
          builder.append(' ');
        }
        char [] chars = node.getValue();
//        System.out.println("==== > "+ new String(chars));
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
        break;
      case IMG:
        positions.add(new NodePosition(node, -1, -1));
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
          if(type == RENDERER) builder.append('\n'); 
        }
        break;
      case SCRIPT:
      case STYLE:
        NodeImpl nodeImpl = (NodeImpl) node;
        if(nodeImpl.getType() == TypeToken.TAG && iterator.hasNext()) iterator.next();
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
  }

  private boolean isValid(List<HTMLNode> list, HTMLNode node, boolean constain) {
    if(list == null) return true;
    boolean listConstain = list.contains(node);
    return constain ? listConstain : !listConstain; 
  }

  public StringBuilder getTextValue() { return builder; }

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
