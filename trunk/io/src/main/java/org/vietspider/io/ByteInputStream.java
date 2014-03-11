/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2007  
 */
public class ByteInputStream {
  
  private byte [] buff;
  
  private int pos = 0;
  
  public ByteInputStream(byte [] bytes) {
    this.buff = bytes;
  }
    
  public void append(byte [] bytes) {
    byte [] newBuff = new byte[(buff.length - pos) + bytes.length];
    System.arraycopy(buff, pos, newBuff, 0, (buff.length - pos));
    System.arraycopy(bytes, 0, newBuff, (buff.length - pos), bytes.length);
    buff = newBuff;
    pos = 0;
  }
  
  public int read() {
    return (pos < buff.length) ? (buff[pos++] & 0xff) : -1;
  }
  
  public boolean available() {
    return buff.length - pos >= 4;
  }
  
  public int readInt(){
    int ch1 = read();
    int ch2 = read();
    int ch3 = read();
    int ch4 = read();
    return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
  }
  
  public void move(int order) { pos += order; }

  public long readLong() {
    return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
  }

}
