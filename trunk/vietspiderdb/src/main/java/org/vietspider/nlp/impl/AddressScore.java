/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 29, 2011  
 */
class AddressScore {

  private final static String [] KEYS = {"cần bán", "bán nhà", "cho thuê", "khu đất", 
    "khu đô thị", "bán chung cư", "bán căn hộ",

    "địa điểm", "tọa lạc", "toạ lạc",  "nằm trên", "nằm trong",
    "địa chỉ nhà/đất"
  };
  
  private final static int [] SCORES = {5, 5, 5, 5, 
    5, 5, 5,

    2, 2, 2, 2, 2,
    3
  };

  
  private final static String[] KEYS_2 = {
    "địa chỉ", "liên hệ", "liên lạc", "nơi đăng", "đăng bởi", "add",
    "họ và tên"
  };
  private final static int [] SCORES_2 = {
    1, 5, 5, 5, 3, 1,
    2
  };
  
  private final static String[] KEYS_3 = {
    "cách", "liền kề", "gần", "nằm gần", "đối diện", "đi đến", "giáp ranh",
    "view", "đi sang", "tiếp giáp", "giáp", "đi tới", "để về", "đi về", 
    "dời nhà về"
  };
  private final static int [] SCORES_3 = {
    -15, -15, -15, -15, -15, -15, -15, 
    -10, -15, -15, -15, -15, -15, -15, 
    -15, -12
  };

  static int calculate(Address address) {
    TextElement element = address.getElement();
    
//    System.out.println("\n\n");
//    System.out.println(address.getPlace());
//    System.out.println(element.getValue());
//    System.out.println(element.getValue().substring(address.getStart(), address.getEnd()));
    
    
    int score = 0;
//    System.out.println(" step 1 "+ score);
    for(int i = 0; i < KEYS.length; i++) {
      int index = element.getLower().indexOf(KEYS[i]);
      if(index < 0) continue;
      /*if(index < address.getStart()) */score += SCORES[i];
    }
    
    TextElement previous = element.previous();
    if(previous != null) {
//      System.out.println(previous.getValue());
      for(int i = 0; i < KEYS.length; i++) {
        int index = previous.getLower().indexOf(KEYS[i]);
        if(index < 0) continue;
        if(index < address.getStart()) score += SCORES[i];
      }
    }
    
//    System.out.println(" step 2 "+ score);
    
    for(int i = 0; i < KEYS_3.length; i++) {
      int index = element.getLower().indexOf(KEYS_3[i]);
      if(index < 0) continue;
      if(KEYS_3[i].indexOf(' ') < 0
          && isUpperCharacter(element.getValue(), index)) continue;
//      System.out.println(element.getLower());
//      System.out.println(KEYS_3[i] + " : "+ index);
      if(index < address.getStart()) score += SCORES_3[i];
    }
    
//    System.out.println(" step 3 "+ score);
    
    for(int i = 0; i < KEYS_2.length; i++) {
      int index = element.getLower().indexOf(KEYS_2[i]);
      if(index < 0) continue;
      if(index < address.getStart()) score -= SCORES_2[i];
      if(SCORES_2[i] == 1) {
        if(element.previous() != null 
            && element.previous().getLower().length() > 70) {
//          System.out.println("truoc "+element.getPrevious().getLower().length());
          score += 1;
        }
        if(element.next() != null 
            && element.next().getLower().length() > 70) {
//          System.out.println("sau "+element.getNext().getLower().length());
          score += 1;
        }
         
      }
    }
    
//    System.out.println(" step 4 "+ score);
    
//    System.out.println(element.getLower());
    if(element.getLower().length() > 100) return score;
    
    previous = element.previous();
    for(int time = 0; time < 5; time++) {
      if(previous == null || previous.getLower().length() > 100) break;
      for(int i = 0; i < KEYS_2.length; i++) {
        int index = previous.getLower().indexOf(KEYS_2[i]);
        if(index < 0) continue;
//        if(time == 0  && index >= address.getStart()) continue;
        int value = SCORES_2[i] - time;
//        System.out.println(KEYS_2[i] + " : " + value);
//        System.out.println(previous.getValue());
//        System.out.println("====  > value "+ value);
        if(value < 1) continue;
        score -= value;
        return score;
      }
      previous = previous.previous();
    }
    
//    System.out.println(" step 5 "+ score);
    
    return score;
  }
  
  private static boolean isUpperCharacter(String upper, int index) {
    char c = upper.charAt(index);
    if(!Character.isLetter(c) 
        || Character.isLowerCase(c)) return false;
    
    if(index < 3) return false;
    char c1 = upper.charAt(index-1);
    char c2 = upper.charAt(index-2);
    if((Character.isWhitespace(c1) || Character.isSpaceChar(c1))
        && (c2 == '.' || c2 == '!' || c2 == '?')) return false;
    
    if(index > upper.length() -2 ) return false;
    c = upper.charAt(index+1);
    if(Character.isUpperCase(c)) return false;
        
//    System.out.println(" ==== > "+ upper);
//    System.out.println(upper.charAt(index) 
//        + " : " + Character.isUpperCase(upper.charAt(index)));
    
    return true;
    
  }
  

}
