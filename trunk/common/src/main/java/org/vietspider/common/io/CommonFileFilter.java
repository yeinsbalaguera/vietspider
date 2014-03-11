/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 14, 2007  
 */
@SuppressWarnings("serial")
public abstract class CommonFileFilter implements FileFilter, Serializable {
  
  public static class StartWith extends CommonFileFilter {
    
    private String startWith = "";
    
    public StartWith(String log) {
      startWith =  log;
    }
    
    public boolean accept(File f) {
      return f.getName().startsWith(startWith);
    }
  }
  
  public static class Folder extends CommonFileFilter {
    
    public boolean accept(File f) {
      if(f.getName().indexOf('.') > -1) return false;
      return f.isDirectory();
    }
  }
  
  public static class OnlyFile extends CommonFileFilter {
    
    public OnlyFile() {
    }
    
    public boolean accept(File f) {
      return f.isFile();
    }
  }
  
  public static class LogFile extends CommonFileFilter {

    public LogFile() {
    }

    public boolean accept(File f) {
      return f.isFile() && f.getName().endsWith(".log");
    }
  }
  
  public static class EndWith extends CommonFileFilter {

    private String ext;
    public EndWith(String ext) {
      this.ext = ext;
    }

    public boolean accept(File f) {
      return f.isFile() && f.getName().endsWith(ext);
    }
  }
  
  public static class FolderContainsData extends CommonFileFilter {

    public FolderContainsData() {
    }

    public boolean accept(File f) {
      if(!f.isDirectory()) return false;
      File [] files = f.listFiles();
      if(files == null || files.length < 1) return false;
      for(int i = 0; i < files.length; i++) {
        if(files[i].length() > 0) return true;
      }
      return false;
    }
  }
  
  
}
