/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 21, 2010  
 */
public class SearchResultComparator implements Comparator<SearchResult> {
  
  public int compare(SearchResult o1, SearchResult o2) {
    if(o1.getScore() > 2.0f && o2.getScore() > 2.0f) {
      long id1 = Long.parseLong(o1.getId());
      long id2 = Long.parseLong(o2.getId());
      if(id1 > id2) return -1;
      if(id1 < id2) return 1;
      return 0;
    }
    
    if(o1.getScore() > 1.0f && o2.getScore() > 1.0f) {
      long id1 = Long.parseLong(o1.getId());
      long id2 = Long.parseLong(o2.getId());
      if(id1 > id2) return -1;
      if(id1 < id2) return 1;
      return 0;
    }

    if(o1.getScore() > o2.getScore()) return -1;
    if(o1.getScore() < o2.getScore()) return 1;
    return 0;
  }
}
