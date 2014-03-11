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
public class LoaderListGenerator extends RandomAccess implements Runnable  {
  
  private static volatile LoaderListGenerator instance;
  
  public static synchronized  LoaderListGenerator getInstance() {
    if(instance == null) {
      instance = new LoaderListGenerator();
    }
    return instance;
  }
  
  private volatile Queue<String> list = new ConcurrentLinkedQueue<String>();
  
  private Thread thread;
  private volatile boolean execute = true;
  private File file;
  
  private LoaderListGenerator() {
    file = UtilFile.getFile("system", "new.load");
    thread = new Thread(this);
    thread.start();
  }
  
  public void add(String value) throws Exception {
    list.add(value);
  }
  
  public void run() {
    while(execute) {
      save();
      try {
        Thread.sleep(1*1000);
      } catch (Exception e) {
      }
    }
    
    save();
    sort();
    File newFile = UtilFile.getFile("system", "load");
    try {
      newFile.delete();
      RWData.getInstance().copy(file, newFile);
      file.delete();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    instance = null;
  }
  
  private void save() {
    if(list.isEmpty()) return;
    StringBuilder builder = new StringBuilder();
    while(!list.isEmpty()) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(list.poll());
    }

    try {
      addToList(builder.toString());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  
  private void sort() {
    try {
      new LoaderListSort(file).sort();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  
  private void addToList(String value) throws Exception {
//    String [] elements = value.split("\n");
//    delete(file, elements);
    
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "rwd");
      random.seek(file.length());
      random.write("\n".getBytes());
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

  public void setExecute(boolean execute) {
    this.execute = execute;
  }


}
