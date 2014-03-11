/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
@NodeMap("primitive-data")
public class PrimitiveData implements Serializable {

  @NodeMap("age")
  private int age;
  
  @NodeMap("time")
  private Long time;
  
  @NodesMap(value = "prices", item = "price")
  private double[] prices;
  
  @NodeMap("create")
  private Date create;
  
  @NodeMap("character")
  private char c;
  
  @NodeMap("trust")
  private boolean trust = false;
  
  @NodesMap(value = "pbeans", item = "bean")
  private List<CDataAttrBean> beans = new ArrayList<CDataAttrBean>();

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public double[] getPrices() {
    return prices;
  }

  public void setPrices(double[] prices) {
    this.prices = prices;
  }

  public Date getCreate() {
    return create;
  }

  public void setCreate(Date create) {
    this.create = create;
  }

  @GetterMap("character")
  public char getCharacter() {
    return c;
  }

  @SetterMap("character")
  public void setCharacter(char c) {
    this.c = c;
  }

  public boolean isTrust() {
    return trust;
  }

  public void setTrust(boolean trust) {
    this.trust = trust;
  }

  public List<CDataAttrBean> getBeans() {
    return beans;
  }

  public void setBeans(List<CDataAttrBean> beans) {
    this.beans = beans;
  }
  
  
}
