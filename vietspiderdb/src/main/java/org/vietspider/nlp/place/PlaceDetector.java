/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.place;

import java.util.List;

import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.Addresses;
import org.vietspider.nlp.impl.Place;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 1, 2011  
 */
public class PlaceDetector {

  private PlaceFilter filter = new PlaceFilter();
  
  PlaceWords placeWords;
  
  public PlaceDetector(Place city) {
    this.placeWords = new PlaceWords(city);
  }

  public void detect(Addresses addresses) {
     List<Address> list = addresses.list();
     for(int i = 0; i < list.size(); i++) {
       detect(list.get(i));
     }
  }

  public void detect(Address address) {
    if(address.score() < 0) return;
    if(address.getPlace().getLevel() == Place.CITY) {
      if(!address.getPlace().getName().equalsIgnoreCase(
          placeWords.getCity().getName())) return;
    }
    
    if(address.getPlace().getLevel() == Place.DISTRICT) {
      int start = address.getStart();
      List<String> values = filter.filter(address.getElement(), start);
      for(int i = 0; i < values.size(); i++) {
        placeWords.add(address.getElement(), 
            address.getPlace().getName(), values.get(i));
      }
    }
    
//    if(address.getPlace().getLevel() == Place.CITY
//        && placeWords.getCity().getName().equals("hà nội")) {
//      int start = address.getStart();
//      List<String> values = filter.filter(address.getElement(), start);
//      String districtName = placeWords.getCity().getChildren().get(0).getName();
//      for(int i = 0; i < values.size(); i++) {
//        placeWords.add(address.getElement(), districtName, values.get(i));
//      }
//    }
    
    List<Address> children  = address.getChildren();
    for(int i = 0; i < children.size(); i++) {
      detect(children.get(i));
    }
  }
  
//  private boolean isChild(Place place, String value) {
//    List<Place> children = place.getChildren();
//    for(int i = 0; i < children.size(); i++) {
//      if(children.get(i).getName().equalsIgnoreCase(value)) return true;
//    }
//    return false;
//  }

  public PlaceWords getPlaceWords() { return placeWords; }
  public void setPlaceWords(PlaceWords placeWords) { this.placeWords = placeWords; }
  
  

}
