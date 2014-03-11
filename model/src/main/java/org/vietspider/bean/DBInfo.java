/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2008  
 */
public interface DBInfo {
  
  public static final int H2 = 0;
  public static final int MY_SQL = 1;
  public static final int MS_SQL_SERVER = 2;
//  public static final int MS_SQL_SERVER_2005 = 3;
  public static final int ORACLE = 3;
  public static final int POST_GRES = 4;
  public static final int APACHE_DERBY = 5;
  public static final int HSQL = 6;
  public static final int MS_SQL_SERVER_EXPRESS = 7;
  
  public int getType() ;
  public void setType(int type) ;
  
  public String getHost() ;
  public void setHost(String host) ;
  
  public String getPort() ;
  public void setPort(String port) ;
  
  public String getDatabase() ;
  public void setDatabase(String database) ;
  
  public String getUsername() ;
  public void setUsername(String username) ;
  
  public String getPassword() ;
  public void setPassword(String password) ;
}
