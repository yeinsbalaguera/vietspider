/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import org.vietspider.model.Groups;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 11, 2008  
 */
public interface ISourceHandler {
  
  public Groups loadGroups() throws Exception;
  
  public void abort() ;
  
  public String [] loadCategories(String group) throws Exception; 
  
  public String [] loadSources(String group, String category) throws Exception;
  
  public void deleteCategories(String group, String [] categories) throws Exception ;
  
  public void deleteSources(String group, String category, String [] sources) throws Exception;

}
