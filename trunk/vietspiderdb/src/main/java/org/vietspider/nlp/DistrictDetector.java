/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.List;

import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2011  
 */
public class DistrictDetector {

  private Address city;
  TextElement root;
  private long start = System.currentTimeMillis();

  public DistrictDetector(Address city, TextElement root) {
    this.city = city;
    this.root = root;
  }
  
  public Place getCityPlace() { return city.getPlace(); }
  
  public Address getCity() { return city; }

  public int detect(Address address) {
    Place parent  = address.getPlace();
    List<Place> children = parent.getChildren();
    String lower = address.getElement().getLower();
    //        System.out.println(" ====  >"+parent.getName());
    int depth = 1;
    for(int i = 0; i < children.size(); i++) {
      int[] indexes = children.get(i).indexOf(lower);
      //      if(children.get(i).getName().indexOf("trần phú") > -1) {
      //        System.out.println("==== >"+ children.get(i).getName());
      //        System.out.println(indexes[0]);
      //        System.out.println(lower);
      //      }
      //      if(index > 0) {
      //              System.out.println(children.get(i).getName() +  " : "+ indexes[0]);
      //              System.out.println(lower);
      //        System.out.println(lower.charAt(index));
      //      }
      if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) continue; 
      Place place = children.get(i);
      address.addChild(new Address(place, address.getElement(), indexes[0], indexes[1]));
      depth = 2;
    }
    if(depth > 1) return depth;

    TextElement element = root;
    
//    List<TextElement> checks = new ArrayList<TextElement>();
    while(element != null) {
//      if(checks.contains(element)) break;
//      checks.add(element);
      //          if(max > -1) break;
      if(city.contains(element)) {
        element = element.next();
        continue;
      }

      lower = element.getLower();
      for(int i = 0; i < children.size(); i++) {
        int[] indexes = children.get(i).indexOf(lower);
        //        if(children.get(i).getName().indexOf("trần phú") > -1) {
        //          System.out.println("==== >"+ children.get(i).getName());
        //          System.out.println(indexes[0]);
        //          System.out.println(lower);
        //        }
        //      if(index > 0) {
        //        System.out.println(children.get(i).getName() +  " : "+ index);
        //        System.out.println(lower.substring(index));
        //        System.out.println(lower.charAt(index));
        //      }
        if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) continue; 
        Place place = children.get(i);
        address.addChild(new Address(place, element, indexes[0], indexes[1]));
        depth = 2;
      }
      element = element.next();
      if(System.currentTimeMillis() - start > 100l) {
//        if(checks.size() == 1) new Exception().printStackTrace();
        break;
      }
    }

    return depth;
  }

  public int detectDistrict(List<Place> children, TextElement element ) {
    int depth = 0;
    for(int i = 0; i < children.size(); i++) {
      int value = detectDistrict(children.get(i), element);
      if(value < 0) continue;
      depth = value;
    }
    return depth;
  }

  public int detectDistrict(Place place, TextElement _element ) {
    int[] indexes = place.indexOf(_element.getLower());
    //    System.out.println(children.get(i).getName() + " : "+ index);
    if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) return -1; 
    //      System.out.println(children.get(i).getName() + " : "+ index);

    Address child = new Address(place, _element, indexes[0], indexes[1]);
    city.addChild(child);
   
    return detect(child);
  }
}
