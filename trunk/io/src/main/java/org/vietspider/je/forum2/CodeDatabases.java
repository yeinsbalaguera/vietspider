/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.forum2;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public abstract class CodeDatabases {
  
  protected  volatile List<CodeDatabase> databases;
  protected volatile CodeDatabase current;
  protected int currentDate = -1;

  protected volatile boolean isClose = false;
  
  protected String folder_path;
  protected String type;
  protected long expire_db = 1*24*60*60*1000;
  
  public CodeDatabases(String folderPath, String type_, long expire_db) throws Exception {
    this.folder_path = folderPath;
    this.type = type_;
    this.expire_db = expire_db;
    databases = new ArrayList<CodeDatabase>();

    File folder = UtilFile.getFolder(folderPath + "/");
    File [] files = UtilFile.listFiles(folder);

    if(files.length > 0) {
      current = createCurrentDb(files[0]);
      int i = 0; 
      for(;i < Math.min(3, files.length); i++) {
        addDatabaseToList(files[i]);
      }
      
      for(;i < files.length; i++) {
        UtilFile.deleteFolder(files[i]);
      }
    } else {
      current = createCurrentDb();
    }

    Calendar calendar = Calendar.getInstance();
    currentDate = calendar.get(Calendar.DATE);
    
    Application.addShutdown(new Application.IShutdown() {
      
      public String getMessage() { return "Close Database/ "+type; }

      public int getPriority() { return 2; }
      
      public void execute() { close(); }
    });
  }

  public abstract void writeTemp();  
  public abstract void close() ;

  private CodeDatabase createCurrentDb(File folder) throws Exception {
    Date date = CalendarUtils.getFolderFormat().parse(folder.getName());
    return createCurrentDb(date);
  }

  private CodeDatabase createCurrentDb(Date date) throws Exception {
    if(date == null) return createCurrentDb();
    Calendar calendar = Calendar.getInstance();
    if(calendar.getTimeInMillis() - date.getTime() >= expire_db) return createCurrentDb();

    String name = CalendarUtils.getFolderFormat().format(date.getTime());
    File folder = UtilFile.getFolder(folder_path + "/" + name+ "/");
    return new CodeDatabase(folder, name, type);
  }

  private CodeDatabase createCurrentDb() throws Exception { 
    Calendar calendar = Calendar.getInstance();
    String name = CalendarUtils.getFolderFormat().format(calendar.getTime());
    File folder = UtilFile.getFolder(folder_path + "/" + name+ "/");
    return new CodeDatabase(folder, name, type);
  }

  protected void nextDatabase()  throws Exception {
    Calendar calendar = Calendar.getInstance();
    int d = calendar.get(Calendar.DATE);
    if(d == currentDate) return;
    currentDate = d;

    Date date = CalendarUtils.getFolderFormat().parse(current.getName());
    if(calendar.getTimeInMillis() - date.getTime() < expire_db 
        && current.size() < 5000000) return;

    databases.add(current);
    current = createCurrentDb();
  }

  private void addDatabaseToList(File folder) throws Exception  {
    String name  = folder.getName();
    if(name.equals(current.getName())) return;
    for(int i = 0; i < databases.size(); i++) {
      if(name.equals(databases.get(i).getName())) return;
    }
    databases.add(new CodeDatabase(folder, name, type));
  }


  public boolean isClose() { return isClose; }

}
