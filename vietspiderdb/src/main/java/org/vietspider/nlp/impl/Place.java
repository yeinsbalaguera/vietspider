/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2011  
 */
public class Place {
  
//  public final static short NATIONAL = 1;
  public final static short CITY = 1;
  public final static short DISTRICT = 2;
  public final static short STREET = 3;

  private String name;
  private short level;
  
  private List<Place> variants = new ArrayList<Place>();
  private List<Place> children = new ArrayList<Place>();
  private List<Place> ignores = new ArrayList<Place>();
  private List<Place> nosigns = new ArrayList<Place>();
  private Place reference;
  
  public Place(String name) {
    this.name = name;
    addNoSign(name);
    level = CITY;
  }
  
  private Place(String name, short level) {
    this.name = name;
    this.level = level;
  }

  public String getName() { return name; }
  public short getLevel() { return level; }

  public List<Place> getVariants() { return variants; }

  public List<Place> getChildren() { return children; }
  public void setChildren(List<Place> children) { this.children = children; }

  public Place addChild(String _name, short _level) {
    _name = _name.trim().toLowerCase();
    for(int i = 0 ; i < children.size(); i++) {
      if(children.get(i).getName().equals(_name)) {
        return children.get(i);
      }
    }

    Place place = new Place(_name, _level);
    place.addNoSign(_name);
    children.add(place);
//    createVariant(place, _name);
//    createVariant2(place, _name);
    return place;
  }

  public void addVariant(String [] elements, int from) {
    for(int i = from; i < elements.length; i++) {
      String value = elements[i].trim().toLowerCase();
      addNoSign(value);
      Place place = new Place(value, this.level);
      place.reference = this;
      variants.add(place);
    }
  }
  
  public void addVariant(String value) {
    value = value.trim().toLowerCase();
    addNoSign(value);
    Place place = new Place(value, this.level);
    place.reference = this;
    variants.add(place);
  }
  
  private void addNoSign(String value) {
    if(value.indexOf(' ') < 0) return;
    value = VietnameseConverter.toTextNotMarked(value);
    Place place = new Place(value, this.level);
    place.reference = this;
    nosigns.add(place);
  }
  
  public int length() { return name.length(); }
  
  public void toList(List<Place> list) {
    list.add(this);
    
    for(int i = 0; i < variants.size(); i++) {
      variants.get(i).toList(list);
    }
    
    for(int i = 0; i < children.size(); i++) {
      children.get(i).toList(list);
    }
  }
  
  public void addIgnore(Place place) {
    if(place.reference == this
        || this.reference == place) return;
//    System.out.println(" name : "+ name);
//    System.out.println("ignore: "+ place.getName());
    ignores.add(place);
  }
  
  public boolean contains(String value) {
    for(int i = 0; i < children.size(); i++) {
      if(value.equalsIgnoreCase(children.get(i).getName())) return true;
    }
    return false;
  }
  
  public void addIgnore(String [] elements, int from) {
    for(int i = from; i < elements.length; i++) {
      String value = elements[i].trim().toLowerCase();
      Place place = new Place(value, this.level);
      ignores.add(place);
    }
  }
  
  public Place traverse(Conditional conditional) {
    if(conditional.valid(this)) return this;
    for(int i = 0; i < variants.size(); i++) {
      if(conditional.valid(variants.get(i))) return variants.get(i);
    }
    
    for(int i = 0; i < children.size(); i++) {
      Place _place = children.get(i).traverse(conditional);
      if(_place != null) return _place;
    }
    return null;
  }
  
  public static interface Conditional {
    public boolean valid(Place place);
  }

  public int[] indexOf(String text) {
    int index = text.indexOf(name);
    
//    System.out.println("cao đạt".equals("cao đạt"));
    
    if(isValid(text, index, name.length())) {
      return new int[]{index, name.length()};
    }
    
//    System.out.println(text);
    for(int i = 0; i < variants.size(); i++) {
      int [] _returns = variants.get(i).indexOf(text);
//      if(variants.get(i).getName().indexOf("thanh xuân") > -1) {
//        System.out.println(text);
//        System.out.println(variants.get(i).getName()+ "  : " + index);
//      }
//      System.out.println(text.substring(index+1));
//      System.out.println(text.charAt(index+1));
//      System.out.println(max + "  : "+ text.length());
//      if(max < text.length()) System.out.println(text.charAt(max));
      if(!isValid(text, _returns[0], _returns[1])) continue;
      
//      if(ignores(text, _returns[0])) continue;
      
      return _returns;
    }
    
    for(int i = 0; i < nosigns.size(); i++) {
      int [] _returns = nosigns.get(i).indexOf(text);
      if(!isValid(text, _returns[0], _returns[1])) continue;
//      if(ignores(text, _returns[0])) continue;
      return _returns;
    }
    
    return new int[]{-1, -1};
  }
  
  private boolean isValid(String text, int index, int length) {
//    if(/*text.indexOf("đinh bộ lĩnh") > -1
//        &&*/ name.indexOf("đinh bộ lĩnh") > -1 ) {
//      System.out.println(name);
//      System.out.println(text);
//      System.out.println("=== >"+index);
//    }
    if(index < 0) return false;
    
    String previous = previous(text, index);
//    System.out.println(name);
//    System.out.println(text);
//    System.out.println(" ==  >"+ previous);
    int idx = previous.indexOf("cách");
    if(idx < 0) idx = previous.indexOf("cạnh");
//    System.out.println(" hihi "+ idx + " : "+ index);
    if(idx > -1 && idx < index) return false;
    
//    System.out.println("  ===  > "+ previous(text, index));
    
    if(index > 0 
        && Character.isLetterOrDigit(text.charAt(index-1))) return false;
    
    int max = index + length;
    if(max < text.length() 
        && Character.isLetterOrDigit(text.charAt(max))) return false;
    
    for(int i = 0; i < ignores.size(); i++) {
//      System.out.println(name);
//      System.out.println(text);
//      System.out.println(ignores.get(i).getName() + " : " + ignores.get(i).indexOf(text)[0]);
      if(ignores.get(i).indexOf(text)[0] > -1) {
//        System.out.println("name " + name);
//        System.out.println("ignore "+ignores.get(i).getName() + " : "+ ignores.get(i).indexOf(text));
//        System.out.println(text);
        return false;
      }
    }
    
    return true;
  }
  
  private String previous(String text, int index) {
    //  System.out.println(" vao day ");
    int end = index;
    while(index > -1) {
      char c = text.charAt(index);
      if(c == '.' 
        || c == ',' 
          || c == '!'
            || c == ':'
              || c == ';') break;
      index--;
      //    System.out.println(index);
    }
    if(index < 0) return text;
    return text.substring(index, end);
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(name).append('/').append(level);
    return builder.toString();
  }
}
