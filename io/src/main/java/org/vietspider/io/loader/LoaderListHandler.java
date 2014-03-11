package org.vietspider.io.loader;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.container.copy.ServiceConfig;
import org.vietspider.container.copy.ServiceConfig.ServiceType;
@ServiceConfig(type = ServiceType.SOFT_REFERENCE)
public class LoaderListHandler extends RandomAccess implements Runnable  {
  
  private static volatile LoaderListHandler instance;
  
  public static synchronized  LoaderListHandler getInstance() {
    if(instance == null) {
      instance = new LoaderListHandler();
    }
    return instance;
  }
  
  private volatile Queue<String> addList = new ConcurrentLinkedQueue<String>();
  private volatile Queue<String> removeList = new ConcurrentLinkedQueue<String>();
  
  private Thread thread;
  private Thread sortThread;
  private volatile boolean sort = false;
  private volatile long lassAccess = -1;
  private File file;
  
  private LoaderListHandler() {
    file = UtilFile.getFile("system", "load");
  }
  
  public void add(String value) throws Exception {
    addList.add(value);
    sort = true;
    lassAccess = System.currentTimeMillis();
    startThread();
  }
  
  public void remove(String value) throws Exception {
    removeList.add(value);
    lassAccess = System.currentTimeMillis();
    startThread();
  }
  
  private void startThread() {
    if(thread == null 
        || !thread.isAlive() 
        || thread.isInterrupted()) {
      thread = new Thread(this);
      thread.start();
    }
  }
  
  public void run() {
    long  timeout = 15*60*1000;
    while(true) {
      if(!addList.isEmpty()) {
      StringBuilder builder = new StringBuilder();
      while(!addList.isEmpty()) {
        if(builder.length() > 0) builder.append('\n');
        builder.append(addList.poll());
      }
      
      try {
        addToList(builder.toString());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      }
      
      if(!removeList.isEmpty()) {
       StringBuilder builder = new StringBuilder();
        while(!removeList.isEmpty()){
          if(builder.length() > 0) builder.append('\n');
          builder.append(removeList.poll());
        }
        try {
          removeFromList(builder.toString());
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
      
      if(addList.isEmpty() && removeList.isEmpty()) {
        long current = System.currentTimeMillis();
        if(current - lassAccess > timeout) break;
        if(sort) createSortThread();
        try {
          Thread.sleep(1*1000);
        } catch (Exception e) {
        }
      } else {
        try {
          Thread.sleep(500);
        } catch (Exception e) {
        }
      }
    }
    instance = null;
  }
  
  private void createSortThread() {
    if(sortThread != null
        && sortThread.isAlive()) return;
    sortThread = new Thread() {
      public void run() {
        try {
          Thread.sleep(60*1000);
        } catch (Exception e) {
        }
        sort();
      }
    };
    sortThread.start();
  }
  
  private void sort() {
    if(!sort 
        || !addList.isEmpty() 
        || !removeList.isEmpty()) return;
    
    try {
      boolean value = !(new LoaderListSort(file).sort());
      if(addList.isEmpty()) sort = value;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    sortThread = null;
  }
  
  private void addToList(String value) throws Exception {
//    String [] elements = value.split("\n");
//    delete(file, elements);
    
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "rwd");
      random.seek(file.length());
      if(file.length() > 0) random.write("\n".getBytes());
      random.write(value.toString().getBytes(Application.CHARSET));
      random.close();
//      LineListSort.getInstance().setLastAccess(System.currentTimeMillis());
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private void removeFromList(String value) throws Exception {
    value  = value.trim();
    if(value.equals("*")) {
      RWData.getInstance().save(file, new byte[0]);
      return;
    }

    String [] elements = value.split("\n");
    for(int i = 0; i< elements.length; i++) {
      elements[i] = elements[i].trim();
    }
    delete(elements);
//    LineListSort.getInstance().setLastAccess(System.currentTimeMillis());
  }	  

  private void delete(String...lines) throws Exception {
    RandomAccessFile random = null;
    RandomAccessFile newRandom = null;

    try {
      random = new RandomAccessFile(file, "rwd");
      File newFile = new File(file.getParentFile(), "new"+file.getName());
      newRandom = new RandomAccessFile(newFile, "rwd");

      String line;
      byte [] newLine = "\n".getBytes();
      while((line = readLine(random)) != null) {
        if((line = line.trim()).isEmpty()) continue;
        if(Character.isIdentifierIgnorable(line.charAt(0))) line  = line.substring(1);
        boolean write = true;
        for(String ele : lines) {
          if(line.startsWith(ele) || line.endsWith(ele)) {
            write = false;
            break;
          }
        }
        if(!write) continue;
        newRandom.write(line.getBytes(Application.CHARSET));
        newRandom.write(newLine);
      }

      random.close();
      newRandom.close();

      RWData.getInstance().copy(newFile, file);
      newFile.delete();
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(newRandom != null) newRandom.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

}
