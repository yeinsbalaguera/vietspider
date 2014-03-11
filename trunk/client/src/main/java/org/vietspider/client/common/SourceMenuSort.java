/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.text.VietComparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 22, 2007  
 */
class SourceMenuSort extends RandomAccess {
  
  private File folder;

  private byte [] newLine = "\n".getBytes();

  private VietComparator comparator = new  VietComparator();
  
//  private volatile long lastModified = -1;
  private static short BUFF_SIZE = 1000;
//  private long lastAccess = -1;
  
  private volatile File file_; 
  private boolean sort = false;

  SourceMenuSort(File file) {
    this.file_ = file;
//    this.lastModified = file.lastModified();
    if(Application.LICENSE == Install.SEARCH_SYSTEM) BUFF_SIZE = 2000;
  }
  
  boolean sort() throws Exception {
    if(!sort) return false;
    long lastModified = file_.lastModified();
    folder = file_.getParentFile();
    RandomAccessFile random = null;

    try {
      random = new RandomAccessFile(file_, "r");
      random.seek(0);

      List<File> files = new LinkedList<File>();
      List<String> lines = new LinkedList<String>();
      String line = null;

      while((line = readLine(random)) != null) {
        if((line = line.trim()).isEmpty()) continue;
        if(Character.isIdentifierIgnorable(line.charAt(0))) {
          line  = line.substring(1);
        }
        lines.add(line);
        if(lines.size() < BUFF_SIZE) continue;
        Collections.sort(lines, comparator);
        files.add(writeFile(lines));       
        lines.clear();
      }

      if(lines.size() > 0) {
        Collections.sort(lines, comparator);
        files.add(writeFile(lines));
        lines.clear();
      }
      
      random.close();
      File tempFile = merge(files);
      if(tempFile == null) return false;
      if(file_.lastModified() == lastModified) {
        RWData.getInstance().copy(tempFile, file_);
        tempFile.delete();
        return true;
      }
      tempFile.delete();
      return false;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private File writeFile(List<String> lines) throws Exception {
    RandomAccessFile random = null;
    File file = createFile();
    try {
      random = new RandomAccessFile(file, "rwd");
      for(String line : lines) {
        random.write(line.getBytes(Application.CHARSET));
        random.write(newLine);
      }
      random.close();
      return file;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private File merge(List<File> list) throws Exception {
    if(list.size() < 1) return null;
    if(list.size() == 1) return list.get(0);
    List<File> newList = new LinkedList<File>(); 
    while(list.size() >= 2) {
      File file1 = list.remove(0);
      File file2 = list.remove(0);
      newList.add(mergeFile(file1, file2));
    }
    if(list.size() == 1) newList.add(list.remove(0));
    return merge(newList);
  }


  private File mergeFile(File file1, File file2) throws Exception {
    RandomAccessFile random1 = null; 
    RandomAccessFile random2 = null;
    RandomAccessFile random = null;
    try {
      random1 = new RandomAccessFile(file1, "rwd");
      random2 = new RandomAccessFile(file2, "rwd");
      File file = createFile();
      random = new RandomAccessFile(file, "rwd");
      String line1 = null;
      String line2 = null;

      while(true) {
        if(line1 == null) line1 = readLine(random1);
        if(line2 == null) line2 = readLine(random2);
        if(line1 == null && line2 != null) {
          random.write(line2.getBytes(Application.CHARSET));
          random.write(newLine);
          line2 = null;
          continue;
        } else if(line2 == null && line1 != null) {
          random.write(line1.getBytes(Application.CHARSET));
          random.write(newLine);
          line1 = null;
          continue;
        } else if(line2 != null && line1 != null) {
          if(comparator.compare(line1, line2) > 0) {
            random.write(line2.getBytes(Application.CHARSET));
            random.write(newLine);
            line2 = null;
            continue;
          } 
          random.write(line1.getBytes(Application.CHARSET));
          random.write(newLine);
          line1 = null;
        } else {
          break;
        }
      }

      random.close();
      random1.close();
      random2.close();

      file1.delete();
      file2.delete();
      return file;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(random1 != null) random1.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(random2 != null) random2.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private File createFile() throws Exception {
    Calendar calendar = Calendar.getInstance();
    String time  = String.valueOf(calendar.getTimeInMillis());
    File file  = new File(folder, file_.getName() + "." + time);
    file.createNewFile();
    return file;
  }

//  public void setLastAccess(long lastAccess) {
//    this.lastAccess = lastAccess;
//  }
  
}
