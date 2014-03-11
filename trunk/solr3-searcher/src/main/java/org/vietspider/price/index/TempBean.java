/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.price.index;

import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 22, 2011  
 */
class TempBean {

  PriceIndex index;
  String city;
  String action;

  TempBean(PriceIndex index, String city, String action) {
    this.index = index;
    this.city = city;
    this.action = action;
  }

  static TempBean create(Article article) {
    NLPRecord nlp = article.getNlpRecord();
    return create(nlp);
  }

  static TempBean create(NLPRecord nlp) {
    if(nlp == null) {
//             System.out.println(" return by nlp");
      return null;
    }
    
    
    List<String> cities = nlp.getData(NLPData.CITY);
    if(cities == null || cities.size() != 1) {
//             System.out.println(" return by cities ");
      return null;
    }
    String city = cities.get(0);
    if(city == null || city.trim().length() < 1) {
//             System.out.println(" return by city ");
      return null;
    }

    List<String> addresses = nlp.getData(NLPData.ADDRESS);
    if(addresses == null || addresses.size() != 1)  {
//             System.out.println(" return by addresses");
      return null;
    }
    String address = addresses.get(0);

    List<String> aoes = nlp.getData(NLPData.ACTION_OBJECT);
    if(aoes == null || aoes.size() != 1) {
//             System.out.println(" return by aos");
      return null;
    }

    String ao = aoes.get(0);
    if(!ao.equals("1,1")
        && !ao.equals("1,2")
        && !ao.equals("1,4")
        && !ao.equals("1,5")) {
//             System.out.println(" return by ao");
      return null;
    }

    List<String> prices = nlp.getData(NLPData.PRICE);
    if(prices == null || prices.size() < 1) {
//             System.out.println(" return by prices 1");
      return null;
    }

    PriceIndex index =  new PriceIndex(nlp.getMetaId());
    for(int i = 0; i < prices.size(); i++) {
      String price = prices.get(i);
      price = normalizePrice(price);
      if(price == null) continue;
      index.getPrices().add(price);
    }

    if(index.getPrices().isEmpty())  {
//             System.out.println(" return by prices 2");
      return null;
    }
    index.setAddress(address);

    return new TempBean(index, city, ao);
  }
  
  private static String normalizePrice(String price) {
    price = normalizePrice2(price);
    if(price == null) return null;
    double number = 0;
    try {
      number = Double.parseDouble(price);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    if(number < 0.5 || number > 2*1000) return null;
    number = number*10;
    number = Math.round(number);
    number = number/10;
    
    price = String.valueOf(number);
    if(price.length() < 2) return null;
    if(price.charAt(price.length() - 1) == '0'
      && price.charAt(price.length() - 2) == '.') {
      price = price.substring(0, price.length()-2);
    }
    
    return price;
  }

  private static String normalizePrice2(String price) {
    int index = price.indexOf("/m2");
    if(index < 0) return null;
    price = price.substring(0, index).trim();
    index = price.indexOf("triệu");
    if(index > 0) {
      return price.substring(0, index).trim();
    }

    index = price.indexOf("tr");
    if(index > 0) {
      return price.substring(0, index).trim();
    }

    index = price.indexOf("nvd");
    if(index < 0) index = price.indexOf("vnđ");
    if(index < 0) index = price.indexOf("đ");
    if(index > 0) {
      price = price.substring(0, index).trim();
    }
    
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < price.length(); i++) {
      if(price.charAt(i) == '.' 
        || price.charAt(i) == ',') continue;
      builder.append(price.charAt(i));
    }
    try {
      double number = Double.parseDouble(builder.toString().trim());
      if(number < 0.5) return null;
      return String.valueOf(number/(1000*1000d));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      return null;
    }
  }

  public static void main(String[] args) {
    String price = "57.000.000 vnđ/m2";
    System.out.println(normalizePrice(price));
    price = "57.000.000 /m2";
    price = "308.333 triệu/m2";
    price = "166.667 tr/m2";
    System.out.println(normalizePrice(price));
    
   
  }
}
