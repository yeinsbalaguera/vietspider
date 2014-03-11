/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.io.File;
import java.util.Comparator;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.paging.Entry;
import org.vietspider.paging.PageIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class Md5HashEntry extends Entry {
  
  private MD5Hash md5Hash;
  
  public Md5HashEntry() {
  }
  
  public Md5HashEntry(MD5Hash md5) {
    this.md5Hash = md5;
    bytes = md5Hash.getDigest();
  }
  
  public MD5Hash getMd5Hash() {
    if(md5Hash == null) {
      md5Hash = new MD5Hash(bytes);
    }
    return md5Hash;
  }

  public void setMd5Hash(MD5Hash md5Hash) {
    this.md5Hash = md5Hash;
    bytes = md5Hash.getDigest();
  }

  public Comparator<Entry> createComparator() {
    return new Comparator<Entry>() {
      public int compare(Entry o1, Entry o2) {
        Md5HashEntry entry1 = (Md5HashEntry)o1;
        Md5HashEntry entry2 = (Md5HashEntry)o2;
        return entry1.getMd5Hash().compareTo(entry2.getMd5Hash());
      }
    };
  }

  @SuppressWarnings("all")
  public PageIO<Md5HashEntry> createIO(File file) {
    PageIO<Md5HashEntry> io = new PageIO<Md5HashEntry>() {
      public Md5HashEntry createEntry() {
        return new Md5HashEntry();
      }
    };
    io.setData(file, MD5Hash.DATA_LENGTH);
    return io;
  }

}
