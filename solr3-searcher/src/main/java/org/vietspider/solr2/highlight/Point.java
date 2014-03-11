/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.highlight;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 15, 2011  
 */
public class Point {
  
  int start;
  int end; 
  int score = 0;
  
  public Point(int score, int start, int end) {
    this.score = score;
    this.start = start;
    this.end = end;
  }
  
  public int getStart() { return start; }
  public int getEnd() { return end; }
  public int getScore() { return score; }

  public static void mergePoints(List<Point> points) {
    Collections.sort(points, new Comparator<Point>() {
      public int compare(Point p1, Point p2) {
        int v = p1.start - p2.start;
        if(v != 0) return v;
        return p1.end - p2.end;
      }
    });
    merge(points);
  }
  
  private static void merge(List<Point> points) {
    for(int i = 0; i < points.size() -1; i++) {
      Point p1  = points.get(i);
      Point p2  = points.get(i+1);
      if(p2.start == p1.start) {
        points.remove(i);
        p2.score += p1.score;
        mergePoints(points);
        return;
      }
      
      if(p2.start <= p1.end) {
        int s = p1.start;
        int e  = p2.end > p1.end ? p2.end : p1.end;
        Point p = new Point(p1.score + p2.score, s, e);
        points.remove(i);
        points.set(i, p);
        mergePoints(points);
        return;
      }
    
    }
  }
}
