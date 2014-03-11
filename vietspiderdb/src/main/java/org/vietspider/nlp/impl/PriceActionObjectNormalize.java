/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.impl.ao.ActionObject;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 16, 2011  
 */
class PriceActionObjectNormalize {

  private HashMap<Short, Collection<?>> map;
  private List<ActionObject> action_objects;
  private List<String> cities;
  private double min_m2;
  private double max_m2;

  @SuppressWarnings("unchecked")
  PriceActionObjectNormalize(HashMap<Short, Collection<?>> map) {
    this.map = map;
    cities = (List<String>) map.get(NLPData.CITY); 
    Collection<?> aos = map.get(NLPData.ACTION_OBJECT);
    action_objects = (List<ActionObject>)aos;
    calculateMinMax();
  }
  
  public boolean hasActionObjects() { 
    return action_objects != null && action_objects.size() > 0;
  }
  
  public double getMin_m2() { return min_m2; }

  public double getMax_m2() { return max_m2; }

  @SuppressWarnings("unchecked")
  void process(List<Price> prices) {
    if(action_objects == null) {
      isNull(prices);
      return;
    }

    if(PriceFilter.TEST) {
      for(int i = 0; i < action_objects.size(); i++) {
        String ao =  action_objects.get(i).getData();
        System.out.println(ao + " : " + NLPData.action_object(ao));
      }
      List<Unit> areas = (List<Unit>) map.get(NLPData.AREA);
      if(areas != null) {
        for(int i = 0; i < areas.size(); i++) {
          System.out.println("dt: " + areas.get(i).getValue());
        }
      }
      List<String> addresses = (List<String>) map.get(NLPData.ADDRESS);
      if(addresses != null) {
        for(int i = 0; i < addresses.size(); i++) {
          System.out.println(addresses.get(i));
        }
      }
    }


    sellApartment(prices);
    rent(prices);
    shellToRent(prices);
    sellHouse(prices);
  }

  private void isNull(List<Price> prices) {
    action_objects = new ArrayList<ActionObject>();
    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      if(price.getUnit() == Price.UNIT_MONTH) {
        action_objects.add(new ActionObject("2,1"));
        map.put(NLPData.ACTION_OBJECT, action_objects);
        return;
      }
      //for rent
    }

  }

  // sell apartment
  private void sellApartment(List<Price> prices) {
    boolean exist = false;
    for(int i = 0; i < action_objects.size(); i++) {
      if("1,4".equals(action_objects.get(i).getData())) {
        exist = true;
        break;
      }
    }
    if(!exist) return;

    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      if(price.getUnit() != Price.UNIT_DEFAULT) continue;
      if(price.getValue() < 5*1000*1000d 
          || price.getValue() >= 100*1000*1000d) continue;
      price.setUnit(Price.UNIT_M2);
      //for rent
    }
  }

  private void rent(List<Price> prices) {
    List<Integer> actions = new ArrayList<Integer>();
    for(int i = 0; i < action_objects.size(); i++) {
      String ao = action_objects.get(0).getData();
      int idx = ao.indexOf(',');
      int action = Integer.parseInt(ao.substring(0, idx));
      if(actions.contains(action)) continue;
      actions.add(action);
    }

    if(actions.size() != 1) return;

    int action = actions.get(0);

    if(action != NLPData.ACTION_FOR_RENT
        && action != NLPData.ACTION_RENT) return;
    //        System.out.println(" ===  >"+ action);

    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      if(price.getUnit() != Price.UNIT_DEFAULT) continue;
      if(price.getValue() >= 100*1000*1000d) continue;
      price.setUnit(Price.UNIT_MONTH);
    }
  }

  private void shellToRent(List<Price> prices) {
    if(action_objects.size() != 1
        || !action_objects.get(0).toString().equals("1,1")) return;

    if(prices.size() != 1
        || prices.get(0).getValue() > 100*1000*1000d
        || prices.get(0).getUnit() == Price.UNIT_M2) return;
    //    System.out.println(" xay ra roi " + prices.get(0).getUnit());

    //action 1,1 - ban nha - unit = total - price < 20tr 
    action_objects.get(0).setData("2,1");
    prices.get(0).setUnit(Price.UNIT_MONTH);
  }

  //sell apartment
  private void sellHouse(List<Price> prices) {
    if(min_m2 < 0) return;

    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      if(price.getUnit() != Price.UNIT_DEFAULT) continue;
      if(price.getValue() < min_m2
          || price.getValue() >= max_m2) continue;
      if(price.getValue() < 300*1000*1000) {
        price.setUnit(Price.UNIT_M2);
      } else {
        price.setUnit(Price.UNIT_TOTAL);
      }
      //for rent
    }
  }

  private void calculateMinMax() {
    for(int i = 0; i < action_objects.size(); i++) {
      String data = action_objects.get(i).getData();
      if("1,1".equals(data)) {
        min_m2  = 5*1000*1000d;
        if(cities != null) {
          for(int j = 0; j < cities.size(); j++) {
            if("thành phố hồ chí minh".equals(cities.get(j))) {
              max_m2 = 200*1000*1000d;
              return;
            } 

            if("hà nội".equals(cities.get(j))) {
              max_m2 = 500*1000*1000d;
              return;
            }
          }
        }
        return;
      } else if("1,4".equals(data)) {
        min_m2  = 10*1000*1000d;
        if(cities != null) {
          for(int j = 0; j < cities.size(); j++) {
            if("thành phố hồ chí minh".equals(cities.get(j))) {
              max_m2 = 100*1000*1000d;
              return;
            } 

            if("hà nội".equals(cities.get(j))) {
              max_m2 = 200*1000*1000d;
              return;
            }
          }
        }
        return;
      }

      if("1,2".equals(data) || "4,2".equals(data)) {
        if(action_objects.size() == 1) {
          min_m2  = 100*1000d;
        } else {
          min_m2  = 1*1000*1000d;
        }
        max_m2 = 50*1000*1000d;
        if(cities != null) {
          for(int j = 0; j < cities.size(); j++) {
            if("thành phố hồ chí minh".equals(cities.get(j))) {
              max_m2 = 100*1000*1000d;
              break;
            } else if("hà nội".equals(cities.get(j))) {
              max_m2 = 500*1000*1000d;
              break;
            }
          }
        }
        break;
      }
    }
  }

}
