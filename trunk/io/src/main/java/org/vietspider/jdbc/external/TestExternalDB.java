/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.jdbc.external;

import java.io.File;

//import org.vietspider.bean.Article;
//import org.vietspider.db.database.DatabaseService;
//import org.vietspider.jdbc.external.ExternalDatabase;

/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 17, 2012
 */
public class TestExternalDB {
  public static void main(String[] args) throws Exception {
    //201201172107260016
    //201201172107220012
    //201201172107220003

    File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vsnews\\data");

    //  System.out.println(file.getCanonicalPath());
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
//    Article article = DatabaseService.getLoader().loadArticle("201201172107260016");
//    System.out.println(article);
//    ExternalDatabase externalConnection = new ExternalDatabase();
//    externalConnection.saveArticle(article);
//    System.exit(0);
    
//    Class.forName("net.sourceforge.jtds.jdbc.Driver");
//    String url = "jdbc:jtds:sqlserver://localhost:50201/vietspider;integratedSecurity=true;";
//    Connection connection = DriverManager.getConnection(url, "sa", "123");
//    
  }
}
