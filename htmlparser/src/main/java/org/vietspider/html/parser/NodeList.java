/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.parser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.vietspider.html.HTMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 15, 2008  
 */
@SuppressWarnings("serial")
public class NodeList implements List<HTMLNode>, Serializable {
  
  private LinkedList<HTMLNode> values = new LinkedList<HTMLNode>();

  @Deprecated()
  @SuppressWarnings("unused")
  public void add(int index, HTMLNode element) {
    throw new UnsupportedOperationException();
  }
  
  void addElement(int index, HTMLNode element) {
    values.add(index, element);
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean add(HTMLNode e) {
    throw new UnsupportedOperationException();
  }
  
  void addElement(HTMLNode element) {
    values.add(element);
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean addAll(Collection<? extends HTMLNode> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean addAll(int index, Collection<? extends HTMLNode> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  void clearElements() { values.clear(); }

  public boolean contains(Object o) { return values.contains(o); }

  public boolean containsAll(Collection<?> c) { return values.contains(c); }

  public HTMLNode get(int index) {
    return values.get(index);
  }

  public int indexOf(Object o) { return values.indexOf(o); }

  public boolean isEmpty() { return values.isEmpty(); }

  @Deprecated()
  public Iterator<HTMLNode> iterator() {
    return values.iterator();
//    throw new UnsupportedOperationException();
  }
  
  public Iterator<HTMLNode> iteratorElement() { return values.iterator(); }


  public int lastIndexOf(Object o) {
    return values.lastIndexOf(o);
  }
  
  @Deprecated()
  public ListIterator<HTMLNode> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public ListIterator<HTMLNode> listIterator(int index) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated()
  @SuppressWarnings("unused")
  public HTMLNode remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  
  void removeElement(Object o) {
    values.remove(o);
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public HTMLNode set(int index, HTMLNode element) {
    throw new UnsupportedOperationException();
  }
  
  void setElement(int index, HTMLNode element) {
    values.set(index, element);
  }

  public int size() { return values.size(); }

  @Deprecated()
  @SuppressWarnings("unused")
  public List<HTMLNode> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray() { return values.toArray(); }

  public <T> T[] toArray(T[] a) { return values.toArray(a); }

}
