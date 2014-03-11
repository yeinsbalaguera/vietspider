/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.util.List;

import org.vietspider.bean.Meta;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 11, 2007
 */
public interface DatabaseUtils {
  
  public void loadEvent(String date, MetaList list) throws Exception;
  
  public void searchMeta(String pattern, MetaList list) throws Exception ;
  
//  public void loadThread(String metaId, MetaList data) throws Exception ;
  
  public List<Meta> loadTopEvent() throws Exception ;
  
  public void loadTopArticles(int top, MetaList list) throws Exception ;
  
  public void execute(String...sqls) throws Exception ;
}
