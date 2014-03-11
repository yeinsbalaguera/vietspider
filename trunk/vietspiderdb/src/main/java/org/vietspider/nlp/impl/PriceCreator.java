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
 * Mar 15, 2011  
 */
class PriceCreator {

  @SuppressWarnings("unchecked")
  Price create(PriceActionObjectNormalize aoNormalize, 
      HashMap<Short, Collection<?>> map, String text) throws Exception {
    Price price = new Price();
    PriceValueParser valueParser = new PriceValueParser(aoNormalize, price);
    
    text = text.trim();

    int index = 0;
    int start = 0;
    double value = 0.0d;

    Unit area = null;
    List<Unit> areas = (List<Unit>) map.get(NLPData.AREA);
    if(areas != null && areas.size() > 0) area = areas.get(0);

    if(text.charAt(0) == '$') {
      price.setPayment(Price.PAY_USD);
      index++;
      start = index;
    }

    char c = '?';
    int rate = 1;
    while(index < text.length()) {
      c = text.charAt(index);

      if(Character.isDigit(c)) {
        index++;
        continue;
      }

      if((c == '.' || c == ',')
          && index > 0 && index < text.length() -1
          && Character.isDigit(text.charAt(index-1))
          && Character.isDigit(text.charAt(index+1))) {
        index++;
        continue;
      }

      //      System.out.println(start+ " : "+ index);
      String number = text.substring(start, index);
      //      System.out.println(" ------------> " + number);
      if(number.length() < 1) number += "0";
      if(value > 1) number = "0." + number; 

      //      System.out.println(number);

      while(index < text.length()) {
        c = text.charAt(index);
        if(Character.isWhitespace(c)
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        }
        break;
      }
      
      if(c == 't' 
        && index < text.length() - 4 
            && text.charAt(index+1) == 'r'
              && (text.charAt(index+2) == 'ă' || text.charAt(index+2) == 'a')
              && text.charAt(index+3) == 'm') {
         index += 4;
         if(number.length() > 2 
             && number.charAt(1) == '.') {
           number = number.substring(2);
         }
         number += "00";
         while(index < text.length()) {
           c = text.charAt(index);
           if(Character.isWhitespace(c)
               || Character.isSpaceChar(c)) {
             index++;
             continue;
           }
           break;
         }
      }

      if(c == 't') {
        if(index < text.length()- 1 
            && text.charAt(index+1) == 'r') {
          
          if(index < text.length() - 3
              && (text.charAt(index+2) == 'ă' || text.charAt(index+2) == 'a')
              && text.charAt(index+3) == 'm') {
            
          }
          
          price.setPayment(Price.PAY_DONG);
          rate = 1000*1000;
//                    System.out.println(" chuan bi dua vao "+ number);
          //          System.out.println(number + " = " + parse(number, unit));
          //          value += parse(number, rate);
        } else if(index < text.length()- 1 
            && (text.charAt(index+1) == 'y' || 
                text.charAt(index + 1) == 'ỷ' ||
                text.charAt(index+1) == 'i' || 
                text.charAt(index+1) == 'ỉ')) {
          if(price.getPayment() != Price.PAY_USD) {
            price.setPayment(Price.PAY_DONG);
            rate = 1000*1000*1000;
          }
          //          value += parse(number, rate);
        } else if(index < text.length() - 1 
            && Character.isDigit(text.charAt(index+1))) { //18t2
          price.setPayment(Price.PAY_DONG);
          number += "." + String.valueOf(text.charAt(index+1));
          if(isSellHouse(map, text, number)) {
            rate = 1000*1000*1000;
          } else {
            rate = 1000*1000;
          }
          //          value += parse(number, rate);
          index += 2;
        } else {
          price.setPayment(Price.PAY_DONG);
          if(isSellHouse(map, text, number)) {
            rate = 1000*1000*1000;
          } else {
            rate = 1000*1000;
          }
          //          value += parse(number, rate);
        }
      } else if(c == 'k' || c == 'n') {
        price.setPayment(Price.PAY_DONG);
        rate = 1000;
      } else if(c == 'd' || c == 'đ' || c == 'v') {
        price.setPayment(Price.PAY_DONG);
        rate = 1;
      } else if(c == 'u' || c == '$') {
        price.setPayment(Price.PAY_USD);
        rate = 1;
        //        try {
        //          value += parse(number, rate);
        //        } catch (Exception e) {
        //        }
      } else if(c == 'l' || c == 'c') {
        price.setPayment(Price.PAY_GOLD);
        rate = 1;
        //        value += parse(number, rate);
        //      } else {
        //        value += parse(number, rate);
      }
      //      System.out.println(" q === >" + text.substring(index) + " : "+ c);
      while(index < text.length()) {
        c = text.charAt(index);
        if(Character.isLetter(c) || c == '$') {
          index++;
          continue;
        }
        break;
      }
      //      System.out.println(" 2 === >" + text.substring(index) + " : "+ c);

      while(index < text.length()) {
        c = text.charAt(index);
        if(Character.isWhitespace(c)
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        }
        break;
      }

      //5,3 triệu đồng/m2
      if(Character.isLetter(c) && c != 'm') {
        while(index < text.length()) {
          c = text.charAt(index);
          if(Character.isLetter(c) || c == '$') {
            index++;
            continue;
          }
          break;
        }
      }
      
      if(c == '/') {
        index++;

        int s = index;
        while(index < text.length()) {
          c = text.charAt(index);
          if(Character.isDigit(c)) {
            index++;
            continue;
          }
          break;
        }

        int total = 1;
        if(index > s) {
          String next = text.substring(index);
          try {
//            System.out.println(text.substring(s, index));
            total = Integer.parseInt(text.substring(s, index));
          } catch (Exception e) {
          }
          
          
          if(total > 5 && next.startsWith("m2")) {
            if(area == null) {
              area = new Unit(total, "m2");
              if(areas == null) {
                areas = new ArrayList<Unit>();
                map.put(NLPData.AREA, areas);
              }
              areas.add(area);
            } else {
              area.setNext(total);
            }
          }
        }

        while(index < text.length()) {
          c = text.charAt(index);
          if(Character.isWhitespace(c)
              || Character.isSpaceChar(c)
              || c == '1') {
            index++;
            continue;
          }
          break;
        }

//                System.out.println(" ====  > "+ c +  " : "+ index + " : "+ (text.length()-1));

        if(c == 'm') {
          if(index >= text.length() - 1) {
            if(total < 2) price.setUnit(Price.UNIT_M2);
            index++;
          } else if(text.charAt(index+1) == '2') {
            if(total < 2) price.setUnit(Price.UNIT_M2);
            index += 2;
//                        System.out.println(text);
//                        System.out.println(" ====  > "+ index + " : "+ (text.length()-1));
          } else if(text.charAt(index+1) == 'o') {
            price.setUnit(Price.UNIT_MONTH);
            while(index < text.length()) {
              c = text.charAt(index);
              if(Character.isLetter(c)) {
                index++;
                continue;
              }
              break;
            }
          } else if((Character.isWhitespace(text.charAt(index+1))
              || Character.isSpaceChar(text.charAt(index+1))) 
              && index < text.length() - 2
              && text.charAt(index+2) == '2') {
            if(total < 2) price.setUnit(Price.UNIT_M2);
            index += 3;
          } 
        } else if(c == 't'
          && (index >= text.length() - 1
              || text.charAt(index+1) == 'h')) {
          price.setUnit(Price.UNIT_MONTH);
          while(index < text.length()) {
            c = text.charAt(index);
            if(Character.isLetter(c)) {
              index++;
              continue;
            }
            break;
          }
        } else if(c == 'p'
          && index < text.length() - 1
          && text.charAt(index+1) == 'h') {
          price.setUnit(Price.UNIT_MONTH);
          while(index < text.length()) {
            c = text.charAt(index);
            if(Character.isLetter(c)) {
              index++;
              continue;
            }
            break;
          }
        } else if(c == 'n'
          && index < text.length() - 1
          && text.charAt(index+1) == 'g') {
          price.setUnit(Price.UNIT_MONTH);
          while(index < text.length()) {
            c = text.charAt(index);
            if(Character.isLetter(c)) {
              index++;
              continue;
            }
            break;
          }
        } else {
          price.setUnit(Price.UNIT_TOTAL);
//                    System.out.println(text.substring(index));
          while(index < text.length()) {
            c = text.charAt(index);
            if(Character.isLetter(c)) {
              index++;
              continue;
            }
            break;
          }
        }
      } else if(c == 'm') {
        if(index >= text.length() - 1) {
          price.setUnit(Price.UNIT_M2);
          index++;
        } else if(text.charAt(index+1) == '2') {
          price.setUnit(Price.UNIT_M2);
          index += 2;
        }
      } else if(c == '1') {
        if(index < text.length() - 1 
            && text.charAt(index+1) == 'm') {
          price.setUnit(Price.UNIT_M2);
          index += 2;
        }
      }

      while(index < text.length()) {
        c = text.charAt(index);
        if(!Character.isDigit(c) 
            || Character.isWhitespace(c)
            || Character.isSpaceChar(c)) {
          index++;
          continue;
        }
        break;
      }

//      System.out.println(" number "+ number + " : "+ rate);
//      try {
        value += valueParser.parse(number, rate);
//      } catch (Exception e) {
//        Exception e1 = new Exception(number +  ": " + e.getMessage());
//        e1.setStackTrace(e.getStackTrace());
//        throw e1;
//      }

      start = index;

      //      System.out.println(" ===  > "+ start + " : "+ index + " : "+ (text.length() ));

      //      System.out.println("=== >"+ c +"|");
      //      if(index < text.length() 
      //          && text.substring(index).trim().length() > 0) {
      //        System.out.println(text);
      //        System.out.println(text.substring(index));
      //      }
      //
      //
      //      break;

    }

//        System.out.println(start + " : "+ text.length());

    if(start < text.length()) {
      String number = "0.0";
      if(value > 0) {
        number = "0." + text.substring(start);
      } else {
        number = text.substring(start);
        //        System.out.println(" cai ni " + text + " : " + number);
      }
      //      System.out.println(number);
      //      System.out.println(price.getUnit());
      //      System.out.println(parse(number, rate));
      value += valueParser.parse(number, rate);
    }

    //    if(price.getType() == -1) {
    //      System.out.println("=== >"+ c +"|");
    //      System.out.println(text);
    //    }

    price.setValue(value);

    if(price.getPayment() == - 1) {
      price.setPayment(Price.PAY_DONG);
    }

    //        System.out.println(value);
    if(PriceFilter.TEST) {
      System.out.println(text + " = "+ price.toString());
    }
    
//    System.out.println(price.getUnit());

    return price;
  }
  
  @SuppressWarnings("unchecked")
  private boolean isSellHouse(HashMap<Short, Collection<?>> map, 
      String text, String number) {
    if(text.indexOf("m2") > -1
        || text.indexOf("tháng") > -1) return false;
    Collection<?> aos = map.get(NLPData.ACTION_OBJECT);
    List<ActionObject> action_objects = (List<ActionObject>)aos;
//    System.out.println("hehee " + action_objects.size());
    
    for(int i = 0; i < action_objects.size(); i++) {
      String ao =  action_objects.get(i).getData();
//      System.out.println(ao);
      if(!ao.startsWith("1")) continue;
      if(number.length() < 2) return true;
      if(number.length() == 3
          && (number.charAt(1) == ',' 
            || number.charAt(1) == '.')) return true;
    }
    return false;
  }
 
}
