/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.CommonMapper;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class PriceIndexMapper extends CommonMapper {
  
  public String toText(PriceIndex index) {
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(index.getId()).append(']');
    builder.append('['); encode(builder, index.getAddress()); builder.append(']');
    
    List<String> list = index.getPrices();
    for(int i = 0; i < list.size(); i++) {
      builder.append('[').append(list.get(i)).append(']');
    }
    return builder.toString();
  }
  
  public PriceIndex toIndex(String text) {
    PriceIndex index = new PriceIndex();
    int from = 0;
    int start = text.indexOf('[', from);
    int end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return null;
    index.setId(text.substring(start+1, end));
    
    from = end + 1;
    start = text.indexOf('[', from);
    end = text.indexOf(']', from);
    if(start < 0 || end  < 0) return index;
    String value = text.substring(start+1, end);
    if(value.length() == 1 && value.charAt(0) == '~') value = null;
    index.setAddress(decode(value));
    
    List<String> prices = new ArrayList<String>();
    
    while(true) {
      from = end + 1;
      start = text.indexOf('[', from);
      end = text.indexOf(']', from);
      if(start < 0 || end  < 0) break;
      prices.add(text.substring(start+1, end));
    }
    index.setPrices(prices);
    
    return index;
  }
  
}
