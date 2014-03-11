/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.chars.refs;

import java.util.Comparator;

import org.vietspider.chars.XMLDataEncoder;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
public class RefsEncoder implements XMLDataEncoder {
  
  protected boolean hexadecimal = false;
  
  public RefsEncoder(){   
  }
  
  public RefsEncoder(boolean hexadecimalV){    
    hexadecimal = hexadecimalV;
  }
    
  public char[] encode (char[] chars) {
    CharRefs charRefs = new CharRefs();
    if(!charRefs.isSorted()) charRefs.sort(new Comparator<CharRef>(){
      public int compare(CharRef o1, CharRef o2){
        if(o1.getValue() == o2.getValue()) return 0;
        if(o1.getValue() > o2.getValue()) return 1;
        return -1;
      }
    });
    CharsSequence refValue = new CharsSequence(chars.length * 6);
    char c;
    CharRef ref;
    int i = 0;
    while (i < chars.length){
      c = chars[i];
      ref = charRefs.searchByValue(c, new Comparator<CharRef>(){
        public int compare(CharRef o1, CharRef o2){
          if(o1.getValue() == o2.getValue()) return 0;
          if(o1.getValue() > o2.getValue()) return 1;
          return -1;
        }
      });
      if (ref != null){
        refValue.append ("&#");
        refValue.append (String.valueOf(ref.getValue()));
        refValue.append (';');
      }else if (!(c < 0x007F)){
        refValue.append ("&#");
        if (hexadecimal) {          
          refValue.append ('x');
          refValue.append (Integer.toHexString (c));
        }else{          
          refValue.append(String.valueOf((int)c));
        }
        refValue.append (';');
      } else{
        refValue.append (c);
      }
      i++;
    }
    return refValue.getValues();
  }

}
