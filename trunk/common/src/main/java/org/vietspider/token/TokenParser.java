/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.token;

import java.io.File;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.util.Queue;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 5, 2006
 */
public class TokenParser {  

  public static abstract class Factory<T> extends Queue<T>  {
    
    public abstract int create(char [] data, int start, int end, int type, Object...params);
    
  }
  
  public synchronized <T,E extends Factory<T>> void createBeans(
      Factory<T> factory, char [] data, Object...params) throws Exception {
    if( data == null || data.length < 2) return ;    
    int start = data[0] == '<' ? 1 : 0;//why start = 1;
    int end = 0;  
    boolean open = false;
    int attribute = -1;
    int counter  = 0;
    while(end < data.length) {
      counter++;
      if(counter > 2*1000*1000l) {
        File file  = UtilFile.getFile("track/temp/", "error." + data.hashCode() + ".html");
        RWData.getInstance().save(file, new String(data).getBytes(Application.CHARSET));
        break;
      }
      if(data[end] == '<' && !open) {
        start = factory.create(data, start, end, TypeToken.CONTENT, params)+1;
        open = true;
      } else if(data[end] == '<' && open && attribute < 1) {
        if(Character.isWhitespace(data[start])) { // test with Character.isSpaceChar()
          if(start < 1) start = 1;
          start = factory.create(data, start-1, end, TypeToken.CONTENT, params)+1;
        }else{
          start = factory.create(data, start, end, TypeToken.TAG, params)+1;
        }
        open = true;
      } else if(data[end] == '>' && open && attribute < 1) {
        start = factory.create(data, start, end, TypeToken.TAG, params)+1;
        if(end < (start-1)) end = start-1;
        open = false;
        attribute = -1;
      } else if(data[end] == '!') {
        if(isStartSpecToken(data, new char[]{'-', '-'}, end)) {
          start = end;          
          end = findEndSpecToken(data, new char[]{'-', '-', '>'}, end); 
          start = factory.create(data, start-1, end, TypeToken.COMMENT, params);
          if(start < data.length  && data[start] == '<') start++; 
          open = false;
          continue;
        }
        
        if(isStartSpecToken(data, new char[]{'D', 'O', 'C', 'T', 'Y', 'P', 'E'}, end)) {
          start = end;          
          end = findEndSpecToken(data, new char[]{'>'}, end); 
          start = factory.create(data, start-1, end, TypeToken.DOCTYPE, params);
          if(start < data.length  && data[start] == '<') start++; 
          open = false;
          continue;
        }
        
        if(isStartSpecToken(data, new char[]{'[', 'C', 'D', 'A', 'T', 'A', '['}, end)){
          start = end;          
          end  = findEndSpecToken(data, new char[]{']', ']', '>'}, end); 
          start = factory.create(data, start-1, end, TypeToken.CONTENT, params); 
          if(start < data.length  && data[start] == '<') start++; 
          open = false;
          continue;
        }
        
      } else if(open && data[end] == '=')  {
        if(attribute == -1) attribute = 0;
      } else if(open && data[end] == '"') {
        if(attribute == 0) {
          // bug fix at 10/06/08 (site ebay.com.vn)
          int index = end - 1;
          while(index > start) {
            char c = data[index];
            if(Character.isWhitespace(c) 
                || Character.isSpaceChar(c)) index--;
            break;
          }
          if(data[index] == '=') { 
            index = -1;
            for(int i = end+1; i < data.length; i++){
              if('"' == data[i]) {
                index = i;
                break;
              }
            }
//            index = CharsUtil.indexOf(data, SpecChar.DOUBLE_QUOTATION_MASK, end+1);
          }
          //end
          if(index < 0) {
            attribute = 1; 
          } else {
            end = index;
            attribute = -1;
          }
        } else if (attribute == 1) {
          attribute = -3;
        }
      } else if(open && data[end] == '\'') {
        if(attribute == 0) {
          
          // bug fix at 10/06/08 (site ebay.com.vn)
          int index = end - 1;
          while(index > start) {
            char c = data[index];
            if(Character.isWhitespace(c) 
                || Character.isSpaceChar(c)) index--;
            break;
          }
          if(data[index] == '=') { 
            index = -1;
            for(int i = end+1; i < data.length; i++){
              if('\'' == data[i]) {
                index = i;
                break;
              }
            }
//            index = CharsUtil.indexOf(data, SpecChar.SINGLE_QUOTATION_MASK, end+1);
          }
          //end
          if(index < 0) {
            attribute = 2; 
          } else {
            end  = index;
            attribute = -1;
          }
        } else if (attribute == 2) {
          attribute = -3;
        }
      }
      end++;      
    }
//    System.out.println("thay co "+ counter);
    if(start < end) factory.create(data, start, end, TypeToken.CONTENT, params);   
  }
  
  //START SPEC TOKEN 
  public boolean isStartSpecToken(char[] value, char [] sSpecs, int start) {
    if(start < 1) return false;
    if(value[start-1] != '<') return false;
    for(int i = 0; i < sSpecs.length; i++) {
      int idx = start+1+i;
      if(idx >= value.length || value[idx] != sSpecs[i]) return false;
    }
    return true;
  }
  
  //END SPEC TOKEN
  public int findEndSpecToken(char[] value, char [] eSpecs, int start){
    int index = CharsUtil.indexOf(value, eSpecs, start);
    if(index > -1) return index +  eSpecs.length;
    index = CharsUtil.indexOf(value, '\n', start);
    if(index > -1) return index+1;
    return value.length;
  }
  
  

}
