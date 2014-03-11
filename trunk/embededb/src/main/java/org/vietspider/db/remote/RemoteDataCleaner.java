/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.remote;

import java.io.File;
import java.sql.Statement;

import org.vietspider.db.database.DatabaseCleaner;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
@SuppressWarnings("unused")
public class RemoteDataCleaner implements DatabaseCleaner {

  public RemoteDataCleaner() throws Exception {
  }

  public void deleteDomain(String date) throws Exception {
    
  }

  public void deleteArticle(String...ids) throws Exception {
   
  }

  public void deleteArticle(Statement statement, String id) throws Exception {
   
  }

  public void deleteMeta1(String id) throws Exception {
    
  }

  public void executeSQL(String sql) throws Exception {
   
  }

  public void deleteImage(String id) throws Exception {
   
  }

  public void deleteRelation(String id) throws Exception {
  }

  public void deleteContent(String id) throws Exception {
  }

  public void deleteNoConstraintData(String min) {
  }

  @Override
  public void deleteExpireDate(File folder, int expire) {
    
  }
  

}
