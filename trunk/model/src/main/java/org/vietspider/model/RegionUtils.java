/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 13, 2009  
 */
public class RegionUtils {
  
  public static String [] getNames(Group group) {
    return getNames(group.getProcessRegions());
  }
  
  public static String [] getNames(List<Region> regions) {
    String [] names = new String[regions.size()];
    for(int i = 0; i < names.length; i++) {
      if(regions.get(i) == null) {
        names[i] = "";
      } else {
        names[i] = regions.get(i).getName();
      }
    }
    return names;
  }
  
  public static Region[] clone(Group group) {
    return toArray(group.getProcessRegions());
  }
  
  public static List<Region> cloneList(Group group) {
    List<Region> newRegions = new ArrayList<Region>();
    List<Region> regions = group.getProcessRegions(); 
    for(int i = 0; i < regions.size(); i++) {
     newRegions.add(regions.get(i).clone());
    }
    return newRegions;
  }
  
  public static Region[] toArray(List<Region> regions) {
    Region[] newRegion = new Region[regions.size()];
    for(int i = 0; i < regions.size(); i++) {
     newRegion[i] = regions.get(i).clone();
    }
    return newRegion;
  }
  
  public static List<Region> toList(Source source) {
    List<Region> regions = new ArrayList<Region>();
    if(source.getProcessRegion() == null) return regions;
    for(int i = 0; i < source.getProcessRegion().length; i++) {
      Region region = source.getProcessRegion()[i];
      if(region == null) continue;
      regions.add(region);
     }
    return regions;
  }
  
  public static void merge(List<Region> regions, List<Region> regions2) {
    if(regions.size() < 1) {
      regions.addAll(regions2);
      return;
    }
    
    for(int i = 0; i < regions.size(); i++) {
      Region region = regions.get(i);
      for(int k = 0; k < regions2.size(); k++) {
        Region region2 = regions2.get(k);
        if(region2.getName().equals(region.getName())) {
          region.setPaths(region2.getPaths());
          region.setProperties(region2.getProperties());
          region.setType(region2.getType());
        }
      }
    }
  }
  
 /* public static void main(String[] args) {
    String [] values = {"ths suc", "bb"};
    String [] newValues = Arrays.copyOf(values, 2);
    for(int i = 0; i < newValues.length; i++) {
      System.out.println(newValues[i]);
    }
  }*/
}
