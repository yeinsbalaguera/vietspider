/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 28, 2009  
 */
@SuppressWarnings("serial")
public class WordIndex implements Serializable {

  protected int code = 0;

  protected TreeSet<WordIndex> children = new TreeSet<WordIndex>(new  WordIndexComparator());

  public WordIndex(int code) {
    this.code = code;
  }

  public int getCode() { return code; }
  
  public TreeSet<WordIndex> getChildren() { return children; }
  
  public void add(String value) {
    //    System.out.println("value |"+ value + "|");
    int index = value.indexOf(' ');
    if(index < 0) {
      int newCode = value.hashCode();
      //      System.out.println("code 1 " + newCode);
      WordIndex child = new WordIndex(newCode);
      WordIndex oldChild = children.ceiling(child);
      if(oldChild != null && oldChild.code == child.code) {
        WordIndex zero = new WordIndex(0);
        WordIndex seft = oldChild.getChildren().ceiling(zero);
        if(seft != null && seft.getCode() == 0) return;
        oldChild.getChildren().add(zero);
        return;
      }
      
      child.getChildren().add(new WordIndex(0));
      children.add(child);
      return;
    }

    //    System.out.println("cat |"+ value.subSequence(0, index) + "|");
    int addCode = value.subSequence(0, index).hashCode();
    //    System.out.println("code 2 " +  addCode);
    value = value.substring(index+1, value.length());
    WordIndex child = new WordIndex(addCode);
    WordIndex wordIndex = children.ceiling(child);
    if(wordIndex != null && wordIndex.code == child.code) {
      wordIndex.add(value);
      return;
    }
    children.add(child);
    child.add(value);
  }

  public boolean contains(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndex> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      for(int k = 0; k < _children.size(); k++) {
      //        System.out.println(_children.get(k).code  +" == "+ elements.get(i));
      //      }
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      WordIndex word = new WordIndex(elements.get(i));
      WordIndex wordIndex = _children.ceiling(word);
      if(wordIndex == null || wordIndex.code != word.code) return false;
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;
      _children = wordIndex.children;
    }
    WordIndex seft = _children.ceiling(new WordIndex(0));
    return seft != null && seft.code == 0;
  }
  
  public boolean remove(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndex> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;

      WordIndex word = new WordIndex(elements.get(i));
      WordIndex wordIndex = _children.ceiling(new WordIndex(elements.get(i)));
      if(wordIndex == null || wordIndex.code != word.code) return false;

      WordIndex seft = wordIndex.children.ceiling(new WordIndex(0));
      if( seft != null && seft.code == 0) {
        wordIndex.children.remove(seft);
        return true;
      }
      
      _children = wordIndex.children;

    }
    return false;
  }

  private List<Integer> split(String value) {
    int start = 0;
    int length = value.length();
    int index = 0;
    List<Integer> values = new ArrayList<Integer>();
    while(index < length) {
      char c = value.charAt(index);
      if(c == ' ') {
        //        System.out.println(" thay |"+value.substring(start, index)+"|");
        values.add(value.substring(start, index).hashCode());
        start = index+1;
      }
      index++;
    }
    if(start < length) {
      //      System.out.println(" thay |"+value.substring(start, length)+"|");
      values.add(value.substring(start, length).hashCode());
    }
    return values;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    Iterator<WordIndex> iterator = children.iterator();
    while(iterator.hasNext()) {
      if(builder.length() > 0) builder.append('\n');
      iterator.next().buildString(builder);
      /* for(int i = 0; i < children.size(); i++) {
      if(builder.length() > 0) builder.append('\n');
      children.get(i).buildString(builder);
    }*/
    }
    return builder.toString();
  }

  private void buildString(StringBuilder builder) {
    builder.append(code).append('[');
    Iterator<WordIndex> iterator = children.iterator();
    while(iterator.hasNext()) {
      iterator.next().buildString(builder);
      builder.append(',');
      //    for(int i = 0; i < children.size(); i++) {
      //      children.get(i).buildString(builder);
      //      builder.append(',');
    }
    builder.append(']');
  }
  
  private final static class WordIndexComparator  implements Serializable,Comparator<WordIndex> {
    
    private final static long serialVersionUID = -9043676225081418946l;
    
    public int compare(WordIndex o1, WordIndex o2) {
      return o2.code - o1.code;
    }
  }

}
