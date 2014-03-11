/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.word;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 18, 2006
 */
public class WordList {
 
  private Word [] words;
  private int size;
  
  public WordList(int initialCapacity){
    words = new Word[initialCapacity];
    size = 0;
  }
  
  public void put(String value, int time){
    for(int i=0; i < size; i++){
      if(CharUtil.equalsIgnoreCase(words[i].getValue(), value)){
        words[i].setTime(time);
        break;
      }
    }
  }
  
  public boolean contains(String value){
    for(int i=0; i< size; i++){
      if(CharUtil.equalsIgnoreCase(words[i].getValue(), value)) return true;
    }
    return false;
  }
  
  public void set(String value, int count){
    for(int i=0; i< size; i++){
      if(CharUtil.equalsIgnoreCase(words[i].getValue(), value)){
        words[i].setTime(words[i].getTime()+1);
        return;
      }
    }    
    if(size >= words.length) createNewArray(words.length + 20);
    words[size] = new Word(value, 1, count);
    size++;
  }  
  
  private void createNewArray(int newLength){
    Word [] newWords = new Word[newLength]; 
    System.arraycopy(words, 0, newWords, 0, Math.min(words.length, size));    
    words = newWords;  
  }
  
  public int size(){ return size; } 
  
  private void trim(){ createNewArray(size); }
  
  public Word [] getWord(){    
    if(words.length != size) trim();
    return words;
  } 
  
  public void setWord(Word [] w, int s){    
    words = w;
    size = s;
  }  
  
}
