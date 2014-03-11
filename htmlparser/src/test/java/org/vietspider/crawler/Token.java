/***************************************************************************
 * Copyright 2001-2007 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.crawler;

import org.vietspider.token.Node;
import org.vietspider.token.TypeToken;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 30, 2007  
 */
public class Token implements Node<String> {
  
  protected char [] value ;
  protected String name;  

  private transient int type = TypeToken.CONTENT ;

  public Token(char [] value, String name, int type){  
    this.value = value;
    this.name = name;
    this.type = type;
  }  
  
  public String name() { return ""; }
  
  public char[] getValue() { return value; }

  public void setValue(char[] value) { this.value = value; }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }
  
  public int getType() { return type; }
  
  
  public boolean isTag() {
    return type != TypeToken.CONTENT && type != TypeToken.COMMENT; 
  }

}
