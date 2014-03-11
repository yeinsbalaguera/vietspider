/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.action;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 16, 2003
 */
public class ExpandSelectionEvent {
  
  public final static int /*DELETE_MODE = 1,*/ VIEW_MODE = 0;
  private int idxGroup ;
  private int idxElement ;
  private String group;
  private String element;
//  private int mode = VIEW_MODE;
  
  public ExpandSelectionEvent(int idxGroup, int idxElement, String grp, String ele) {
    this.idxGroup = idxGroup;
    this.idxElement = idxElement;
    this.group = grp;
    this.element = ele;
  }
  
  public int getIdxElement(){ return idxElement; }

  public String getElement() { return element; }

  public String getGroup() { return group; }

  public int getIdxGroup() { return idxGroup; }

//  public void setMode(int mode) { this.mode = mode; }
//  public int getMode() { return mode; }
}
