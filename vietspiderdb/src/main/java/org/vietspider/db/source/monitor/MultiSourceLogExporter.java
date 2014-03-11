/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.source.monitor;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 5, 2009  
 */
public class MultiSourceLogExporter {
  
  private File tmpFile;
  
  public MultiSourceLogExporter() {
    tmpFile = new File(UtilFile.getFolder("content/summary/eid/"), "multi_data.tmp");
    if(tmpFile.exists()) return;
  }
  
  public File export(String[]dateFolders) {
    File file = new File(UtilFile.getFolder("content/summary/eid/"), "multi_data");
    if(!file.exists()) {
      try {
        file.createNewFile();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    if(tmpFile == null) return file;
    
    try {
      if(!tmpFile.exists()) tmpFile.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      exportDb(dateFolders);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      RWData.getInstance().copy(tmpFile, file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    tmpFile.delete();
    return file;
  }
  

  private void exportDb(String[] dateFolders) throws Exception {
    MultiSourceMonitorDatabase mdb  = new MultiSourceMonitorDatabase();
    SourceMonitorDatabases databases = SourceLogHandler.getInstance().getDatabases();
    for(int i = 0; i < dateFolders.length; i++) {
      SourceMonitorDatabase database = databases.getDatabase(dateFolders[i]);
      mdb.convertTo(database);
    }
//    if(database == null) return;
    mdb.exportDb(tmpFile);
    mdb.close();
  }
  
  public void loadData(MenuInfo menuInfo) throws Exception {
    File file = new File(UtilFile.getFolder("content/summary/eid/"), "multi_data");
    String value  = new String(RWData.getInstance().load(file), "utf-8");
    String [] values  = value.split("\n");
    for(int i = 0; i < values.length; i++) {
      String [] elements = values[i].split("/");
      if(elements.length < 5) continue;
      
      String line = elements[0];
      try {
        int visit = Integer.parseInt(elements[1]);
        long link = Integer.parseInt(elements[2]);
        int data = Integer.parseInt(elements[3]);
//        long downloaded = Long.parseLong(elements[4]);
        long laccess = Long.parseLong(elements[4]);
        menuInfo.add(line, visit, data, link,/* downloaded,*/ laccess);
      } catch (Throwable e) {
      }
    }
  }
  
  
  
}
