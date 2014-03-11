/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2010  
 */
public class TempQueue<T extends Serializable>  extends Thread {

  protected volatile ConcurrentLinkedQueue<T> queue;
  protected File folder;
  protected AtomicInteger counter = new AtomicInteger(0);

  public TempQueue(String path) {
    queue = new ConcurrentLinkedQueue<T>();
    folder = UtilFile.getFolder(path);
    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Temp Queue";}
      public void execute() {
        save();
      }
    });
    load();
    this.start();
  }
  
  public void run() {
    while(true) {
      if(counter.get() > 1000) save();
      if(counter.get() < 100) load();
      try {
        Thread.sleep(5*1000l);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  protected void load() {
    File [] files = UtilFile.listModifiedFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile();
      }
    });

    if(files == null || files.length < 1) return;

    for(int i = 0; i < files.length; i++) {
      try {
        byte []  bytes = RWData.getInstance().load(files[i]);

        ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
        DataInputStream buffered = new DataInputStream(byteInput);
        try {
          while(true) { 
            try {
              int length = buffered.readInt();
              bytes = new byte[length];
              buffered.read(bytes, 0, length); 
              T data = toData(bytes);
              //          System.out.println(" === > "+ data.getContentIndex().getId());
              queue.add(data);
              counter.incrementAndGet();
            } catch (EOFException e) {
              break;
            }
          }
        } finally {
          buffered.close();
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      } finally {
        files[i].delete();
      }
      //      System.out.println(" doc xong duoc "+ idx);
    }
  }

  @SuppressWarnings("unchecked")
  private T toData(byte[] bytes) throws Throwable {
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(byteInputStream);
      return (T)objectInputStream.readObject();
    } catch (StackOverflowError e) {
      LogService.getInstance().setMessage("QUEUE.TEMP - LOAD", new Exception(e), e.toString() );
      return null;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return null;
    } finally {
      try {
        if(byteInputStream != null) byteInputStream.close();
      } catch (Exception e) {
      }
      try {
        if(objectInputStream != null)  objectInputStream.close();
      } catch (Exception e) {
      }
    } 
  }

  void save() {
    ByteArrayOutputStream bytesOutput =  new ByteArrayOutputStream(10*1024*1024);
    DataOutputStream buffered = new DataOutputStream(bytesOutput);
    Iterator<T> iterator = queue.iterator();
    while(iterator.hasNext()) {
      T data = iterator.next();
      writeBuffer(buffered, data);
      iterator.remove();
      counter.decrementAndGet();
    }

    byte [] bytes = bytesOutput.toByteArray();
    if(bytes.length < 10) return;

    File file = searchNewFile();
    try {
      RWData.getInstance().save(file, bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private void writeBuffer(DataOutputStream buffered, T data) {
    //  System.out.println("luc save "+ data.getContentIndex());
    ByteArrayOutputStream bytesObject = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(bytesObject);
      out.writeObject(data);
      out.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    } finally {
      try {
        if(bytesObject != null) bytesObject.close();
      } catch (Exception e) {
      }
      try {
        if(out != null) out.close();
      } catch (Exception e) {
      }
    }

    byte [] bytes = bytesObject.toByteArray();
    try {
      buffered.writeInt(bytes.length);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    try {
      buffered.write(bytes, 0, bytes.length);
      buffered.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private File searchNewFile() {
    //    File folder = UtilFile.getFolder("content/tp/temp/docs2/");
    int name = 0;
    File file = new File(folder, String.valueOf(name));
    while(file.exists()) {
      name++;
      file = new File(folder, String.valueOf(name));
    }
    return file;
  }

  public void add(T article) { 
    queue.add(article); 
    counter.incrementAndGet();
  }

  public boolean isEmpty() { return queue.isEmpty(); }
  
  public int size() { return queue.size(); }
  
//  Iterator<SolrArticle> iterator() { return queue.iterator(); }

  public T poll() {
    counter.decrementAndGet();
    return queue.poll(); 
  }
}
