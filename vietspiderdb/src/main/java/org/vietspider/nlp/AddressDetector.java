/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.AddressUtils;
import org.vietspider.nlp.impl.Addresses;
import org.vietspider.nlp.impl.Country;
import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.impl.PlaceLoader;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2011  
 */
public class AddressDetector {
  
  private final static AddressDetector instance = new AddressDetector();
  
  public final static AddressDetector getInstance() { return instance; }

  private final AddressCityDetector cityDetector = new AddressCityDetector();
  
  public Addresses detectAddresses(TextElement root) {
    Country country = null;
    try {
      country = PlaceLoader.load();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return detectAddresses(country, root);
  }
  
  public Addresses detectAddresses(Country country, TextElement root) {
    Addresses addresses = new Addresses();
    List<Address> list = cityDetector.detectCity(country, root);
    addresses.setList(list);
    if(list.size() == 1) list.get(0).putScore(0);
//    System.out.println("city size:" + addresses.size());
    for(int i = 0; i < list.size(); i++) {
      detect(country, list.get(i), root);
    }
    
    for(int i = 0; i < list.size(); i++) {
      AddressUtils.removeDuplicate(list.get(i));
    }
    
    TextElement element = root;
    while(element != null) {
      String lower = element.getLower();
      if(lower.indexOf("chính chủ") > -1
          || lower.indexOf("thổ cư") > -1) {
        addresses.setChinhchu(true);
      } 
      element = element.next();
    }
    
//    System.out.println(addresses.size());
    return addresses;
  }
 
  public void detect(Country country, Address city, TextElement root) {
    DistrictDetector pd = new DistrictDetector(city, root);
    
    detect(country, pd, city.getElement());
    
    TextElement element = root;
    while(element != null) {
//      System.out.println("hihihi ");
      //          if(max > -1) break;
      if(!city.contains(element)) {
        detect(country, pd, element);
      }
      element = element.next();
    }
    
  }
  
  private int detect(Country country, DistrictDetector pd, TextElement element) {
    Place city = pd.getCityPlace();
    List<Place> children = city.getChildren();
    
//    System.out.println("=======================================================");
//    TextElement ele = pd.root;
//    while(ele != null) {
//      System.out.println(ele.getValue());
//      System.out.println("================================================");
//      ele = ele.next();
//    }
    
    if(city != country.getHN()) {
      return pd.detectDistrict(children, element);
    }
    
    int depth = 0;
    
    for(int i = 0; i < children.size(); i++) {
      //    System.out.println(children.get(i).getName() + " : "+ children.get(i).getLevel());
      if("hn1".equals(children.get(i).getName())) continue;
      int _value = pd.detectDistrict(children.get(i), element);
      if(_value < 0) continue;
      depth = _value;
    }
    
    if(depth > 0) return depth;
    
    for(int i = 0; i < children.size(); i++) {
      if("hn1".equals(children.get(i).getName())) {
        children = children.get(i).getChildren();
        break;
      }
    }
    
    Address p_address = pd.getCity();
    for(int i = 0; i < children.size(); i++) {
      int[] indexes = children.get(i).indexOf(element.getLower());
      if(indexes[0] < 0) continue; 
      depth = 2;
      Place place = children.get(i);
      Address child = new Address(place, element, indexes[0], indexes[1]);
      p_address.addChild(child);
      if(place.getName().startsWith("quận ")) p_address = child;
    }
    
    return depth;
  }

  
}
