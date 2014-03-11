/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;

import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 21, 2009  
 */
class GCDeleteExpire {
  
  private final static long EXPIRE_FILE = 30*24*60*60*1000l;
  
  public void deleteCrawlerFile() {
    deleteExpireFile(UtilFile.getFolder("track/link/"));
    deleteEmpty(UtilFile.getFolder("sources/sources/"));
  }
  
  private void deleteExpireFile(File file) {
    if(file.isDirectory()) {
      File [] files = file.listFiles();
      if(files == null || files.length < 1) {
        file.delete();
        return;
      }

      for(File f : files) {
        deleteExpireFile(f);
      }
      return;
    }
    long time = System.currentTimeMillis() - file.lastModified();
    if(time > EXPIRE_FILE) file.delete();
  }
  
  private void deleteEmpty(File file) {
    if(file.isDirectory()) {
      File [] files = file.listFiles();
      if(files == null) return;
      for(File f : files) {
        deleteEmpty(f);
      }
      return;
    }
    if(file.length() < 1) file.delete();
  }

}
