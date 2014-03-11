/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 18, 2011  
 */
class PriceValueParser {
  
  private Price price;
  private PriceActionObjectNormalize aoNormalize;
  
  PriceValueParser(PriceActionObjectNormalize aoNormalize, Price price) {
    this.aoNormalize = aoNormalize;
    this.price = price;
  }
  
  double parse(String value, int rate) throws Exception {
    //    if("0.2".equals(value)) new java.lang.Exception().printStackTrace();
//            System.out.println("===== >"+ value+"|");
    if(value.length() < 1) return 0.0f;
    int idx1 = value.indexOf('.');
    int idx2 = value.indexOf(',');
    //    System.out.println(" idx1 " + idx1 + " idx 2 "+ idx2);
    if(idx1 < 0 && idx2 < 0) {
      double f = Double.parseDouble(value);
      if(rate >= 1000d*1000d && f/rate > 1) return f;
      return f*rate;
    }

    if(idx1 > -1 && idx2 > -1) {
      StringBuilder builder = new StringBuilder(value);
      if(idx2 > idx1) {
        builder.setCharAt(idx2, '.');
        builder.deleteCharAt(idx1);
      } else {
        while(idx2 > -1) {
          builder.deleteCharAt(idx2);
          idx2 = builder.indexOf(",");
        }
      }
      double f = Double.parseDouble(builder.toString());
      if(rate >= 1000d*1000d && f/rate > 1) return f;
      return f*rate;
    }

    int idx = idx1 > 0 ? idx1 : idx2;
    
    //900,000,000 or 900.000.000
    char sc = value.charAt(idx);
    int index = idx + 1;
    int counter = 1;
    while(index > -1) {
      index = value.indexOf(sc, index);
      if(index < 0) break;
      counter++;
      index++;
    }
    if(counter > 1) {
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        if(c == sc) continue;
        builder.append(c);
      }
      
      double f = Double.parseDouble(builder.toString());
//      System.out.println(" == > "+ f +  " : "+ aoNormalize.getMax_m2());
      if(aoNormalize.getMax_m2() > 1 
          && f >= aoNormalize.getMax_m2()
          && price.getUnit() == Price.UNIT_M2) {
        price.setUnit(Price.UNIT_TOTAL);
      }
      return f;
    }
    
    
    counter = value.length() - idx;
    StringBuilder builder = new StringBuilder();

//    System.out.println(counter + " : "+ value.length() + " , " + idx);
//    System.out.println((unit != Price.UNIT_M2) +  ": "+ counter);
    if(counter > 3) {
      for(int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        if(!Character.isDigit(c)) continue;
        builder.append(c);
      }
      //      System.out.println(builder +  " : "+ unit);
    } else {
      builder.append(value);
      builder.setCharAt(idx, '.');
    }

    //    System.out.println("hehe" + value);
    double f = Double.parseDouble(builder.toString());
    if(rate == 1000*1000*1000 && counter > 3) {
      return f*1000*1000;
    }
    if(rate >= 1000d*1000d && f/rate > 1) return f;
    f = f*rate;
    
    if(price.getUnit() == Price.UNIT_M2 
        && f > 100*1000d*1000d ) {
      builder.setLength(0);
      builder.append(value);
      builder.setCharAt(idx, '.');
//      System.out.println(builder);
      f = Double.parseDouble(builder.toString())*rate;
    } 
    return f;
  }
}
