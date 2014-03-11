/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.query;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.nlp.DistrictDetector;
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
public class QAddressDetector {

  private final static QAddressDetector instance = new QAddressDetector();

  public final static QAddressDetector getInstance() { return instance; }

  private final QAddressCityDetector cityDetector = new QAddressCityDetector();

  public Addresses detectAddresses(TextElement root) {
    Country country = null;
    try {
      country = PlaceLoader.load();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return detectAddresses(country, root);
  }
  
  public List<String> detectSuggestion(TextElement root) {
    Country country = null;
    try {
      country = PlaceLoader.load();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return cityDetector.detectSuggestion(country, root);
  }

  public Addresses detectAddresses(Country country, TextElement root) {
    Addresses addresses = new Addresses();
    List<Address> list = cityDetector.detectCity(country, root);

    if(list.size() > 0) {
      addresses.setList(list);
      detectAddresses(list, /*country,*/ root);
      return addresses;
    }

    list.add(new Address(country.getHN(), root, 0, 0));
    list.add(new Address(country.getHCM(), root, 0, 0));
    for(int i = 0; i < country.getCities().size(); i++) {
      list.add(new Address(country.getCities().get(i), root, 0, 0));
    }
    addresses.setList(list);
    detectAddresses(list, /*country, */root);

    List<Address> removes = new ArrayList<Address>();
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getChildren().size() > 0) continue;
      removes.add(list.get(i));
    }

    for(int i = 0; i < removes.size(); i++) {
      list.remove(removes.get(i));
    }

//    for(int i = 0; i < list.size(); i++) {
//      String [] data = list.get(i).toAddress(false, false);
//      for(String ele : data) {
//        System.out.println(ele);
//      }
//    }

    return addresses;
  }

  private void detectAddresses(List<Address> list, /*Country country,*/ TextElement root) {
    if(list.size() == 1) list.get(0).putScore(0);
    //    System.out.println("city size:" + addresses.size());
    for(int i = 0; i < list.size(); i++) {
      detect(/*country,*/ list.get(i), root);
    }

    for(int i = 0; i < list.size(); i++) {
      AddressUtils.removeDuplicate(list.get(i));
    }
  }

  public void detect(/*Country country,*/ Address city, TextElement root) {
    DistrictDetector pd = new DistrictDetector(city, root);
    detect(/*country,*/ pd, city.getElement());

    TextElement element = root;
    while(element != null) {
      //          if(max > -1) break;
      if(!city.contains(element)) {
        detect(/*country,*/ pd, element);
      }
      element = element.next();
    }

  }

  private void detect(/*Country country, */DistrictDetector pd, TextElement element) {
    Address city = pd.getCity();
    Place pcity = city.getPlace();
    if(pcity == null) return;
    List<Place> children = pcity.getChildren();

    for(int i = 0; i < children.size(); i++) {
      //    System.out.println(children.get(i).getName() + " : "+ children.get(i).getLevel());
      if("hn1".equals(children.get(i).getName())) continue;
      pd.detectDistrict(children.get(i), element);
    }
    
    if(city.getChildren().size() > 0) return;

    for(int i = 0; i < children.size(); i++) {
      Place place = children.get(i);
      Address child = new Address(place, element, 0, 0);
      pd.detect(child);
      if(child.getChildren().size() < 1) continue;
      city.addChild(child);
    }
  }


}
