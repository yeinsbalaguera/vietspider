/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

import java.util.Iterator;
import java.util.TreeSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 6, 2009  
 */
public class WordComparator {
  
  public int compare(String title1, String title2) {
    String ltitle1 = title1.toLowerCase();
    String ltitle2 = title2.toLowerCase();
    TreeSet<String> words1 =  split(ltitle1);
    TreeSet<String> words2 =  split(ltitle2);
    return compare(words1, words2);
  }
  
  public int compare(TreeSet<String> words1, TreeSet<String> words2) {
    int counter = 0;
    Iterator<String> iterator = words1.iterator();
    while(iterator.hasNext()) {
      String word = iterator.next();
      if(words2.contains(word)) counter++;
    }
    
    return Math.max(words1.size() - counter, words2.size() - counter);
  }
  
  public TreeSet<String> split(String text) {
    TreeSet<String> words = new TreeSet<String>();
    
    int start = 0;
    int length = text.length();
    
    while(start < length){
      char c = text.charAt(start);
      if(Character.isLetterOrDigit(c)) break;
      start++;
    }
    
    int index = start;
    while(index < length){
      char c = text.charAt(index);
//      if(c == '<') {
//        words.add(text.substring(start, index));
//        int end  = text.indexOf(index+1, '>');
//        index = end+1;
//        start = end+1;
//        index++;
//        continue;
//      }
      
      if(!Character.isLetterOrDigit(c)) {
        words.add(text.substring(start, index));
        start = index+1;
      }
      index++;
    }
    
    if(start < length) words.add(text.substring(start, length));
    return words;
  }
  
  public static void main(String[] args) {
    String title1  = "Hi ho";
    String title2  = "...HI..... ho...";
    WordComparator comparator = new WordComparator();
    
    title2 = "” Sửa máy <b>tính</b> tại nhà  0916349183 ″  ";
    title1 = "”” sửa chữa máy tính tận nhà (0916.349.183) tuấn!! ″  ″";
    
    title1 = "Bán liền kề 19 ô 31 đô thị mới Văn Khê 82,5m2 hướng ĐN";
    title2 = "Bán liền kề 19 ô 31 đô thị mới Văn Khê 82,5m2 hướng ĐN";
    
    System.out.println(comparator.compare(title1, title2));
  }
  
  
  
}
