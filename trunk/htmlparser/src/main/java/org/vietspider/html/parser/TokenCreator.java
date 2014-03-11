/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.chars.CharsUtil;
import org.vietspider.html.Name;
import org.vietspider.token.TokenParser;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2008  
 */
public final class TokenCreator {
  
  private CharsToken tokens;
  
  ParserService service;
  
  public TokenCreator(ParserService service) {
    this.service = service;
  }
  
  public TokenCreator(ParserService service, CharsToken tokens) {
    this.service = service;
    this.tokens = tokens;
  }
  
  public int create(char [] data, int start, int end, int type, Map<Integer, String> replaces){ 
    if(start >= end) return end;
    if(start > data.length) return data.length;  
//    char [] value = CharsUtil.cutAndTrim(data, start, Math.min(end, data.length));
    char [] value = cutAndTrim(data, start, Math.min(end, data.length), replaces);
   
    Name name;
    if(value.length < 1) return end;
    
    if(type != TypeToken.TAG){     
      if(type == TypeToken.DOCTYPE) {
        name = Name.DOCTYPE;
      } else if(type == TypeToken.COMMENT) {
        name = Name.COMMENT;
      } else if(type == TypeToken.CODE_CONTENT) {
        name = Name.CODE_CONTENT;
      } else {
        name = Name.CONTENT;
        int s = start;
        int e =  Math.min(end, data.length);
        value = new char[e - s];
        System.arraycopy(data, s, value, 0, value.length);
      }
      tokens.push(new NodeImpl(value, name));
      return end;
    }
    
    if(value[0] == '/'){
      if(value.length <= 1) return end;     
      value  = CharsUtil.cutAndTrim(value, 1, value.length);
      name = HTML.getName(new String(value).toUpperCase());
      if(name != null){
        tokens.push(new NodeImpl(value, name, TypeToken.CLOSE));
      } else {
        char [] newValue = new char[value.length+1];
        newValue[0] = '/'; 
        System.arraycopy(value, 0, newValue, 1, value.length);
        tokens.push(new NodeImpl(newValue, Name.UNKNOWN));
        return end;
      }
      return end;
    }
    
    String nameValue = new String(CharsUtil.cutBySpace(value, 0)).toUpperCase();
    if(nameValue.charAt(nameValue.length()-1) == '/'){      
      nameValue = nameValue.substring(0, nameValue.length()-1).trim();      
    }   
    
   /* if(nameValue.equals("!DOCTYPE")){
      if(tokens.getDocument() != null) {
        NodeImpl nodeImpl = new NodeImpl(value, Name.DOCTYPE);
        nodeImpl.setIsOpen(false);
        tokens.getDocument().setDoctype(nodeImpl);
      }
      return end;
    }*/
    
    name = HTML.getName(nameValue); 
    if(name != null){
      tokens.push(new NodeImpl(value, name, TypeToken.TAG));    
    } else {
      tokens.push(new NodeImpl(value, Name.UNKNOWN));
      return end;
    }
    if(name == Name.SCRIPT){
      return findEndScript(data, new char[]{'s','c','r','i','p','t'}, end);
    } else if(name == Name.STYLE){
      return findEndScript(data, new char[] {'s','t','y','l','e'}, end);
    }
    return end;
  }  

  private int findEndScript(char [] value, char [] c, int start){    
    HashMap<Integer, String> replaces = new HashMap<Integer, String>();
    int [] idx = indexEndNode(value, c, start, replaces);   
    if(idx.length < 1) return start;   
    create(value, start+1, idx[0], TypeToken.CODE_CONTENT, replaces);    
    return create(value, idx[1], idx[2], TypeToken.TAG, replaces);
  }  
 
  private int[] indexEndNode(char [] values,
      char [] c, int start, HashMap<Integer, String> replaces){
    boolean is = false;
    int [] idx = new int[3];
    for(int i = start; i < values.length; i++) {
      if(values[i] == '>') {
        if(i >= 2 && values[i-1] == '-' && values[i-2] == '-') {
          replaces.put(i, "&gt;");
        }
      } else if(values[i] == '<') {
        is = true;
        idx[0] = i;
        int k = i+1;

        TokenParser tokenParser = service.getTokenParser();

        if(values[k] == '!'
          && tokenParser.isStartSpecToken(values, new char[]{'-', '-'}, k)) {
          replaces.put(i, "&lt;");
          continue;
//        int startComment = k;          
//        int endComment  = tokenParser.findEndSpecToken(values, new char[]{'-', '-', '>'}, k);
//        startComment = (startComment - 1) > values.length ? values.length : endComment;
//        if(startComment < values.length  && values[startComment] == '<') {
//        i = startComment - 1;  
//        continue;
//        }
//        break;
        }

        while(k < values.length){
          if(values[k] == '/')  idx[1] = k;
          if(values[k] != '/' && !Character.isWhitespace(values[k])) break;
          k++;
        }

        for(int j = 0; j < c.length; j++){
          if(k+j >= values.length - 1){
            is = false;
            break;
          }

          if(c[j] == Character.toLowerCase(values[k+j])) continue;
          is = false;
          break;
        }    

        if(!is) continue;                

        k += c.length;   
        while(k < values.length){
          if(values[k] != '/' && !Character.isWhitespace(values[k])) break;
          k++;
        }  
        if(k >= values.length) return new int[0];
        idx[2] = k;
        if(values[k] == '>') return idx;
      }
    }
    return new int[0];
  }
  
  private char [] cutAndTrim(char [] data,
      int start, int end, Map<Integer, String> replaces) {
    int s = start;
    int e = end-1;
    while(s < end){
      if(!Character.isWhitespace(data[s]) && !Character.isSpaceChar(data[s])) break;
      s++;
    }
    
    while(e > start){
      if(!Character.isWhitespace(data[e]) && !Character.isSpaceChar(data[e])) break;
      e--;
    }
    
    e++;
    if(e <= s) return new char[0];
    char [] newChar = new char[e - s];
    System.arraycopy(data, s, newChar, 0, newChar.length);
    
    if(replaces == null || replaces.size() < 1) return newChar;
    
    Iterator<Integer> rIter = replaces.keySet().iterator();
    while(rIter.hasNext()) {
      int key = rIter.next();
      if(key >= e) continue;
      if(key < s) {
        rIter.remove();
        continue;
      }
      String rValue = replaces.get(key);
      newChar = insert(newChar, key - s, rValue.toCharArray());
    }

    return newChar;
  }
  
  private char[] insert(char [] data, int start, char [] values) {
    char [] newData = new char[data.length + values.length-1];
    System.arraycopy(data, 0, newData, 0, start);
    System.arraycopy(values, 0, newData, start, values.length);
    System.arraycopy(data, start+1, newData, start+values.length, data.length-start-1);
    return newData;
  }
}
