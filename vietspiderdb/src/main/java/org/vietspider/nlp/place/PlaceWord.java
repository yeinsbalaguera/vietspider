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
 * Jan 21, 2011  
 */
public class PlaceWord {
  
  private Place district;
  private List<String> children  = new ArrayList<String>();
  private List<TextElement> text_elements  = new ArrayList<TextElement>();
  
  public PlaceWord(Place place) {
    this.district = place;
  }
  
  public Place getDistrict() { return district; }
  public void setDistrict(Place district) { this.district = district; }
  
  public List<String> getChildren() { return children; }
  public void setChildren(List<String> children) { this.children = children; }
  
  public void add(TextElement element, String street) {
    if(district.contains(street)) return;
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i).equalsIgnoreCase(street)) return;
    }
    text_elements.add(element);
    children.add(street);
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if(children.size() < 1) return builder.toString();
    builder.append("@|name: ").append(district.getName());
    builder.append("\n");
    for(int i = 0; i < children.size(); i++) {
      builder.append(children.get(i)).append('\n');
      builder.append("//").append(text_elements.get(i).getValue()).append('\n');
    }
    return builder.toString();
  }
  
 
}
