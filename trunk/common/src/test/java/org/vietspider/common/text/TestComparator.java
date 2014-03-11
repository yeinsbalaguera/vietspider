/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 30, 2009  
 */
public class TestComparator {
  public static void main(String[] args) {
    List<String> l = new ArrayList<String>();
    //    l.add("ăn uống");
    //    l.add("y thuật");
    //    l.add("yếu tố di truyền");
    //    l.add("y vật lí");
    //    l.add("ỷ tài");

    l.add("ăn năn");
    l.add("ăn mực");
    l.add("ăn mừng");
    l.add("ăn nằm");
    Collections.sort(l, new VietComparator());
    System.out.println(l);

    /*long time = 100000;
//    Collator col = Collator.getInstance(new Locale("vi"));
    long start = System.currentTimeMillis();
    for(int i = 0; i < time; i++) {
      List<String> sortList  = new ArrayList<String>();
      sortList.addAll(l);
      Collections.sort(sortList, new VietComparator());
    }
    long end = System.currentTimeMillis();
    System.out.println("mat tong cong "+  (end - start));

    start = System.currentTimeMillis();
    for(int i = 0; i < time; i++) {
      List<String> sortList  = new ArrayList<String>();
      sortList.addAll(l);
      Collections.sort(sortList, new VietComparator2());
    }
    end = System.currentTimeMillis();
    System.out.println("mat tong cong 2 "+  (end - start));*/
  }
}
