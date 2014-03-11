/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 15, 2011  
 */
public class Price {
  
  public final static short PAY_DONG = 0;
  public final static short PAY_USD = 1;
  public final static short PAY_GOLD = 2;
  
  public final static short UNIT_M2 = 3;
  public final static short UNIT_TOTAL = 4;
  public final static short UNIT_MONTH = 5;
  public final static short UNIT_DEFAULT = -1;
  
  private double value;
  private short payment = -1;
  private short unit = UNIT_DEFAULT;
  private double [] totals;
  
  public Price() {
  }
  
  public Price(double value) {
    this.value = value;
  }

  public double getValue() { return value; }
  public void setValue(double value) { this.value = value; }

  public short getPayment() { return payment; }
  public void setPayment(short type) { this.payment = type; }
  
  public short getUnit() { return unit; }
  public void setUnit(short unit) {
//    if(unit == UNIT_M2) new Exception().printStackTrace();
    this.unit = unit; 
  }
  
  public void calculateTotals(List<Unit> units) {
    if(unit != UNIT_M2) {
      totals = new double[]{value};
      return;
    }
    
    if(units == null || units.size() < 1) return;
    List<Float> areas = new ArrayList<Float>();
    for(int j = 0; j < units.size(); j++) {
      Unit _unit = units.get(j);
      while(_unit != null) {
        areas.add(_unit.getValue());
        _unit = _unit.getNext();
      }
    }
    
    totals = new double[areas.size()];
    for(int i = 0; i < areas.size(); i++) {
      totals[i] = areas.get(i)*value;
      totals[i] = Math.rint(totals[i]*1000)/1000;
    }
  }
  
  public double toVND(int priceUSD, int priceGold) {
    if(payment == PAY_USD) {
      payment = PAY_DONG;
      value = value*priceUSD;
      value = Math.rint(value*1000)/1000;
    } else if(payment == PAY_GOLD) {
      payment = PAY_DONG;
      value = value*priceGold;
      value = Math.rint(value*1000)/1000;
    }
    return value;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    if(payment == PAY_DONG) {
      builder.append(toText(value));
    } else if(payment == PAY_GOLD){
      builder.append(value).append(' ').append("cây");
    } else if(payment == PAY_USD){
      builder.append(value).append(' ').append("usd");
    }
    
    if(unit == UNIT_M2) {
      builder.append("/m2");
    } else if(unit == UNIT_MONTH) {
      builder.append("/tháng");
    } else if(unit == UNIT_TOTAL) {
      builder.append("/tổng");
    }
    
    return builder.toString();
  }
  
  public static String toText(double value) {
    StringBuilder builder = new StringBuilder();
    if(value >= 1000d*1000d && value < 1000d*1000d*1000d) {
      double real = value/(1000d*1000d);
      real = Math.rint(real*1000)/1000;
      String v = String.valueOf(real);
      int len = v.length();
      if(len > 2
          && v.charAt(len - 2) == '.'
            && v.charAt(len - 1) == '0') {
        v = v.substring(0, len-2);
      }
      builder.append(v).append(' ').append("triệu");
    } else if(value >= 1000d*1000d*1000d) {
      double real = value/(1000d*1000d*1000d);
      real = Math.rint(real*1000)/1000;
      String v = String.valueOf(real);
      int len = v.length();
      if(len > 2
          && v.charAt(len - 2) == '.'
            && v.charAt(len - 1) == '0') {
        v = v.substring(0, len-2);
      }
      builder.append(v).append(' ').append("tỷ");
    } else {
      builder.append(value).append(' ').append("đồng");
    }
    
    return builder.toString();
  }
  
  public boolean equals(Price price) {
//    System.out.println(unit +  " : "+ price.unit);
//    System.out.println(value +  " : "+ price.value);
    if(unit != price.unit) return false;
    return value == price.value;
  }
  
}
