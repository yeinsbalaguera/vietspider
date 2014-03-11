/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.paging;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Comparator;

import org.vietspider.bean.Article;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class MetaIdEntry extends Entry {
  
  private long metaId = -1;

  public MetaIdEntry() {
    bytes = new byte[8];
  }
  
  public MetaIdEntry(long v) {
    this(v, Article.WAIT, Entry.INSERT);
  }
  
  public MetaIdEntry(long v, int st) {
    this(v, st, Entry.INSERT);
  }

  public MetaIdEntry(long v, int st, short t) {
    bytes = new byte[8];
    bytes[0] = (byte)(v >>> 56);
    bytes[1] = (byte)(v >>> 48);
    bytes[2] = (byte)(v >>> 40);
    bytes[3] = (byte)(v >>> 32);
    bytes[4] = (byte)(v >>> 24);
    bytes[5] = (byte)(v >>> 16);
    bytes[6] = (byte)(v >>>  8);
    bytes[7] = (byte)(v >>>  0);
    
    this.metaId = v;
    this.status = st;
    this.type = t;
  }

  public long getMetaId() {
    if(metaId != -1) return metaId;
    return (((long)bytes[0] << 56) +
        ((long)(bytes[1] & 255) << 48) +
        ((long)(bytes[2] & 255) << 40) +
        ((long)(bytes[3] & 255) << 32) +
        ((long)(bytes[4] & 255) << 24) +
        ((bytes[5] & 255) << 16) +
        ((bytes[6] & 255) <<  8) +
        ((bytes[7] & 255) <<  0));
  }

  public Comparator<Entry> createComparator() {
    return new Comparator<Entry>() {
      public int compare(Entry o1, Entry o2) {
        MetaIdEntry entry1 = (MetaIdEntry)o1;
        MetaIdEntry entry2 = (MetaIdEntry)o2;
        return (int)(entry1.getMetaId() - entry2.getMetaId());
      }
    };
  }

  @SuppressWarnings("all")
  public PageIO<MetaIdEntry> createIO(File file) {
    PageIO<MetaIdEntry> io = new PageIO<MetaIdEntry>() {
      public MetaIdEntry createEntry() {
        return new MetaIdEntry();
      }
    };
    io.setData(file, 8);
    return io;
  }
  
  @Override()
  public void update(RandomAccessFile random, long pointer) throws Exception {
    int oldStatus = random.readInt();
//    System.out.println(" chuan update"+ oldStatus+ " : "+ status);
    if(oldStatus < status || status > 1) {
//      System.out.println("chuan bi update ");
      random.seek(pointer + bytes.length);
      random.writeInt(status);
    }
    
   /* long length = random.length(); 
    long pointer = 0;
    random.seek(pointer);
    while(random.getFilePointer() <= length - bytes.length) {
      byte [] newBytes = new byte[bytes.length];
      random.readFully(newBytes);
      
//      System.out.println(getMetaId(bytes)+ "/"+getMetaId(newBytes) + "/"+compare(bytes, newBytes));
      if(compare(newBytes)) {
        int oldStatus = random.readInt();
        if(oldStatus < status || status > 1) {
//          System.out.println(" chuan bi edit roi "+ status);
          random.seek(random.getFilePointer()-4);
          random.writeInt(status);
        }
        return;
      } 
      pointer += bytes.length + 4;
      random.seek(pointer);
    }*/
  }


}
