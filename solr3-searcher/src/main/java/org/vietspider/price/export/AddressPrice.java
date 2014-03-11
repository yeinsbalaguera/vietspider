/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 23, 2011  
 */
public class AddressPrice {
  
  private String address;
  private List<Price> prices = new ArrayList<Price>();
  
  public AddressPrice(String address) {
    this.address = address;
  }
  
  public String getAddress() {
    return address;
  }

  public void addPrice(String text, String id,  
      double min, double max, double rate) {
    double price = Double.valueOf(text);
    if(price < min || price > max) return;
    for(int i = 0; i < prices.size(); i++) {
      if(prices.get(i).value >= (price - rate)
          && prices.get(i).value <= (price + rate)) {
        prices.get(i).metaIds.add(id);
        return;
      }
    }
    prices.add(new Price(price, id));
  }
  
  public void build(StringBuilder builder) {
    builder.append(address);
    builder.append('\n');
    
    Collections.sort(prices, new Comparator<Price>() {
      public int compare(Price p1, Price p2) {
        return (int) (p1.value - p2.value);
      }
    });
    
    for(int i = 0; i < prices.size(); i++) {
      if(i > 0) builder.append('\n');
      prices.get(i).build(builder);
    }
    
    builder.append("\n\n");
  }
  
  private static class Price  {
    
    private double value;
    private List<String> metaIds = new ArrayList<String>();
    
    Price(double value, String id) {
      this.value = value;
      metaIds.add(id);
    }
    
    void build(StringBuilder builder) {
      builder.append(value).append('\n');
      for(int  i = 0; i < metaIds.size(); i++) {
        if(i > 0) builder.append(" - ");
        builder.append(metaIds.get(i));
      }
    }
    
  }
}
