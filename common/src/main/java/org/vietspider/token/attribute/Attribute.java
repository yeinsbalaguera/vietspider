/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:50 PM
 */

package org.vietspider.token.attribute;

import java.io.Serializable;

import org.vietspider.token.INameValue;

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class Attribute implements INameValue<String, String>, Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String name;
  private String value;
  
  private char mark = '\"';
  
  Attribute(String n) {
    this.name = n;
  }
  
  public Attribute(String n, String v){
    name = n;
    value = v;
  }
  
  public Attribute(String n, String v, char m){
    name = n;
    value = v;
    mark = m;
  }
  
  public String getName(){ return name; }    
 
  public void setName( String n){ name = n; }  
  
  public String getValue(){ return value; }  
  
  public void setValue(String v){ value = v; }
  
  public char getMark() { return mark; }
  
  public void setMark(char mark) { this.mark = mark; }  
  
  public boolean equals(Object obj){
    if(obj == this) return true;
    if(obj instanceof Attribute){
      return ((Attribute)obj).getName().equalsIgnoreCase(name);
    }
    if(obj instanceof String){
      return name.equalsIgnoreCase((String)obj);
    }
    return super.equals(obj);
  }

  public String name() { return name; }

}
