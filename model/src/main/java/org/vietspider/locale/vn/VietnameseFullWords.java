/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.io.File;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2009  
 */
public class VietnameseFullWords extends VietnameseWords {
  
  public VietnameseFullWords(File file) {
    super(file);
   }
  
  public VietnameseFullWords() {
   super("full.vn.dict.cfg");
  }
  
  /*public boolean startsWith(String value, String prefix) {
    char ta[] = value.toCharArray();
    int to = 0;
    char pa[] = prefix.toCharArray();
    int po = 0;
    int pc = prefix.length();
    if (0 > value.length() - pc) return false;
    while (--pc >= 0) {
      if(comparator.compare(ta[to++], pa[po++]) != 0) {
        return false;
      }
    }
    return true;
  }*/

}
