/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.TextSpliter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2009  
 */
public class VietWordSplitter extends VietnameseFullWords {

  public VietWordSplitter(File file) {
    super(file);
  }

  public VietWordSplitter() {
    super();
  }

  public List<Word> split(String text) {
    List<String> sequences = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      if(Character.isIdentifierIgnorable(c)) c = '\"';
      switch (c) {
      case ';':
      case ':':
      case '?':
      case '(':
      case ')':
      case '\n':
      case '\'':
      case '\"':
      case '!':
      case '“':
      case '”':
      case '‘':
      case '’':
      case '´':
        String seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(seq);
        }
        break;
      case '/':
      case '-':
      case '_':
        if(i > 0 && i < text.length() - 1
            && Character.isLetterOrDigit(text.charAt(i-1))
            && Character.isLetterOrDigit(text.charAt(i+1)) 
        ) {
          break;
        } 
        seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(seq);
        }
        break;
      case '.':
      case ',':
      case '…':
        if(i < (length-1) && i > 0 
            && Character.isDigit(text.charAt(i-1))
            && Character.isDigit(text.charAt(i+1))) {
          break;
        }
        seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) {
          sequences.add(seq);
        }
        break;
      default:
        break;
      }
      i++;
    }

    i = Math.min(length, i);
    String seq = text.substring(start, i);
    seq = CharsUtil.normalize(seq);
    if(!seq.isEmpty()) {
      sequences.add(seq);
    }

    List<Word> list = new ArrayList<Word>();
    for(int j = 0; j < sequences.size(); j++) {
      String _seq = sequences.get(j);
      if(_seq.length() < 1) continue;
      split(list, _seq);
    }

    return list;
  }

  protected void split(List<Word> list, String pattern) {
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(pattern, ' ');
    int i = 0;
    while(i < elements.length){
      elements[i] = elements[i].trim();
      if(elements[i].length() < 1) {
        i++;
        continue;
      }
      
      i = merge(list, elements, i);
      
      //      System.out.println(" ===  > "+i + " : "+ elements[i]);
      /*char c = elements[i].charAt(0);
      char lc = Character.toLowerCase(c);

      CNode cnode = cnodes.get(lc);
      //      System.out.println(" ===  >"+ c + " : "+ cnode);
      if(cnode == null) {
//        System.out.println(" =====  >" + c + " : "+ elements[i] );
        if(Character.isUpperCase(c)) {
//          System.out.println(" chay thu  "+ elements[i]);
          int idx = upper(list, elements, i);
          if(idx > i) {
            i = idx;
            continue;
          }
        } 
        list.add(new Word(elements[i]));
        i++;
        continue;
      }

      int index = cnode.startWith(elements, i);
      if(index <= i+1) {
//        System.out.println(" =====  >" + c + " : "+ elements[i] );
        if(Character.isUpperCase(c)) {
          int idx = upper(list, elements, i);
          if(idx > i) {
            i = idx;
            continue;
          }
        } 
        list.add(new Word(elements[i]));
        i++;
        continue;
      }
      
//      System.out.println(" =====  >" + c + " : "+ elements[i] + " : "+ i + " : " + index );

      //      System.out.println(i + " : "+ index);
      //      StringBuilder builder = new StringBuilder();
      //      for(; i < Math.min(index, elements.length); i++) {
      //        if(builder.length() > 0) builder.append(' ');
      //        builder.append(elements[i]);
      //      }
      //      System.out.println(builder.toString());
      list.add(new Word(elements, i, Math.min(index, elements.length), false));
      i = Math.min(index, elements.length);*/
    }
  }

  private int upper(List<Word> list, String[] elements, int i) {
    boolean isUpperCase = true;
    if(i == 0 && elements.length > 2 && elements[1].length() > 0) {
      char lc = Character.toLowerCase(elements[1].charAt(0));
      CNode cnode = cnodes.get(lc);
      if(cnode != null && cnode.startWith(elements, 1) > 2 )  return i;
    }
    
    for(int k = 1; k < elements[i].length(); k++) {
      if(Character.isLowerCase(elements[i].charAt(k))) {
        isUpperCase = false;
        break;
      }
    }

    if(isUpperCase) {
      list.add(new Word(elements[i], 2));
      return i+1;
    }

    int idx = i;
    while(idx < elements.length - 1){
      if(elements[idx+1].length() < 1) break;
      char c2 = elements[idx+1].charAt(0);
      //        System.out.println(c2 + " : " + Character.isUpperCase(c2));
      if(!Character.isUpperCase(c2)) break;
      idx++;
    }
    
    //      System.out.println(c  + " : " + i + " : "+ idx );
    if(idx > i) {
      //        System.out.println(builder);
      list.add(new Word(elements, i, idx+1, 1));
      return idx+1;
    } 
    return i;
  }
  
  private int merge(List<Word> list, String[] elements, int i) {
    char c = elements[i].charAt(0);
    char lc = Character.toLowerCase(c);
    CNode cnode = cnodes.get(lc);
    //      System.out.println(" ===  >"+ c + " : "+ cnode);
    int index = -1;
    if(cnode == null 
        || (index = cnode.startWith(elements, i)) <= i+1) {
//      System.out.println(" =====  >" + c + " : "+ elements[i] );
      if(Character.isUpperCase(c)) {
        
        if(i < elements.length -1 && isPrefix(elements[i])
            && elements[i+1].length() > 0) {
          char lc1 = Character.toLowerCase(elements[i+1].charAt(0));
          CNode cnode1 = cnodes.get(lc1);
          int idx1 = -1;
          if(cnode1 != null && (idx1 = cnode1.startWith(elements, i+1)) > 2 ) {
            list.add(new Word(elements, i, Math.min(idx1, elements.length), -1));
            return Math.min(idx1, elements.length);
          }
        }
//        System.out.println(" chay thu  "+ elements[i]);
        int idx = upper(list, elements, i);
        if(idx > i) return idx;
      } 
      list.add(new Word(elements[i]));
      return i+1;
    }

    list.add(new Word(elements, i, Math.min(index, elements.length), -1));
    return Math.min(index, elements.length);
  }
  
  private boolean isPrefix(String word) {
    return "Sở".equals(word) 
    || "Cty".equals(word)
    || "Bộ".equals(word)
    || "Phòng".equals(word)
    || "Hội".equals(word)
    || "Cục".equals(word);
  }
  
  public boolean contains(String[] elements) {
    char lc = Character.toLowerCase(elements[0].charAt(0));
    CNode cnode = cnodes.get(lc);
    if(cnode != null && cnode.startWith(elements, 0) > 2) {
//      for(String ele : elements) {
//        System.out.println(ele);
//      }
      return true;
    }
    return false;
  }
   
}
