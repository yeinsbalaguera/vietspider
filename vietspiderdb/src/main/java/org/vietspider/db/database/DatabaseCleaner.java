/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.io.File;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public interface DatabaseCleaner {

  public void deleteDomain(String date) throws Exception;
  
  public void deleteArticle(String...ids) throws Exception;
  
  public void deleteMeta1(String id) throws Exception;
  
  public void deleteImage(String id) throws Exception;
  
  public void deleteRelation(String id) throws Exception;
  
  public void deleteContent(String id) throws Exception;
  
  public void deleteNoConstraintData(String min);
  
  public void executeSQL(String sql) throws Exception;
  
  public void deleteExpireDate(File folder, int expire) ;
  
  
}
