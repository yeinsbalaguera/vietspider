/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 10, 2009  
 */
public class NodePosition {
  
  private int start = 0;
  private int end = 0;
  private HTMLNode node;

  public NodePosition(HTMLNode node, int start, int end) {
    this.node = node;
    this.start = start;
    this.end = end;

  }

  public int getStart() { return start; }

  public int getEnd() { return end; }

  public HTMLNode getNode() { return node; }
  
  public NodePosition clone(int index) {
    return new NodePosition(node, start - index, end - index);
  }

}

