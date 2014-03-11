/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.db.dict.non.NWord;
import org.vietspider.db.dict.non.NWordValue;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 14, 2009  
 */
public class TranslateNode {
  
  private String value = "";

  private List<TranslateNode> children = new ArrayList<TranslateNode>();

  public TranslateNode(String v) {
    this.value = v;
  }

  public List<TranslateNode> getChildren() { return children; }

  public String getValue() { return value; }

  public void addKey(TranslateKey key) {
    if(children.size() < 1) {
      NWord nword = key.getValue();
      NWordValue [] values = nword.getValues();
      for(int i = 0; i < values.length; i++) {
//        System.out.println("values[i].getValue() "+values[i].getValue());
        children.add(new TranslateNode(values[i].getValue()));
      }
      return;
    }
    for(int i = 0;  i < children.size(); i++) {
      children.get(i).addKey(key);
    }
  }

  public void addKey(String text) {
    if(children.size() < 1) {
      children.add(new TranslateNode(text));
      return;
    }

    for(int i = 0; i < children.size(); i++) {
      children.get(i).addKey(text);
    }
  }
  
  public List<StringBuilder> build() {
    List<StringBuilder> builders = new ArrayList<StringBuilder>();
    if(children.size() > 0) {
      for(int i = 0; i < children.size(); i++) {
        builders.addAll(children.get(i).build());
      }
    }
    
    if(value.length() < 0) return builders;
    
    if(builders.size() < 1) builders.add(new StringBuilder(100));
    
    for(int i = 0; i < builders.size(); i++) {
      builders.get(i).insert(0, ' ').insert(0, value);
    }
    
    return builders;
  }

}
