/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text.vn;


/** 
 * Author : Le Bien Thuy
 * Dec 18, 2007  
 */
public class VNIgnoreCaseMarkComparator implements VNComparator {

  public int compare(char c1, char c2) {
    if(c1 == c2) return 0;
    int enc1 = encode(c1);
    int enc2 = encode(c2);
    return (enc1 < 0 || enc2 < 0) ? c1 - c2 : enc1- enc2;
  }

  public  int compare(String c1, String c2) {
    int min = Math.min(c2.length(), c1.length());
    for(int i = 0; i < min; i++){
      int k = compare(c1.charAt(i), c2.charAt(i));
      if(k != 0) return k;
    }
    return c1.length() - c2.length();
  }

  private int encode(char c){
    switch (c) {
    case 'A':
    case 'a':
    case 'À':
    case 'à': 
    case 'Á':
    case 'á': 
    case 'Ả':
    case 'ả':
    case 'Ã':
    case 'ã': 
    case 'Ạ':
    case 'ạ':
    case 'Ă':
    case 'ă':
    case 'Ằ': 
    case 'ằ':
    case 'Ắ': 
    case 'ắ':
    case 'Ẳ': 
    case 'ẳ':

    case 'Ẵ': 
    case 'ẵ':
    case 'Ặ': 
    case 'ặ':
    case 'Â': 
    case 'â':
    case 'Ầ': 
    case 'ầ':
    case 'Ấ': 
    case 'ấ':
    case 'Ẩ': 
    case 'ẩ':
    case 'Ẫ': 
    case 'ẫ':
    case 'Ậ': 
    case 'ậ': return 1;
    
    case 'B': 
    case 'b': return 2;
    case 'C': 
    case 'c': return 3;

    case 'D': 
    case 'd':
    case 'Đ': 
    case 'đ': return 4;
    
    case 'E': 
    case 'e': 
    case 'È': 
    case 'è':
    case 'É': 
    case 'é':
    case 'Ẻ': 
    case 'ẻ':
    case 'Ẽ': 
    case 'ẽ':
    case 'Ẹ': 
    case 'ẹ':
    case 'Ê': 
    case 'ê':
    case 'Ề': 
    case 'ề':
    case 'Ế': 
    case 'ế':
    case 'Ể': 
    case 'ể':
    case 'Ễ': 
    case 'ễ':
    case 'Ệ':
    case 'ệ': return 5;
    
    case 'F': 
    case 'f': return 6;
    case 'G':
    case 'g': return 7;
    case 'H': 
    case 'h': return 8;
    
    case 'I': 
    case 'i': 
    case 'Ì': 
    case 'ì':
    case 'Í': 
    case 'í':
    case 'Ỉ': 
    case 'ỉ':
    case 'Ĩ': 
    case 'ĩ':
    case 'Ị': 
    case 'ị': return 9;
    
    case 'J': 
    case 'j': return 10;
    
    case 'K': 
    case 'k': return 11;
    case 'L': 
    case 'l': return 12;
    case 'M': 
    case 'm': return 13;
    case 'N': 
    case 'n': return 14;
    
    case 'O': 
    case 'o': 
    case 'Ò': 
    case 'ò': 
    case 'Ó':    
    case 'ó': 
    case 'Ỏ': 
    case 'ỏ': 
    case 'Õ': 
    case 'õ': 
    case 'Ọ': 
    case 'ọ': 
    case 'Ô': 
    case 'ô': 
    case 'Ồ': 
    case 'ồ': 
    case 'Ố': 
    case 'ố': 
    case 'Ổ': 
    case 'ổ': 
    case 'Ỗ': 
    case 'ỗ': 
    case 'Ộ': 
    case 'ộ': 
    case 'Ơ': 
    case 'ơ': 
    case 'Ờ': 
    case 'ờ': 
    case 'Ớ': 
    case 'ớ': 
    case 'Ở': 
    case 'ở': 
    case 'Ỡ': 
    case 'ỡ': 
    case 'Ợ': 
    case 'ợ': return 15;
    
    case 'P': 
    case 'p': return 16;
    case 'Q': 
    case 'q': return 17;    
    case 'R': 
    case 'r': return 18;
    case 'S': 
    case 's': return 19;

    case 'T': 
    case 't': return 20;
    
    case 'U': 
    case 'u': 
    case 'Ù': 
    case 'ù':
    case 'Ú': 
    case 'ú': 
    case 'Ủ': 
    case 'ủ': 
    case 'Ũ': 
    case 'ũ': 
    case 'Ụ': 
    case 'ụ': 
    case 'Ư': 
    case 'ư':     
    case 'Ừ': 
    case 'ừ': 
    case 'Ứ': 
    case 'ứ': 
    case 'Ử': 
    case 'ử': 
    case 'Ữ': 
    case 'ữ': 
    case 'Ự': 
    case 'ự': return 21;
    
    case 'V': 
    case 'v': return 22;
    case 'W': 
    case 'w': return 23;
    case 'X': 
    case 'x': return 24;
    
    case 'Y': 
    case 'y': 
    case 'Ỳ': 
    case 'ỳ': 
    case 'Ý': 
    case 'ý': 
    case 'Ỷ': 
    case 'ỷ': 
    case 'Ỹ': 
    case 'ỹ': 
    case 'Ỵ': 
    case 'ỵ': return 25;
    case 'Z': 
    case 'z': return 26;
    default: return -1;
    }
  }
  
  public static void main(String[] args) {
    String value = "tuyên";
    String pattern = "tuyon";
    VNIgnoreCaseMarkComparator comparator = new VNIgnoreCaseMarkComparator();
    System.out.println(comparator.compare(value, pattern));
  }
}
