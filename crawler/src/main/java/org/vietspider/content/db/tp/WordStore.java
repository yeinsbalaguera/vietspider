/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.locale.vn.CNode;
import org.vietspider.locale.vn.Word;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 19, 2011  
 */
public class WordStore {
  
  protected HashMap<Character, CNode> ignoreWords = new HashMap<Character, CNode>();
  protected HashMap<Character, CNode> ignoreNouns = new HashMap<Character, CNode>();
  protected HashMap<Character, CNode> configNouns = new HashMap<Character, CNode>();
  protected HashMap<String, String> abbreviates = new HashMap<String, String>();
  
  public WordStore() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    loadIgnoreWords(ignoreWords, new File(folder, "default.ignore.word.txt"));
    loadIgnoreWords(ignoreNouns, new File(folder, "default.ignore.noun.txt"));
    loadIgnoreWords(configNouns, new File(folder, "config.noun.txt"));
    loadAbbreviate();
  }
  
  public boolean isConfig(Word word) {
    return exists(configNouns, word);
  }
  
  public boolean isIgnoreNoun(Word word) {
    return exists(ignoreNouns, word);
  }
  
  public boolean isIgnoreWord(Word word) {
    return exists(ignoreWords, word);
  }
  
  public String getAbbreviateValue(String key) {
    return abbreviates.get(key.toLowerCase());
  }
  
  private void loadIgnoreWords(HashMap<Character, CNode> ignores, File file) {
    try {
      if(!file.exists() || file.length() < 1) return;
      byte [] bytes = RWData.getInstance().load(file);
      String data = new String(bytes, Application.CHARSET);
      TextSpliter spliter = new TextSpliter();
      List<String> list = spliter.toList(data, ';');
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i).trim();
        if(value.length() < 1) continue;
//        System.out.println(value);
        char c = Character.toLowerCase(value.charAt(0));
        CNode cnode =  ignores.get(c);
        if(cnode == null) {
          cnode = new CNode(c);
//          System.out.println(value + " : " +  value);
          ignores.put(c, cnode);
        }
        cnode.add(value);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private boolean exists(HashMap<Character, CNode> ignores, Word word) {
    String value = word.getValue();
    char c = value.charAt(0);
    CNode cnode = ignores.get(Character.toLowerCase(c));
//      System.out.println(" ===  >"+ c + " : "+ cnode + " : " + word.getValue());
    if(cnode == null) return false;
    
//    if(value.trim().length() == 1) {
//      System.out.println(c+ " :  "+ value);
//    }
//    if(value.length() == 1 && c == '-') return true;
    
    if(word.getNoun() > 0 && value.length() < 3) return true;

    boolean _return = cnode.exist(word);
//    if(word.getValue().equals("TBKTSG")) {
//      System.out.println(word.getValue() + " : "+ _return + " : " + word.getElements().length);
//    }
//    if(index > 0) {
//    }
    return _return;
  }
  
  private void loadAbbreviate() {
    try {
      File file = new File(UtilFile.getFolder("system/dictionary/vn/"), "abbreviate.list.txt");
      if(!file.exists() || file.length() < 1) return;
      byte [] bytes = RWData.getInstance().load(file);
      String data = new String(bytes, Application.CHARSET);
      TextSpliter spliter = new TextSpliter();
      List<String> list = spliter.toList(data, '\n');
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i).trim();
        if(value.length() < 1) continue;
        int idx = value.indexOf('=');
        if(idx < 1) continue;
//        System.out.println(value.substring(0, idx) + " : " + value.substring(idx+1));
        abbreviates.put(value.substring(0, idx).trim().toLowerCase(), value.substring(idx+1).trim());
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
}
