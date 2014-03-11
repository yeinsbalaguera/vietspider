/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 29, 2011  
 */
public class Addresses {
  
  private List<Address>  list;
  private boolean chinhchu = false;

  public List<Address> list() { return list; }
  public void setList(List<Address> list) { this.list = list; }
  
  public boolean isChinhchu() { return chinhchu; }
  public void setChinhchu(boolean chinhchu) { this.chinhchu = chinhchu; }
  
  public String[] toCities(boolean filter) {
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < list.size(); i++) {
      Address address = list.get(i);
      if(filter && address.score() < 0) continue;
//      Point point = address.getPoint();
//      if(point.getStart() <= point.getEnd()) continue;
      values.add(address.getPlace().getName());
    }
    return values.toArray(new String[0]);
  }
  
  public String getAddress() {
    String [] values = toAddress(false, true);
    if(values.length < 1) return null;
    return AddressUtils.toPresentation(values[0]);
  }
  
  public String[] toAddress(boolean test, boolean filter) {
    List<String> values = new ArrayList<String>();
    if(list.size() < 1) return values.toArray(new String[0]);
    if(chinhchu) {
      String [] elements = list.get(0).toAddress(test, false);
      for(String ele : elements) {
        if(ele != null) values.add(ele);
      }
      if(values.size() == 1) return values.toArray(new String[0]);
      values.clear();
    }
    
    for(int j = 0; j < list.size(); j++) {
      Address add = list.get(j);
      String [] elements = add.toAddress(test, filter);
      for(String ele : elements) {
        if(ele.indexOf(',') < 0 && elements.length > 1) continue;
        if(ele != null) values.add(ele);
      }
    }
    return values.toArray(new String[0]);
  }
  
//  private boolean isSingle(Address address) {
//    while(address != null) {
//      if(address.getChildren().size() > 1) return false;
//      if(address.getChildren().size() == 0) return true;
//      address = address.getChildren().get(0);
//    }
//    return true;
//  }
  
}
