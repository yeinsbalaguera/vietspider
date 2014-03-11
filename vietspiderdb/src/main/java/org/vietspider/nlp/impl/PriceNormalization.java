/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.LogService;
import org.vietspider.nlp.INlpNormalize;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2011  
 */
public class PriceNormalization implements INlpNormalize {

  private PriceCreator creator = new PriceCreator();
  private int priceUSD = 21000;
  private int priceGold = 37000000;

  @SuppressWarnings("all")
  public void normalize(String id, HashMap<Short, Collection<?>> map) {
    List<String> list = (List<String>) map.get(NLPData.PRICE);
    if(list == null) return;
    
    PriceActionObjectNormalize aoNormalize = new PriceActionObjectNormalize(map);

    //    List<Unit> areas = (List<Unit>) map.get(NLPData.AREA);
    //    System.out.println(id);
    List<Price> prices = createPrices(aoNormalize, id, map, list);

    //collect total data
    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      price.toVND(priceUSD, priceGold);
    }

    Iterator<Price> iterator = prices.iterator();
    while(iterator.hasNext()) {
      Price price = iterator.next();
      double value = price.getValue();
      if(value <= 10*1000d) iterator.remove();
      if(price.getUnit() == Price.UNIT_M2) {
        if(value > 1000*1000d*1000d) iterator.remove();
      } else if(value > 500*1000*1000d*1000d) iterator.remove();
    }

    
    aoNormalize.process(prices);

    if(aoNormalize.hasActionObjects()) {
      for(int i = 0; i < prices.size(); i++) {
        Price price = prices.get(i);
        if(price.getUnit() != Price.UNIT_DEFAULT
            || price.getValue() < aoNormalize.getMax_m2()) continue;
        price.setUnit(Price.UNIT_TOTAL);
      }
    }

    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      iterator = prices.iterator();
      while(iterator.hasNext()) {
        Price _price = iterator.next();
        if(_price == price) continue;
        if(aoNormalize.getMin_m2() > -1
            && _price.getValue() < aoNormalize.getMin_m2()) {
          iterator.remove();
        } else  if(_price.getValue() < 50000) {
          iterator.remove();
        } else if(price.equals(_price)) {
          iterator.remove();
        }
      }
    }
    
    addPriceFromArea(map, prices);

    putMap(map, prices);

    //    if(areas != null) {
    //      for(int i = 0; i < areas.size(); i++) {
    //        System.out.println(areas.get(i));
    //      }
    //    }
    //   
    //    for(int i = 0; i < list.size(); i++) {
    //      System.out.println(list.get(i));
    //    }
  }

  protected void putMap(HashMap<Short, Collection<?>> map, List<Price> prices) {
    List<String> presentation = new ArrayList<String>();
    List<String> totals = new ArrayList<String>();
    List<String> m2 = new ArrayList<String>();
    List<String> month = new ArrayList<String>();
    for(int i = 0; i < prices.size(); i++) {
      Price price = prices.get(i);
      presentation.add(price.toString());
      if(price.getUnit() == Price.UNIT_TOTAL) {
        totals.add(String.valueOf(price.getValue()));
      } else if(price.getUnit() == Price.UNIT_MONTH) {
        month.add(String.valueOf(price.getValue()));
      } else if(price.getUnit() == Price.UNIT_M2) {
        m2.add(String.valueOf(price.getValue()));
      }
    }

    map.put(NLPData.PRICE, presentation);
    map.put(NLPData.PRICE_TOTAL, totals);
    map.put(NLPData.PRICE_MONTH, month);
    map.put(NLPData.PRICE_UNIT_M2, m2);
  }

  @SuppressWarnings("unchecked")
  private void addPriceFromArea(HashMap<Short, Collection<?>> map, List<Price> prices) {
    List<Unit> areas = (List<Unit>) map.get(NLPData.AREA);
    if(areas == null) return;
    List<String> addresses = (List<String>) map.get(NLPData.ADDRESS);
    if(addresses == null || addresses.size() > 1) return;
    List<Price> newPrices = new ArrayList<Price>();
    for(int i = 0; i < areas.size(); i++) {
      Unit unit = areas.get(i);
      while(unit != null) {
        if(unit.getValue() == 0) {
          unit = unit.getNext();
          continue;
        }
        for(int j = 0; j < prices.size(); j++) {
          Price price = prices.get(j);
          if(price.getUnit() == Price.UNIT_TOTAL) {
            double value = (price.getValue()/(1000d*1000d))/unit.getValue();
            value = Math.rint(value*1000)/1000;
            Price newPrice = new Price(value*1000d*1000d);
            newPrice.setPayment(Price.PAY_DONG);
            newPrice.setUnit(Price.UNIT_M2);
            newPrices.add(newPrice);
            if(PriceFilter.TEST) {
              System.out.println("new price 1: "+ String.valueOf(price.getValue()/(1000d*1000d)) 
                  + "/" + unit.getValue() + " = "  + newPrice.toString());
            }
          } else if(price.getUnit() == Price.UNIT_M2) {
            double value = price.getValue()*unit.getValue();
            value = Math.rint(value*1000)/1000;
            Price newPrice = new Price(value);
            newPrice.setPayment(Price.PAY_DONG);
            newPrice.setUnit(Price.UNIT_TOTAL);
            newPrices.add(newPrice);
            if(PriceFilter.TEST) {
              System.out.println("new price 2: "+ String.valueOf(price.getValue()/(1000d*1000d)) 
                  + "x" + unit.getValue() + " = "  + newPrice.toString());
            }
          }
        }
        unit = unit.getNext();
      }
    }
    for(int i = 0; i < newPrices.size(); i++) {
      Price price = newPrices.get(i);
      boolean add = true;
      for(int j = 0; j < prices.size(); j++) {
        if(price.equals(prices.get(j))) {
          add = false;
          break;
        }
      }
      if(add) prices.add(price);
    }
  }
  


  private List<Price> createPrices(PriceActionObjectNormalize aoNormalize, 
      String id, HashMap<Short, Collection<?>> map, List<String> list) {
    List<Price> prices = new ArrayList<Price>();
    for(int i = 0; i < list.size(); i++) {
      String text = list.get(i);
      List<String> elements = split(text);
      //      System.out.println(text.indexOf('-'));
      //      if(elements.length == 1)  elements = text.split("–");
      String unit  = extractUnit(elements);
      for(String ele : elements) {
        //        System.out.println("ele: "+ ele);
        if(unitIndexOf(ele) < 0) {
          ele += " " + unit; 
        }
        try {
          prices.add(creator.create(aoNormalize, map, ele));
        } catch (NumberFormatException e) {
        } catch (Exception e) {
          LogService.getInstance().setMessage(id, e, e.toString());
        } 
      }
    }
    return prices;
  }

  private int unitIndexOf(String text) {
    int index = 0;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isLetter(c) || c == '$') return index;
      index++;
    }
    return -1;
  }

  private String extractUnit(List<String> elements) {
    for(int i = 0; i < elements.size(); i++) {
      if(elements.get(i).length() < 1) continue;
      if(elements.get(i).charAt(0) == '$') return "usd";
      int index = unitIndexOf(elements.get(i));
      if(index < 0) continue;
      return elements.get(i).substring(index);
    }
    return "triệu";
  }

  private List<String> split(String value) {
    int start  = 0;
    int index  = 0;
    List<String> list = new ArrayList<String>();
    while(index < value.length()) {
      char c = value.charAt(index);
      //      if(!Character.isLetterOrDigit(c) 
      //          && c != ' ' && c != ',' && c != '-' && c != '/') {
      //        System.out.println(" =====>" + (int)c + " : "+ (int)separator + " : " + (c ==  separator));
      //        System.out.println(" =====>" + c + " : "+ separator + " : " + (c ==  separator));
      //        System.out.println("=======>" + (c == '\r'));
      //      }
      if(c == '-' || c == '–') {
        list.add(value.substring(start, index));
        start = index+1;
      }
      index++;
    }
    if(start < value.length()) {
      list.add(value.substring(start, value.length()));
    }
    return list;
  }



}
