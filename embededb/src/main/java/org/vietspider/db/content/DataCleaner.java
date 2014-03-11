/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.sql.Statement;

import org.vietspider.db.database.DatabaseCleaner;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class DataCleaner implements DatabaseCleaner {

  public DataCleaner() throws Exception {
  }

  public void deleteDomain(String date) throws Exception {
    
  }

 /* private void deleteDomain1(String date) throws Exception {

  }

  private void deleteDomain2(String date) throws Exception {
   
  }*/

 /* private void deleteDomain(Statement statement, String date) { 
   
  }*/

  public void deleteArticle(String...ids) throws Exception {
    ArticleDatabases databases = null;
    if(IDatabases.getInstance() instanceof ArticleDatabases) {
      databases = (ArticleDatabases) IDatabases.getInstance();
    }
    if(databases == null) return;
    for(String id : ids) {
      databases.delete(id);
    }
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
  
  public  void deleteExpireDate(File folder, int expire) {
    IDatabases.getInstance().deleteExpireDate(folder, expire);
  }

}
