/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
public class WordIndexBak implements Serializable {
  
  private final static long serialVersionUID = -3683047580319654549l;

  protected int code = 0;

  protected TreeSet<WordIndexBak> children;

  public WordIndexBak(int code) {
    if(this.code == -1) code = 0;
    this.code = code;
    children = new TreeSet<WordIndexBak>(new  WordIndexComparator());
  }

  public int getCode() { return code; }
  
  public TreeSet<WordIndexBak> getChildren() { return children; }
  
  public void add(String value) {
    //    System.out.println("value |"+ value + "|");
    int index = value.indexOf(' ');
    if(index < 0) {
      int newCode = value.hashCode();
      //      System.out.println("code 1 " + newCode);
      WordIndexBak child = new WordIndexBak(newCode);
      WordIndexBak oldChild = children.ceiling(child);
      if(oldChild != null && oldChild.code == child.code) {
        WordIndexBak zero = new WordIndexBak(0);
        WordIndexBak seft = oldChild.getChildren().ceiling(zero);
        if(seft != null && seft.getCode() == 0) return;
        oldChild.getChildren().add(zero);
        return;
      }
      
      child.getChildren().add(new WordIndexBak(0));
      children.add(child);
      return;
    }

    //    System.out.println("cat |"+ value.subSequence(0, index) + "|");
    int addCode = value.subSequence(0, index).hashCode();
    //    System.out.println("code 2 " +  addCode);
    value = value.substring(index+1, value.length());
    WordIndexBak child = new WordIndexBak(addCode);
    WordIndexBak wordIndex = children.ceiling(child);
    if(wordIndex != null && wordIndex.code == child.code) {
      wordIndex.add(value);
      return;
    }
    children.add(child);
    child.add(value);
  }

  public boolean contains(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndexBak> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      for(int k = 0; k < _children.size(); k++) {
      //        System.out.println(_children.get(k).code  +" == "+ elements.get(i));
      //      }
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      WordIndexBak word = new WordIndexBak(elements.get(i));
      WordIndexBak wordIndex = _children.ceiling(word);
      if(wordIndex == null || wordIndex.code != word.code) return false;
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;
      _children = wordIndex.children;
    }
    WordIndexBak seft = _children.ceiling(new WordIndexBak(0));
    return seft != null && seft.code == 0;
  }
  
  public boolean remove(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndexBak> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;

      WordIndexBak word = new WordIndexBak(elements.get(i));
      WordIndexBak wordIndex = _children.ceiling(new WordIndexBak(elements.get(i)));
      if(wordIndex == null || wordIndex.code != word.code) return false;

      WordIndexBak seft = wordIndex.children.ceiling(new WordIndexBak(0));
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
    buildString(builder);
    return builder.toString();
  }

  public void buildString(StringBuilder builder) {
    builder.append(code).append('[');
    Iterator<WordIndexBak> iterator = children.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      if(counter > 0) builder.append(',');
      iterator.next().buildString(builder);
      counter++;
    }
    builder.append(']');
  }
  
  public void write(DataOutputStream stream) throws Exception {
    stream.writeInt(code);
    Iterator<WordIndexBak> iterator = children.iterator();
    while(iterator.hasNext()) {
      iterator.next().write(stream);
    }
    stream.writeInt(-1);
    stream.flush();
  }
  
  public long read(DataInputStream stream, long pos, long length) throws Exception {
    while(pos < length) {
      int c = stream.readInt();
//      System.out.println(c +" / "+ pos+" / "+length);
      pos += 4;
      if(c == -1) return pos;
      WordIndexBak wordIndex = new WordIndexBak(c);
      pos = wordIndex.read(stream, pos, length);
      children.add(wordIndex);
    }
    return pos;
  }
  
  public static WordIndexBak readObject(DataInputStream stream, long pos, long length) throws Exception {
    int c = stream.readInt();
    pos +=4;
    if(c == -1) return null;
    WordIndexBak wordIndex = new WordIndexBak(c);
    wordIndex.read(stream, pos, length);
    return wordIndex;
  }
  
  private final static class WordIndexComparator  implements Serializable,Comparator<WordIndexBak> {
    
    private final static long serialVersionUID = -9043676225081418946l;
    
    public int compare(WordIndexBak o1, WordIndexBak o2) {
      return o2.code - o1.code;
    }
  }

}
