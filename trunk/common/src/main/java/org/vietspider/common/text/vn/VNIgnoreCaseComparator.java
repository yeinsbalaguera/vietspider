/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text.vn;


/** 
 * Author : Le Bien Thuy
 * Dec 18, 2007  
 */
public class VNIgnoreCaseComparator implements VNComparator {

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
    case 'a': return 1;
    case 'À':
    case 'à': return 2;
    case 'Á':
    case 'á': return 3;
    case 'Ả':
    case 'ả': return 4;
    case 'Ã':
    case 'ã': return 5;
    case 'Ạ':
    case 'ạ': return 6;
    case 'Ă':
    case 'ă': return 7;
    case 'Ằ': 
    case 'ằ': return 8;
    case 'Ắ': 
    case 'ắ': return 9;
    case 'Ẳ': 
    case 'ẳ': return 10;

    case 'Ẵ': 
    case 'ẵ': return 11;
    case 'Ặ': 
    case 'ặ': return 12;
    case 'Â': 
    case 'â': return 13;
    case 'Ầ': 
    case 'ầ': return 14;
    case 'Ấ': 
    case 'ấ': return 15;
    case 'Ẩ': 
    case 'ẩ': return 16;
    case 'Ẫ': 
    case 'ẫ': return 17;
    case 'Ậ': 
    case 'ậ': return 18;
    case 'B': 
    case 'b': return 19;
    case 'C': 
    case 'c': return 20;

    case 'D': 
    case 'd': return 21;
    case 'Đ': 
    case 'đ': return 22;
    case 'E': 
    case 'e': return 23;
    case 'È': 
    case 'è': return 24;
    case 'É': 
    case 'é': return 25;
    case 'Ẻ': 
    case 'ẻ': return 26;
    case 'Ẽ': 
    case 'ẽ': return 27;
    case 'Ẹ': 
    case 'ẹ': return 28;
    case 'Ê': 
    case 'ê': return 29;
    case 'Ề': 
    case 'ề': return 30;

    case 'Ế': 
    case 'ế': return 31;
    case 'Ể': 
    case 'ể': return 32;
    case 'Ễ': 
    case 'ễ': return 33;
    case 'Ệ':
    case 'ệ': return 34;
    case 'F': 
    case 'f': return 35;
    case 'G':
    case 'g': return 36;
    case 'H': 
    case 'h': return 37;
    case 'I': 
    case 'i': return 38;
    case 'Ì': 
    case 'ì': return 39;
    case 'Í': 
    case 'í': return 40;

    case 'Ỉ': 
    case 'ỉ': return 41;
    case 'Ĩ': 
    case 'ĩ': return 42;
    case 'Ị': 
    case 'ị': return 43;
    case 'J': 
    case 'j': return 44;
    case 'K': 
    case 'k': return 45;
    case 'L': 
    case 'l': return 46;
    case 'M': 
    case 'm': return 47;
    case 'N': 
    case 'n': return 48;
    case 'O': 
    case 'o': return 49;
    case 'Ò': 
    case 'ò': return 50;

    case 'Ó':    
    case 'ó': return 51;
    case 'Ỏ': 
    case 'ỏ': return 52;
    case 'Õ': 
    case 'õ': return 53;
    case 'Ọ': 
    case 'ọ': return 54;
    case 'Ô': 
    case 'ô': return 55;
    case 'Ồ': 
    case 'ồ': return 56;
    case 'Ố': 
    case 'ố': return 57;
    case 'Ổ': 
    case 'ổ': return 58;
    case 'Ỗ': 
    case 'ỗ': return 59;
    case 'Ộ': 
    case 'ộ': return 60;

    case 'Ơ': 
    case 'ơ': return 61;
    case 'Ờ': 
    case 'ờ': return 62;
    case 'Ớ': 
    case 'ớ': return 63;
    case 'Ở': 
    case 'ở': return 64;
    case 'Ỡ': 
    case 'ỡ': return 65;
    case 'Ợ': 
    case 'ợ': return 66;
    case 'P': 
    case 'p': return 67;
    case 'Q': 
    case 'q': return 68;    
    case 'R': 
    case 'r': return 69;
    case 'S': 
    case 's': return 70;

    case 'T': 
    case 't': return 71;
    case 'U': 
    case 'u': return 72;
    case 'Ù': 
    case 'ù': return 73;
    case 'Ú': 
    case 'ú': return 74;
    case 'Ủ': 
    case 'ủ': return 75;
    case 'Ũ': 
    case 'ũ': return 76;
    case 'Ụ': 
    case 'ụ': return 78;
    case 'Ư': 
    case 'ư': return 78;    
    case 'Ừ': 
    case 'ừ': return 79;
    case 'Ứ': 
    case 'ứ': return 80;

    case 'Ử': 
    case 'ử': return 81;
    case 'Ữ': 
    case 'ữ': return 82;
    case 'Ự': 
    case 'ự': return 83;
    case 'V': 
    case 'v': return 84;
    case 'W': 
    case 'w': return 85;
    case 'X': 
    case 'x': return 86;
    case 'Y': 
    case 'y': return 87;
    case 'Ỳ': 
    case 'ỳ': return 88;
    case 'Ý': 
    case 'ý': return 89;
    case 'Ỷ': 
    case 'ỷ': return 90;
    case 'Ỹ': 
    case 'ỹ': return 91;
    case 'Ỵ': 
    case 'ỵ': return 92;
    case 'Z': 
    case 'z': return 93;
    default: return -1;
    }
  }
  public static void main(String[] args) {
    String value = "tuyên";
    String pattern = "tuyên truyền";
    VNIgnoreCaseComparator comparator = new VNIgnoreCaseComparator();
    System.out.println(comparator.compare(value, pattern));
  }
}
