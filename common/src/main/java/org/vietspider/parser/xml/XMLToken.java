/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.parser.xml;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.XMLDataDecoder;
import org.vietspider.token.TokenParser.Factory;
import org.vietspider.token.TypeToken;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 17, 2006
 */
public class XMLToken extends Factory<XMLNode> {
  
  private XMLNode xmlType;
  private XMLDataDecoder decoder;
  
  public XMLToken (){
    
  }
  
  public XMLToken (XMLDataDecoder decoder){
    this.decoder = decoder;
  }
  
  @SuppressWarnings("unused")
  public int create(char [] data, int start, int end, int type, Object...params){         
    if(start >= end) return end;
    if(start > data.length) return data.length;
    
    char[] value = CharsUtil.cutAndTrim(data, start, Math.min(end, data.length)); 
    if(value.length < 1) return end;
    
    if(type != TypeToken.TAG){     
      if(decoder != null) value = decoder.decode(value);      
      push(new XMLNode(value, null, type));
      return end;
    }            
    
    if(value[0] == '/'){
      if(value.length <= 1) return end;
      value  = CharsUtil.cutAndTrim(value, 1, value.length);
      push(new XMLNode(value, new String(value), TypeToken.CLOSE));
      return end;
    }
    
    String nameValue = new String(CharsUtil.cutBySpace(value, 0));
    if(value[value.length-1] == '?' &&
        value[0] == '?' ) {
      xmlType = new XMLNode(value, nameValue, TypeToken.SINGLE);
    } else {
      push(new XMLNode(value, nameValue, isSingleTag(value)));
    }
    return end;
  }
  
  int isSingleTag(char[] value){
    if(value[value.length - 1] == '/') return TypeToken.SINGLE;
    else if(value[0] == '!') return TypeToken.SINGLE;
    return TypeToken.TAG;
  }

  public XMLNode getXmlType() { return xmlType; }
  
  @SuppressWarnings("unused")
  public boolean isValid(char[] data, int start, int end, int checkType) {
    return true;
  }

}
