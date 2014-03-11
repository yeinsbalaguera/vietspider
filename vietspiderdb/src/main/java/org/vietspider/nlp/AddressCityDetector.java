/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.Country;
import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 31, 2011  
 */
public class AddressCityDetector extends AbstractCityDetector {

  public List<Address> detectCity(Country country, TextElement root) {
    List<Address> addresses = new ArrayList<Address>();
    if(country == null) return addresses;

    List<Place> cities = country.getCities();

    TextElement element = root;
    while(element != null) {
      if(contains(addresses, element)) {
        element = element.next();
        continue;
      }
      String lower = element.getLower();
      //      int max = lower.indexOf("liên hệ");
      //      if(max < 0) max = lower.indexOf("lien he");

      if(country.getHCM() != null) {
        int[] indexes = country.getHCM().indexOf(lower);
        
        if(!ignores(lower, indexes[0])) {
//        if(indexes[0] > -1 /*|| (max > -1 && index > max)*/)  {
          add(addresses, country.getHCM(), element, indexes[0], indexes[1]);
        }
      }

      if(country.getHN() != null) {
        int[] indexes = country.getHN().indexOf(lower);
        
        if(!ignores(lower, indexes[0])) {
//        if(indexes[0] > -1 /*|| (max > -1 && index > max)*/)  {
//                    System.out.println(element.getValue());
          add(addresses, country.getHN(), element, indexes[0], indexes[1]);
        }
      }

      for(int i = 0; i < cities.size(); i++) {
        int[] indexes = cities.get(i).indexOf(lower);
//        if(indexes[0] > -1) {
//          System.out.println(cities.get(i).getName());
////          System.out.println(lower);
//        }
        
        if(!ignores(lower, indexes[0])) {
//        System.out.println(cities.get(i).getName() +  " : "+ indexes[0]);
//        if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) continue;
//                System.out.println(cities.get(i).getName());
          add(addresses, cities.get(i), element, indexes[0], indexes[1]);
        }
      }

      //      if(max > -1) break;
      element = element.next();
    }
    
//    System.out.println(addresses.size());

    if(addresses.size() < 1 && country.getHCM() != null) {
      detectHCM(addresses, country, root);
    }

    if(addresses.size() < 1 && country.getHN() != null) {
      detectHN(addresses, country, root);
    }
    
//    removeDaNangFromHaiPhong(addresses);
    

    return addresses;
  }
  

  private void detectHCM(List<Address> addresses, 
      Country country, TextElement root) {
    TextElement element = root;
    //      System.out.println(element);
    List<Place> districtes = country.getHCM().getChildren();
    while(element != null) {
      if(contains(addresses, element)) {
        element = element.next();
        continue;
      }
      String lower = element.getLower();
      //        int max = lower.indexOf("liên hệ");
      //        if(max < 0) max = lower.indexOf("lien he");
      //        System.out.println(lower);

      //        System.out.println(cities.get(0).getName());
      for(int i = 0; i < districtes.size(); i++) {
        int[] indexes = districtes.get(i).indexOf(lower);
        //          System.out.println(districtes.get(0).getName() + " : "+ index); 
        if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) continue;
        add(addresses, country.getHCM(), element, indexes[0], indexes[1]);
      }
      //        if(max > -1) break;
      element = element.next();
    }
  }

  private void detectHN(List<Address> addresses, 
      Country country, TextElement root) {
    List<Place> districtes = country.getHN().getChildren();

    List<Place> streets = null;

    TextElement element = root;
    while(element != null) {
      if(contains(addresses, element)) {
        element = element.next();
        continue;
      }
      String lower = element.getLower();
      for(int i = 0; i < districtes.size(); i++) {
        if("hn1".equals(districtes.get(i).getName())) {
          streets = districtes.get(i).getChildren();
          continue;
        }
        int[] indexes = districtes.get(i).indexOf(lower);
        if(indexes[0] < 0) continue;
        add(addresses, country.getHN(), element, indexes[0], indexes[1]);
      }
      element = element.next();
    }

    if(addresses.size() > 1 || streets == null) return;

    element = root;
    while(element != null) {
      if(contains(addresses, element)) {
        element = element.next();
        continue;
      }
      for(int i = 0; i < streets.size(); i++) {
        int[] indexes = streets.get(i).indexOf(element.getLower());
        if(indexes[0] < 0) continue; 
        add(addresses, country.getHN(), element, indexes[0], indexes[1]);
      }
      element = element.next();
    }

  }

}
