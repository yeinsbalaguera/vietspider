/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 29, 2011  
 */
public abstract class StorableTempQueue<T extends Serializable> {
  
  private File folder;
  
  protected volatile long sizeOfWorking = 100;
  
//  protected volatile CopyOnWriteArrayList<T> queue = new CopyOnWriteArrayList<T>();
  protected volatile ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();
  
  public StorableTempQueue(File folder, long sizeOfWorking) {
    this.folder = folder;
    this.sizeOfWorking = sizeOfWorking;
  }
  
//  protected File getFolder() { return folder; }

  protected File[] listTempFiles() {
   return UtilFile.listModifiedFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        return f.isFile();
      }
    });
  }


  public void storeTemp() { store1(queue); }
  
  public void setSizeOfWorking(long sizeOfWorking) {
    this.sizeOfWorking = sizeOfWorking;
  }

  public synchronized void store1(Queue<T> list) {
    ByteArrayOutputStream bytesOutput =  new ByteArrayOutputStream(10*1024*1024);
    DataOutputStream buffered = new DataOutputStream(bytesOutput);
    
    while(!list.isEmpty()) {
      writeBuffer(buffered, list.poll());
    }
    
//    Iterator<T> iterator = list.iterator();
//    while(iterator.hasNext()) {
//      T data = iterator.next();
//      writeBuffer(buffered, data);
//      list.remove(data);
//    }

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
  
  protected void load(File [] files, java.util.Collection<T> working) throws Throwable {
    int idx = 0;
    int fileIndex  = files.length-1;
    while(idx < sizeOfWorking) {
      if(fileIndex < 0) break;
      File file  = files[fileIndex];
      byte []  bytes = RWData.getInstance().load(file);
      file.delete();

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
            working.add(data);
            idx++;
          } catch (EOFException e) {
            break;
          }
        }
      } finally {
        buffered.close();
      }
      fileIndex--;
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
      LogService.getInstance().setMessage("Temp Queue Storable - LOAD", new Exception(e), e.toString() );
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

  
}
