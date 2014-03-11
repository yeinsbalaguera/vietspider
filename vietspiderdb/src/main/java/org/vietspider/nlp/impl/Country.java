/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2011  
 */
public class Country {

  private Place hn;
  private Place hcm;

  private List<Place> cities = new ArrayList<Place>();

  public List<Place> getCities() { return cities; }
  public void setCities(List<Place> list) { this.cities = list; }
  
  public Place addDefaultCity(String name) {
//    String name = place.getName();
    if(hn != null && name.equals(hn.getName())) return null;
    if(hcm != null && name.equals(hcm.getName())) return null;
    for(int i = 0; i < cities.size(); i++) {
      if(cities.get(i).getName().equals(name)) return null;
    }
    Place place = new Place(name);
    cities.add(place);
//    System.out.println(place.getName());
    return place;
  }

  public Place addCity(String _name) {
    _name = _name.trim().toLowerCase();
    for(int i = 0 ; i < cities.size(); i++) {
      if(cities.get(i).getName().equals(_name)) {
        return cities.get(i);
      }
    }


    if(_name.equals("hà nội")) {
      if(hn != null) return hn;
      hn  = new Place(_name);
      return hn;
    } else if(_name.equals("thành phố hồ chí minh")) {
      if(hcm != null) return hcm;
      hcm = new Place(_name);
      return hcm;
    }
    Place place = new Place(_name);
    cities.add(place);
    return place;
  }

  public Place getHN() { return hn; }
  public void setHn(Place hn) { this.hn = hn; }

  public Place getHCM() { return hcm; }
  public void setHcm(Place hcm) { this.hcm = hcm; }

}
