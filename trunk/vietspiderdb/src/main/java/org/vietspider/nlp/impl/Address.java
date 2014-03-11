/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.LogService;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2011  
 */
public class Address {

  private Place place;
  private TextElement element;
  private Point point;
  //  private int score = -100;
  //  private int start;
  //  private int end;
  private Address parent;
  private int time = 1;

  private String action;
  private String type;
  
  private List<Address> children = new ArrayList<Address>();

  public Address(Place place, 
      TextElement element, int start, int length){
    this.element = element;
    this.place = place;
    point = this.element.putPoint(NLPData.ADDRESS, -100, start, start+length);
  }

  public Place getPlace() { return place; }
  public void setPlace(Place place) { this.place = place; }

  public int getTime() { return time; }
  public void increaseTime() { this.time++; }
  public void setTime(int time) { this.time = time; }

  public TextElement getElement() { return element; }
  public void setElement(TextElement element) {
    this.element = element;
  }

  public List<Address> getChildren() { return children; }
  public void addChild(Address address) {
    //    if(address.getElement() == element
    //        && address.getStart() > start) {
    //      return;
    //    }
    address.parent = this;
    children.add(address);
  }

  public Address getParent() { return parent; }

  public int score() {
    if(point.getScore() > -100) return point.getScore();
    point.setScore(AddressScore.calculate(this));
    return point.getScore();
  }

  public void putScore(int _score) { this.point.setScore(_score); }

  public int getStart() { return point.getStart(); }
  public void setStart(int start) { this.setStart(start); }

  public int getEnd() { return point.getEnd(); }
  public void setEnd(int end) { this.setEnd(end); }

  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public Point getPoint() { return point; }

  public boolean contains(TextElement ele) {
    return contains(ele, 0);
  }
  
  private boolean contains(TextElement ele, int level) {
    if(level > 10) {
      LogService.getInstance().setMessage(null, "===============bug===================");
      LogService.getInstance().setMessage(null, element.getValue());
      LogService.getInstance().setMessage(null, ele.getValue());
      return false;
    }
    
    if(element == ele) return true;
    for(int i = 0; i < children.size(); i++) {
      boolean _return = children.get(i).contains(ele, level+1);
      if(_return) return false;
    }
    return false;
  }

  public String[] toAddress(boolean test, boolean filter) {
    List<StringBuilder> list = toAddress2(test, filter);
    String[] addresses = new String[list.size()];
    for(int i = 0; i < addresses.length; i++) {
      addresses[i] = list.get(i).toString();
    }
    return addresses;
  }

  private List<StringBuilder> toAddress2(boolean test, boolean filter) {
    List<StringBuilder> list = new ArrayList<StringBuilder>();
    if(children.size() < 1) {
      //      if(point.getStart() == point.getEnd()) return list;
      StringBuilder builder = new StringBuilder();
      builder.append(place.getName());
      if(test) builder.append('/').append(score()).append('/').append(time);
      list.add(builder);
      return list;
    } 

    HashSet<Integer> same = isSameLevel();
    List<Address> children1 = new ArrayList<Address>();
    List<Address> children2 = new ArrayList<Address>();
    for(int i = 0; i < children.size(); i++) {
      if(same.contains(i)) {
        children1.add(children.get(i));  
      } else {
        children2.add(children.get(i));
      }
    }

    if(children1.size() > 0) {
      //      System.out.println(element.getLower() + "  = "+ children.size());
      //      for(int i = 0; i < children.size(); i++) {
      //        System.out.println(children.get(i).getPlace());
      //      }

      Collections.sort(children1, new Comparator<Address>() {
        public int compare(Address a1, Address a2) {
          if(a1.getEnd() < a2.getStart()) return -1;
          if(a1.getEnd() > a2.getStart()) return 1;
          if(a1.getPlace() == a2.getPlace()) {
          }
          return 0;
        }
      });

      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < children1.size(); i++) {
        Address add = children1.get(i);
        if(filter && add.score() < 0) continue;
        //        if(add.point.getStart() == add.point.getEnd()) continue;
        if(builder.length() > 0) builder.append(", ");
        builder.append(add.getPlace().getName());
        if(test) builder.append('/').append(add.score()).append('/').append(add.getTime());
      }
      //      if(builder.length() > 0)  builder.append(", ");
      //      builder.append(place.getName());
      //      if(test) builder.append('/').append(score()).append('/').append(time);
      list.add(builder);
    } 

    //        System.out.println(place.getName() + " : " + children2.size() +  " : "+list.size());
    for(int i = 0; i < children2.size(); i++) {
      if(filter && children2.get(i).score() < 0) continue;
      //      System.out.println(children2.get(i).getPlace().getName() );
      list.addAll(children2.get(i).toAddress2(test, filter));
    }

    if(list.size() < 1) list.add(new StringBuilder());

    for(int i = 0;  i < list.size(); i++) {
      StringBuilder builder = list.get(i);
      //        System.out.println(builder);
      //        System.out.println(place.getName()+ " : "+ point.getStart()+ " : "+ point.getEnd());
      //        if(point.getStart() != point.getEnd()) {
      if(builder.length() > 0) builder.append(", ");
      //        System.out.println(builder);
      builder.append(place.getName());
      if(test) builder.append('/').append(score()).append('/').append(time);
      //      }
    }

    return list;
  }

  private HashSet<Integer> isSameLevel() {
    HashSet<Integer> set = new HashSet<Integer>();
    if(children.size() < 2) return set;
    if(children.get(0).getPlace().getLevel() != Place.STREET) return set;
    for(int i = 0; i < children.size(); i++) {
      for(int j = i+1; j < children.size(); j++) {
        //        System.out.println(children.get(i).getPlace());
        //        System.out.println(children.get(i).getElement().getValue());
        //        System.out.println(children.get(j).getPlace());
        //        System.out.println(children.get(j).getElement().getValue());
        if(children.get(i).getElement() != children.get(j).getElement()) continue;
        set.add(i);
        set.add(j);
      }
    }
    return set;
  }

}
