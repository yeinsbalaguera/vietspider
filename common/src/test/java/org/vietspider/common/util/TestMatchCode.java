/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestMatchCode {

  private static int search(CharSequence pattern, CharSequence text) {
    int pSize = pattern.length();

    int[] values = new int[pattern.length()];
    values[0] = -1;
    
    int i = 0, j = -1;
    
    while(i < pSize - 1) {
      if (j > -1 && pattern.charAt(i) != pattern.charAt(j)) {
        j = values[j];
        continue;
      }
      i++; 
      j++;
      values[i] = pattern.charAt(i) != pattern.charAt(j) ? values[i] : values[j];
    } 
    
    i = j = 0;
    
    int mSize = text.length();
    while (i < mSize && j < pSize) {
      if (j > -1 && pattern.charAt(j) != text.charAt(i)) {
        j = values[j];
        continue;
      }
      i++;
      j++;
    }
    return j == pSize ? i - j : mSize;         
  }
  
  private static void extract() {
    File file1 = new File("F:\\Temp2\\vnexpress\\1.htm");
    File file2 = new File("F:\\Temp2\\vnexpress\\2.htm");
    try {
      if(!file2.exists()) file2.createNewFile();
      FileInputStream fileInputStream = new FileInputStream(file1);
      FileChannel channel = fileInputStream.getChannel();
      ByteBuffer buff = ByteBuffer.allocate((int)file1.length());      
      channel.read(buff);
      buff.rewind();      
      byte[] data = buff.array();

      buff.clear();
      channel.close();
      fileInputStream.close();
      
      String text = new String(data, "utf-8");
      System.out.println(text);
      String startPattern  = "table";
      String endPattern  = "<table border=0 cellpadding=0 cellspacing=0 width=\"100%\">";
      int start = search(text, startPattern);
      System.out.println(start);
      text = text.substring(start);
      int end = search(text, endPattern);
      text = text.substring(0, end);
      
      FileOutputStream output = new FileOutputStream(file2);
      FileChannel outChannel = output.getChannel();
      buff = ByteBuffer.allocateDirect(data.length);
      buff.put(data);
      buff.rewind();
      outChannel .write(buff);        
      buff.clear();
      outChannel .close();
    } catch (Exception e) {
      e.printStackTrace();
    }
      
  }

  public static void main(String[] args) {
    extract();
//    String pattern = "ds";
//    String text = "Yahoo momomoomomomomomomhdsdfdsfdsomomomomomo";
//    int offset = search(pattern, text);
  }
}