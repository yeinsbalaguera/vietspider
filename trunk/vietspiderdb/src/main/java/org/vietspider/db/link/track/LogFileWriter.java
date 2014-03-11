/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.link.track;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 29, 2010  
 */
public class LogFileWriter {


  //  protected List<LinkLog> temp = new Vector<LinkLog>();
  private  StringBuilder builder = new StringBuilder();
  private int counter = 0;

  private long lastAccess;
  private final static int MAX_RECORD = 50;

  private String dateFolder;
  private String fileName;

  public LogFileWriter(String dateFolder, String fileName) throws Exception {
    this.dateFolder = dateFolder;
    this.fileName = fileName;
    lastAccess = System.currentTimeMillis();
  }

  synchronized void save(LinkLog log) {
    lastAccess = System.currentTimeMillis();
    //    temp.add(log);  
    builder.append(log.toShortString()).append('\n');
    counter++;
    //    System.out.println("=== >"+ +hashCode()+ " :" +file.getName()+ " : "+ counter);
    if(counter < MAX_RECORD) return;
    write();
  }

  private void write() {
    counter = 0;
    //    List<LinkLog> list = new ArrayList<LinkLog>();
    //    list.addAll(temp);
    //    temp.clear();

    //    StringBuilder builder = new StringBuilder();
    //    for(int i = 0; i < list.size(); i++) {
    //      builder.append(list.get(i).toShortString()).append('\n');
    //    }
    //    System.out.println(builder);

    FileOutputStream output = null;
    FileChannel channel = null;

    File file = getFile();
    try {
      String text = builder.toString();
      builder.setLength(0);
      output = new FileOutputStream(file, true);
      channel = output.getChannel();
      byte [] data = text.getBytes(Application.CHARSET);
      ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
      buff.put(data);
      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear();

      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

  }

  private File getFile() {
    File folder = UtilFile.getFolder("track/logs/sources/" + dateFolder);
    File file  = new File(folder, fileName+".log");
    if(!file.exists()) {
      try {
        file.createNewFile();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return file;
    }

    int index = 1;
    while(file.length() > 5*1024*1024l) {//100*1024*1024l
      file  = new File(folder, fileName +"." + String.valueOf(index)+".log");

      index++;
      if(index < 10) continue;
      
      file  = new File(folder, fileName+".log");
      file.delete();
      try {
        file.createNewFile();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      break;
    }

    return file;
  }

  boolean isTimeout() {
    if(System.currentTimeMillis() - lastAccess >= 15*60*1000) return true;
    return false;
  }

  public void close() {
    write();
  }



}
