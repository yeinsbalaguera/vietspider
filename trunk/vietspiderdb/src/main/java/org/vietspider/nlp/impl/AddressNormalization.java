/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.INlpNormalize;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 19, 2011  
 */
public class AddressNormalization implements INlpNormalize {

    @SuppressWarnings("all")
    public void normalize(String id, HashMap<Short, Collection<?>> map) {
      List<String> list = (List<String>) map.get(NLPData.ADDRESS);
      if(list == null || list.size() < 2) return;
      
      for(int i = 0; i < list.size(); i++) {
        String address = list.get(i);
        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
          String temp = iterator.next();
          if(temp == address) continue;
          if(address.endsWith(temp)) iterator.remove();
        }
      }
    }
}
