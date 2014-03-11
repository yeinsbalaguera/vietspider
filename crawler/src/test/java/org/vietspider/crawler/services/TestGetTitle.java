/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.util.ArrayList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 10, 2006
 */
public class TestGetTitle {
  
  private static int getTitle(List<char[]> values){
    int idx = 0;
    for(; idx < values.size(); idx++){
      String title = fromChars(values.get(idx), 15);
      if(title != null){
        System.out.println(title);
        break;
      }
    }
    return idx;
  }
  
  private static String getDescription(List<char[]> values, int idx){
    Integer size = new Integer(70);
    StringBuilder des = new StringBuilder();
    char [] chars ;
    for(; idx < values.size(); idx++){
      chars = values.get(idx);      
      String text = fromChars(chars, size);
      if(text != null) des.append(text);
      if(size.intValue() <= 0) break;
    }
    return des.toString();
  }

  private static String fromChars(char [] chars, Integer size){
    int i = 0;
    boolean start = false;
    int counter = 0;
    while(i< chars.length){
      if(!start && Character.isLetterOrDigit(chars[i])) start = true;
      if(start && Character.isWhitespace(chars[i])) counter++;
      if(counter > size) break;
      i++;
    }      
    if(counter > 0){
      size = size - counter;
      if(counter < size - 1) return new String(chars);
      if(i >= chars.length) i = chars.length;
      char[] newChars = new char[i];
      System.arraycopy(chars, 0, newChars, 0, newChars.length);      
      return new String(newChars);
    }    
    return null;
  }
  
  public static void main(String[] args) {
    List<char[]> list = new ArrayList<char[]>();
    list.add("10 cÃ´ gÃ¡i Ä‘Æ°á»£c Ban GiÃ¡m kháº£o xÃ¡c Ä‘á»‹nh lÃ  Ä‘áº¹p nháº¥t  vÃ  Ä‘Æ°á»£c tham dá»± tiáº¿p cÃ¡c vÃ²ng thi Ã¡o táº¯m".toCharArray());
    list.add("Mai PhÆ°Æ¡ng ThÃºy - NgÆ°á»�i Ä‘áº¿n muá»™n may máº¯n ".toCharArray());
    list.add("Tháº¿ rá»“i sÃ¡ng 10/8, trong khi thÃ­ sinh khÃ¡c chá»‰ viá»‡c chá»‰nh trang vÃ¡y Ã¡o Ä‘á»ƒ trÃ¬nh diá»…n thÃ¬ ThÃºy pháº£i â€œkhÃ¡m nghiá»‡mâ€� trÆ°á»›c Ä‘Ã£. ".toCharArray());
    list.add("RiÃªng tÃ´i sau khi quan sÃ¡t cÃ¡c gÆ°Æ¡ng máº·t dá»± tuyá»ƒn, nháº¯n tin cho Ä‘á»“ng nghiá»‡p tráº» BÃ­ch HÆ°Æ¡ng á»Ÿ miá»�n Nam: Hoa háº­u Ä‘Ã£ xuáº¥t hiá»‡n vÃ o phÃºt chÃ³t. 1m79. Táº§m quá»‘c táº¿. VÃ o Nha Trang, tháº¥y LÆ°u Báº£o Anh cÅ©ng ráº¥t Ä‘áº¹p, Ä‘áº±m, sÃ¡ng trÆ°ng. Tháº­t nan giáº£i cho BGK. ".toCharArray());
    list.add("Cá»™ng cáº£ 2 Ä‘iá»�u láº¡i, cÃ³ ngÆ°á»�i dá»± cáº£m ThÃºy chá»‰ Ä‘oáº¡t Ã� háº­u.  ".toCharArray());
    list.add("Kaâ€™The- Hoa háº­u ThÃ¢n thiá»‡n Ä‘áº¿n tá»« cao nguyÃªn Di Linh ".toCharArray());
    list.add("Kaâ€™The Ä‘áº¿n tá»« Di Linh tá»«ng lÃ m BGK Ä‘áº·c biá»‡t áº¥n tÆ°á»£ng trong má»™t cuá»™c tráº¯c nghiá»‡m Ä‘i tÃ¬m Hoa háº­u ThÃ¢n thiá»‡n ".toCharArray());
    int idx = getTitle(list);
    String des = getDescription(list, idx+1);
    System.out.println(des);
  }
}
