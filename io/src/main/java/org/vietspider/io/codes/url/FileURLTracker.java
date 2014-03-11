/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.io.codes.url;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.Application.IShutdown;
import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.ConcurrentSetIntFile;
import org.vietspider.common.io.LogService;
import org.vietspider.io.codes.CacheCodesLoader;
import org.vietspider.io.codes.CodesTracker;
import org.vietspider.io.codes.FileCodesReader;
import org.vietspider.io.codes.FileCodesWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2008  
 */
public class FileURLTracker extends CodesTracker<ConcurrentSetInt> {
  
  private ConcurrentSetInt codes;
  
  private FileCodesReader reader;
  private IShutdown shutdown;
  
  protected File file;
  
  public FileURLTracker(File file_) {
    this.file = file_;
    
    codes = new ConcurrentSetIntFile();
    reader = new FileCodesReader();
    writer = new FileCodesWriter();
    
    shutdown = new Application.IShutdown() {
      public void execute() {
        execute = false;
        if(codes.size() < 1) return;
        saveTemp(file.getParentFile(), file.getName()+".temp2", codes);
      }
    };
    Application.addShutdown(shutdown);
    
    CacheCodesLoader codeLoader = new CacheCodesLoader();
    
    File f = new File(file.getParentFile(), file.getName() + ".temp");
    if(f.exists())  {
      codeLoader.load(f, codes);
      f.delete();
    }
    
    f = new File(file.getParentFile(), file.getName() + ".temp2");
    if(f.exists())  {
      codeLoader.load(f, codes);
      f.delete();
    }
    
    new Thread(this).start();
  }
  
  public void run() {
    while(execute) {
      if(codes.size() > 0) {
        synchronizedWriter();
        writer.write(file, codes);
        new Thread(writer).start();
      }
      
      try {
        if(codes.size() > 0) {
          Thread.sleep(10*1000);
        } else {
          Thread.sleep(2*60*1000);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    Application.removeShutdown(shutdown);
    if(codes.size() < 1) return;
    synchronizedWriter();
    writer.write(file, codes);
    writer.save();
  }
  
  public void write(int code) {
    if(codes.contains(code)) return;
    codes.add(code);
  }

  @SuppressWarnings("unused")
  public boolean search(int code, boolean resave) {
    if(codes.contains(code)) return true;

    synchronizedWriter();
    reader.putData(file, code);
    synchronizeReader();
    
    try {
      if(reader.getPosition() != -1) return true;
    } catch (Exception e) {
      return true;
    }
   
    return false;
  }
  
  private void synchronizeReader() {
    synchronized (reader) {
      while(reader.isRunning()) {
        try {
          reader.wait(); 
        } catch (InterruptedException e) {
        }
      }
    }
  }
  
  public boolean isEmptyTemp() { return codes.size() < 1; }
  
}
