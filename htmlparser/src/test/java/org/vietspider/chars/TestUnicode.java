/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.chars;

import java.io.File;

import org.vietspider.chars.unicode.Normalizer;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 26, 2007
 */
public class TestUnicode {
  
  private final static Normalizer composer = new Normalizer(Normalizer.C);  
  
  private static void translate(HTMLNode node){
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT)){
        n.setValue(composer.normalize(new String(n.getValue())).toCharArray());
      }
    }
    
   /* List<HTMLNode> children = node.getChildren();   
    if(children == null) return;
    for(HTMLNode child : children){
      if(child.isNode(Name.CONTENT)){
        child.setValue(composer.normalize(new String(child.getValue())).toCharArray());
      }
      if(child.isNode(Name.SCRIPT) || child.isNode(Name.STYLE)) continue;
      translate(child);
    }  */
  }
  
  public static void main(String[] args) throws Exception {
    String string = "Hà Nội";
    LogService.getInstance().setMessage(null, string);
    LogService.getInstance().setMessage(null, composer.normalize(string));
    LogService.getInstance().setMessage(null, java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFD));
    
    HTMLDocument document = new HTMLParser2().createDocument(new File("F:\\Temp\\Clean Data\\3.html"),"utf-8");
    translate(document.getRoot());
    
    File file = new File("F:\\Temp\\Clean Data\\unicode.htm");    
    org.vietspider.common.io.RWData.getInstance().save(file, document.getRoot().getTextValue().getBytes("utf-8"));
  }
}
