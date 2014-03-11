/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.paging;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public abstract class Entry  {

  public final static short INSERT = 1;
  public final static short DELETE = -1;
  public final static short UPDATE = 0;

  protected byte[] bytes;
  protected int status = INSERT;
  protected short type = INSERT;

  protected Entry() {
  }

  public Entry(/*File file,*/ byte[] bytes, int st, short t) {
    //    this.file = file;
    this.bytes = bytes;
    this.status = st ;
    this.type = t;
  }

  public byte[] getData() { return bytes; }
  public void setData(byte[] bytes) { this.bytes = bytes; }

  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }

  public short getType() { return type; }
  public void setType(short type) { this.type = type; }

  public abstract <T extends Entry> PageIO<T> createIO(File file);

  public abstract Comparator<Entry> createComparator();

  public void update(RandomAccessFile random, long pointer) throws Exception {
    int oldStatus = random.readInt();
    if(oldStatus != status) {
      random.seek(pointer + bytes.length);
      random.writeInt(status);
    }
    
    /*long length = random.length(); 
    long pointer = 0;
    random.seek(pointer);
    while(random.getFilePointer() <= length - bytes.length) {
      byte [] newBytes = new byte[bytes.length];
      random.readFully(newBytes);
      
//      System.out.println(getMetaId(bytes)+ "/"+getMetaId(newBytes) + "/"+compare(bytes, newBytes));
      if(compare(newBytes)) {
        int oldStatus = random.readInt();
        if(oldStatus != status) {
          random.seek(random.getFilePointer()-4);
          random.writeInt(status);
        }
        return;
      } 
      pointer += bytes.length + 4;
      random.seek(pointer);
    }*/
  }

  protected boolean compare(byte [] newBytes) {
    for(int i = 0; i < bytes.length; i++) {
      if(bytes[i] != newBytes[i]) return false;
    }
    return true;
  }
  
 /* public long getMetaId(byte [] data) {
    return (((long)data[0] << 56) +
        ((long)(data[1] & 255) << 48) +
        ((long)(data[2] & 255) << 40) +
        ((long)(data[3] & 255) << 32) +
        ((long)(data[4] & 255) << 24) +
        ((data[5] & 255) << 16) +
        ((data[6] & 255) <<  8) +
        ((data[7] & 255) <<  0));
  }*/

  //  public File getFile() { return file; }
  //  public void setFile(File file) { this.file = file; }
}
