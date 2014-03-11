/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 3, 2008  
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
/**
  * Microsoft SQL Server JDBC test program
  */
public class TestSQLServer {
  
  public TestSQLServer() throws Exception {
    // Get connection
    Class.forName("net.sourceforge.jtds.jdbc.Driver");
    Connection connection = DriverManager.getConnection(
        "jdbc:jtds:sqlserver://192.168.1.103:1433/vietspiderdb;integratedSecurity=true;", "sa", "123");
    if (connection != null) {
      System.out.println();
      System.out.println("Successfully connected");
      System.out.println();
      // Meta data
      DatabaseMetaData meta = connection.getMetaData();
      System.out.println("\nDriver Information");
      System.out.println("Driver Name: "
          + meta.getDriverName());
      System.out.println("Driver Version: "
          + meta.getDriverVersion());
      System.out.println("\nDatabase Information ");
      System.out.println("Database Name: "
          + meta.getDatabaseProductName());
      System.out.println("Database Version: "+
          meta.getDatabaseProductVersion());
    }
  } // Test
  public static void main (String args[]) throws Exception {
    TestSQLServer test = new TestSQLServer();
  }
}