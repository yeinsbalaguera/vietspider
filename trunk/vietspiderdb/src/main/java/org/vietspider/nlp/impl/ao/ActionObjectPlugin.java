/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.ArrayList;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public interface ActionObjectPlugin {
  
  final static short TOP  = 1;
  final static short COMMON  = 0;
  final static short BOTTOM  = -1;
  
  final static short _BREAK  = 1;
  final static short _CONTINUE  = 0;
  
  short type();

  short  process(TextElement element, ArrayList<ActionObject> list);
  
}
