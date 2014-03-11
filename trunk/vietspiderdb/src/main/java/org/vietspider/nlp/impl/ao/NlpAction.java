/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2011  
 */
public class NlpAction {
  
  short type;
  String label;
  int start = -1;
  
  NlpAction(short type, String label) {
    this.type = type;
    this.label = label;
  }

  public short getType() { return type; }
  public String getLabel() { return label;  }

  public int getStart() { return start; }
  public void setStart(int start) { this.start = start; }
  
  public int getEnd() { return start + label.length(); }
}
