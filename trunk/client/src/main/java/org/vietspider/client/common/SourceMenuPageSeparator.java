/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RandomAccess;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 1, 2008  
 */
public class SourceMenuPageSeparator extends RandomAccess {
  
  private File folder;
  private byte [] newLine = "\n".getBytes();
  
  public SourceMenuPageSeparator(File folder) {
    this.folder = folder;
  }

  public void separate(File sortFile) {
    RandomAccessFile random = null;
    try { 
      random = new RandomAccessFile(sortFile, "r");
      
      String line;
      List<String> lines = new ArrayList<String>();
      int counter = 0;
      while((line = readLine(random)) != null) {
        if((line = line.trim()).isEmpty()) continue;
        if(Character.isIdentifierIgnorable(line.charAt(0))) {
          line  = line.substring(1);
        }
        lines.add(line);
        if(lines.size() < 20) continue; 
        writeFile(lines, counter);
        lines.clear();
        counter++;
      }
      if(lines.size() > 0) writeFile(lines, counter);
      random.close();
    } catch (EOFException e) {
    } catch (Exception e) {
      ClientLog.getInstance().setException(e);
    } finally {
      try {
        if(random != null) random.close(); 
      } catch (Exception e) {
        ClientLog.getInstance().setException(e);
      }
    }
  }
  
  private File writeFile(List<String> lines, int counter) throws Exception {
    File file = new File(folder, "data"+String.valueOf(counter));
    file.createNewFile();
    RandomAccessFile random = null;
    
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
}
