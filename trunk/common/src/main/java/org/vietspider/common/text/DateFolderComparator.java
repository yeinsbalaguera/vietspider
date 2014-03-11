/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 5, 2009  
 */
public class DateFolderComparator implements Comparator<String> {
  
  public int compare(String o1, String o2) {
    int size = Math.min(o1.length(), o2.length());
    for(int i = 0; i < size; i++) {
      char c1 = o1.charAt(i);
      char c2 = o2.charAt(i);
      int compare = 0;
      if(!Character.isDigit(c1) 
          || !Character.isDefined(c2)) {
        compare = c2 - c1;
      } else {
        int n1 = Integer.parseInt(String.valueOf(c1));
        int n2 = Integer.parseInt(String.valueOf(c2));
        compare = n2 - n1;
      }
      if(compare != 0) return compare;
    }
    return 0;
  }
  
}
