/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class UINodeRenderer {
  
  private int vertical;
  private int horizontal;
  private HTMLNode node;
  
  private List<UINodeRenderer> children = new ArrayList<UINodeRenderer>();
  
  public UINodeRenderer(HTMLNode node, int ver, int hor) {
    this.node = node;
  }
}
