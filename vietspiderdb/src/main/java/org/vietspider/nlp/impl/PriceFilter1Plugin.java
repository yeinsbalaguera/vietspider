/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class PriceFilter1Plugin  {

  protected Point filter(TextElement element, short type) {
    String text = element.getLower();
    if(text == null) return null;

    int start = 0;
    Point point = null;
    Point root = null;

    while(start < text.length()) {
      start = text.indexOf("giá", start);
      if(start < 0) break;
      //                System.out.println(text.substring(start));
      int max = text.indexOf(". ", start+3);
      if(max < 0) {
        max = text.length();
      }

      //      point = search(text, point, start, max);
      Point p = search(text, point, start, max, true);
      if(point != p) {
        if(point != null) point.setNext(p);
        point = p;
        if(root == null) root = p;
      }

      start = max+1;
    }

    if(root == null) {
      //      System.out.println(text);
      root = search(text, null, 0, text.length(), false);
    }

    if(root != null) {
      element.putPoint(type, root);
    }
    return root;
    //    
  }

  protected Point search(String text, Point point, int start, int max, boolean trust) {
    int index = start;

    Point root = point;
    while(index < max) {
      char c = text.charAt(index);
      if(!Character.isDigit(c) && c != '$') {
        index++;
        continue;
      }

      int end = searchEnd(text, index, trust);

//      System.out.println("-------  > " + text.substring(start, index)+ " : "+ end +  " : "+ index);
      if(end  <= index) {
        index++;
        continue;
      }
      
      if(index > 1 
          && text.charAt(index-1) == 'm') {
        index++;
        continue;
      }
      
//      System.out.println(" hehehe  "+ text.charAt(index-1));
      

      String value  = text.substring(index, end);
//      System.out.println(value);
      String previous =  previous(text, index);
//      System.out.println("previous "+ previous+"|" + endWith(previous, "đặt cọc"));
//      System.out.println("value "+ value);
//      if(PriceFilter.TEST) {
//        System.out.println(" =====  > "+ value + " : "+ validData(previous, value, trust));
//      }
      if(!validData(previous, value, trust)) {
        index = end + 1;
        //        System.out.println(end  + "  : " + max + "  : "+ index);
        continue;
      }
      
      if(value.endsWith("/m")) {
        String next = TextProcessor.nextWord(text, end + 1);
        if("ngang".equals(next)) {
          index = end + 1;
          //        System.out.println(end  + "  : " + max + "  : "+ index);
          continue;
        }
      }
      
//      System.out.println("=============> "+ value);

      Point p = new Point(5, index, end);
      //      System.out.println(point);
      if(point != null) point.setNext(p);
      point = p;
      if(root == null) root = p;
      //      System.out.println(point);

      start = end;
      index = end+1;
    }

    return root;
  }
  
  private String previous(String text, int index) {
    while(index > 0) {
      char c = text.charAt(index);
//      System.out.println("|"+c + "|" + (Character.isWhitespace(c)
//          || Character.isSpaceChar(c)));
      if(Character.isLetterOrDigit(c)) {
        return text.substring(0, index-1);
      }
      index--;
    }
    
    return "";
  }
  
  private boolean endWith(String text, String pattern) {
    int i = text.length()-1;
    if(i < 0) return false;
    int j = pattern.length()-1;
    while(i > -1 && j > -1) {
      char c1 = text.charAt(i);
      char c2 = pattern.charAt(j);
      
      if(!Character.isLetterOrDigit(c1)
           && !Character.isLetterOrDigit(c2)
          ) {
        i--;
        j--;
        continue;
      }
      
      if(Character.isLetterOrDigit(c1)
          && Character.isLetterOrDigit(c2)
          && c1 == c2) {
        i--;
        j--;
        continue;
      }
      
      return false; 
    }
    
//    System.out.println(i + " : "+ j);
    
    return true;
  }

  int searchEnd(String text, int index, boolean trust) {
    int start = index;
    while(index < text.length()) {
      char c = text.charAt(index);
//      System.out.println(text.substring(start, index) + " : " + c);
      if(c == 't') {
        //        int s = index;
        int i = next1(text, index);
        
        if(i > 0 && i < text.length() - 1) {
          char c1 = text.charAt(index+1);
          if(c1 == 'y' 
            || c1 == 'ỷ' 
              || c1 == 'ỉ' 
                || c1 == 'i') {
            char c2 = text.charAt(i + 1);
            if(c2 == ',' || c2 == '.') return i+1;
          }
        }
        //                System.out.println(text.substring(start, index)+  ": ");
        if(i < 0) break;
        index = i;
        continue;
      } else if(c == '.' || c == ',') {
        if(index > 0 && index < text.length()-1) {
          char c1 = text.charAt(index-1);
          char c2 = text.charAt(index+1);
          if(Character.isDigit(c1) && Character.isDigit(c2)) {
            index++;
            continue;
          }
        }
        //        System.out.println(text);
        //        System.out.println(text.substring(0, start));
        //        System.out.println(text.substring(start, index));
        break;
      } else if(c == '$' 
        || ((c == 'k' || c == 'n' 
          || c == 'đ' || c == 'd' ) && notLetter(text, index+1))
          || c == '-' || c == '–') {
        index++;
        continue;
      } else if(c == 'l' && notLetter(text, index+1) && trust) {
        index++;
        continue;
      } else if(c == '1' && index < text.length() - 2
          && text.charAt(index+1) == 'm') {
        index += 2;
        continue;
      } else if(c == '/') {
        //        System.out.println(text.substring(start, index));
        //        int i = index;
        int i = next2(text, index+1);
        if(i > 0) index = i;
        break;
      } else if(c == '(') {
        int i = next3(text, index+1);
//        System.out.println(text.substring(0, index));
//        System.out.println(text.substring(index));
        if(i > 0) {
          index = i;
          continue;
        }
        break;
      } else if(c == 'u') {
        //        System.out.println(text.substring(start, index));
        //        System.out.println("nenene "+ index + " : " + (text.length() - 2));
        //        System.out.println(text.charAt(index+1)+ " : " + text.charAt(index+2));
        if(index < text.length() - 2 
            && text.charAt(index+1) == 's'
              && text.charAt(index+2) == 'd') {
          index += 3;
          continue;
        }
        break;
      } else if(c == 'đ' || c == 'd') {//đồng
        //      System.out.println("nenene "+ index + " : " + (text.length() - 2));
        //      System.out.println(text.charAt(index+1)+ " : " + text.charAt(index+2));
        if(index < text.length() - 3 
            && (text.charAt(index+1) == 'ồ' || text.charAt(index+1) == 'o')
            && text.charAt(index+2) == 'n'
              && text.charAt(index+3) == 'g') {
          index += 4;
          continue;
        }
        break;
      } else if(c == 'v') {//đồng
        //      System.out.println("nenene "+ index + " : " + (text.length() - 2));
        //      System.out.println(text.charAt(index+1)+ " : " + text.charAt(index+2));
        if(index < text.length() - 2 
            && text.charAt(index+1) == 'n'
              && (text.charAt(index+2) == 'đ' || text.charAt(index+2) == 'd')) {
          index += 3;
          continue;
        }
        break;
      } else if(c == 'l') {
        if(index < text.length() - 4
            && (text.charAt(index+1) == 'ư' || text.charAt(index+1) == 'u')
            && (text.charAt(index+2) == 'ợ' || text.charAt(index+2) == 'o')
            && text.charAt(index+3) == 'n' && text.charAt(index+4) == 'g') {
          index += 5;
          continue;
        }
        break;
        //      } else if(c == 'c') {
        //        if(index < text.length() - 2 
        //            && (text.charAt(index+1) == 'â' || text.charAt(index+1) == 'a')
        //            && text.charAt(index+2) == 'y') {
        //          index += 3;
        //          continue;
        //        }
        //        break;
      } else if(c == 'n') {
        if(index < text.length() - 3
            && text.charAt(index+1) == 'g'
              && (text.charAt(index+2) == 'à' || text.charAt(index+2) == 'a')
              && text.charAt(index+3) == 'n') {
          index += 4;
          continue;
        } else if(index < text.length() - 1
            && text.charAt(index+1) == 'g' && notLetter(text, index+2)) {
          index += 2;
          continue;
        }
        break;
      } else if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c) || Character.isDigit(c)) {
        index++;
        continue;
      }
      break;
    }

    //    if(index > start) {
    //      System.out.println(text.substring(start, index));
    //    }
//    System.out.println(" truoc do "+ index);
    index = trim(text, start, index);
//    System.out.println(" sau do "+ index);
    //    if(index > 0) {
    //      System.out.println(" ===  >|"+text.substring(start, index)+"|");
    //    }

    return index;
  }

  private int trim(String text, int start, int index) {
    index--;
    if(index >= text.length()) {
      index = text.length()-1;
    }

    while(index > start) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)
          || c == '-' || c == '–'  //|| c == '('
            || c == '.' || c == ',') {
        index--;
        continue;
      }
      return index+1;
    }
    return start;
  }

  private int next1(String text, int index) {//trieu
    if(index >= text.length() - 1) return -1;

    //    System.out.println("==========================================");
    //    System.out.println(text.charAt(index));
    index += 1;
    char c = text.charAt(index);
    //    System.out.println(text.substring(0, index));
    //    System.out.println(text.charAt(index) +  ": " + (c == 'l') +   ": " + ( notLetter(text, index+1)));

    //ty
    if((c == 'y' || c == 'ỷ' 
      || c == 'i' || c == 'ỉ')
        && notLetter(text, index+1)) return index+1;

//    if((c == 'i' || c == 'ỉ')
//        && notLetter(text, index+1)) return index+1;

    //    System.out.println(" 00000000 > "+ text.substring(index) + " : "+ c);
    if(c == '/') return index;

    //tr
    if(c != 'r') {
      //      System.out.println(text.substring(0, index));
      //      System.out.println(" 00000000 > "+ text.substring(index) +  " : "+notLetter(text, index+1));
      if(notLetter(text, index)) return index + 1;
      return -1;
    }

    if(index + 3 < text.length()) {
      char c1 = text.charAt(index+1);
      char c2 = text.charAt(index+2);
      char c3 = text.charAt(index+3);
      if((Character.isWhitespace(c1)  || Character.isSpaceChar(c1))
          && (c2 == 'm') && c3 == '2' && notLetter(text, index+4)) return index+4;
    }

    if(notLetter(text, index+1)) return index+1;

    if(index + 2 < text.length()) {
      char c1 = text.charAt(index+1);
      char c2 = text.charAt(index+2);
      if((c1 == 'ă' || c1 == 'a') 
          && c2 == 'm' && notLetter(text, index+3)) return index+3;
    }

    if(index + 3 < text.length()) {
      char c1 = text.charAt(index+1);
      char c2 = text.charAt(index+2);
      char c3 = text.charAt(index+3);
      //      System.out.println(c1 + ":" + c2 + ":"+ c3);
      if(c1 == 'i' && (c2 == 'ệ' || c2 == 'e') 
          && c3 == 'u' && notLetter(text, index+4)) return index+4;
    }

    //    if(index + 3 < text.length()) {
    //      char c1 = text.charAt(index+1);
    //      char c2 = text.charAt(index+2);
    //      char c3 = text.charAt(index+3);
    //      if((c2 == 'á' || c1 == 'a') && c1 == 'n'  
    //          && c3 == 'g' && notLetter(text, index+4)) return index+4;
    //    }

    return -1;
  }

  private int next2(String text, int index) {
//            int s = index;
    //    System.out.println(text);
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c) 
          || Character.isDigit(c)) {
        index++;
        continue;
      }
      //      System.out.println(c);
      break;
    }
    //    System.out.println(" chay vao day " + s + " : "+ index + " : "+ (text.length() -1 ));

//    System.out.println(index + " : " + text.charAt(index));
//    System.out.println("sub "+text.substring(s, index));

    if(index >= text.length()) return -1;

    if(index  < text.length()) {
      char c1 = text.charAt(index);
      //      System.out.println(" ===  >" + c1);
      if(c1 == 't' 
        && notLetter(text, index+1)) return index+1;
    }

    if(index + 1 < text.length()) {
      char c1 = text.charAt(index);
      char c2 = text.charAt(index+1);
      //      System.out.println(c1 + " === " +  c2);
      if(c1 == 'm' && c2 == '2') return index+2;
      if(c1 == 'n' && c2 == 'g'
        && notLetter(text, index+2)) return index+2;
      if(c1 == 't' && c2 == 'h' 
        && notLetter(text, index+2)) return index+2;
    }

    if(index + 2 < text.length()) {
      char c1 = text.charAt(index);
      char c2 = text.charAt(index+1);
      char c3 = text.charAt(index+2);
      //      System.out.println(c1 + ":" + c2 + ":"+ c3);
      if(c1 == 'c' 
        && (c2 == 'ă' || c2 == 'a') 
        && c3 == 'n') return index+3;

      if(c1 == 'n' 
        && (c2 == 'ề' || c2 == 'e') 
        && c3 == 'n') return index+3;

      if(c1 == 'm' 
        && (Character.isWhitespace(c2) || Character.isSpaceChar(c2)) 
        && c3 == '2') return index+3;
    }

    if(index < text.length()) {
      char c1 = text.charAt(index);
      //      System.out.println(text.substring(0, index));
      if(c1 == 'm') return index+1;
    }

    if(index + 3 < text.length()) {
      char c1 = text.charAt(index);
      char c2 = text.charAt(index+1);
      char c3 = text.charAt(index+2);
      char c4 = text.charAt(index+3);
      if(c1 == 't' 
        && (c2 == 'ổ' || c2 == 'o') 
        && c3 == 'n' && c4 == 'g') return index+4;
    }

    if(index + 4 < text.length()) {
      char c1 = text.charAt(index);
      char c2 = text.charAt(index+1);
      char c3 = text.charAt(index+2);
      char c4 = text.charAt(index+3);
      char c5 = text.charAt(index+4);
      //      System.out.println(c1 + " : " + c2 + " : " + c3 + " : " + c4 + " : " + c5);
      if(c1 == 't' && c2 == 'h' &&
          (c3 == 'á' || c3 == 'a') 
          && c4 == 'n' && c5 == 'g') return index+5;

      if(c1 == 'p' && c2 == 'h' &&
          (c3 == 'ò' || c3 == 'o') 
          && c4 == 'n' && c5 == 'g') return index+5;

      if(c1 == 'n' && c2 == 'g' &&
          (c3 == 'ư' || c3 == 'u') &&
          (c4 == 'ờ' || c4 == 'o')
          && c5 == 'i') return index+5;

    }

    return -1;
  }

  private boolean notLetter(String text, int index) {
    if(index >= text.length()) return true;
    char c = text.charAt(index);
    if(c == '%' || c == '&') return false;
    if(Character.isDigit(c)) return true;
    return !Character.isLetter(c);
  }

  private int next3(String text, int index) {
    //  int s = index;
    //System.out.println(text);
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      //      System.out.println(c);
      break;
    }

    if(index >= text.length()) return -1;

    char c = text.charAt(index);

    //    System.out.println(" ====== next 3===> "+c);

    //    if(c == 't' || c == 'u') return index;

    if(c == 't') return next1(text, index);

    //    System.out.println(" ====== next 3===> "+c + " : " 
    //        + text.charAt(index+1) + " : " + text.charAt(index+2)+
    //        ":" + (c == 'u' && index < text.length() - 1
    //            && text.charAt(index+1) == 's' 
    //              && text.charAt(index+2) == 'd'));

    if(c == 'u' && index < text.length() - 2
        && text.charAt(index+1) == 's' 
          && text.charAt(index+2) == 'd') return index+3;

    return -1;
  }
  
  protected boolean validData(String previous, String text, boolean trust) {
    int len = text.length();
    if(len < 2) return false;
    
    previous = previous.trim();
    
//    System.out.println(text +  " : "+ previous);
    
    if(!isPreviousValid(previous)) return false;
    
//    System.out.println("text: "+text);

    if(len > 1) {
      char c = text.charAt(len-1);
      if(c == '$') return true;
      if(trust && c == 'l') return true;
    }

    //m2
    if(len > 2 
        && text.charAt(len-2)== 'm'
          && text.charAt(len-1)== '2')  return true;
    
    if(len > 2 
        && text.charAt(len-2)== '1'
          && text.charAt(len-1)== 'm')  return true;

    if(len > 3 
        && text.charAt(len-3)== 'm'
          && Character.isWhitespace(text.charAt(len-2))
          && text.charAt(len-1)== '2')  return true;

    if(len > 2 
        && text.charAt(len-2)== '/'
          && (text.charAt(len-1)== 'm' 
            || text.charAt(len-1)== 't'))  return true;

    //usd
    if(len > 3 
        && text.charAt(len-3)== 'u'
          && text.charAt(len-2)== 's'
            && text.charAt(len-1)== 'd')  return true;

    //ty, tr
//        System.out.println(text.charAt(len-2)+ " : "+ text.charAt(len-1));
    if(len > 2 && text.charAt(len-2)== 't' 
      && (text.charAt(len-1) == 'y' || text.charAt(len-1) == 'ỷ'
        || text.charAt(len-1) == 'r'
          || text.charAt(len-1) == 'i' || text.charAt(len-1) == 'ỉ'
      ))  return true;

    if(len > 4 && text.charAt(len-4)== 't' 
      && text.charAt(len-3) == 'r'
        && (text.charAt(len-2) == 'ă' || text.charAt(len-2) == 'a') 
        && text.charAt(len-1) == 'm')  return true;

    //    if(len > 5 && text.charAt(len-5)== 't' 
    //      && text.charAt(len-4) == 'r'
    //        && text.charAt(len-3) == 'i'
    //          && (text.charAt(len-2) == 'e' || text.charAt(len-2) == 'ệ') 
    //          && text.charAt(len-1) == 'u')  return true;

    if(len > 3 && text.charAt(len-3)== 'c' 
      && (text.charAt(len-2) == 'a' || text.charAt(len-2) == 'â') 
      && text.charAt(len-1) == 'y')  return true;

    if(len > 3 && text.charAt(len - 3) == 'c' 
      && (text.charAt(len - 2) == 'ă' || text.charAt(len - 2) == 'a') 
      && text.charAt(len - 1) == 'n') return true;

    if(len > 4 && (text.charAt(len-4)== 'đ' || text.charAt(len-4)== 'd') 
        && (text.charAt(len-3) == 'ồ' || text.charAt(len - 3) == 'o')
        && text.charAt(len-2) == 'n' 
          && text.charAt(len-1) == 'g')  return true;
    //VNĐ

    if(len > 3 &&  text.charAt(len-3) == 'v'
      && text.charAt(len-2) == 'n' 
        && (text.charAt(len-1)== 'đ' || text.charAt(len-1)== 'd'))  return true;

    if(len > 3 && text.charAt(len - 3) == 'c' 
      && (text.charAt(len - 2) == 'ă' || text.charAt(len - 2) == 'a') 
      && text.charAt(len - 1) == 'n') return true;

    if(len > 5) {
      char c1 = text.charAt(len-5);
      char c2 = text.charAt(len-4);
      char c3 = text.charAt(len-3);
      char c4 = text.charAt(len-2);
      char c5 = text.charAt(len-1);
      

//      if(c1== 't' && c2 == 'r'
//        && c3 == 'i' 
//          && (c4 == 'e' || c4 == 'ệ') 
//          && c5 == 'u')  return true;

      if(c1 == 't' && c2 == 'h'
        && (c3 == 'á' || c3 == 'a') 
        && c4 == 'n' && c5 == 'g') return true;

      if(c1 == 'n' && c2 == 'g' &&
          (c3 == 'ư' || c3 == 'u') &&
          (c4 == 'ờ' || c4 == 'o')
          && c5 == 'i') return true;

      if(c1 == 'p'
        && c2 == 'h'
          && (c3 == 'ò' || c3 == 'o') 
          && c4 == 'n'
            && c5 == 'g') return true;
    }

    //nền
    if(len > 3 && text.charAt(len - 3) == 'n' 
      && (text.charAt(len - 2) == 'ề' || text.charAt(len - 2) == 'e') 
      && text.charAt(len - 1) == 'n') return true;

    if(len > 2 && text.charAt(len - 2) == 'n' 
      && text.charAt(len - 1) == 'g') return true;

    if(len > 2 && text.charAt(len-2)== 't') { 
//      System.out.println(previous);
//      System.out.println(" xay ra roi "+ text);
//      && (Character.isDigit(text.charAt(len-1)) 
       if(text.charAt(len - 1) == 'h') return true;
//       System.out.println(Character.isDigit(text.charAt(len-1)) + " " + endWith(previous, "giá"));
       if(Character.isDigit(text.charAt(len-1)) 
           &&  endWith(previous, "giá")) return true; 
//      System.out.println(" xay raday "+ text);
      return false;
    }
    
    int index = text.indexOf("triệu");
    if(index < 0) index = text.indexOf("trieu");
    if(index > 0) {
      index += 6;
      return isDigitElement(text, index, text.length());
    }

    index = text.indexOf("ty");
    if(index < 0) index = text.indexOf("tỷ");
    if(index < 0) index = text.indexOf("tỉ");
    if(index < 0) index = text.indexOf("ti");
    if(index > 0) {
      index += 2;
      return isDigitElement(text, index, text.length());
    }

    if(text.charAt(0) == '$') {
      return isDigitElement(text, 1, text.length());
    }

    if(text.charAt(text.length() - 1) == 't' 
      &&  isDigitElement(text, 0, text.length()-1)) {
      if(previous.endsWith("giá") 
          || previous.endsWith("bán")) return true;
//      System.out.println("=== > "+ previous);
//      System.out.println("=== > " + text);
      return false;
    }

    if(text.charAt(text.length() - 1) == 'đ'
      || text.charAt(text.length() - 1) == 'd') {
      return isDigitElement(text, 0, text.length()-1);
    }

    //    if(trust) {
    //      return isDigitElement(text, 0, text.length());
    //    }

    return false;
  }
  
  protected boolean isPreviousValid(String previous) {
    int idx = previous.lastIndexOf('.');
    if(idx > 0) previous = previous.substring(idx+1);
//    System.out.println(" previous "+ previous + " : "+ endWith(previous, "tặng ngay"));
    if(endWith(previous, "nhận đặt chỗ")
        || endWith(previous, "phí sang tên")
        || endWith(previous, "gtgt")
        || endWith(previous, "vốn đầu tư")
        || endWith(previous, "giữ chỗ chỉ")
        || endWith(previous, " x")
        || endWith(previous, "tặng ngay")
        || endWith(previous, "cọc")
        || endWith(previous, "tòa nhà")
        || previous.indexOf("mức đầu tư khoảng") > -1
        || previous.indexOf("phiếu mua hàng") > -1
        || previous.indexOf("bộ nội thất") > -1
        || previous.indexOf("đặt cọc") > -1
        || previous.indexOf("vốn đầu tư") > -1
        || (previous.indexOf("đóng") > -1 && previous.indexOf("đợt") > -1)
        || (previous.indexOf("thu") > -1 && previous.indexOf("đợt") > -1)
        || endWith(previous, "hơn")
        || previous.indexOf("đã có khách trả") > -1
        || endWith(previous, "giữ lại")
        || endWith(previous, "chênh lệch")
        || previous.indexOf("bán chênh") > -1
        || endWith(previous, "chênh")
        || endWith(previous, "đúc")) return false;
    
//    System.out.println("text: "+text);
    
    if(previous.length() > 3 
        && previous.charAt(previous.length() - 1) == 'x'
          && Character.isDigit(previous.charAt(previous.length() - 2))) return false;
    
    return true;
  }

  private boolean isDigitElement(String text, int index, int max) {
    while(index < max) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)
          || Character.isDigit(c)
          || c == ',' || c == '.') {
        index++;
        continue;
      }
      return false;
    }
    return true;
  }

}
