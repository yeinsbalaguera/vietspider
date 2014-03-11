/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpNormalize;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2011  
 */
public class AreaNormalization implements INlpNormalize {

  @SuppressWarnings("all")
  public void normalize(String id, HashMap<Short, Collection<?>> map) {
    List<Unit> list = (List<Unit>) map.get(NLPData.AREA);
    if(list == null) return;
    
    if(list.isEmpty()) {
      map.put(NLPData.AREA, null);
      return;
    }
    
    Collection<?> values = map.get(NLPData.ACTION_OBJECT);
    if(values == null || values.size() < 1) {
      map.put(NLPData.AREA, null);
      return;
    }
    float [] range = range(values);
    map.put(NLPData.AREA, process(list, range));
    
//    List<Unit> results = (List<Unit>) map.get(NLPData.AREA);
//    
//    if(results == null) return;
//    if(results.size() > 0) return;
    
//    System.out.println("=========================================================");
//    Iterator<?> iterator = values.iterator();
//    while(iterator.hasNext()) {
//      String key = iterator.next().toString();
//      System.out.println(" ==== > " + key + " : "+ NLPData.action_object(key));
//    }
//   
//    for(int i = 0; i < list.size(); i++) {
//      System.out.println(list.get(i).toString());
//    }
  }
  
  private float[] range(Collection<?> values) {
    Iterator<?> iterator = values.iterator();
    while(iterator.hasNext()) {
      String key = iterator.next().toString();
      if(key.endsWith(",2")
          || key.endsWith(",7")
          || key.endsWith(",10")) return null;
      
      if(key.endsWith(",5")) {
        return new float[]{50.0f, 1500.0f}; 
      }
      if("1,6".equals(key)) {
        return new float[]{10.0f, 1500.0f}; 
      }
      if("1,1".equals(key) ||
          "2,1".equals(key) || "5,0".equals(key)) {
        return new float[]{10.0f, 10000.0f}; 
      } else if(key.endsWith(",3")) {
        return new float[]{5.0f, 5000.0f}; 
      } else if("2,7".equals(key)) {
        return new float[]{10.0f, 100000.0f};
      } else if("1,4".equals(key) 
          || "2,4".equals(key)) {
        return new float[]{30.0f, 500.0f};
      }
    }
    
    return new float[]{10.0f, 500.0f};
  }
  
  private List<Unit> process(List<Unit> list, float[] range) {
    Unit unit = null;
    
    if(range != null) {
      for(int i = 0; i < list.size(); i++) {
        Unit temp = remove(list.get(i), range[0], range[1]);
        if(temp == null) continue;
        if(unit == null) {
          unit = temp;
          continue;
        }
        unit.setNext(temp);
      }
    } else if(list.size() > 0){
      unit = list.get(0);
      for(int i = 1; i < list.size(); i++) {
        float f = list.get(i).getValue();
        if(f < 10 && unit.getValue() - f >= 30) continue;
        unit.setNext(list.get(i));
      }
    }
    
    List<Unit> units = new ArrayList<Unit>();
    if(unit == null) return units;
    
    unit.sort();
//    System.out.println(unit.toString());
    
    units.add(unit);
    return units;
  }
  
  private Unit remove(Unit unit, float min, float max) {
    Unit value = null;
    Unit current = null;
    while(unit != null) {
      float area = unit.getValue();
//      System.out.println(area + " : "+ (area < min ||  area >= max));
      if(area < min ||  area >= max) {
//        System.out.println(" remove: " + area);
        unit = unit.getNext();
        continue;
      }
      
//      System.out.println(area);
      
      if(value == null) {
        value =  new Unit(area, unit.getLabel());
        current = value;
        unit = unit.getNext();
        continue;
      }
      
      current.setNext(area);
      current = current.getNext();
      unit = unit.getNext();
    }
//    System.out.println(value.toString());
    return value;
  }


}
