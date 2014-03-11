/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.serialize;

import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public interface XMLMapper {

  public void toXML(Object bean, XMLNode node) throws Exception ;
  
  public XMLDocument toXMLDocument(Object bean) throws Exception ;
  
}
