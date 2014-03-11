/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.token.attribute;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import org.vietspider.token.Node;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 6, 2006
 */
public class Attributes extends AbstractList<Attribute> implements List<Attribute>, Serializable {
  
  private static final long serialVersionUID = 7705648152712338120L;

  private Attribute [] attributes;

  private Node<?> node;
  
  private int size = 0;
  
  public Attributes(Node<?> n){
    super();
    this.node = n;
    this.attributes = new Attribute[5];
  }  
  
  @Override
  public Attribute get(int index) {
    rangeCheck(index);
    return  attributes[index];
  }

  @Override
  public int size() { return size; }
  
  public Attribute get(String name){
    for(int i = 0; i < size; i++) {
      if(attributes[i].getName().equalsIgnoreCase(name)) return attributes[i];
    }
    return null;
  }
  
  public void set(Attribute attr){  
    for(int i = 0; i < size; i++) {
      if(attributes[i].getName().equalsIgnoreCase(attr.getName())) {
        attributes[i].setValue(attr.getValue());
        setNodeValue();
        break;
      }
    }
  } 

  public void remove(String name){ 
//    System.out.println(" ===  > "+ name + " : "+ size);
    for(int i = 0; i < size; i++) {
//      System.out.println(attributes[i].getName() + " : "+ name);
      if(attributes[i].getName().equalsIgnoreCase(name)) {
        remove(i);
        i--;
//        break;
      }
    }
  }     
  
  public void removeAll(){ 
    clear();
    setNodeValue();
  }
  
  public int indexOf(Object elem) {
    if(elem instanceof Attribute) {
      return super.indexOf(elem);
    }
    if(elem == null) return -1;
    for(int i=0; i < size(); i++ ){
      if(get(i).equals(elem)) return i;
    }
    return -1;
  }
  
  public String toString() { return buildString().toString(); }
  
  private StringBuilder buildString() {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < size; i++){
      builder.append(attributes[i].getName())
             .append('=').append(attributes[i].getMark())
             .append(attributes[i].getValue()).append(attributes[i].getMark());
      if(i < size - 1) builder.append(' ');
    }
    return builder;
  }
  
  public Attribute remove(int index) {
    rangeCheck(index);

//    System.out.println(" ====  truoc > "+attributes.length);
    
    modCount++;
    Attribute oldValue = attributes[index];
    
    attributes[index] = null;
    
//    if(index > 0) {
//      for(int i = index+1; i < size; i++) {
//        attributes[index - 1] = attributes[index];
//      }
//    }
    
//    System.out.println("bebee " + new String(node.getValue()));
//    System.out.println(" truoc "+ size);
    
    int numMoved = size - index - 1;
    if (numMoved > 0) System.arraycopy(attributes, index+1, attributes, index, numMoved);
    attributes[--size] = null; // Let gc do its work
//    System.out.println(" sau "+ size);
//    System.out.println("remove "+ index +  " :  "  +size);
    setNodeValue();
//    System.out.println("bebee 2  " + new String(node.getValue()));
    return oldValue;
  }
  
  public void clear() {
    modCount++;
    for (int i = 0; i < size; i++) {
      attributes[i] = null;
    }
    size = 0;
    setNodeValue();
  }
  
  public int indexOf(Attribute o) {
    if (o == null) {
      for (int i = 0; i < size; i++) {
        if (attributes[i]==null) return i;
      }
    } else {
      for (int i = 0; i < size; i++) {
        if (o.equals(attributes[i])) return i;
      }
    }
    return -1;
  }

  private void rangeCheck(int index) {
    if (index >= size) throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
  }
  
  public boolean add(Attribute e) {
    addElement(e);
    setNodeValue();
    return true;
  }
  
  protected void addElement(Attribute e) {
    ensureCapacity(size + 1);  
    attributes[size++] = e;
  }
  
  private void ensureCapacity(int minCapacity) {
    modCount++;
    int oldCapacity = attributes.length;
    if (minCapacity <= oldCapacity) return ;
    int newCapacity = (oldCapacity * 3)/2 + 1;
    if (newCapacity < minCapacity) newCapacity = minCapacity;
    attributes = Arrays.copyOf(attributes, newCapacity);
  }
  
  private void setNodeValue() {
    if(!node.isTag()) return;
    StringBuilder builder = buildString();
    builder.insert(0, ' ').insert(0, node.getName());
    node.setValue(builder.toString().toCharArray());
//    System.out.println(" ====  > " + new String(node.getValue()));
  }

}
