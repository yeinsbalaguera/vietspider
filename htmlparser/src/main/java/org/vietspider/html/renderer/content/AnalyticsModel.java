/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.content;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class AnalyticsModel {

  private HTMLNode node;
  private int score = 0;
  private AnalyticsRenderer renderer;
  
  public AnalyticsModel(HTMLNode n, int score) {
    this.node = n;
    this.score = score;
  }

  public HTMLNode getNode() { return node; }
  public void setNode(HTMLNode node) { this.node = node; }

  public int getScore() { return score; }
  public void setScore(int score) { this.score = score; }

  public AnalyticsRenderer getRenderer() { return renderer; }
  public void setRenderer(AnalyticsRenderer renderer) { this.renderer = renderer; }
}
