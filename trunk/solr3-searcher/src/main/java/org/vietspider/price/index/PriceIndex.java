/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 15, 2011  
 */
public class PriceIndex {

  private String id;
  private String address;
  private List<String> prices = new ArrayList<String>();
  
  public PriceIndex() {
  }
  
  public PriceIndex(String id) {
    this.id = id;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public List<String> getPrices() { return prices; }
  public void setPrices(List<String> prices) { this.prices = prices; }
  
  public boolean equalsValue(PriceIndex index) {
    if(!address.equalsIgnoreCase(index.address)) return false;
    if(prices.size() != index.prices.size()) return false;
    for(int i = 0; i < prices.size(); i++) {
      if(!prices.get(i).equals(index.prices.get(i))) return false;
    }
    
    return true;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(id);
    builder.append('\"').append(address).append('\"');
    builder.append('\"');
    for(int i = 0; i < prices.size(); i++) {
      if(i > 0) builder.append(',');
      builder.append(prices.get(i));
    }
    builder.append('\"');
    return builder.toString();
  }
  
}
