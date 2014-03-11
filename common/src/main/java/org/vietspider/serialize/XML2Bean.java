/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  
 *    
 **************************************************************************/
package org.vietspider.serialize;

import org.vietspider.common.Application;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 13, 2007
 */
@Deprecated
public class XML2Bean  {

  private static XML2Bean MAPPER = new XML2Bean();

  public final static XML2Bean getInstance() { return MAPPER; } 

  public XML2Bean () {
  }

  @Deprecated
  public <T> T toBean(Class<T> clazz, byte [] bytes) throws Exception {
    return toBean(clazz, new String(bytes, Application.CHARSET).trim());
  }

  @Deprecated
  public <T> T toBean(Class<T> clazz, String value) throws Exception {
    XMLDocument document = XMLParser.createDocument(value, null);
    return toBean(clazz, document);
  }

  @Deprecated
  public <T> T toBean(Class<T> clazz, XMLDocument document) throws Exception {
    return toBean(clazz, document.getRoot());
  }
  
  public <T> T toBean(Class<T> clazz, XMLNode node) throws Exception {
    return XML2Object.getInstance().toObject(clazz, node);
  }
  
  
  @Deprecated
  public <T> T toObject(Class<T> clazz, XMLNode root) throws Exception {
    return XML2Object.getInstance().toObject(clazz, root);
  }
  
}
