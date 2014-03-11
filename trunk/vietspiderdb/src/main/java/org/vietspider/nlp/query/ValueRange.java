/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.query;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2011  
 */
public abstract class ValueRange<T> {
  
  public final static short RANGE = 100;
  public final static short AREA_RANGE = 101;
  public final static short PRICE_TOTAL_RANGE = 102;
  public final static short PRICE_M2_RANGE = 103;
  public final static short PRICE_MONTH_RANGE = 104;

  private short type;
  private List<Range<T>> ranges;
  
  public ValueRange(short type) {
    this.type = type;
    ranges = new ArrayList<Range<T>>();
  }

  public short getType() { return type; }

  public List<Range<T>> getRanges() {
    return ranges;
  }

  public void addRange(T min, T max) {
    this.ranges.add(new Range<T>(min, max));
  }
  
  public boolean valid(String text)  {
    T value = create(text);
    for(int i = 0; i < ranges.size(); i++) {
      Range<T> range = ranges.get(i);
      if(compareMin(range, value)
          && compareMax(range, value))  return true;
    }
    
    return false;
  }
  
  public String[] toQueries() {
    String [] queries = new String[ranges.size()];
    for(int i = 0; i < queries.length; i++) {
      queries[i] = ranges.get(i).toQuery();
    }
    return queries;
  }
  
  public int size() { return ranges.size(); }
  
  public String toQuery(int index) { return ranges.get(index).toQuery(); }
  
  abstract T create(String value) ;
  
  abstract boolean compareMin(Range<T> range, T value) ;
  
  abstract boolean compareMax(Range<T> range, T value) ;
  
  public static class DoubleValueRange extends ValueRange<Double>{
    
    public DoubleValueRange(short type) {
      super(type);
    }

    Double create(String value) { return Double.valueOf(value); }

    @Override
    boolean compareMin(Range<Double> range, Double value) {
      return value >= range.min;
    }

    @Override
    boolean compareMax(Range<Double> range, Double value) {
      return value <= range.max;
    }
    
  }
  
 public static class FloatValueRange extends ValueRange<Float>{
    
    public FloatValueRange(short type) {
      super(type);
    }

    Float create(String value) { return Float.valueOf(value); }

    @Override
    boolean compareMin(Range<Float> range, Float value) {
      return value >= range.min;
    }

    @Override
    boolean compareMax(Range<Float> range, Float value) {
      return value <= range.max;
    }
    
  }
  
  private static class Range<T> {
    
    private T min;
    private T max;
    
    Range(T min, T max) {
      this.min = min;
      this.max = max;
    }
    
    String toQuery() {
      StringBuilder builder = new StringBuilder(20);
      builder.append('[').append(min).append(" TO ").append(max).append(']');
      return builder.toString();
    }
    
  }
  
  /*Unit unit = (Unit)iterator.next();
  float min = 0;
  float max = 0;
  if(unit.getNext() != null 
      && unit.getNext().getNext() == null) {
    min = unit.getValue();
    max = unit.getNext().getValue();
    _query.add(areaParser.parse("[" + min + " TO " + max+ "]"), clause);
  } else {
    while(unit != null) {
      if(unit.getValue() >= 20) {
        min = unit.getValue() - 3;
        max = unit.getValue() + 3;
      } else {
        min = unit.getValue() - 1;
        max = unit.getValue() + 1;
      }
      _query.add(areaParser.parse("[" + min + " TO " + max+ "]"), clause);
      unit = unit.getNext();
    }
  }*/
  
  
}
