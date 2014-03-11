/**
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2007, 8:58 PM
 */
package org.vietspider.parser.rss2;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 21, 2006  
 */
public interface IMetaChannel extends IMetaItem {
  
  public void setGenerator(String generator);  
  
  public String getGenerator();

}
