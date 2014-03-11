/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.name;

import java.util.HashMap;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2009  
 */
public class NameNode {
  
  private char[] chars;
  
  private Name name;
  
  private  HashMap<Character, NameNode> children = new HashMap<Character, NameNode>();
  
  private char key;
  
  public NameNode(Name name, int index, NameNode...nodes) {
//    System.out.println(" tao lai " );
    this.name = name;
    this.chars = name.name().toCharArray();
    this.key = chars[index];
    for(int i = 0; i < nodes.length; i++) {
      children.put(nodes[i].key, nodes[i]);
    }
  }
  
  public NameNode(char [] chars, int index, NameNode...nodes) {
    this.chars = chars;
    this.key = chars[index];
    for(int i = 0; i < nodes.length; i++) {
      children.put(nodes[i].key, nodes[i]);
    }
  }
  
  public Name valueOf(int index, String value) {
//    System.out.println(" : "+ new String(chars));
    if(value.length() == chars.length) {
      for(int i = index+1; i < chars.length; i++) {
//        System.out.println(i+ " : "+ value.length()+ " : "+ chars.length);
        if(chars[i] != value.charAt(i)) return null;
      }
//      System.out.println(" thay co " +  " : " + chars.length + " : "+ new String(chars));
      return name;
      
//      System.out.println(" thay co " + index);
//      if(i == index && i < chars.length) return null;
//      return Name.NO_NAME;
    }
    
    NameNode nameNode = children.get(value.charAt(index+1));
    if(nameNode == null) return null;
    return nameNode.valueOf(index+1, value);
    
   /* index++;
    for(int i = 0; i < nodes.length; i++) {
      if(nodes[i].key != value.charAt(index)) continue;
//      System.out.println("=======================" + new String(chars)+ " : "+ key +"=============================");
      return nodes[i].valueOf(index, value);
    }
    return null;*/
  }
  
/*  public Name valueOf(int index, String value) {
    if(key != value.charAt(index)) return null;
    
    if(value.length() == chars.length) {
      int i = index+1;
      for(; i < chars.length; i++) {
//        System.out.println(i+ " : "+ value.length()+ " : "+ chars.length);
        if(chars[i] != value.charAt(i)) break;
      }
//      System.out.println(" thay co " + counter + " : " + chars.length + " : "+ new String(chars));
      if(i == chars.length) return name != null ? name  : Name.NO_NAME;
      
//      System.out.println(" thay co " + index);
//      if(i == index && i < chars.length) return null;
//      return Name.NO_NAME;
    }
    
    index++;
    for(int i = 0; i < nodes.length; i++) {
      System.out.println("=======================" + new String(chars)+ " : "+ key +"=============================");
      Name name_ = nodes[i].valueOf(index, value);
      if(name_ != null) return name_;
    }
    
    return Name.NO_NAME;
  }*/


  public char key() { return key; }


  public Name getName() {
    return name;
  }
  
}
