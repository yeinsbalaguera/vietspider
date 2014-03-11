/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.crawler;

import org.vietspider.chars.CharsUtil;
import org.vietspider.token.TypeToken;
import org.vietspider.token.TokenParser.Factory;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 17, 2006
 */
@SuppressWarnings("serial")
public class ListToken extends Factory<Token> {
  
  public ListToken (){
  }
  
  private org.vietspider.common.util.Node<Token> current;
  
  public void toFirst() { current = first; }
  
  public Token next() {
    return null;
//    Token result = current.value;
//    current = current.next;
//    if(current == null) current = null;
//    return result;
  }
  
  public boolean hasNextElement(){
    return current != null;
  }
  
  @SuppressWarnings("unused")
  public int create(char [] data, int start, int end, int type, Object...params){         
    if(start >= end) return end;
    if(start > data.length) return data.length;    
    char[] value = CharsUtil.cutAndTrim(data, start, Math.min(end, data.length)); 
    if(value.length < 1) return end;    
    if(type != TypeToken.TAG){      
      push(new Token(value, null, type));
      return end;
    }            
    if(value[0] == '/'){
      if(value.length <= 1) return end;
      value  = CharsUtil.cutAndTrim(value, 1, value.length);
      push(new Token(value, new String(value), TypeToken.CLOSE));
      return end;
    }
    String nameValue = new String(CharsUtil.cutBySpace(value, 0));
    push(new Token(value, nameValue, isSingleTag(value)));
    return end;
  }
  
  int isSingleTag(char[] value){
    if(value[value.length - 1] == '/') return TypeToken.SINGLE;
    else if(value[0] == '!') return TypeToken.SINGLE;
    return TypeToken.TAG;
  }
  

}
