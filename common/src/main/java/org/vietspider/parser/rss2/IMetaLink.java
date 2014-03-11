/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */
public interface IMetaLink {
  
  public String getHref();
  public void setHref(String value);
  
  public String getType();
  public void setType(String value);
  
  public String getRel();
  public void setRel(String value);

}
