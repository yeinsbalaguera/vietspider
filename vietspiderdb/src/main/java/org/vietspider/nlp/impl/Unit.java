/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2011  
 */
public class Unit {
  
  private float value;
  private Unit next;
  private String label;

  public Unit(float value, String label) {
    this.value = value;
    this.label = label;
  }
  
  public String getLabel() { return label; }

  public float getValue() { return value; }
  public void setValue(float value) { this.value = value; }

  public Unit getNext() { return next; }
  public void setNext(float v) { 
    setNext(new Unit(v, label));
//    new Exception().printStackTrace();
  }
  
  private boolean hasValue(Unit unit) {
    Unit check = this;
    while(check != null) {
      if(check.value == unit.value) return true;
      check = check.next;
    }
    return false;
  }
  
  void setNext(Unit unit) {
    List<Unit> units = new ArrayList<Unit>();
    while(unit != null) {
      if(!hasValue(unit)) {
        units.add(unit);
      }
      unit = unit.next;
    }
    
    unit = this;
    while(unit.next != null) {
      unit = unit.next;
    }
    
    for(int i = 0; i < units.size(); i++) {
      unit.next = units.get(i);
      unit = unit.next;
    }
     
  }

  public void merge(Unit unit) {
    while(unit != null) {
      setNext(unit);
      unit = unit.next;
    }
    sort();
  }
  
  public boolean equals(Unit unit) {
    Unit my = this;
    while(my != null && unit != null) {
      if(my.value != unit.value) return false;
      my = my.next;
      unit = unit.next;
    }
    return my == null && unit == null;
  }
  
  public void sort() {
    List<Float> list = new ArrayList<Float>();
    Unit unit = this;
    while(unit != null) {
      list.add(unit.value);
      unit = unit.next;
    }
    Collections.sort(list);
    int i = 0;
    unit = this;
    while(unit != null) {
      unit.value = list.get(i);
      unit = unit.next;
      i++;
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    if(Math.floor(value)==Math.ceil(value)) {
      builder.append((int)value);
    } else {
      builder.append(value);
    }
    builder.append(label);//"m2"
    Unit unit = next;
    while(unit != null) {
      builder.append('-');
      if(Math.floor(unit.value)==Math.ceil(unit.value)) {
        builder.append((int)unit.value);
      } else {
        builder.append(unit.value);
      }
      builder.append(label);
      unit = unit.next;
    }
    return builder.toString();
  }

  public String toShortString() {
    int counter = 0;
    Unit unit = this;
    while(unit != null) {
      counter++;
      if(unit.next == null) break;
      unit = unit.next;
    }

    if(counter < 4) return toString();
    StringBuilder builder = new StringBuilder();
    if(Math.floor(value)==Math.ceil(value)) {
      builder.append((int)value);
    } else {
      builder.append(value);
    }
    builder.append(label);
    builder.append("->");
    if(Math.floor(unit.value)==Math.ceil(unit.value)) {
      builder.append((int)unit.value);
    } else {
      builder.append(unit.value);
    }
    builder.append(label);
    return builder.toString();
  }
}
