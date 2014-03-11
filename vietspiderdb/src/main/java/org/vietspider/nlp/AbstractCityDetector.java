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
 * Apr 5, 2011  
 */
public abstract class AbstractCityDetector {
  
  public static boolean ignores(String text, int index) {
    if(index < 0) return true;
    String previous = previous(text, index);
//    System.out.println(previous );
//    System.out.println("==  > "+ previous.endsWith("chuyển nhà ra"));
    if(previous.startsWith("công ty") 
        || previous.startsWith("sàn bất động sản")
        || previous.endsWith("chuyển nhà ra")
        || previous.startsWith("cty")
        || previous.indexOf("liên hệ") > -1
        || previous.indexOf("đường") > -1) {
//      System.out.println(" false ");
      return true;
    }
    return false;
  }

  public static String previous(String text, int index) {
    //  System.out.println(" vao day ");
    int end = index; 
    while(index > -1) {
      char c = text.charAt(index);
      if(c == '.' 
        || c == ',' 
          || c == '!'
            || c == ':'
              || c == ';') break;
      index--;
      //    System.out.println(index);
    }
    if(index < 0) return text.substring(0, end).trim();
    return text.substring(index, end).trim();
  }
  

  protected boolean contains(List<Address> addresses, TextElement element) {
    for(int i = 0; i < addresses.size(); i++) {
      if(addresses.get(i).contains(element)) return true;
    }
    return false;
  }
  
  protected void add(List<Address> addresses, 
      Place city, TextElement element, int start, int length) {
    Address address = new Address(city, element, start, length);

    for(int i = 0; i < addresses.size(); i++) {
      if(addresses.get(i).getPlace() == address.getPlace()) {
        addresses.get(i).increaseTime();
        if(address.score() <= addresses.get(i).score()) return;
        address.setTime(addresses.get(i).getTime());
        addresses.set(i, address);
        return;
      }
    }
    addresses.add(address);
  }


  /*private void removeDaNangFromHaiPhong(List<Address> addresses) {
  boolean exist = false;
  for(int i = 0; i < addresses.size(); i++) {
    if("hải phòng".equals(addresses.get(i).getPlace().getName())) {
      exist = true;
      break;
    }
  }

  if(!exist) return;

  Iterator<Address>  iterator = addresses.iterator();
  while(iterator.hasNext()) {
    Place place = iterator.next().getPlace();
    if("đà nẵng".equals(place.getName())) {
      iterator.remove();
    }
  }
}*/
}
