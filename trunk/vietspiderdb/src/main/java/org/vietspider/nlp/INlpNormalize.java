/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.Collection;
import java.util.HashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2011  
 */
public interface INlpNormalize {

  public void normalize(String id, HashMap<Short, Collection<?>> map)  ;
  
}
