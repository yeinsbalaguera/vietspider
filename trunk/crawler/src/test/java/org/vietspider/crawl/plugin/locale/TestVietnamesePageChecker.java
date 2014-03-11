/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.locale;

import java.text.Normalizer;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.crawl.plugin.handler.VietnamesePageChecker;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.TextRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2009  
 */
public class TestVietnamesePageChecker {
  public static void main(String[] args) throws Exception {
   String address =  "http://profile.myspace.com/index.cfm?fuseaction=user.viewProfile&friendID=358892135";
    
    java.net.URL url = new java.net.URL(address);
    HTMLDocument document = new HTMLParser2().createDocument(url.openStream(),"utf-8");
    
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      node.setValue(chars);              
    }  
    
    TextRenderer renderer = new TextRenderer(document.getRoot(), null);
    VietnamesePageChecker pageChecker = new VietnamesePageChecker();
    System.out.println(pageChecker.check(renderer.getTextValue().toString()));
  }
}
