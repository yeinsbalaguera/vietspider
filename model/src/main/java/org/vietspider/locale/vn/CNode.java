/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.util.HashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 4, 2011  
 */
public class CNode {

  private char c;

  private HashMap<String, Node> collection = new HashMap<String, Node>();

  public CNode(char c) {
    this.c = c;
  }

  public char character() { return c; }

  public Node add(String word) {
    int idx = word.indexOf(' ');
    if(idx > 0) {
      String prefix = word.substring(0, idx);
      String key = prefix.toLowerCase();
      Node node = collection.get(key);
      if(node == null) {
        node = new Node(prefix);
        collection.put(key, node);
      } 
      node.addNext(word.substring(idx+1));
      return node;
    }
    
    String key = word.toLowerCase();
    Node node = collection.get(key);
    if(node == null) {
      node = new Node(word);
      collection.put(key, node);
    } 
    node.single = true;
    return node;
  }

  boolean contains(String text) {
    int idx = text.indexOf(' ');
    if(idx > 0) {
      String prefix = text.substring(0, idx);
      String key = prefix.toLowerCase();
      Node node = collection.get(key);
      if(node == null) return false;
      return true;//node.contains(text.substring(idx+1));
    } 
    String key = text.toLowerCase();
    Node node = collection.get(key);
    if(node == null) return false;
    return node.single;
  }

  public int startWith(String[] elements, int start) {
    Node node = collection.get(elements[start].toLowerCase());
    //    System.out.println(elements[start].toLowerCase() + " : " + node);
    if(node == null) return -1;
    int idx = node.startWith(elements, start);
    if(idx < 0) return -1;
    //    System.out.println("==== > "+ node.value);
    return start + idx + 1;
  }
  
  public boolean exist(Word word) {
    String [] elements = word.getElements();
    if(elements.length < 1) return false;
    Node node = collection.get(elements[0].toLowerCase());
//    if(word.getValue().equals("TBKTSG")) {
//      System.out.println(node +  " : "+ word.getValue());
//    }
//        System.out.println(elements[start].toLowerCase() + " : " + node);
    if(node == null) return false;
    return node.exist(word, 1);
  }


}
