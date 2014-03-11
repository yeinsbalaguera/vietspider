/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.                *
 *   
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.pool.ThreadPool;

/**
 * Author : Nhu Dinh Thuan Email:nhudinhthuan@yahoo.com Nov 12, 2006
 */
public class GCData implements Runnable {

  // private final static String DELETE_DATA_OF_DATE = "delete.data.of.date";

  private static volatile GCData gcData;

  private volatile int lastDate;

  private GCData(){
    //  new Exception(" init GCDATA " + this.hashCode());
  }

  public static void createService() {
    gcData = new GCData();

    new Thread(gcData).start();
  }

  public static GCData getService() {
    return gcData;
  }

  public void run() {
    while (true) {
      try {
        execute();
        SystemProperties system = SystemProperties.getInstance();
        if (system.isModified()) system.store();
        Thread.sleep(15 * 1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private synchronized void execute() throws Exception {
    File file = UtilFile.getFile("system", "delete.data");
    if (file.exists() && file.length() > 0) {
      String deleteValue = new String(RWData.getInstance().load(file), Application.CHARSET);
      if (deleteValue != null && !deleteValue.trim().isEmpty()) {
        long lastModified = file.lastModified();
        deleteData(deleteValue);
        if (lastModified == file.lastModified()) file.delete();
        return;
      }
    }

    if (CrawlerConfig.CLEAN_DATA_HOUR > -1) {
      Calendar calendar = Calendar.getInstance();
      if (calendar.get(Calendar.HOUR_OF_DAY) != CrawlerConfig.CLEAN_DATA_HOUR) return;
    }

    Calendar calendar = Calendar.getInstance();
    int date = calendar.get(Calendar.DATE);
    if(date == lastDate) return;
    
    lastDate = date;

    LogService.getInstance().setMessage("GCEXECUTOR", null, "Start delete expire data.");

    GCDeleteDatabase deleteService = new GCDeleteDatabase();
    GCDeleteExpire deleteExpire = new GCDeleteExpire();
    GCDeleteResource deleteResource = new GCDeleteResource();
    
    deleteExpire.deleteCrawlerFile();
    deleteResource.deleteExpireFileData();

    String expire = SystemProperties.getInstance().getValue("delete.expire.data");
    if(expire == null || expire.trim().isEmpty() || "true".equalsIgnoreCase(expire)) {
      try {
        deleteService.deleteExpireDatabaseData();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        deleteService.deleteNoConstraintData();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    /*if(DbIndexerService.getInstance() != null) {
      try {
        DbIndexerService.getInstance().optimize();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
*/
    
    SessionStores.getInstance().deleteExpireStore();
    LogService.getInstance().setMessage("GCEXECUTOR", null, "Finish delete expire data.");
  }

  public void deleteDomain(String value) {
    if (value.isEmpty()) return;
    String domain = value.replace('/', '.');

    File file = UtilFile.getFile("system", "delete.data");
    if (!file.exists() || file.length() < 1) {
      try {
        RWData.getInstance().save(file, domain.getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }
    StringBuilder builder = new StringBuilder();
    try {
      builder.append(new String(RWData.getInstance().load(file), Application.CHARSET));
      builder.append(';').append(' ').append(domain);
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private void deleteData(final String value) {
    try {
      CrawlService crawlService = CrawlService.getInstance();
      ThreadPool<String, Link> threadPool = crawlService.getThreadPool();
      boolean running = threadPool != null && !threadPool.isPause();
      if (running) threadPool.setPause(true);
      GCDeleteDatabase deleteDatabase = new GCDeleteDatabase();
      GCDeleteResource deleteResource = new GCDeleteResource();
      String[] elements = value.split(";");

      Vector<String> deleteDates = new Vector<String>();

      for (String date : elements) {
        date = date.replace('.', '/');
        deleteDates.add(date);
      }

      for (int i = 0; i < deleteDates.size(); i++) {
        String domain = deleteDates.get(i);
        if ((domain = domain.trim()).isEmpty()) continue;
        String[] values = domain.split("\\|");
        if (values.length == 1) {
          deleteResource.deleteDataByDate(domain);
        } else if (values.length == 3) {
          deleteDatabase.deleteDataByDomain(values[0], values[1], values[2]);
        } else if (values.length == 4) {
          deleteDatabase.deleteDataByDomain(values[0], values[1], values[2], values[3]);
        }
      }

      if (running) threadPool.setPause(false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  protected File getFile(String fileName, boolean auCreate) throws Exception {
    int idx = fileName.lastIndexOf('/');
    String folder = fileName.substring(0, idx);
    fileName = fileName.substring(idx+1);
    if(auCreate) return UtilFile.getFile(folder, fileName);
    File file = new File(UtilFile.getFolder(folder), fileName);
    return file.exists() ? file : null;
  }

  /*
   * public static void main(String[] args) throws Exception {
   * UtilFile.FOLDER_DATA = "D:\\VietSpider build 9\\data\\";
   * 
   * File folder = UtilFile.getFolder("track/logs"); File [] files =
   * UtilFile.listModifiedFiles(folder, null);
   * //UtilFile.listFiles(folder.getAbsolutePath());
   * System.out.println(folder.getAbsolutePath()); for(File ele : files) {
   * System.out.println(ele); } }
   */
}
