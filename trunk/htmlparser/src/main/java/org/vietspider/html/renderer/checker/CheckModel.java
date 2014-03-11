/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */

public class CheckModel {
  
  public final static int  UNCHECK = -1;
  public final static int  NOT = 0;
  public final static int  RIGHT= 1;

  private int raw = UNCHECK;
  private int text = UNCHECK;
  
  private int totalOfLink = 0;
  
  private volatile HTMLNode node;
  private volatile HTMLNode removeNode;
  
  public CheckModel(HTMLNode n) {
    this.node = n;
    this.removeNode = n;
  }

  public int getRawStatus() { return raw; }
  public void setRawData(int status) { this.raw = status; }
  public boolean hasRawData() { return raw == RIGHT; }
  
  public int getTextBlockStatus() { return text; }
  public void setTextBlockStatus(int status) { this.text = status; }
  public boolean hasTextBlock() { return text == RIGHT; }

  public HTMLNode getNode() { return node; }
  public void setNode(HTMLNode node) { this.node = node; }

  public int getTotalOfLink() { return totalOfLink; }
  public void setTotalOfLink(int totalOfLink) { this.totalOfLink = totalOfLink;  }

  public HTMLNode getRemoveNode() { return removeNode; }
  public void setRemoveNode(HTMLNode removeNode) { this.removeNode = removeNode; }

}
