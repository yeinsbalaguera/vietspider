/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.paging;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public abstract class PageIO<T extends Entry> {

  protected File file;
  protected int dataSize;
  
  protected volatile boolean writing = false; 

  protected volatile long lastAccess = System.currentTimeMillis();

  protected ConcurrentLinkedQueue<T> temps = new ConcurrentLinkedQueue<T>();

  public PageIO() {
  }

  public PageIO(File file, int dSize) {
    this.file = file; 
    this.dataSize = dSize;
  }

  public void setData(File file, int dSize) {
    this.file = file; 
    this.dataSize = dSize;
  }

  @SuppressWarnings("unchecked")
  public void write(Entry entry) {
    lastAccess = System.currentTimeMillis();
    temps.add((T)entry);
  }
  
  public int getTotalPage(int pageSize) {
    lastAccess = System.currentTimeMillis();
    long length  = file.length();
    long total = length/(dataSize + 4);
    int entrySize = dataSize+4;
    if(total%entrySize == 0) {
      return  (int)(total/pageSize);
    }
    return (int)(total/pageSize + 1);
  }

  public List<T> loadPageByDesc(int page, int pageSize, EntryFilter filter) {
    lastAccess = System.currentTimeMillis();
    List<T> entries = new ArrayList<T>();
    RandomAccessFile random = null;
    if(page < 1) page = 1;
    try {
//      System.out.println(file.getAbsolutePath());
      if(!file.exists()) file.createNewFile();
      random = new RandomAccessFile(file, "r");
      long length  = random.length();
      
      int entrySize = dataSize + 4;
      
      long start = length - (page-1)*entrySize*pageSize;
      if(start < 0) return entries;
//      System.out.println(" file "+ file.getName());

      while(true) {
        start -= entrySize;
        if(start < 0) break;

        random.seek(start);

        byte [] bytes = new byte[dataSize];
        int st = -1;
        try {
          random.readFully(bytes);
          st = random.readInt();
        } catch (EOFException e) {
          LogService.getInstance().setThrowable(null, e, "Load page "+ String.valueOf(page) 
              + ",  page size "+ String.valueOf(pageSize));
          break;
        }
        T entry = createEntry();
        entry.setData(bytes);
        entry.setStatus(st);
        if((filter == null 
            || filter.validate(entry))) entries.add(entry);
//        if(entry instanceof MetaIdEntry) {
//          System.out.println(" meta id "+ ((MetaIdEntry)entry).getMetaId());
//        }
        if(entries.size() >= pageSize) break;
      }

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return entries;
  }

  public List<T> loadPageByAsc(int page, int pageSize, EntryFilter filter){
    lastAccess = System.currentTimeMillis();
    List<T> entries = new ArrayList<T>();
    if(!file.exists()) return entries;
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      long length = random.length();
      int entrySize = dataSize + 4;
      long start = (page - 1) * entrySize * pageSize;
      if (start < 0) return entries;

      while (true){
        if (start >= length) break;
        random.seek(start);

        byte [] bytes = new byte[dataSize];
        int st = -1;
        try {
          random.readFully(bytes);
          st = random.readInt();
        } catch (EOFException ex) {
          LogService.getInstance().setThrowable(ex);
          break;
        }

        T entry = createEntry();
        entry.setData(bytes);
        entry.setStatus(st);
        if((filter == null 
            || filter.validate(entry))) entries.add(entry);
        if (entries.size()>= pageSize) break;
        start += entrySize;
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally{
      try {
        if (random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return entries;
  }

  public void commit() {
    if(writing) return;
    writing = true;
    append();
    update();
    writing = false;
  }

  protected void append() {
    List<T> entries = subList(Entry.INSERT);
    if(entries.size() < 1) return;
    append(entries);
  }
  
  protected void append(List<T> entries) {
    lastAccess = System.currentTimeMillis();
    //    System.out.println(" prepare append file "+ file.getAbsolutePath()+ " -> "+ entries.size());
    RandomAccessFile random = null;
    FileChannel channel = null;
    try {
      random = new RandomAccessFile(file, "rws");
      channel = random.getChannel();

      random.seek(file.length());

      int size = entries.size()*(dataSize + 4);

      ByteBuffer buff = ByteBuffer.allocateDirect(size);
      for(T entry : entries){
        buff.put(entry.getData());
        buff.putInt(entry.getStatus());
      }

      buff.rewind();
      if(channel.isOpen()) channel.write(buff);
      buff.clear();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  protected void update() {
    List<T> entries = subList(Entry.UPDATE);
    if(entries.size() < 1) return;

    lastAccess = System.currentTimeMillis();

    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "rws");
      
      long length = random.length(); 
      long pointer = 0;
      int entrySize = dataSize + 4;
      random.seek(pointer);
      
      while(pointer < length) {
        byte [] newBytes = new byte[dataSize];
        random.readFully(newBytes);
        
        T entry = search(entries, newBytes);
        if(entry == null) {
          pointer += entrySize;
          random.seek(pointer);
        } else {
          entry.update(random, pointer);
          pointer += entrySize;
        }
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  private T search(List<T> entries, byte[] bytes) {
    for(int i = 0; i < entries.size(); i++) {
      T  entry = entries.get(i);
      if(entry.compare(bytes)) {
        entries.remove(i);
        return entry;
      }
    }
    return null;
  }
  
  public void optimize(int status, OptimizePlugin...plugins) {
    if(!file.exists() || writing) return;
    lastAccess = System.currentTimeMillis();
    
    writing = true;
    
    File newFile = new File(file.getParentFile(), file.getName()+".temp");
    
//    System.out.println(" chuan bi optimize "+ newFile.getAbsolutePath() + " : "+ status);
    
    RandomAccessFile random = null;
    RandomAccessFile newRandom = null;
    try {
      if(newFile.exists()) newFile.delete();
      if(newFile.exists()) {
        LogService.getInstance().setThrowable(new Exception("Cann't delete "+ newFile.getAbsolutePath()));
        return;
      }
      newFile.createNewFile();
      
      random = new RandomAccessFile(file, "r");
      newRandom = new RandomAccessFile(newFile, "rw");
     
      random.seek(0);
      long length = random.length();
      long point = 0;
      int entrySize = dataSize + 4;
      
      while(point < length) {
        byte [] bytes = new byte[dataSize];
        try {
          random.readFully(bytes);
          int st = random.readInt();
//          System.out.println(toLong(bytes) + "/" + st + "/"+ status);
          boolean  write = st != status;
          if(write) {
            for(int i = 0; i < plugins.length; i++) {
              if(plugins[i].isRemove(bytes)) {
                write = false;
                break;
              }
            }
          }
          
          if(write) {
            newRandom.write(bytes);
            newRandom.writeInt(st);
          } 
        } catch (EOFException e) {
          LogService.getInstance().setThrowable(e);
          break;
        }
        point += entrySize;
      }
      
      random.close();
      newRandom.close();
      
      file.delete();
      newFile.renameTo(file);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
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
      
      writing = false;
    }
  }

  protected List<T> subList(short type) {
    Iterator<T> iterator = temps.iterator();
    List<T> entries = new ArrayList<T>();

    Comparator<Entry> comparator = null;
    while(iterator.hasNext()) {
      T entry = iterator.next();
      if(entry.getType() != type) continue;
      if(entry.getData() == null || entry.getData().length != dataSize) continue;
      entries.add(entry);
      iterator.remove();
      if(comparator == null) comparator = entry.createComparator();
    }

    if(comparator != null) Collections.sort(entries, comparator);
    return entries;
  }

  public abstract T createEntry();

  public boolean isTimeout()  { 
    return System.currentTimeMillis() - lastAccess >= 5*60*1000;
  }
  
  public void delete()  {
    file.delete();
    lastAccess = -1;
  }
  
  
  public static interface OptimizePlugin {
    public boolean isRemove(byte [] bytes);
  }
}
