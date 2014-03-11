/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.Collection;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2010  
 */
public interface INlpExtractor<T> {
  
  public void extract(String id, Collection<?> values, TextElement element);
  
  public Collection<T> createCollection();
  
  public void closeCollection(Collection<?> collection);
  
  public boolean isExtract(TextElement element);

  public short type() ;
}
