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
public class WordIndex2 implements Serializable {
  
  private final static long serialVersionUID = -3683047580319654549l;

  protected int code = 0;

  protected TreeSet<WordIndex2> children;

  public WordIndex2(int code) {
    if(this.code == -1) code = 0;
    this.code = code;
    children = new TreeSet<WordIndex2>(new  WordIndexComparator());
  }

  public int getCode() { return code; }
  
  public TreeSet<WordIndex2> getChildren() { return children; }
  
  public void add(String value) {
    //    System.out.println("value |"+ value + "|");
    int index = value.indexOf(' ');
    if(index < 0) {
      int newCode = value.hashCode();
      //      System.out.println("code 1 " + newCode);
      WordIndex2 child = new WordIndex2(newCode);
      WordIndex2 oldChild = children.ceiling(child);
      if(oldChild != null && oldChild.code == child.code) {
        WordIndex2 zero = new WordIndex2(0);
        WordIndex2 seft = oldChild.getChildren().ceiling(zero);
        if(seft != null && seft.getCode() == 0) return;
        oldChild.getChildren().add(zero);
        return;
      }
      
      child.getChildren().add(new WordIndex2(0));
      children.add(child);
      return;
    }

    //    System.out.println("cat |"+ value.subSequence(0, index) + "|");
    int addCode = value.subSequence(0, index).hashCode();
    //    System.out.println("code 2 " +  addCode);
    value = value.substring(index+1, value.length());
    WordIndex2 child = new WordIndex2(addCode);
    WordIndex2 wordIndex2 = children.ceiling(child);
    if(wordIndex2 != null && wordIndex2.code == child.code) {
      wordIndex2.add(value);
      return;
    }
    children.add(child);
    child.add(value);
  }

  public boolean contains(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndex2> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      for(int k = 0; k < _children.size(); k++) {
      //        System.out.println(_children.get(k).code  +" == "+ elements.get(i));
      //      }
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      WordIndex2 word = new WordIndex2(elements.get(i));
      WordIndex2 wordIndex2 = _children.ceiling(word);
      if(wordIndex2 == null || wordIndex2.code != word.code) return false;
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;
      _children = wordIndex2.children;
    }
    WordIndex2 seft = _children.ceiling(new WordIndex2(0));
    return seft != null && seft.code == 0;
  }
  
  public boolean remove(String value) {
    List<Integer> elements = split(value);
    TreeSet<WordIndex2> _children = children;
    for(int i = 0; i < elements.size(); i++) {
      //      int index = Collections.binarySearch(_children, new WordIndex(elements.get(i)), COMPARATOR);
      //      System.out.println("search "+elements.get(i)+ " == > " +index);
      //      if(index < 0) return false;

      WordIndex2 word = new WordIndex2(elements.get(i));
      WordIndex2 wordIndex2 = _children.ceiling(new WordIndex2(elements.get(i)));
      if(wordIndex2 == null || wordIndex2.code != word.code) return false;

      WordIndex2 seft = wordIndex2.children.ceiling(new WordIndex2(0));
      if( seft != null && seft.code == 0) {
        wordIndex2.children.remove(seft);
        return true;
      }
      
      _children = wordIndex2.children;

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
    Iterator<WordIndex2> iterator = children.iterator();
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
    Iterator<WordIndex2> iterator = children.iterator();
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
      WordIndex2 wordIndex2 = new WordIndex2(c);
      pos = wordIndex2.read(stream, pos, length);
      children.add(wordIndex2);
    }
    return pos;
  }
  
  public static WordIndex2 readObject(DataInputStream stream, long pos, long length) throws Exception {
    int c = stream.readInt();
    pos +=4;
    if(c == -1) return null;
    WordIndex2 wordIndex2 = new WordIndex2(c);
    wordIndex2.read(stream, pos, length);
    return wordIndex2;
  }
  
  private final static class WordIndexComparator  implements Serializable,Comparator<WordIndex2> {
    
    private final static long serialVersionUID = -9043676225081418946l;
    
    public int compare(WordIndex2 o1, WordIndex2 o2) {
      return o2.code - o1.code;
    }
  }

}
