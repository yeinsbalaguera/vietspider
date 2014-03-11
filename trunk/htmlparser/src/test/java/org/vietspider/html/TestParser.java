/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html;

import java.io.File;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.unicode.Normalizer;
import org.vietspider.html.parser.HTMLParser;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 7, 2006
 */
public class TestParser {

  static RefsDecoder decoder = new RefsDecoder();
  static Normalizer normalizer = new Normalizer();

//  public static void print(String text, HTMLNode element){
//    List<HTMLNode> children = element.getChildren();      
//    for(HTMLNode ele : children) print(text +" ",ele);
//  }
  
  public static synchronized void decode(HTMLNode node, boolean isNormalize){
    List<HTMLNode> children = node.getChildren();   
    if(children == null) return ;
    for(HTMLNode child : children){
//      if(child.isNode(Name.COMMENT)) continue;
      if(child.isNode(Name.CONTENT)){
        char [] chars = child.getValue();
        chars = decoder.decode(chars);
        chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
        if(isNormalize) chars = normalizer.normalize(chars);
        if(chars.length < 1) continue;
        child.setValue(chars);              
      }else if(!child.isNode(Name.SCRIPT) && !child.isNode(Name.STYLE)){
        decode(child, isNormalize) ;
      }
    }  
  }

  public static void main(String[] args) throws Exception {
    HTMLDocument doc = HTMLParser.createDocument(new File("F:\\Temp\\default.html"), "ASCII");
    HTMLNode node = doc.getRoot();
    decode(node, true);
    System.out.println(node.getTextValue());
//    print("",node);  
  }

}
