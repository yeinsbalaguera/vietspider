/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TextElement {
  
  TextElement previous;
  TextElement next;
  
  private String value;
  private String lower;
  private HashMap<Short, List<Point>> nlpdata;
  
  public TextElement(String value) {
    this.nlpdata = new HashMap<Short, List<Point>>();
    this.value = value;
  }
  
  public TextElement() {
    this.nlpdata = new HashMap<Short, List<Point>>();
  }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  
  public String getLower() {
    if(lower  != null) return lower;
    if(value == null) return value;
    lower = value.toLowerCase();
    return lower;
  }

  public HashMap<Short, List<Point>> getNlpdata() { return nlpdata; }
  
  public List<Point> getPoint(short type) {
    return nlpdata.get(type);
  }
  
  public List<Point> getPoints() {
    List<Point> points = new ArrayList<Point>();
    Iterator<Map.Entry<Short, List<Point>>> iterator = nlpdata.entrySet().iterator();
    while(iterator.hasNext()) {
      List<Point> list = iterator.next().getValue();
      for(int i = 0; i < list.size(); i++) {
        Point p = list.get(i);
        while(p != null) {
          points.add(p);
          p = p.next;
        }
      }
    }
    return points;
  }
  
  public Point putPoint(short type, int score, int start, int end) {
    Point point = new Point(score, start, end);
    putPoint(type, point);
    return point;
  }
  
  public void putPoint(short type, Point point) {
    List<Point> points = nlpdata.get(type);
    if(points == null) {
//      System.out.println(" type "+ type + " : "+ points);
      points = new ArrayList<Point>();
      nlpdata.put(type, points);
    }
    points.add(point);
  }
  
  public TextElement previous() { return previous; }
  public TextElement next() { return next; }
  public boolean hasNext() { return next != null; }
  
  public static class Point { 
    
    int start;
    int end;
    int score;
    
    Properties properties = new Properties();
    
    Point next;
    
    public Point(int score, int start, int end) {
      this.score = score;
      this.start = start;
      this.end = end;
    }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    public int getEnd() { return end; }
    public void setEnd(int end) { this.end = end; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Point getNext() { return next; }
    public void setNext(Point next) { this.next = next; }
    
    public void setProperty(String name, String value) {
      properties.setProperty(name, value);
    }
    
    public String getProperty(String name) {
      return properties.getProperty(name);
    }
    
  }
  
  
}
