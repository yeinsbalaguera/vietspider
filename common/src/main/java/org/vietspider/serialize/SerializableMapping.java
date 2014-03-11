/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import org.vietspider.parser.xml.XMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public interface SerializableMapping<T> {
  
  public T create() ;
  
  public void toField(T object, XMLNode node, String name, String value) throws Exception ;
  
  public XMLNode toNode(T object) throws Exception ;
  
}
