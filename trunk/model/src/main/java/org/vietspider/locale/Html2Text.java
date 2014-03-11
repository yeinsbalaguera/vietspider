/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.html.Group;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class Html2Text {
  
  private static Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
  
  private static Name [] styles = {Name.FONT, Name.SUB, Name.SUP};
  private static char [] endSentences  = {'.', ':', '?'};
  
  public static String toText(List<NodeImpl> tokens) {
//    CharacterUtil charUtil = new CharacterUtil();
    
//    char [] lastElement = null;
    StringBuilder builder = new StringBuilder();
    
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(!node.isNode(Name.CONTENT)) continue;
      if(node.isNode(Name.SCRIPT) 
          || node.isNode(Name.STYLE)) {
        i++;
        continue;
      }
      char [] chars = node.getValue();
      if(chars.length < 1) continue;
      
//      int counter = charUtil.count(chars);    
//      if(lastElement != null 
//          && isSentence(lastElement) 
//          && counter < 5) continue;
      
      if(!isStyle(node) && !isSentence(chars)){
        char [] nText = new char[chars.length+1];
        System.arraycopy(chars, 0, nText, 0, nText.length-1);
        nText[chars.length] = '.';
        chars = nText;
      }
      builder.append(chars).append(' ');
//      lastElement = chars;
    }
    return builder.toString();
  }
  
  private static boolean isSentence(char[] text){
    char c = text[text.length - 1]; 
    for(char ele : endSentences){
      if(c == ele) return true;
    }
    return false;
  }
  
  public static  int countWords(CharSequence charSeq){
    int start = 0;
    int counter = 0;
    Matcher matcher = wordPattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }
  
  private static  boolean isStyle(HTMLNode node){
    if(node.getConfig().type() == Group.Fontstyle.class) return true;
    if(node.getConfig().type() == Group.Phrase.class) return true;
    for(Name name : styles) {
      if(node.getName() == name) return false;
    }
    return false;
  }
}
