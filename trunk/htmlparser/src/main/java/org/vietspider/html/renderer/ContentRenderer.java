/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.renderer.checker.LinkNodeChecker;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 30, 2008  
 */
public final class ContentRenderer {

  private List<NodePosition> positions = new ArrayList<NodePosition>();

  private String text = "";
  private LinkNodeChecker linkNodeChecker;

  public ContentRenderer (HTMLNode root, 
      List<HTMLNode> ignores, LinkNodeChecker linkNodeChecker) {
    this.linkNodeChecker = linkNodeChecker;
    StringBuilder builder = new StringBuilder();
    build(builder, root, ignores);
    text = builder.toString();
  }

  private void build(StringBuilder builder, HTMLNode node, List<HTMLNode> ignores) {
    if(node == null) return;
    switch (node.getName()) {
    case CONTENT:
      char [] chars = node.getValue();
      if(!isEmpty(chars)) {
//      if(isValid(contents, node, constain)) {
        int start = builder.length();
        for(int k = 0; k < chars.length; k++) {
          builder.append(chars[k] == '\n' ? ' ' : chars[k]);
        }
        HTMLNode parent = node.getParent();
        if(parent != null && parent.isNode(Name.SPAN)) builder.append(' ');

        int end = builder.length();
        positions.add(new NodePosition(node, start, end));
      }
//    } 
      break;
    case OBJECT:
    case IMG:
      builder.append(new String(node.getValue()));
      int position = builder.length()-1;
      positions.add(new NodePosition(node, position, position));
      break;
    case P:
    case LI:        
      if(!isEndWithNewLine(builder)) {
        builder.append('\n');
      }
      break;
    case H1:
    case H2:
    case H3:
    case H4:
    case H5:
    case H6:
      separateBlock(builder, 1);
      if(!isEndWithNewLine(builder)) {
        builder.append('\n');
      }
      break;
//    case A:
    case BR:
      separateBlock(builder, 1);
      break;
//    case INPUT:
//    separateBlock(builder, 2);
//    break;
    case IFRAME:
      //builder.append("IFRAME HERE");
      separateBlock(builder, 10);
      break;
    case FORM:
    case TEXTAREA:
      separateBlock(builder, 2);
      break;
    case UL:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    case DIV:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    case TABLE:
      separateBlock(builder, node, 4/*, wrappers*/);
      break;
    case TR:
      separateBlock(builder, node, 3/*, wrappers*/);
      break;
//    case TD:
//      separateBlock(builder, node, 2/*, wrappers*/);
//      break;
    case SCRIPT:
    case STYLE:
      return;
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

    if(!ignores.contains(node)) {
      List<HTMLNode> children  = node.getChildren();
      if(children == null) return;
      for(int i = 0; i < children.size(); i++) {
        build(builder, children.get(i), ignores);
      }
    } 

    switch (node.getName()) {
    case IFRAME:
      separateBlock(builder, 2);
      break;
    case UL:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    case DIV:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    case TABLE:
      separateBlock(builder, node, 4/*, wrappers*/);
      break;
    case TR:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    case TD:
      separateBlock(builder, node, 2/*, wrappers*/);
      break;
    default:     
      break;
    }
  }

  public String getTextValue() { return text; }

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
//      if(np.getNode().isNode(Name.IMG)) {
//        System.out.println("===> "+ new String(np.getNode().getValue()));
//        System.out.println("=== > "+ np.getStart()+ " : "+ np.getEnd());
//        System.out.println("xxxx > "+ start+ " : " + end);
//      }
      if(np.getStart() < start) continue;
      if(np.getEnd() > end + 1) break;
      nodes.add(np.getNode());
    }
    return nodes;
  }

  /* public List<NodePosition> getPositionCollecton(int start, int end) {
    List<NodePosition> nodes = new ArrayList<NodePosition>();
    for(int i = 0; i < positions.size(); i++) {
      NodePosition np = positions.get(i);
      if(np.getStart() < 0 
          || np.getStart() < start) continue;
      if(np.getEnd() > end + 1) break;
      nodes.add(np.clone(start));
    }
    return nodes;
  }*/

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

  private void separateBlock(StringBuilder builder, 
      HTMLNode node, int time/*, List<HTMLNode> wrappers*/) {
    if(!isWrapperContent(node) /*|| wrappers.contains(node)*/) return;
    separateBlock(builder, time);
  }

  private void separateBlock(StringBuilder builder, int time) {
    int index = builder.length() - 1;
    while(index > -1) {
      char c = builder.charAt(index);
      if(Character.isSpaceChar(c) 
          || Character.isWhitespace(c)) { 
        index--;
      } else {
        break;
      }
    }
    StringBuilder pattern = new StringBuilder();
    for(int i  = 0; i < time; i++) {
      pattern.append('\\');
    }
    builder.insert(index + 1, pattern.toString());
  }

  private boolean isWrapperContent(HTMLNode node){
    List<HTMLNode> children = node.getChildren();
    if(children == null) return false;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode child = children.get(i);
      if(child.isNode(Name.CONTENT) 
          || isWrapperContent(child)) return true;
    }
    return false;
  }

  public LinkNodeChecker getLinkChecker() { return linkNodeChecker; }

}
