/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.query;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.nlp.AbstractCityDetector;
import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.Country;
import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 31, 2011  
 */
public class QAddressCityDetector extends AbstractCityDetector  {

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
          add(addresses, country.getHCM(), element, indexes[0], indexes[1]);
        }
      }

      if(country.getHN() != null) {
        int[] indexes = country.getHN().indexOf(lower);
        if(!ignores(lower, indexes[0])) {
          //          System.out.println(element.getValue());
          add(addresses, country.getHN(), element, indexes[0], indexes[1]);
        }
      }

      for(int i = 0; i < cities.size(); i++) {
        int[] indexes = cities.get(i).indexOf(lower);
        if(!ignores(lower, indexes[0])) {
        //        System.out.println(cities.get(i).getName());
          add(addresses, cities.get(i), element, indexes[0], indexes[1]);
        }
      }

      //      if(max > -1) break;
      element = element.next();
    }

    return addresses;
  }
  
  public List<String> detectSuggestion(Country country, TextElement element) {
    List<String> list = new ArrayList<String>();
    List<Address> addresses = new ArrayList<Address>();
    if(country.getHCM() != null) {
      detectSuggestionCity(addresses, country.getHCM(), element);
    }

    if(country.getHN() != null) {
      detectSuggestionCity(addresses, country.getHN(), element);
    }
    
    List<Place> cities = country.getCities();
    for(int k = 0; k < cities.size(); k++) {
      detectSuggestionCity(addresses, cities.get(k), element);
//      List<Place> districtes = cities.get(k).getChildren();
//
//      String lower = element.getLower();
//      for(int j = 0; j < districtes.size(); j++) {
//        int[] indexes = districtes.get(j).indexOf(lower);
//        if(indexes[0] < 0 /*|| (max > -1 && index > max)*/) continue;
//        add(addresses, cities.get(k), element, indexes[0], indexes[1]);
//      }
    }

    for(int i = 0; i < addresses.size(); i++) {
      list.add(addresses.get(i).getPlace().getName());
    }
    return list;
  }
  
  private void detectSuggestionCity(List<Address> addresses, Place city, TextElement element) {
    List<Place> districtes = city.getChildren();
    
    String lower = element.getLower();
    for(int i = 0; i < districtes.size(); i++) {
      int[] indexes = districtes.get(i).indexOf(lower);
      if(indexes[0] >=0 ) {
        add(addresses, city, element, indexes[0], indexes[1]);
        return;
      }
      
      List<Place> streets = districtes.get(i).getChildren();
      for(int j = 0; j < streets.size(); j++) {
        indexes = streets.get(j).indexOf(lower);
        if(indexes[0] >=0 ) {
          add(addresses, city, element, indexes[0], indexes[1]);
          return;
        }
      }
      //end streets
    }
  }
  

}
