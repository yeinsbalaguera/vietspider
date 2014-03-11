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
public class SourceLogExporter {
  
  private File tmpFile;
  private SourceMonitorDatabase database;
  
  private String dateFolder;
  
  public SourceLogExporter(/*File sFile,*/ SourceMonitorDatabase database, String dateFolder) {
    if(dateFolder == null) return;
    tmpFile = new File(UtilFile.getFolder("content/summary/eid/"), dateFolder +"_data.tmp");
    if(tmpFile.exists()) return;
    
    this.dateFolder = dateFolder; 
    this.database = database;
  }
  
  public File export() {
    if(dateFolder == null) return null;
    
    File dbFolder = UtilFile.getFolder("content/summary/"+dateFolder+"_msdb/");
    long dblastModified = lastModified(dbFolder); 
    File file = new File(UtilFile.getFolder("content/summary/eid/"), dateFolder +"_data");
    if(file.exists() && file.length() > 0) {
      if(Math.abs(dblastModified - file.lastModified()) < 2*60*1000) return file;
    } else {
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
      exportDb();
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
  

  private void exportDb() throws Exception {
    if(database == null) return;
    database.exportDb(tmpFile);
  }
  
  private long lastModified(File folder) {
    long lastModified = System.currentTimeMillis();
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(lastModified < files[i].lastModified()) {
        lastModified = files[i].lastModified();
      }
    }
    return lastModified;
  }
  
  public void loadData(MenuInfo menuInfo) throws Exception {
    File file = new File(UtilFile.getFolder("content/summary/eid/"), dateFolder +"_data");
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
