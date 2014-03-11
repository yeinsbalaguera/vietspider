/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 22, 2007  
 */
public class RandomAccess {
  
  public String readLine(RandomAccessFile random) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int c = -1;
    boolean eol = false;
    while (!eol) {
      switch (c = random.read()) {
      case -1:
      case '\n':
        eol = true;
        break;
      case '\r':
        eol = true;
        long cur = random.getFilePointer();
        if ((random.read()) != '\n') random.seek(cur);
        break;
      default:
        outputStream.write(c);
      break;
      }
    }
    if ((c == -1) && (outputStream.size() == 0)) return null;
    return new String(outputStream.toByteArray(), "UTF-8");
  }
  
}
