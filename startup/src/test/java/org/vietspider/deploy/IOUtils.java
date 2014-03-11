/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 24, 2008  
 */
public class IOUtils {
  
  synchronized public static void deleteFolder(File file, boolean deleteParent){
    File[] list = file.listFiles();
    if( list == null || list.length < 1){
      if(deleteParent) file.delete();
      return;
    }
    for(File ele : list){
      if(ele.isDirectory()) deleteFolder(ele, true); else ele.delete();
    }
    if(deleteParent) file.delete();
  }
  
  public static void copy(File from, File to) throws Exception {
    FileInputStream inputStream = null;
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(to);
      inputStream = new FileInputStream(from);

      copy(inputStream, outputStream);

      outputStream.close();
      inputStream.close();
    } finally {
      try {
        if(outputStream != null) outputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void copy(FileInputStream inputStream, FileOutputStream outputStream) throws Exception {
    FileChannel srcChannel = null;
    FileChannel desChannel = null;

    try {
      srcChannel = inputStream.getChannel();
      desChannel = outputStream.getChannel();

      srcChannel.transferTo(0, srcChannel.size(), desChannel);

      srcChannel.close();
      desChannel.close();
    } finally {
      try {
        if(srcChannel != null) srcChannel.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        if(desChannel != null) desChannel.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
