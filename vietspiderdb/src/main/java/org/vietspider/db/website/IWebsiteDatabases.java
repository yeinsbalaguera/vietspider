/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.website;

import java.util.List;

import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
public interface IWebsiteDatabases {
  
  public Website search(String host) ;
  
  public void load(Websites websites);
  
  public void saveWebsites(List<Website> websites);
  
  public void save(Website website);
  
  public void save(String..._urls) ;
  
  public void save(List<String> _urls);
  
  public void setSleep(long sleep);
}
