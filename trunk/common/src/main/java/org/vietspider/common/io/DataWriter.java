/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 4, 2007
 */
@Deprecated
public class DataWriter {

  public synchronized void save(File file, byte[] data) throws Exception {
    if(file.isDirectory()) return;  
    if(!file.exists()) file.createNewFile();          
    FileOutputStream output = null;
    try {
      output = new FileOutputStream(file);
      save(output, data);    
    } finally {
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public synchronized void save(FileOutputStream output, byte[] data) throws Exception {
    FileChannel channel = null;
    try {
      channel = output.getChannel();
      ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
      buff.put(data);
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear();
      channel.close();
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  } 

  public synchronized void append(File file, byte[] data) throws Exception {
    if(file.isDirectory()) return ;  
    if(!file.exists()) file.createNewFile();    
    FileOutputStream output = null;
    try {
      output = new FileOutputStream(file, true);
      append(output, data);
      output.close();
    } finally {
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public synchronized void append(FileOutputStream output, byte[] data) throws Exception {
    FileChannel channel = null;
    try { 
      channel = output.getChannel();
      append(channel, data);
      channel.close();
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  } 

  private synchronized void append(FileChannel channel, byte[] data) throws Exception {
    ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
    buff.put(data);
    buff.rewind();
    if(channel.isOpen()) channel.write(buff);
    buff.clear(); 
  }

  public synchronized void save(File file, InputStream input) throws Exception {
    save(file, input, false);
  }

  public synchronized void save(File file, InputStream input, boolean append) throws Exception {
    if(file.isDirectory()) return;  
    if(!file.exists()) file.createNewFile();          
    FileOutputStream output = null;
    try {
      output = new FileOutputStream(file, append);
      save(output, input);
      output.close();
    } finally {
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public synchronized void save(FileOutputStream output, InputStream input) throws Exception {
    byte [] bytes = new byte[4*1024];
    int read = 0;
    while((read = input.read(bytes)) > -1) {
      output.write(bytes, 0, read);
    }
    output.flush();
  } 

  public void copy(String from, String to) throws Exception {
    File src = new File(from);
    File des = new File(to);
    copy(src, des);
  }

  public void copy(File from, File to) throws Exception {
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
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public void copy(FileInputStream inputStream, FileOutputStream outputStream) throws Exception {
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
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(desChannel != null) desChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public void delete(File file, long pos, int totalByte) throws Exception {
    String newFileName = file.getName() + ".delete.temp";
    File newFile = UtilFile.getFile(file.getParentFile(), newFileName);

    FileInputStream inputStream = new FileInputStream(file);
    FileOutputStream outputStream = new FileOutputStream(newFile);

    FileChannel srcChannel = null;
    FileChannel desChannel = null;

    try {
      srcChannel = inputStream.getChannel();
      desChannel = outputStream.getChannel();

      srcChannel.transferTo(0, pos, desChannel);
      srcChannel.transferTo(pos + 12, srcChannel.size() - pos - totalByte, desChannel);

      srcChannel.close();
      desChannel.close();
    } finally {
      try {
        if(srcChannel != null) srcChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(desChannel != null) desChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    try {
      file.delete();
      copy(newFile, file);
      newFile.delete();
    } finally {
      if(newFile.exists()) newFile.delete();
    }

  }

  //public static void main(String[] args) throws Exception {
  //File file = new File("F:\\Setup\\Project\\libs spider\\data\\track\\logs\\a.txt");
  //if(!file.exists()) file.createNewFile();
  //DataWriter writer = new DataWriter();
  //writer.append(file, "thuan nhu".getBytes());
  //writer.append(file, "tiep tuc".getBytes());
  //}
}
