/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
class AbbreviateDictionary2 {
  
  private static volatile AbbreviateDictionary2 instance;
  
  private static synchronized  AbbreviateDictionary2 getInstance() {
    if(instance != null) return instance;
    try {
      instance = new AbbreviateDictionary2();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return instance;
  }

  protected Map<String, String> map;

  private AbbreviateDictionary2() throws Exception {
    map = new HashMap<String, String>();
    File file = UtilFile.getFolder("system/dictionary/vn/abbr/classified/data.txt");
    String text = new String(RWData.getInstance().load(file), Application.CHARSET);
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(text, '\n');
    for(int i = 0; i < elements.length; i++) {
      String [] couple = spliter.toArray(elements[i], '=');
      if(couple.length < 2) continue;
      map.put(couple[0].toLowerCase().trim(), couple[1].trim());
    }
  }
  
  public int size() { return map.size(); }

  public final String search(String word) {
    return null;
  }

  public final void remove(String word) {
    return;
  }
  
  public String compile(String text) {
    StringBuilder builder = new StringBuilder(text);
    StringBuilder lower = new StringBuilder(text.toLowerCase());
    Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String,String> entry = iterator.next();
      replace(builder, lower, entry.getKey(), entry.getValue());
    }
    
    return builder.toString();
  }
  
  private void replace(StringBuilder builder, StringBuilder lower, String key, String value) {
    int index = lower.indexOf(key);
//    System.out.println(" kiem key "+ key + " : "+ index);
    while(index > -1) {
      if(index > 0 && Character.isLetterOrDigit(lower.charAt(index-1))) {
        index = lower.indexOf(key, index + value.length());
        continue;
      }
      
      if((index + key.length()) < lower.length() - 1 
          && Character.isLetterOrDigit(lower.charAt(index+key.length()))) {
        index = lower.indexOf(key, index + value.length());
        continue;
      }
      builder.replace(index, index + key.length(),  value);
      lower.replace(index, index + key.length(),  value.toLowerCase());
      index = lower.indexOf(key, index + value.length());
    }
  }
  
/*  int indexOf(String str, int fromIndex) {
    return String.indexOf(value, 0, count,
                          str.toCharArray(), 0, str.length(), fromIndex);
  }
  
  static int indexOf(char[] source, int sourceOffset, int sourceCount,
      char[] target, int targetOffset, int targetCount, int fromIndex) {
    if (fromIndex >= sourceCount) {
      return (targetCount == 0 ? sourceCount : -1);
    }
    if (fromIndex < 0)  fromIndex = 0;

    if (targetCount == 0) return fromIndex;

    char first  = target[targetOffset];
    int max = sourceOffset + (sourceCount - targetCount);

    for (int i = sourceOffset + fromIndex; i <= max; i++) {
       Look for first character. 
      if (source[i] != first) {
        while (++i <= max && source[i] != first);
      }

       Found first character, now look at the rest of v2 
      if (i <= max) {
        int j = i + 1;
        int end = j + targetCount - 1;
        for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++);

        if (j == end) {
           Found whole string. 
          return i - sourceOffset;
        }
      }
    }
    return -1;
  }*/
  
 /* public String compile(String text) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    int start = index;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(!Character.isLetterOrDigit(c)) {
        String word = text.substring(start, index);
        String value = map.get(word.toLowerCase());
//        System.out.println("thay co "+ "|"+word+"|"+ value);
        if(value != null) {
          builder.append(value);
        } else {
          builder.append(word);
        }
        
        builder.append(c);
        start = index+1;
      } 
      index++;
    }
    if(start < length)  {
      String word = text.substring(start, length);
      String value = map.get(word.toLowerCase());
      builder.append(word);
//    System.out.println("thay co "+ "|"+word+"|"+ value);
      if(value != null) {
        builder.append('(').append(value).append(')');
      }
    }
    return builder.toString();
  }*/

  public final void save(String word, String value) {
    
  }

}

