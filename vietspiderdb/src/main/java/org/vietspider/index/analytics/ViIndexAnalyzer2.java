/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.index.analytics;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.index.word.ProperNounExtractor;
import org.vietspider.index.word.WordSeparator;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 24, 2006
 */
public class ViIndexAnalyzer2 {

  private SequenceSplitter sequenceSplitter;
  private ProperNounExtractor nounExtractor;
  private WordSeparator wordSeparator;

  //  private PhraseFilter filter;

  public ViIndexAnalyzer2() {
    sequenceSplitter = new SequenceSplitter();
    nounExtractor = new ProperNounExtractor();
    wordSeparator = new WordSeparator();
  }

  public ViIndexAnalyzer2(SequenceSplitter splitter) {
    this.sequenceSplitter = splitter;
    nounExtractor = new ProperNounExtractor();
    wordSeparator = new WordSeparator();
  }
  
  public SequenceSplitter getSequenceSplitter() { return sequenceSplitter; }

  public void setSequenceSplitter(SequenceSplitter sequenceSplitter) {
    this.sequenceSplitter = sequenceSplitter;
  }

  public List<PhraseData> analyze(String text) {
    return analyzePhrases(sequenceSplitter.split(new PhraseData(false, text, 0)));
  }

  public List<PhraseData> analyzeSequences(String text){
    List<PhraseData> sequences = sequenceSplitter.split(new PhraseData(false,text, 0));
    return sequences;
  }

  public List<PhraseData> analyzePhrases(List<PhraseData> sequences){
    List<PhraseData> phrases = nounExtractor.extract(sequences);

    List<PhraseData> values = new ArrayList<PhraseData>();

    for(int i = 0; i < phrases.size(); i++) {
      PhraseData phrase = phrases.get(i);
      if(phrase.isAtomic()) {
        values.add(phrase);
        //        if(filter == null || filter.isValid(phrase)) values.add(phrase);
        //        PhraseData newPhrase = phrase.clone();
        //        newPhrase.setAtomic(false);
        //        newPhrase.setValue(newPhrase.getValue().toLowerCase());
        //        values.addAll(wordSeparator.split(newPhrase, filter));
      } else {
        values.addAll(wordSeparator.split(phrase, null));
      }
    }

    return values;
  }

  public String extract(String text){
    List<PhraseData> values = analyze(text);

    StringBuilder builder = new StringBuilder();

    int counter = 0;
    for(PhraseData ele : values) {
      if(!hasCharacter(ele.getValue())) continue;
      builder.append(' ').append(ele.getValue()).append(';').append(' ');
      counter++;
      if(counter > 15) {
        counter = 0;
        builder.append('\n').append('\n');
      }
    }
    return builder.toString();
  }

  public String buildQuery(List<PhraseData> phrases){
    StringBuilder content = new StringBuilder();
    for(PhraseData ele : phrases) {
      String value = ele.getValue();
      if(!hasCharacter(value)) continue;

      int index = 0;
      int length = value.length();
      if(content.length() > 0) content.append(' ');
      while(index < length) {
        content.append(Character.toLowerCase(value.charAt(index)));
        index++;
      }
    }
    return content.toString();
  }

  private boolean hasCharacter(String value) {
    int  index = 0;
    while(index < value.length()) {
      if(Character.isLetterOrDigit(value.charAt(index)) ) return true;
      index++;
    }
    return false;
  }


}
