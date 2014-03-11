/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp.vn.comparator;

import java.util.List;

import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.TpWorkingData.TpWord;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class TpDocumentMatcher {
  
  private boolean test = false;
  private boolean printKey = false;
  private boolean printWord = false;
  
  private StringBuilder info = new StringBuilder();

  public StringBuilder getInfo() { return info; }
  
  public int compare(TpWorkingData doc1, TpWorkingData doc2){
    if(doc1 == null || doc2 == null) return 0;
    int value1  = compare1(doc1, doc2);
    if(!test && value1 < 10) return 0;
    int value2  = compare1(doc2, doc1);
//    System.out.println(value2);
    if(!test && value2 < 10) return 0;
    
    if(test)  {
      System.out.println("value 1 "+ value1 + ", value 2 " + value2);
      if(value1 < 10 || value2 < 10) return 0;
    }
    
    return (value1+value2)/2;
  }
  
  public int compare1(TpWorkingData doc1, TpWorkingData doc2) {
    int keyTimeTotal = doc1.getTotalKeyWord();
    
    int[] values = compare(doc1.getKeyWords(), doc2.getKeyWords(), printKey);
    int dupKeyNumber = values[0];
    int dupKeyTime = values[1];
    
    int keyTimeRatio = 0;
    if(keyTimeTotal > 0) {
      keyTimeRatio = (dupKeyTime*100)/keyTimeTotal;
      //ignore
      if(!test && (keyTimeRatio < 1 || (keyTimeTotal > 10 && keyTimeRatio < 10))) return 0;
    }
    
    int keyNumberRatio = 0;
    int keyTotal = doc1.getKeys().size();
    if(keyTotal > 0) {
      keyNumberRatio = (dupKeyNumber*100)/keyTotal;
      //ignore
//      System.out.println(keyNumberRatio +  ": "+ keyTotal);
//      if(!test && (keyNumberRatio < 1 || (keyTotal > 10 && keyNumberRatio < 10))) return 0;
    }
    
    int keyRatio = (keyNumberRatio*2 + keyTimeRatio)/3;
    
    
    if(!test && !isValidKey(keyRatio, keyTimeTotal)) return 0;
    
    values = compare(doc1.getWords(), doc2.getWords(), printWord);
    int dupWordNumber = values[0];
    int dupWordTime = values[1];
    
    int wordTimeRatio = 0;
    int wordTimeTotal = doc1.getTotalWord();
    if(wordTimeTotal > 0) {
      wordTimeRatio = (dupWordTime*100)/wordTimeTotal;
      if(!test && !isValidWord(wordTimeRatio, wordTimeTotal)) return 0;
    }
    
    int wordNumberRatio = 0;
    int wordTotal = doc1.getWords().size();
    if(wordTotal > 0) {
      wordNumberRatio = (dupWordNumber*100)/wordTotal;
//      System.out.println(" vao day bebe "+ wordNumberRatio);
//      if(!test && (wordNumberRatio < 1 || (wordTotal > 10 && wordNumberRatio < 10))) return 0;
    }
    
    int wordRatio = (wordNumberRatio*2 + wordTimeRatio)/3;
    
    
    if(test) {
      System.out.println(doc1.getId() + " key number=" + dupKeyNumber + "/" + keyTotal
          + " time=" + dupKeyTime + "/"+ keyTimeTotal + " ratio="+ keyRatio + "%");
      System.out.println(doc1.getId() + " word number=" + dupWordNumber + "/"+ wordTotal 
          + " time=" + dupWordTime + "/"+ wordTimeTotal + " ratio="+  wordTimeRatio+"%");
      
      if(!isValidKey(keyRatio, keyTimeTotal)) return 0;
      if(!isValidWord(wordTimeRatio, wordTimeTotal)) return 0;
      
    }
    
//      System.out.println(" ========= "+ dupKeyNumber + " :  "+ wordRatio);
//    if(dupKeyNumber == 1 && wordRatio < 25) return 0;
    
    if(keyTotal >= 10) {
      return (keyRatio*2 + wordRatio)/3;
    } 
//    else if (totalKey >= 1 && totalKey < 10) {
//      return (keyRatio + wordRatio*2)/3;
//    }
    return wordRatio;
  }
  
  public int[] compare(List<TpWord> wordIndex1, List<TpWord> wordIndex2, boolean print){
    int time = 0;
    int number = 0;
    for(int i = 0; i < wordIndex1.size(); i++) {
      TpWord word = wordIndex1.get(i);
      for(int j = 0; j < wordIndex2.size(); j++) {
        if(word.getCode() != wordIndex2.get(j).getCode()) continue;
        if(print)  {
          System.out.println(" ===  > " + word.getValue() + " : " + wordIndex2.get(j).getValue());
        }
        time += word.getTime();
        number++;
        break;
      }
    }
    return new int[]{number, time}; 
//    return compareNumber(nodes, wordIndex1, wordIndex2);
  }
  
  private boolean isValidKey(int keyRatio, int keyTimeTotal) {
    if(keyRatio < 5 
        || (keyTimeTotal > 10 && keyRatio < 10)
      ) return false;
    return true;
  }
  
  private boolean isValidWord(int wordTimeRatio, int wordTimeTotal) {
    if(wordTimeRatio < 5 
        || (wordTimeTotal > 15 && wordTimeRatio < 15)
       ) return false;
    return true;
  }
  
  public void setTest(boolean value) { test = value; }
  public boolean isTest() { return test; }

  public boolean isPrintKey() { return printKey; }
  public void setPrintKey(boolean printKey) { this.printKey = printKey; }

  public boolean isPrintWord() { return printWord; }
  public void setPrintWord(boolean printWord) { this.printWord = printWord; }
  
  
}
