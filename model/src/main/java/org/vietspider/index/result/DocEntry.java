/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.result;

import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 7, 2009  
 */
public class DocEntry /* extends Entry*/ {
  
  private long metaId = -1;
  
  private int score = 0; 
  
  public DocEntry() {
  }
  
  public DocEntry(long v) {
    metaId = v;
  }
  
  public DocEntry(long v, int st) {
    metaId = v;
    score = st;
  }

 
  public long getMetaId() { return metaId;  }

  public int getScore() { return score; }

  public void setScore(int score) { this.score = score;  }
  
  public static class DocEntryComparator implements Comparator<DocEntry> {

    public int compare(DocEntry entry1, DocEntry entry2) {
      return entry2.getScore() - entry1.getScore();
    }
    
  }
 
}
