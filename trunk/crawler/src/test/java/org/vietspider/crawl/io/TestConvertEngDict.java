/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RandomAccess;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 26, 2009  
 */
public class TestConvertEngDict {
  
  public static void main(String[] args) throws Exception {
    File fileInput  = new File("D:\\Temp\\anhviet109K.index");
    RandomAccessFile random = new RandomAccessFile(fileInput, "r");
    RandomAccess access = new RandomAccess();
    
    String line = null;
    Set<String> set = new HashSet<String>();
    while((line = access.readLine(random)) != null) {
      int index = indexOf(line);
      if(index < 0) continue;
      String word = line.substring(0, index);
      set.add(word.toLowerCase());
    }
    
    File fileOuput  = new File("D:\\Temp\\english.single.word");
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    Iterator<String> iterator = set.iterator();
    while(iterator.hasNext()) {
      String value = iterator.next();
      writer.append(fileOuput, (value + "; ").getBytes());
    }
  }
  
  private static int indexOf(String value) {
    int index = 0;
    while(index < value.length()) {
      if(!Character.isLetterOrDigit(value.charAt(index))) break;
      index++;
    }
    return index;
  }
}
