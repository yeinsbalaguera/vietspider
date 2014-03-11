/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 10, 2007  
 */
public class ExceptionWriter implements Runnable {

  protected ConcurrentLinkedQueue<TraceObject> traces;
  protected String folder;
//  private String fileName;
  //  private FileOutputStream ouputStream;

  protected boolean print = false;
  protected volatile boolean execute = true;
  protected volatile long lastAccess = System.currentTimeMillis();
  
  protected volatile long sleep = 15*1000l;

  //  private final static long MAX_SIZE = 5000000 ;

  public ExceptionWriter(String folder) {
    traces = new ConcurrentLinkedQueue<TraceObject>();
    this.folder = folder;
    
    if(Application.LICENSE == Install.SEARCH_SYSTEM) sleep =60*1000l;
    
    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Solr Article Database";}
      public void execute() {
        execute = false; 
        if(!traces.isEmpty())  write();
      }
    });
    
  }

  public void run() {
    while(execute) {
      if(!traces.isEmpty())  write();
      try {
        Thread.sleep(sleep);
      }catch (Exception e) {
        e.printStackTrace();
      }
    }
    write();
  }
  
  public void close() { execute = false; }

  public synchronized  boolean isOutOfSize() {
    return traces.size() > 100;
  }

  public synchronized void put(TraceObject obj) {
    traces.add(obj); 
    lastAccess = System.currentTimeMillis();
  }
  
  public boolean isTimeout() { 
    return System.currentTimeMillis() - lastAccess >  15*60*1000l; 
  }

  public synchronized void write() {
    FileOutputStream output = null;
    FileChannel channel = null;
    try {
      output = getOutput();
      if(output == null) return;
      channel = output.getChannel();
      while(!traces.isEmpty()) {
        String text = traces.poll().getMessage().toString();
        byte [] bytes = text.getBytes(Application.CHARSET);
        ByteBuffer buff = ByteBuffer.allocateDirect(bytes.length);
        buff.put(bytes);
        buff.rewind();
        if(channel.isOpen()) channel.write(buff);
        buff.clear();
//        output.write(text.getBytes(Application.CHARSET));
        output.flush();
      }
      
    } catch( Exception e) { 
      e.printStackTrace();
    } finally {
      
      try {
       if(channel != null) channel.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if(output != null) output.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected FileOutputStream getOutput() {
    FileOutputStream output = null;
    //    if(fileName == null || !fileName.equals(name)) {
    /* try {
        if(ouputStream != null) ouputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }*/

    String fileName = DataOfDay.getDateFoder();
    File file = new File(UtilFile.getFolder(folder), fileName+".log");
    if(file.length() >= 100*1024*1024l) file.delete();
    try {
      if(!file.exists()) file.createNewFile();
      output = new FileOutputStream(file, true);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    try {
      if(!print) System.setErr(new PrintStream(output));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return output;
    //    }

    //    return ouputStream;
  }

  public static class TraceObject {

    StringBuilder builder = new StringBuilder();

    public TraceObject (String label, String [] elements) {
      Calendar calendar = Calendar.getInstance();
      SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
      String dateTime = dateFormat.format(calendar.getTime()); 
      if(label == null) label = "MESSAGE: ";
      for(String element : elements) {
        if(element == null) continue;
        if(builder.length() > 0) builder.append('\n');
        builder.append(label).append(':').append(' ');
        builder.append(dateTime).append(':').append(' ').append(element);
      }
      builder.append('\n');
    }

    public TraceObject (String label, Throwable exp, String message) {
      builder.append('\n');
      if(label == null) label = "EXCEPTION: ";
      if(message != null) {
        builder.append(label).append(':').append(message).append('\n');
      }

      builder.append(label).append(':').append(' ');
      Calendar calendar = Calendar.getInstance();
      SimpleDateFormat dateFormat = CalendarUtils.getDateTimeFormat();
      builder.append(dateFormat.format(calendar.getTime())).append(':').append(' ');
      builder.append(exp.toString()).append('\n');

      StackTraceElement[] traces = exp.getStackTrace();
      for(StackTraceElement ele : traces) {
        if(label != null) builder.append(label).append(':').append(' ');
        int lineNumber = ele.getLineNumber();
        if(lineNumber > -1) {
          builder.append(ele.toString() + ": " +  String.valueOf(lineNumber)).append('\n');
        } else {
          builder.append(ele.toString()).append('\n');
        }
      }
      builder.append('\n');
    }

    public StringBuilder getMessage() { return builder; }
  }

  public void setPrint(boolean print) { this.print = print; }
}
