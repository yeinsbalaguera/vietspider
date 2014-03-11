/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.server.handler.cms.FileWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public class FileWriterImpl implements FileWriter {
  
  private String folder;
  
  public FileWriterImpl() {
    this("system/cms/search/");
  }

  public FileWriterImpl(String folder) {
    this.folder = folder;
  }
  
  public boolean isExists(String name) {
    File file = new File(UtilFile.getFolder(folder), name);
    return file.exists();
  }
  
  public void write(OutputStream output, String name) throws Exception {
    File file = new File(UtilFile.getFolder(folder), name);
    if(!file.exists() || file.length() < 1) return;
    FileInputStream input = null;
    FileChannel channel = null;
    try {
      input  = new FileInputStream(file);
      channel = input.getChannel();
      ByteBuffer buff = ByteBuffer.allocate(2048);
//      long total = 0;
      int read = -1;
      while( (read = channel.read(buff)) != -1) {
        channel.read(buff);
        buff.rewind();    
        byte [] bytes = new byte[read];
        buff.get(bytes, 0, read);
        output.write(bytes);
//        total += bytes.length;
        buff.clear();
        output.flush();
      }
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      try {
        if(input != null) input.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
}
