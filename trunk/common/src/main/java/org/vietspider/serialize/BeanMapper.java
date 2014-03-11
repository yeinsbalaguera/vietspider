/***************************************************************************
 * Copyright 2001-2007 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.serialize;

import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 21, 2007  
 */
public interface BeanMapper {

  public <T> T toObject(Class<T> clazz, XMLDocument document) throws Exception;
  
  public <T> T toObject(Class<T> clazz, XMLNode node) throws Exception ;
  
  public <T> void toObject(Class<T> clazz, T object, XMLNode node) throws Exception ;
}
