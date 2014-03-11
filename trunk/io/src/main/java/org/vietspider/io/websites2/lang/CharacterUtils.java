/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 24, 2009  
 */
class CharacterUtils {
  
  static boolean isVietnamese(String value) {
    int counter =0;
    for(int i = 0; i < value.length(); i++) {
      if(isNotVietCharater(value.charAt(i))) continue;
      counter++;
      if(counter >= 3) return true;
    }
    return false;
  }
  
  static boolean isNotVietCharater(char c){
    switch (c) {
      case 'Ẳ': 
      case 'ẳ': 
      case 'Ẵ': 
      case 'ẵ': 
      case 'Ẩ': 
      case 'ẩ': 
      case 'Ẫ': 
      case 'ẫ': 
      case 'Đ': 
      case 'đ': 
      case 'Ễ': 
      case 'ễ': 
      case 'Ổ': 
      case 'ổ': 
      case 'Ỗ': 
      case 'ỗ': 
      case 'Ử': 
      case 'ử': 
      case 'Ữ': 
      case 'ữ': 
        return false;
      default: 
        return true;
    }
  }
}
