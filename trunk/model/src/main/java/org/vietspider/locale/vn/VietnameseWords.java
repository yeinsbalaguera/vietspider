/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2009  
 */
public abstract class VietnameseWords {
  
  protected HashMap<Character, CNode> cnodes = new HashMap<Character, CNode>();
  
  private TreeSet<String> treeSet = new TreeSet<String>();
//  protected HashMap<String, Node> map = new HashMap<String, Node>();
  
  public VietnameseWords(File file) {
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String data = new String(bytes, Application.CHARSET);
      TextSpliter spliter = new TextSpliter();
      List<String> list = spliter.toList(data, ';');
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i).trim();
        if(value.length() < 1) continue;
        char c = Character.toLowerCase(value.charAt(0));
        CNode cnode =  cnodes.get(c);
        if(cnode == null) {
          cnode = new CNode(c);
          cnodes.put(c, cnode);
        }
        cnode.add(value);
        treeSet.add(list.get(i).toLowerCase());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
  }
  
  public TreeSet<String> getDict() { return treeSet; }

  public VietnameseWords(/*VNComparator _comparator,*/ String name) {
    InputStream inputStream = VietnameseFullWords.class.getResourceAsStream(name);
    try {
      ByteArrayOutputStream arrayOutputStream = RWData.getInstance().loadInputStream(inputStream);
      byte [] bytes = arrayOutputStream.toByteArray();
      String data = new String(bytes, Application.CHARSET);
//      dict  = data.split(";");
      TextSpliter spliter = new TextSpliter();
      List<String> list = spliter.toList(data, ';');
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i).trim();
        if(value.length() < 1) continue;
        char c = Character.toLowerCase(value.charAt(0));
        CNode cnode =  cnodes.get(c);
        if(cnode == null) {
          cnode = new CNode(c);
          cnodes.put(c, cnode);
        }
        cnode.add(value);
        treeSet.add(list.get(i).toLowerCase());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public boolean contains(String text) {
//    System.out.println(" text "+ text +  ":  " + treeSet.contains(text.toLowerCase()));
    return treeSet.contains(text.toLowerCase());
  }
  
  protected String trim(String word) {
    int start = 0;
    while(start < word.length()) {
      char c = word.charAt(start);
      if(Character.isLetterOrDigit(c)) break;
      start++;
    }

    int end = word.length()-1;
    while(end > 0) {
      char c = word.charAt(end);
      if(Character.isLetterOrDigit(c)) break;
      end--;
    }
    if(start == 0 && end == word.length()-1) return word;
    return word.substring(start, end+1);
  }
  
}
