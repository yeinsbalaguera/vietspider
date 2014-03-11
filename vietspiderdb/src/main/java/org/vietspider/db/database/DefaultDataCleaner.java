/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.io.File;
import java.sql.Statement;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
public class DefaultDataCleaner implements DatabaseCleaner {


  public DefaultDataCleaner() throws Exception {
  }
  
  public void deleteDomain(String date) throws Exception {
  }

 /* public void deleteFilter(String name) throws Exception {
    String sql = createSQL("$name", name, "deleteFilter");
    Statement statement = null;
    Connection connection = JdbcConnection.get();
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      statement.close(); 
    }finally{
      if(statement != null) statement.close();
      JdbcConnection.release(connection);
    }
  }*/

  public void deleteArticle(String...ids) throws Exception {
  }
  
  public void deleteArticle(Statement statement, String id) throws Exception {
  }
  
  public void deleteMeta1(String id) throws Exception {
   
  }

  public void deleteImage(String id) throws Exception {
   
  }

  public void deleteRelation(String id) throws Exception {
   
  }

  public void deleteContent(String id) throws Exception {
    
  }

  public void deleteNoConstraintData(String min) {
  }
  
  
  public void executeSQL(String sql) throws Exception {
    
  }

  @Override
  public void deleteExpireDate(File folder, int expire) {
    
  }
  
  

}
