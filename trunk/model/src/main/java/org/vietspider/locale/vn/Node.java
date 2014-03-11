/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.util.ArrayList;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 4, 2011  
 */
public class Node {
  
  String value;
  
  boolean single = false;
  
  private List<Node> nexts = new ArrayList<Node>();
  
  public Node(String text) {
    int idx = text.indexOf(' ');
    if(idx < 0) {
      this.value = text;
      return;
    }
    this.value = text.substring(0, idx);
    nexts.add(new Node(text.substring(idx+1)));
  }
  
  public void addNext(String text) {
    nexts.add(new Node(text));
  }
  
  public int startWith(String[] elements, int start) {
    int max = 0;
    if(start+1 >= elements.length) return start;
    boolean match = false;
    for(int i = 0; i < nexts.size(); i++) {
      if(elements[start+1].equalsIgnoreCase(nexts.get(i).value)) {
        int _return = nexts.get(i).startWith(elements, start+1);
//        System.out.println(" ==  > "+ _return);
        if(_return + 1 > max) max = _return + 1;
        match = true;
      }
    }
    if(!match && nexts.size() > 0) {
      if(single) return max;
      return -1;
    }
    return max;
  }
  
  public boolean exist(Word word, int start) {
//    System.out.println(" ===  >"+ value+  " : "+ word.getValue() + " : "+ nexts.size() + " : "+ word.getElements().length);
//    if(nexts.size() == 1) {
//      System.out.println(nexts.get(0).value);
//    }
    String [] elements = word.getElements();
    if(start >= elements.length) {
      if(elements.length == 1) return single;
      return nexts.size() < 1;
    }
    for(int i = 0; i < nexts.size(); i++) {
      if(elements[start].equalsIgnoreCase(nexts.get(i).value)) {
        boolean _return = nexts.get(i).exist(word, start+1);
        if(_return) return true;
      }
    }
    return single;
  }
  
}
