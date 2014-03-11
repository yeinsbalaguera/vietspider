/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 30, 2011  
 */
public class AddressUtils {
  
  public static void removeDuplicate(Address address) {
    List<Address> list = new ArrayList<Address>();
    collect(list, address, Place.STREET);
    for(int i = 0; i < list.size(); i++) {
      Address child1 = list.get(i);
      for(int j = i + 1; j < list.size(); j++) {
        Address child2 = list.get(j);
        if(!compare(child1, child2)) continue;
        if(child1.score() < child2.score()) {
          child1.getParent().getChildren().remove(child1);
          continue;
        }
        child2.getParent().getChildren().remove(child2);
      }
    }
    
    list.clear();
    collect(list, address, Place.DISTRICT);
    for(int i = 0; i < list.size(); i++) {
      Address child1 = list.get(i);
      for(int j = i + 1; j < list.size(); j++) {
        Address child2 = list.get(j);
        if(child1.getPlace() != child2.getPlace()) continue;
          
        if(child1.getChildren().size() > 0 
            && child2.getChildren().size() < 1) {
//          System.out.println(child1.getElement().getValue());
//          System.out.println(child2.getElement().getValue());
//          System.out.println(child1.score()+  " : "+ child2.score());
          if(child2.score() > child1.score()) {
            child1.putScore(child2.score());
          }
          child2.getParent().getChildren().remove(child2);
          continue;
        } else if(child1.getChildren().size() < 1 
            && child2.getChildren().size() > 0) {
//          System.out.println(child1.score()+  " : "+ child2.score());
          if(child1.score() > child2.score()) {
            child2.putScore(child1.score());
          }
          child1.getParent().getChildren().remove(child1);
          continue;
        } else if(child1.getChildren().size() == 0 
            && child2.getChildren().size() == 0) {
          if(child1.score() < child2.score()) {
            child1.getParent().getChildren().remove(child1);
            continue;
          }
          child2.getParent().getChildren().remove(child2);
        } 
      }
    }
  }
  
  private static void collect(List<Address> list, Address parent, short level) {
    if(parent.getPlace().getLevel() == level) list.add(parent);
    List<Address> children = parent.getChildren();
    for(int i = 0; i < children.size(); i++) {
      collect(list, children.get(i), level);
    }
  }
  
  private static boolean compare(Address address1, Address address2) {
//    System.out.println(address1.getPlace().getName() + " : "+ address2.getPlace().getName());
    while(address1 != null && address2 != null) {
      if(address1.getPlace() != address2.getPlace()) return false;
      address1 = address1.getParent();
      address2 = address2.getParent();
    }
    return address1 == address2;
  }
  
  public static String toPresentation(String text) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < text.length(); i++) {
      if(i == 0 || Character.isWhitespace(text.charAt(i - 1))) {
        builder.append(Character.toUpperCase(text.charAt(i)));
      } else {
        builder.append(text.charAt(i));
      }
    }
    return builder.toString();
  }
  

}
