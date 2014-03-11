/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 11, 2009  
 */
public class FileNameComparator implements Comparator<File>{

  public int compare(File f1, File f2) {
    return compare(f1.getName(), f2.getName());
  }

  private int compare(String name1, String name2) {
    int max = Math.min(name1.length(), name2.length());
    int i = 0;
    while(i < max) {
      char c1 = name1.charAt(i);
      char c2 = name2.charAt(i);
      if(!Character.isDigit(c1) 
          || !Character.isDigit(c2)) {
        i++;
        continue;
      }

      int v1 = Integer.parseInt(String.valueOf(c1));
      int v2 = Integer.parseInt(String.valueOf(c2));
      if(v1 > v2) return -1;
      if(v1 < v2) return 1;
      i++;
    }
    return 0;
  }

  /*public static void main(String[] args) {
      File folder = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\content\\cindexed\\");
      File [] files = folder.listFiles();
      java.util.Arrays.sort(files, new FileNameComparator());
      for(int i = 0; i < files.length; i++) {
        System.out.println(files[i].getName());
      }
    }*/

}
