/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2010  
 */
public class PhoneUtils {

  private final static String[] prefixeMobiles = {
    "90", "93", // - MobiFone
    "91", "94",// - Vinaphone
    "92",// - Vietnam Mobile
    "95",// - S-Fone
    "96",// - EVN Telecom
    "97", "98",// - Viettel

    "120", "121", "122", "126", "128", // - MobiFone
    "163", "164", "165", "166", "167", "168", "169", //- Viettel Mobile
    "123", "124", "125", "127", "129", // - VinaPhone
    "188", // - Vietnam Mobile
    "199" // - Beeline[1]
  };

  private final static String[] prefixeRegions = {
    "4", "8", "511", "31", "650", "710", "61"
  };

  static int checkPrefix(String text) {
    return checkPrefix(text, 0);
  }

  static int checkPrefix(String text, int from) {
    if(text.isEmpty() || from >= text.length()) return -2;

    if(text.length() > from + 3 
        && text.charAt(from) == '8' 
          && text.charAt(from + 1) == '4' 
            && text.charAt(from + 2) == '-') return 3;
    
    if(text.length() > from + 3 
        && text.charAt(from) == '8' 
          && text.charAt(from + 1) == '4') return 1;

    char c = text.charAt(from);
    if(c != '0' && c != 'o' && c != 'O') return -2;

    StringBuilder builder = new StringBuilder();
    for(int i = from + 1; i < Math.min(text.length(), from + 5); i++) {
      c = text.charAt(i);
      if(Character.isDigit(c)) builder.append(c);
    }

    text = builder.toString();

    for(int i = 0; i < prefixeMobiles.length; i++) {
      if(text.startsWith(prefixeMobiles[i])) return 3;
    }

    for(int i = 0; i < prefixeRegions.length; i++) {
      if(text.startsWith(prefixeRegions[i])) return 1;
    }
    return -1;
  }

  public static boolean isMobile(String text) {
    return isMobile(text, 0);
  }

  public static boolean isMobile(String text, int from) {
    if(text.length() < from + 3) return false;
    //    System.out.println(text.substring(from));
    if(text.charAt(from) != '0') return false;

    for(int i = 0; i < prefixeMobiles.length; i++) {
      if(prefixeMobiles[i].length() == 2
          && prefixeMobiles[i].charAt(0) == text.charAt(from + 1)
          && prefixeMobiles[i].charAt(1) == text.charAt(from + 2)) return true;

      if(prefixeMobiles[i].length() == 3
          && prefixeMobiles[i].charAt(0) == text.charAt(from + 1)
          && prefixeMobiles[i].charAt(1) == text.charAt(from + 2)
          && prefixeMobiles[i].charAt(2) == text.charAt(from + 3)) return true;
    }

    return false;
  }

  public static boolean isCode(String text, int from) {
    if(text.length() < from + 3) return false;
    //   System.out.println(text.substring(from));
    if(text.charAt(from) == '8' 
      && text.charAt(from+1) == '4') {
      return true;
    }
    
    if(text.charAt(from) == '0' 
      && text.charAt(from+1) == '4') {
      return true;
    }
    
    if(text.charAt(from) == '0' 
      && text.charAt(from+1) == '8') {
      return true;
    }

    return false;
  }

}
