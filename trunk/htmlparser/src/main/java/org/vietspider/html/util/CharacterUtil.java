/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.html.util;

import java.util.Arrays;
import java.util.List;

import org.vietspider.html.HTMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 20, 2007
 */
public class CharacterUtil {

  public final char [] separators  = {';', ',', '.', ':','\'', '"', '|' , '{', '}',
      '?', '(', ')', '<', '>', '{', '}', '[', ']', '!', '/', '\\', '+', '=', '-', '_', '&'};

  public CharacterUtil() {
    java.util.Arrays.sort(separators);
  }
  
  public boolean checkContent(List<HTMLNode> nodes, int min){
    int count  = 0;
//    System.out.println(" tai day co "+ nodes.size());
    for(HTMLNode node : nodes) {
//      System.out.println(new String(node.getValue()));
      count += checkContent(count, node.getValue(), min);
      if(count >= min) return true;
    }
//    System.out.println(count + " : "+ min);
    return count >= min;
  }
  
  public boolean checkContent(char [] values, int min) { 
    return checkContent(0, values, min) >= min; 
  }
  
  private int checkContent(int count, char [] values, int min){
    int i = 0;
    boolean start = true;
//    System.out.println(new String(values));
    while(i < values.length){  
      if(Character.isWhitespace(values[i]) 
          || Character.isSpaceChar(values[i]) 
          || Arrays.binarySearch(separators, values[i]) > -1 ){
        if(!start){
          count++;
          start = true;
        }
      } else {
        start = false;
      }
      if(count >= min) return count;
      i++;        
    }
    if(!start) return count+1;
    return count;
  }
  
  public int count(char [] values){
    int i = 0;
    int count = 0;
    boolean start = true;
    while(i < values.length){  
      if(Character.isWhitespace(values[i]) 
          || Character.isSpaceChar(values[i]) 
          || Arrays.binarySearch(separators, values[i]) > -1 ){
        if(!start){
          count++;
          start = true;
        }
      } else {
        start = false;
      }
      i++;        
    }
    if(!start) return count+1;
    return count;
  }
  
  public  char[] toLowercase(char [] chars){
    char [] newChars = new char[chars.length];
    for(int i = 0; i < chars.length; i++){
      if(Character.isUpperCase(chars[i])){
        newChars[i] = Character.toLowerCase(chars[i]);
        continue;
      }      
      newChars[i] = chars[i]; 
    }
    return newChars;
  }
  
  /*public static void main(String[] args) throws Exception {
    CharacterUtil test = new CharacterUtil();
    
    System.out.println("=--- > "+ total1+ " : "+ total2);
    
//    HTMLDocument document = HTMLParser.createDocument(new File("E:\\Temp\\Clean Data\\a.htm"), "utf-8");
//    
//    HTMLNode node = document.getRoot();
//    
//    List<HTMLNode> list = new ArrayList<HTMLNode>();
//    test.searchTextNode(node, list);
//    
//    for(HTMLNode ele : list) {
//      System.out.println(ele.getTextValue());
//    }
    
//    System.out.println(test.count("                             ?nh    Mobelkraft".toCharArray()));
  }*/
}
