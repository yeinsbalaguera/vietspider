/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.price.index.PriceIndex;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 23, 2011  
 */
public class AddressPrices {
  
  private String city;
  private String action;
  
  private double minValue = 5;
  private double maxValue = 2000;
  private double rate = 5;
  
  private List<AddressPrice> aps = new ArrayList<AddressPrice>();
  
  public AddressPrices(String city, String action) {
    this.city = city;
    this.action = action;
  }
  
  public String getCity() { return city; }

  public String getAction() { return action; }
  
  public double getMinValue() { return minValue; }
  public void setMinValue(double minValue) { this.minValue = minValue;  }

  public double getMaxValue() { return maxValue; }
  public void setMaxValue(double maxValue) { this.maxValue = maxValue; }

  public double getRate() { return rate; }
  public void setRate(double rate) { this.rate = rate; }

  public void addAddressPrice(PriceIndex index) {
    String address = index.getAddress();
    List<String> prices = index.getPrices();
    AddressPrice ap = null;
    for(int i = 0; i < aps.size(); i++) {
      if(aps.get(i).getAddress().equals(address)) {
        ap = aps.get(i);
        break;
      }
    }
    if(ap == null) {
      ap = new AddressPrice(address);
      aps.add(ap);
    }
    for(int i = 0; i < prices.size(); i++){
      ap.addPrice(prices.get(i),
          index.getId(), minValue, maxValue, rate);
    }
  }
  
  public void build(StringBuilder builder) {
    Collections.sort(aps, new Comparator<AddressPrice>() {
      public int compare(AddressPrice ap1, AddressPrice ap2) {
        return ap1.getAddress().compareTo(ap2.getAddress());
      }
    });
    
    for(int i = 0; i < aps.size(); i++) {
      if(i > 0) builder.append('\n');
      aps.get(i).build(builder);
    }
  }
  
}
