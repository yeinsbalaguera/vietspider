/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.url;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.vietspider.common.io.UtilFile;
import org.vietspider.db.url.UrlDatabase;
import org.vietspider.db.url.UrlDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 27, 2009  
 */
public class TestUrlImportExportDatabase {
  
  private static File dbFile  = new File("D:\\Temp\\url\\");
  
  public static void importUrl(File file) throws Exception  {
    UtilFile.deleteFolder(dbFile, false);
    String name  = file.getName();
    if(name.endsWith(".txt")) name  = name.substring(0, name.length()-4);
    FileInputStream stream = new FileInputStream(file);
    UrlDatabases.getInstance().importStream(stream, dbFile.getAbsolutePath());
    stream.close();
  }
  
  public static void exportUrl(File file, String name) throws Throwable  {
    file.delete();
    file.createNewFile();
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    UrlDatabase db = UrlDatabases.getInstance().getDatabase(dbFile.getAbsolutePath());
    db.export(fileOutputStream);
  }
  
  public static void main(String[] args) throws Throwable {
    File file = new File("F:\\Projects\\bak\\360YahooPlus.txt");
//    importUrl(file);
//    System.out.println("import successfull");
    
//    TestUrlDatabase.searchPage(20000);
    
    file  = new File("D:\\Temp\\url_output.txt");
    exportUrl(file, "360YahooPlus");
    
    System.exit(0);
  }
}
