/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.tp.vn.KeyFilter;
import org.vietspider.db.dict.WordIndex2;
import org.vietspider.index.analytics.PhraseData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 3, 2009  
 */
public class ClassifiedKeyFilter  implements KeyFilter  {

  private final static ClassifiedKeyFilter INSTANCE = new ClassifiedKeyFilter();

  public static final  ClassifiedKeyFilter getInstance() { return INSTANCE; }

  protected WordIndex2 wordIndex2;

  private ClassifiedKeyFilter() {
    loadIgnore(false);
  }

  public WordIndex2 getWordIndex() { return wordIndex2; }
  
  public boolean isKey(PhraseData phrase) {
    String pattern  = phrase.getValue();
    if(wordIndex2.contains(pattern.toLowerCase())) return true;

   /* int index = 0;
    boolean hasLetter = false;
    boolean hasDigit = false;
    int length = pattern.length();
    StringBuilder number = new StringBuilder();
    while(index < length) {
      char c = pattern.charAt(index);
      if(Character.isLetter(c)) hasLetter = true;
      else if(Character.isDigit(c)) {
        number.append(c);
        hasDigit = true;
      }
      if(hasDigit && hasLetter) return true;
      index++;
    }

    //check is phone number
    if(hasDigit && !hasLetter 
        && number.length() > 7 
        && number.length() < 12 ) {
      phrase.setValue(number.toString());
      return true;
    }*/

    return false;
  }

  private void loadIgnore(boolean recursive) {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    File file = new File(folder, "classified.key.tp");
    File txtFile = new File(folder, "classified.key.word.txt");
    //    System.out.println(file.lastModified() + "  : "+txtFile.lastModified());
    if(!file.exists() || file.length() < 1 
        || (txtFile.exists() && txtFile.lastModified() > file.lastModified())) {

      wordIndex2 = new WordIndex2(0);
      try {
        String text = new String(RWData.getInstance().load(txtFile), Application.CHARSET);
        List<String> values = split(text);
        for(int i = 0; i < values.size(); i++) {
          String value = values.get(i);
          if(wordIndex2.contains(value)) continue;
          wordIndex2.add(value);
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      saveIndex(file);
      return;
    }

    try {
      byte [] bytes = RWData.getInstance().load(file);
      ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
      wordIndex2 = (WordIndex2) objectInput.readObject();
      objectInput.close();
      return;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      if(recursive) return;
      if(file.delete()) loadIgnore(true);
    }
  }

  protected void saveIndex(File file) {
    try {
      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
      objectOutputStream.writeObject(wordIndex2);
      objectOutputStream.close();
      if(file.exists()) file.delete();
      RWData.getInstance().save(file, bytesOutput.toByteArray());
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private List<String> split(String text) {
    List<String> sequences = new ArrayList<String>();
    int i = 0;
    int start = 0;
    int length = text.length();
    while(i < length){
      char c = text.charAt(i);
      switch (c) {
      case ';':
      case ',':
        String seq = text.substring(start, i);
        start = i+1;
        seq = CharsUtil.normalize(seq);
        if(!seq.isEmpty()) sequences.add(seq);
        break;
      default:
        break;
      }
      i++;
    }

    i = Math.min(length, i);
    String seq = text.substring(start, i);
    seq = CharsUtil.normalize(seq);
    if(!seq.isEmpty()) sequences.add(seq);
    return sequences;
  }
}