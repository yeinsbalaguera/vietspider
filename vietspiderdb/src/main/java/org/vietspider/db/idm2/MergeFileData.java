/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 8, 2009  
 */
class MergeFileData {
  
  private File file;

  private long lastModified = -1;

  MergeFileData(File f) { this.file  = f; }
  
  File getFile() { return file; }

  boolean isLoad() {
    if(lastModified != file.lastModified()) {
      lastModified = file.lastModified();
      return true;
    }
    return false;
  }

  void transferTo(FileChannel desChannel) throws IOException {
    FileInputStream inputStream = null;
    FileChannel srcChannel = null;
    try {
      inputStream = new FileInputStream(file);
      srcChannel = inputStream.getChannel();
//      System.out.println(" bat dau voi "+ desChannel.size()+ " / "+ srcChannel.size());
      srcChannel.transferTo(0, srcChannel.size(), desChannel);
//      System.out.println(" ket thuc voi "+ desChannel.size());
    } finally {
      try {
        if(srcChannel != null) srcChannel.close();
      } catch (IOException e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(inputStream != null) inputStream.close();
      } catch (IOException e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  

}
