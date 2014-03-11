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
public class ArticleWordSplitter extends VietWordSplitter {

  public ArticleWordSplitter(File file) {
    super(file);
  }

  public ArticleWordSplitter() {
    super();
  }

  public List<Word> split(String text) {
    TextSpliter spliter = new TextSpliter();
    List<String> elements = spliter.toList(text, '\n');
    List<String> sequences = new ArrayList<String>();
    for(int i = 0; i < elements.size(); i++) {
      String value = elements.get(i).trim();
//      System.out.println(value);
//      System.out.println(value.charAt(0));
      if(value.length() < 1) continue;
      if(value.charAt(0) == '(') {
        int idx = value.indexOf(')', 1);
        if(idx > 0 && idx < 25) value = value.substring(idx+1);
      }
//      System.out.println("=============>"+value);
      splitChunk(sequences, value);
    }
    
//    System.out.println(sequences.size());
    
    List<Word> list = new ArrayList<Word>();
    for(int j = 0; j < sequences.size(); j++) {
      String _seq = sequences.get(j);
      if(_seq.length() < 1) continue;
      split(list, _seq);
    }
//    System.out.println(list.size());

    return list;
  }
  
  private void splitChunk(List<String> sequences, String text) {
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
  }
   
}
