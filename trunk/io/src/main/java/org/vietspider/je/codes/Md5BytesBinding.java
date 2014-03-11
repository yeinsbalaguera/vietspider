/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.je.codes;

import org.vietspider.common.io.MD5Hash;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.DatabaseEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class Md5BytesBinding implements EntryBinding<byte[]> {

  public Md5BytesBinding() {
  }

  public byte[] entryToObject(DatabaseEntry entry) {
    int len = entry.getSize();
    if (len == 0) return null;

    byte[] bytes = new byte[MD5Hash.DATA_LENGTH];
    System.arraycopy(entry.getData(), entry.getOffset(), bytes, 0, bytes.length);
    return bytes;
  }

  // javadoc is inherited
  public void objectToEntry(byte[] bytes, DatabaseEntry entry) {
    entry.setData(bytes, 0, bytes.length);
  }
}


