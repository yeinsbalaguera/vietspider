/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.text.Normalizer;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.NodeImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2009  
 */
public class DocumentDecoder {
  
  public static void decode(RefsDecoder decoder, HTMLDocument document){
//    CharsToken tokens = document.getTokens();
//    Iterator<NodeImpl> iterator = tokens.iterator();
//    Normalizer normalizer = worker.getResource(Normalizer.class);
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);
      
      replace(chars, '\n', ' ');
      
      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
//      chars = normalizer.normalize(chars);
      node.setValue(chars);              
    }  
  }  
  
  private static void replace(char [] chars, char oldChar, char newChar) {
    int i = 0;
    while(i < chars.length) {
      if(chars[i] == oldChar) {
        chars[i] = newChar;
      }
      i++;
    }
  }

  
  public static void decode(List<NodeImpl> tokens){
    RefsDecoder decoder = new RefsDecoder();
    if(tokens == null) return;
//    Normalizer normalizer = worker.getResource(Normalizer.class);
    for(int i = 0; i < tokens.size(); i++) {
      HTMLNode node = tokens.get(i);
      if(!node.isNode(Name.CONTENT)) continue; 
      char [] chars = node.getValue();
      
      chars = decoder.decode(chars);
      
      replace(chars, '\n', ' ');
      
      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
//      chars = normalizer.normalize(chars);
      node.setValue(chars);
    }
  }
}
