/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 10, 2008  
 */
public class TextCounter {
  
  private Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
  private Pattern wordPatternOnlyLetter = Pattern.compile("\\b[\\p{L}]");
  
  public int countWord(CharSequence chars, int start, int end){
    int counter = 0;
    Matcher matcher = wordPattern.matcher(chars);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      if(start >= end) break;
      counter++;
    }
    return counter;
  }
  
  public int countWordOnlyLetter(CharSequence chars, int start, int end){
    int counter = 0;
    Matcher matcher = wordPatternOnlyLetter.matcher(chars);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      if(start >= end) break;
      counter++;
    }
    return counter;
  }
  
  
  public int countSentence(CharSequence chars) {
    return countSentence(chars, 0, chars.length());
  }
  
  public int countSentence(CharSequence chars, int start, int end) {
    int index = start < 0 ? 0 : start;
    end  = end > chars.length() ? chars.length() : end;
    
    while(index < end) {
      char c = chars.charAt(index);
      if(Character.isLetterOrDigit(c)) break;
      index++;
    }
    
    while(end > index) {
      char c = chars.charAt(end-1);
      if(Character.isLetterOrDigit(c)) break;
      end--;
    }
    
    if(index >= end) return 0;
    
    int counter = 1;
    while(index < end) {
      char c = chars.charAt(index);
      switch (c) {
      case '?':
      case '.':
      case '!':
//      case ';':  
        while(index < end) {
          c = chars.charAt(index);
          if(Character.isLetterOrDigit(c)) {
            if(isValid(chars, index-1, end)) counter++;
            /*{ 
              counter++;
              if(counter == 2 && chars.subSequence(start, end).length() < 100) {
                System.out.println("=====================================");
                System.out.println(chars.subSequence(start, end));
                System.out.println("=====================================");
              }
            }*/
            break;
          }
          index++;
        }
        break;
      case '\n':
        while(index < end) {
          c = chars.charAt(index);
          if(Character.isLetterOrDigit(c) && Character.isUpperCase(c)) {
            counter++;
            break;
          } else if(c == '.' || c == '?' ||  c == '!') {
            index--;
            break;
          } else {
            index++;
          }
         
        }
        break;
      default:
        break;
      }
      index++;
    }
//    if(counter == 2) System.err.println(" thia nha "+ chars);
    return counter;
  }
  
  
  public int[] countByBreakChar(CharSequence chars, int start, char break_char) {
    int index = start < 0 ? 0 : start;
    int end = chars.length();
    
    while(index < end) {
      char c = chars.charAt(index);
      if(c == break_char) break;
      if(Character.isLetterOrDigit(c)) break;
      index++;
    }
    
    while(end > index) {
      char c = chars.charAt(end-1);
      if(c == break_char) break;
      if(Character.isLetterOrDigit(c)) break;
      end--;
    }
    
    if(index >= end) return new int[]{0, -1};
    
    int counter = 1;
    while(index < end) {
      char c = chars.charAt(index);
      if(c == break_char) return new int[]{counter, index};
      switch (c) {
      case '?':
      case '.':
      case '!':
      case ';':  
        while(index < end) {
          c = chars.charAt(index);
          if(c == break_char) return new int[]{counter, index};
          if(Character.isLetterOrDigit(c)) {
            counter++;
            break;
          }
          index++;
        }
        break;
      default:
        break;
      }
      index++;
    }
    return new int[]{counter, index};
  }
  
  private boolean isValid(CharSequence chars, int index, int end) {
    if(index  < 1 || index >= chars.length() - 1)  return true;
    int idx = index;
    
    while(idx < end) {
      char c = chars.charAt(idx);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        while(idx < chars.length()) {
          c = chars.charAt(idx);
          if(Character.isLetterOrDigit(c)) return true;
          idx++;
        }
        return false;
      } else if(Character.isLetterOrDigit(c))  return false;
      idx++;
    }
//    System.out.println(" xay ra het "+ chars);
    return false;
    /*char c1 = chars.charAt(index-1);
    char c2 = chars.charAt(index+1);
    
    return !(Character.isLetter(c1) && Character.isLetter(c2) 
        && Character.isUpperCase(c1) && Character.isUpperCase(c2));*/    
  }
  
  public int countWords(CharSequence charSeq){
    int start = 0;
    int counter = 0;
    Matcher matcher = wordPattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }
  
  
  public static void main(String[] args) {
    TextCounter textCounter = new TextCounter();
//    String text = ".a..";
//    String text = "    .Thực ra, có nhiều người đòi hỏi ở HLV  Calisto phát hiện nhiều hơn nữa những gương mặt trong lần tập trung này..... Đơn giản bởi người ta muốn nhìn thấy #một hình ảnh mới về ĐTQG. Sự xuất hiện của nhiều tân binh tạo ra cảm giác về một cuộc cách mạng về lực lượng. ....";
//    String text  = "Chỉ số VN-Index của Sở GDCK TP.HCM (HOSE) tăng 8,82 điểm (2,73%) lên 331,62 điểm.&nbsp; ";
//    String text = "Dịch vụ Quảng bá Website ,đưa Website lên Top trong bộ máy tìm kiếm Google ,Yahoo ,MSN Live...tăng thứ hạng PageRank và Alexa."
//+"Vui lòng liên hệ : Mr.Hùng - nick Y!M : dbp.hung hoặc hung3rd \n"
//+" Mobile : 0902710425 gặp Mr.Hùng \n"
//+" Email : dbp.hung@yahoo.com.vn ; dangbuiphihung@gmail.com";
//    String text = "Trong vai trò khách mời, Thủy Top xuất hiện rất nổi bật với chiếc váy dài màu xanh lá cây khoe vòng 1 rất gợi cảm của mình. Dù";
//    String text = "Vui lòng liên hệ : Mr.Hùng - nick Y!M : dbp.hung hoặc hung3rd";
    String text = "Cho thuê nhà công vụ chia chác 80.000 USD";
    System.out.println(textCounter.countWordOnlyLetter(text, 0, text.length()));
    System.out.println(textCounter.countSentence(text, 0, text.length()));
    System.out.println(textCounter.countByBreakChar(text, 0, '#'));
  }

}

