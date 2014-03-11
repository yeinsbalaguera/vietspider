/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.paging.MetaIdEntry;
import org.vietspider.paging.PageIOs;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 19, 2011  
 */
public class DeleteDatabase {
  
  private Set<Long> idCollection = new HashSet<Long>();
  
  private File folder;
  private long lastModified = -1;
  private int update = 0;
//  private boolean execute = true;
  private String fileName;
  
  private SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd");

  public DeleteDatabase(File folder, String fileName) {
    this.folder = folder;
    this.fileName = fileName;
    
    File file = new File(folder, fileName);
    try {
      if(!file.exists()) file.createNewFile();
    } catch (Exception e) {
      LogService.getInstance().setMessage(this, e, file.getAbsolutePath());
    }
    
    monitor();
  }
  
  public void monitor() {
    //load
    File file = new File(folder, fileName);
    if(!file.exists()) return;
    
    if(update >= 1) saveFile();
    
    if(lastModified != file.lastModified()) {
      try {
        loadFile(file);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  public boolean isDelete(String id) {
    if((id = id.trim()).length() < 1) return false;
    try {
      return isDelete(Long.parseLong(id));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    return false;
  }
  
  public boolean isDelete(long id) {
    return idCollection.contains(id);
  }
  
  public void add(Domain domain, String id) {
    try {
      updatePageIO(domain, id);
      idCollection.add(Long.parseLong(id));
      update++;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
 /* public void run() {
    File file = new File(folder, fileName);
    try {
      if(!file.exists()) file.createNewFile();
      load(file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    while(execute) {
      try {
        if(lastModified != file.lastModified()) {
          load(file);
        }
        if(update > 10)  save();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      try {
        Thread.sleep(30*1000);
      } catch (Exception e) {
      }
    }
  }*/
  
  public synchronized void saveFile() {
    File file = new File(folder, fileName);//"delete.txt"
    if(!file.exists()) return;
    Calendar calendar = Calendar.getInstance();
    long current = calendar.getTimeInMillis();
    long expire = 6*30*24*60*60*1000l;
    Iterator<Long> iterator = idCollection.iterator();
    StringBuilder builder = new StringBuilder();
    while(iterator.hasNext()) {
      long value = iterator.next();
      
      try {
        String id = String.valueOf(value);
        id  = id.substring(0, 8);
        Date date = idFormat.parse(id);
//        System.out.println(" current - date " + (current - date.getTime()) + " : " + expire);
        if(current - date.getTime() >= expire) continue;
      } catch (Exception e) {
        continue;
      }
      
      if(builder.length() > 0) builder.append('\n');
      builder.append(value);
    }
    try {
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
//    System.out.println(" chuan bi save file " + builder);
    lastModified = file.lastModified();
  }
  
  private void loadFile(File file) throws Exception {
    if(!file.exists()) return;
    String text = new String(RWData.getInstance().load(file), Application.CHARSET);
    String [] elements = text.split("\n");
    for(int i = 0; i < elements.length; i++) {
      if(elements[i].length() < 8) continue;
      try {
        idCollection.add(Long.parseLong(elements[i].trim()));
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }
    lastModified = file.lastModified();
  }
  
  private void updatePageIO(Domain domain, String metaId) {
    if(domain == null) return;
    long entryId = Long.parseLong(metaId);
    MetaIdEntry entry = new MetaIdEntry(entryId, Article.DELETE);
    File [] files = EIDFolder2.getFiles(domain, metaId);
    PageIOs.getInstance().write(files, entry);
//    TrackIdService.getInstance().save(domain, metaId);
  }
}
