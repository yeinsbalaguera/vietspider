/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.place;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 1, 2011  
 */
public class PlaceWords {

  private List<PlaceWord> list = new ArrayList<PlaceWord>();
  private Place city;
  
  PlaceWords(Place city) {
    this.city = city;
    for(int i = 0; i < city.getChildren().size(); i++) {
      list.add(new PlaceWord(city.getChildren().get(i)));
    }
  }
  
  public Place getCity() { return city; }
  public void setCity(Place city) { this.city = city; }

  public List<PlaceWord> getList() { return list; }
  public void setList(List<PlaceWord> list) { this.list = list; }

  public void add(TextElement element, String district, String street) {
    for(int i = 0; i < list.size(); i++) {
      PlaceWord place =  list.get(i);
      if(place.getDistrict().getName().equalsIgnoreCase(district)) {
        place.add(element, street);
        return;
      }
    }
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      if(builder.length() > 0) builder.append("\n\n");
      builder.append(list.get(i).toString());
    }
    return builder.toString();
  }
  
}
