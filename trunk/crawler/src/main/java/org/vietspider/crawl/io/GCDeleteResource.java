/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Oct 20, 2007
 */
class GCDeleteResource {

  /*
   * public void deleteExpireFileData() { String [] folders = {"track/history/",
   * "track/logs/", "client/logs/", "content/summary/"}; for(String ele :
   * folders) { delele(UtilFile.getFolder(ele), CrawlerConfig.EXPIRE_DATE); }
   * File file = UtilFile.getFolder("content/images/"); delele(file,
   * CrawlerConfig.EXPIRE_DATE); file =
   * UtilFile.getFolder("content/summary/eid"); delele(file,
   * CrawlerConfig.EXPIRE_DATE); file = UtilFile.getFolder("content/indexed/");
   * File [] files = file.listFiles(); for(File ele : files) { delele(ele,
   * CrawlerConfig.EXPIRE_DATE); } file =
   * UtilFile.getFolder("track/downloaded/"); files = file.listFiles(); for(File
   * ele : files) { delele(ele, CrawlerConfig.EXPIRE_DATE); } file =
   * UtilFile.getFolder("track/url/"); delele(file, CrawlerConfig.EXPIRE_DATE);
   * file = UtilFile.getFolder("track/user/"); delele(file,
   * CrawlerConfig.EXPIRE_DATE); files = file.listFiles(); for(int i = 0; i <
   * files.length; i++) { if(files[i].isFile()) files[i].delete(); } }
   */

  /*
   * public void deleteCrawlerFile() {
   * deleteExpireFile(UtilFile.getFolder("track/link/"));
   * deleteEmpty(UtilFile.getFolder("sources/sources/")); }
   */

  /*
   * private void delele(File folder, int expire){ File [] files =
   * UtilFile.listFiles(folder); try { delete(files, expire); } catch(Exception
   * exp){ LogService.getInstance().setThrowable(exp); } }
   */

  void deleteFolder(File file, int expire) {
    if (!file.isDirectory()) return;
    //  System.out.println(" ===== ==========> "+ file.getName());

    File[] files = UtilFile.listFiles(file, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isDirectory())
          return false;
        return validate(f.getName());
      }
    });

    if(files != null) {
      for (int i = expire; i < files.length; i++) {
        //    System.out.println("delete folder " + files[i].getName());
        UtilFile.deleteFolder(files[i]);
      }
    }

    files = UtilFile.listFiles(file, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isFile())
          return false;
        return validate(f.getName());
      }
    });

    if(files != null) {
      for (int i = expire; i < files.length; i++) {
        //    System.out.println("delte file " + files[i].getName());
        files[i].delete();
      }
    }

    files = UtilFile.listFiles(file, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isDirectory())
          return false;
        return !validate(f.getName());
      }
    });

    if(files != null) {
      for (int i = 0; i < files.length; i++) {
        deleteFolder(files[i], expire);
      }
    }

    files = UtilFile.listFiles(file, null);
    if (files == null || files.length < 1)
      file.delete();
  }

  void deleteExpireFileData() {
    deleteTrackArticle();
    deleteDownloaded();
    
    int expire = CrawlerConfig.EXPIRE_DATE;

    String[] folders = { "track/", "client/logs/", 
        "content/summary/", "content/images/", "content/indexed/" };
    for (String folder : folders) {
      new GCDeleteResource().deleteFolder(UtilFile.getFolder(folder), expire);
    }
    
//    File folder = UtilFile.getFolder("content/cindexed/");
//    TopicTrackingServices tpdatabases = TopicTrackingServices.getInstance();
    
//    if(tpdatabases != null 
//        && tpdatabases.getDbIndexers() != null) {
//      tpdatabases.getDbIndexers().deleteExpireDate(folder, expire);
//    } else {
//    DbIndexers dbIndexers = DbIndexers.getInstance();
//    if(dbIndexers != null) dbIndexers.deleteExpireDate(folder, expire);
//    }
    
//    File folder = UtilFile.getFolder("content/tp/");
//    File files [] = folder.listFiles();
//    for(int i = 0; i < files.length; i++) {
//      File file  = new File(files[i], "indexed/");
//      
//      if(file.exists() && file.isDirectory()) {
//        ContentIndexers.getInstance().getDbIndexers().deleteExpireDate(file, expire);
//      }
//      
////      file  = new File(files[i], "database3/");
////      if(file.exists() && file.isDirectory()) {
////        if(tpdatabases != null) {
////          tpdatabases.deleteExpireDate1(file, expire);
////        }
////      }
//    }
    
    File folder  = new File(UtilFile.getFolder("content"), "database/");
    if(folder.exists() && folder.isDirectory()) {
      DatabaseService.getDelete().deleteExpireDate(folder, expire);
//      IDatabases.getInstance().deleteExpireDate(folder, expire);
    }
    
//    folder = UtilFile.getFolder("client/history/");
//    if(dbIndexers != null) dbIndexers.deleteExpireDate(folder, expire);
  }

  private void deleteTrackArticle() {
    File folder = UtilFile.getFolder("track/user/");
    File [] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile();
      }
    });
    for (File file : files) {
      file.delete();
    }
  }

  private void deleteDownloaded() {
    //will remove from build 20
    File downloadedFolder = UtilFile.getFolder("track/downloaded/");

    File folder = UtilFile.getFolder("track/url/");
    File [] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });
    if(files != null && files.length > 0){ 
      files = UtilFile.listFiles(files[0], null);
      if(files.length >= CrawlerConfig.EXPIRE_DATE) {
        UtilFile.deleteFolder(downloadedFolder);
      }
    }

    folder = UtilFile.getFolder("track/title/");
    files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });
    if(files != null && files.length > 0){ 
      files = UtilFile.listFiles(files[0], null);
      if(files.length >= CrawlerConfig.EXPIRE_DATE) {
        UtilFile.deleteFolder(downloadedFolder);
      }
    }
    //end function
  }

  private boolean validate(String name) {
    if (name.length() < 10)
      return false;
    String date = name.substring(0, 10);
    try {
      CalendarUtils.getFolderFormat().parse(date);
      return true;
    } catch (Exception e) {
    }
    return false;
  }

  void deleteDataByDate(String date) throws Exception {
    Date dateValue = null;
    String dateFolder = null;
    try {
      dateValue = CalendarUtils.getDateFormat().parse(date);
      dateFolder = CalendarUtils.getFolderFormat().format(dateValue);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("GC", e);
      return;
    }
    if (dateValue == null) return;

    try {
      // delete database
      DatabaseService.getDelete().deleteDomain(date);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }
    // delete data indexed for search
    /*if (DbIndexerService.getInstance() != null) {
      try {
        DbIndexerService.getInstance().deleleIndexedByDate(date);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("GC", e);
      }
    }*/

    try {
      File file = UtilFile.getFolder("content/images/");
      UtilFile.deleteFolder(new File(file, dateFolder));
      DatabaseService.getDelete().deleteDomain(date);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }

    try {
      SimpleDateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");
      String idDate = idDateFormat.format(dateValue);
      LogService.getInstance().setMessage("GC", null, "delete from image where id like '"+idDate+"%'");
      DatabaseService.getDelete().executeSQL("delete from image where id like '"+idDate+"%'");
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }

    // delete mining indexed
    File file = UtilFile.getFolder("content/indexed/");
    File[] files = file.listFiles();
    for (File ele : files) {
      try {
        UtilFile.deleteFolder(new File(ele, dateFolder));
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("GC", e);
      }
    }

    try {
      UtilFile.getFile("content/summary/", dateFolder).delete();
      EIDFolder2.delete(dateFolder);
      
//      IDTracker.getInstance().delete(dateFolder);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }

    try {
      UtilFile.deleteFolder(UtilFile.getFolder("content/summary/" + dateFolder + "_mdb/"));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }
    
    try {
      UtilFile.deleteFolder(UtilFile.getFolder("track/logs/sources/" + dateFolder));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("GC", e);
    }
  }
  
}
