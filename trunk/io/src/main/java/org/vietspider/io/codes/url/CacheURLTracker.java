/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.io.codes.url;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.Application.IShutdown;
import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.ConcurrentSetIntCacher;
import org.vietspider.common.io.LogService;
import org.vietspider.io.codes.CacheCodeWriter;
import org.vietspider.io.codes.CodesTracker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2008  
 */
public class CacheURLTracker extends CodesTracker<ConcurrentSetInt> {
  
  private final static int CACHE_SIZE = 128*1000;
  private final static int FILE_SIZE = 500*1024;
  
  private ConcurrentSetInt codes;
  protected File file;
  
  private IShutdown shutdown;
  
  private boolean isModified = false;

  public CacheURLTracker(File file_) throws IndexOutOfBoundsException {
    this.file = file_;
    if(file.length() >= FILE_SIZE) throw new IndexOutOfBoundsException(); 
    
    codes = new ConcurrentSetIntCacher();
    writer = new CacheCodeWriter();
    
    shutdown = new Application.IShutdown() {
      public void execute() {
        execute = false;
        if(!isModified) return;
        isModified = false;
        saveTemp(file.getParentFile(), file.getName()+".temp2", codes);
      }
    };
    Application.addShutdown(shutdown);
    
//    CacheCodesLoader codesLoader = new CacheCodesLoader();
    codes.loadFile(file);
    
    File f = new File(file.getParentFile(), file.getName()+".temp");
    if(f.exists())  {
      codes.loadFile(f);
      isModified = true;
      f.delete();
    }
    
    f = new File(file.getParentFile(), file.getName()+".temp2");
    if(f.exists())  {
      codes.loadFile(f);
      isModified = true;
      f.delete();
    }

    
    new Thread(this).start();
  }
  
  public void run() {
    while(execute) {
      
      if(isModified && codes.size() > 0) {
        synchronizedWriter();
        writer.write(file, codes);
        isModified = false;
        new Thread(writer).start();
      }
      
      try {
        Thread.sleep(3*60*1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    Application.removeShutdown(shutdown);
    if(!isModified) return;
    synchronizedWriter();
    writer.write(file, codes);
    writer.save();
    isModified = false;
  }
  
  public void write(int code) {
    codes.add(code);
    isModified = true;
  }

  public boolean isOutOfSize() { return codes.size() >= CACHE_SIZE; }
  
  @SuppressWarnings("unused")
  public boolean search(int code, boolean resave) { return codes.contains(code); }
  
  public boolean isEmptyTemp() { return !isModified; }
  
}
