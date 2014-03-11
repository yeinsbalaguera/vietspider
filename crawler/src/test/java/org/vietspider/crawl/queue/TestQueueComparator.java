/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2011  
 */
public class TestQueueComparator {
  public static void main(String[] args) {
    List<Source> sources = new ArrayList<Source>();
    Source source1 = new Source();
    source1.setName("name1");
    source1.setCategory("cate1");
    source1.setGroup("group1");
    Calendar calendar = Calendar.getInstance();
//    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) -1);
    source1.setPriority(1);
    source1.setLastCrawledTime(calendar.getTimeInMillis());
    sources.add(source1);
    
    Source source2 = new Source();
    source2.setName("name2");
    source2.setCategory("cate2");
    source2.setGroup("group2");
    source2.setPriority(0);
    calendar = Calendar.getInstance();
//    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    source2.setLastCrawledTime(calendar.getTimeInMillis());
    sources.add(source2);
    
    final long current = System.currentTimeMillis();
    Collections.sort(sources, new Comparator<Source>() {
      public int compare(Source source1, Source source2) {
        long last1 = current - source1.getLastCrawledTime();
        long last2 = current - source2.getLastCrawledTime();
        long out = 12*60*60*1000l;
        if(last1 >= out && last2 < out) return -1;
        if(last1 < out && last2 >= out) return 1;
        return source1.getPriority() - source2.getPriority();
      }
    });
    
    for(Source source : sources) {
      System.out.println(source);
    }
  }
}
