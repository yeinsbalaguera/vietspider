/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.ArrayList;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionBottom4Plugin extends ActionAbstractPlugin {
  
  public short type() { return -1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();

    int index = AOTextProcessor.indexOf(lower, "ô");
    if(index < 0) return _CONTINUE;
    int from = index;
    
    index += 2; 
    
    while(index < lower.length()) {
      char c = lower.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      if(Character.isLetterOrDigit(c)) break;
      return _CONTINUE;
    }
    
    int s = index;
    if(s >= lower.length()) return _CONTINUE;
    
    while(index < lower.length()) {
      char c = lower.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        index++;
        continue;
      }
      break;
    }
    String next = lower.substring(s, index);
    
    if(next.equals("số")) {
//      ActionExtractor.println("bebe "+lower.substring(0, index));
//      String previous = TextProcessor.nextWord(lower, index);
//      ActionExtractor.println(" = ==  > "+ previous);
      element.putPoint(NLPData.ACTION_OBJECT, -1, from, index);
      add(list,  new ActionObject("<1,2", "ô số"));
      return _BREAK;
    }
    
    try {
      Integer.parseInt(next);
      element.putPoint(NLPData.ACTION_OBJECT, -1, from, index);
      add(list, new ActionObject("1,2", "ô"));
      return _BREAK;
    } catch (Exception e) {
      //no catch
    }
    
   
    return _CONTINUE;

  }

}
