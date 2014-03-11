/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.result;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 12, 2009  
 */
public class CachedEntry2 {

  private ConcurrentHashMap<Long, Float> hentries;
//  private int size = 0;

  private long total = 0;

  private File file;

  public CachedEntry2() {
    hentries = new ConcurrentHashMap<Long, Float>();/*new Comparator<DocEntry>() {

      public int compare(DocEntry entry1, DocEntry entry2) {
        if(entry1.getMetaId() == entry2.getMetaId()) return 0;
        return entry2.getScore() - entry1.getScore();
      }

    });*/
  }

  public CachedEntry2(File file) {
    this();
    this.file = file;
  }

  //  public boolean isFromFile() { return file.length() > 10; }

  public void increateTotal(int value) { total += value; }

  public long getTotalData() { return total; }

  public void addDocEntry(Long id, Float score ) {
    hentries.put(id, score);
    //    size++;
  }

  public int collectionSize() { return /*size*/ hentries.size();}

  public int getTotalPage(int pageSize) {
    int totalEntries = 0;
    if(!hentries.isEmpty()) {
      totalEntries = hentries.size();
    } else {
      totalEntries = (int)(file.length()/12);
    }

    if(totalEntries%12 == 0) {
      return  totalEntries/pageSize;
    }
    return totalEntries/pageSize + 1;
  }

  public List<DocEntry> loadPageByAsc(int page, int pageSize){
    List<DocEntry> entries = new ArrayList<DocEntry>();
    if(!hentries.isEmpty()) {
      int start = (page - 1) * pageSize;
      if (start < 0) return entries;

      Iterator<Map.Entry<Long, Float>> iterator = hentries.entrySet().iterator();
      int  counter = 0; 
      while(iterator.hasNext()) {
        Map.Entry<Long, Float> mapEntry = iterator.next();
        DocEntry entry = new DocEntry(mapEntry.getKey(), (int)mapEntry.getValue().floatValue());
        if(counter >= start) entries.add(entry);
        if (entries.size()>= pageSize) break;
        counter++;
      }
      return entries;
    }

    if(!file.exists() || file.length() < 12) return entries;
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");
      random.seek(0);
      total = random.readLong();

      long length = random.length();
      //      int entrySize = dataSize + 4;
      long start = (page - 1) * 12 * pageSize + 12;
      if (start < 0) return entries;

      random.seek(start);
      while (true){
        if (start >= length) break;

        DocEntry entry = new DocEntry(random.readLong());
        entry.setScore(random.readInt());

        entries.add(entry);
        if (entries.size()>= pageSize) break;
        start += 12; 
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

  public void save() {
    DocEntry [] entries = new DocEntry [hentries.size()];

    Iterator<Map.Entry<Long, Float>> iterator = hentries.entrySet().iterator();
    int  index = 0; 
    while(iterator.hasNext()) {
      Map.Entry<Long, Float> mapEntry = iterator.next();
      entries[index] = new DocEntry(mapEntry.getKey(), (int)mapEntry.getValue().floatValue());
      index++;
    }
    java.util.Arrays.sort(entries, new DocEntry.DocEntryComparator());
    append(entries);
  }

  protected void append(DocEntry [] entries) {
    //    System.out.println(" prepare append file "+ file.getAbsolutePath()+ " -> "+ entries.size());
    RandomAccessFile random = null;
    FileChannel channel = null;
    try {
      random = new RandomAccessFile(file, "rws");
      channel = random.getChannel();

      random.seek(0);

      ByteBuffer buff = ByteBuffer.allocateDirect(entries.length*12 + 12);
      buff.putLong(total);
      buff.putInt(0);
      for(int i = 0; i < entries.length; i++){
        buff.putLong(entries[i].getMetaId());
        buff.putInt(entries[i].getScore());
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

}
