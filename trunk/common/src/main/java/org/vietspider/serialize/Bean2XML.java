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
@Deprecated()
public class Bean2XML  {
  
  private static Bean2XML MAPPER = new Bean2XML();
  
  public final static Bean2XML getInstance() { return MAPPER; } 
  
  
  public Bean2XML() {
  }
  
  @Deprecated()
  public XMLDocument toXMLDocument(Object bean) throws Exception {
    return Object2XML.getInstance().toXMLDocument(bean);
  }
  
  @Deprecated
  public void toXML(Object bean, XMLNode node)  throws Exception  {
    Unknown2XML.getInstance().toXML(bean, node);
//    NodeMap map = bean.getClass().getAnnotation(NodeMap.class);
//    if (map == null) return;
//    toXMLValue(bean.getClass(), map.depth(), bean, node);
  }
  
}
