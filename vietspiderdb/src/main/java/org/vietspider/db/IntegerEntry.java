/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Comparator;

import org.vietspider.bean.Article;
import org.vietspider.paging.Entry;
import org.vietspider.paging.PageIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class IntegerEntry extends Entry {
  
  private int id = -1;

  public IntegerEntry() {
    bytes = new byte[4];
  }
  
  public IntegerEntry(int v) {
    this(v, Article.WAIT, Entry.INSERT);
  }
  
  public IntegerEntry(int v, int st) {
    this(v, st, Entry.INSERT);
  }

  public IntegerEntry(int v, int st, short t) {
    bytes = new byte[4];
    bytes[0] = (byte)((v >>> 24) & 0xFF);
    bytes[1] = (byte)((v >>> 16) & 0xFF);
    bytes[2] = (byte)((v >>>  8) & 0xFF);
    bytes[3] = (byte)((v >>>  0) & 0xFF);
    
    this.id = v;
    this.status = st;
    this.type = t;
  }

  public int getId() {
    if(id != -1) return id;
    id = ((bytes[0] << 24) + ((bytes[1] & 0xFF) << 16) 
        + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF));
    return id;
  }

  public Comparator<Entry> createComparator() {
    return new Comparator<Entry>() {
      public int compare(Entry o1, Entry o2) {
        IntegerEntry entry1 = (IntegerEntry)o1;
        IntegerEntry entry2 = (IntegerEntry)o2;
        return entry1.getId() - entry2.getId();
      }
    };
  }

  @SuppressWarnings("all")
  public PageIO<IntegerEntry> createIO(File file) {
    PageIO<IntegerEntry> io = new PageIO<IntegerEntry>() {
      public IntegerEntry createEntry() {
        return new IntegerEntry();
      }
    };
    io.setData(file, 4);
    return io;
  }
  
  @Override()
  public void update(RandomAccessFile random, long pointer) throws Exception {
    int oldStatus = random.readInt();
    if(oldStatus < status || status > 1) {
      random.seek(pointer + bytes.length);
      random.writeInt(status);
    }
  }
  
 /* public static void main(String[] args) {
    int id = 538205484;
    IntegerEntry entry = new IntegerEntry(id);
    System.out.println(id + " / "+entry.getId());
    System.out.println("Giao máy miễn phí trong nội thành HN (hà nội)".length());
  }*/


}
