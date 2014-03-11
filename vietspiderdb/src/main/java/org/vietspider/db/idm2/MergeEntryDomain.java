/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 8, 2009  
 */
public class MergeEntryDomain implements IEntryDomain {
  
  private String fileName;
  private File fileData;
  
  private long lastAccess = System.currentTimeMillis();

  private List<MergeFileData> fileDatas = new ArrayList<MergeFileData>();
  
  public MergeEntryDomain(String name) {
    this.fileName = name;// builder.toString();
    if(fileName.length() > 0) {
      this.fileData = new File(UtilFile.getFolder("content/temp/eid/"), fileName+".eid");
    } else {
      this.fileData = new File(UtilFile.getFolder("content/temp/eid/"), "all.eid");
    }
  }
  
  public String getFileName() { return fileName; }
  
  public File getFile() { return fileData; }

  boolean isTimeout() { return System.currentTimeMillis() - lastAccess >= 15*60*1000; }
  
  void updateLastAccess() { lastAccess = System.currentTimeMillis(); }

  void scanFiles() throws IOException {
    File folder = UtilFile.getFolder("content/summary/eid");
    File [] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File a) {
        return a.isDirectory();
      }
    });
    
    for(int i = 0; i < Math.min(files.length, 30); i++) {
      File file  = null;
      if(fileName.length() > 0) {
        file  = new File(files[i], files[i].getName()+"."+ fileName+".eid");
      } else {
        file  = new File(files[i], files[i].getName()+".eid");
      }
//      System.out.println("hihihi "+file.getName());
//      System.out.println(" single source file "+ file.getAbsolutePath());
      if(!file.exists()) continue;
      if(exist(file)) continue;
      fileDatas.add(new MergeFileData(file));
    }
    
    boolean newFile = false;
    for(int i = 0 ; i < fileDatas.size(); i++) {
      if(fileDatas.get(i).isLoad()) {
        newFile = true;
        break;
      }
    }
    
    if(!newFile && fileData.length() > 0) return;
    
    if(fileData.exists()) {
      if(!fileData.delete()) throw new IOException("Cann't delete temp file");
    }
    
    fileData.createNewFile();
    
    Collections.sort(fileDatas, new Comparator<MergeFileData>() {
      public int compare(MergeFileData a, MergeFileData b) {
        String name1 = a.getFile().getName();
        String name2 = b.getFile().getName();
        int idx = name1.indexOf('.');
        if(idx > 0) name1 = name1.substring(0, idx);
        idx = name2.indexOf('.');
        if(idx > 0) name2 = name2.substring(0, idx);
//        System.out.println(" thay co name1 "+ name1 + "/"+name2);
        return name2.compareTo(name1);
      }
    });
    
    FileOutputStream outputStream = null;
    FileChannel desChannel = null;
    try {
      outputStream = new FileOutputStream(fileData, true);
      desChannel = outputStream.getChannel();
      
      for(int i = fileDatas.size()-1; i > -1; i--) {
        if(fileDatas.get(i).getFile().length() < 1) continue;
//        System.out.println("transfer " + fileDatas.get(i).getFile().getName());
        fileDatas.get(i).transferTo(desChannel);
      }
      
    } finally {
      try {
        if(desChannel != null) desChannel.close();
      } catch (IOException e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(outputStream != null) outputStream.close();
      } catch (IOException e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
  }
  
  private boolean exist(File file) {
    for(int i = 0 ; i < fileDatas.size(); i++) {
      String name = fileDatas.get(i).getFile().getName();
      if(file.getName().equals(name)) return true;
    }
    return false;
  }
  
   
}
