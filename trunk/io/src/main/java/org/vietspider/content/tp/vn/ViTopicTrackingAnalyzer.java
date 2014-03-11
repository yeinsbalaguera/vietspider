/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.vn;

import javax.management.RuntimeErrorException;

import org.vietspider.content.tp.TopicTrackingAnalyzer;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.index.analytics.SequenceSplitter;
import org.vietspider.index.word.PhraseFilter;
import org.vietspider.index.word.ProperNounExtractor;
import org.vietspider.index.word.WordSeparator;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 24, 2006
 */
public class ViTopicTrackingAnalyzer implements TopicTrackingAnalyzer {

  private SequenceSplitter sequenceSplitter;
  private ProperNounExtractor nounExtractor;
  private WordSeparator wordSeparator;

  private PhraseFilter phraseFilter;
  private KeyFilter keyFilter;

  public ViTopicTrackingAnalyzer() {
    this(null);
  }

  public ViTopicTrackingAnalyzer(PhraseFilter filter) {
    this.phraseFilter = filter;
    sequenceSplitter = new SequenceSplitter();
    nounExtractor = new ProperNounExtractor();
    wordSeparator = new WordSeparator();
    
    this.keyFilter = new DefaultKeyFilter();
    this.phraseFilter = new TpConjuntionFilter();
  }
  
  public void setKeyFilter(KeyFilter keyFilter) { this.keyFilter = keyFilter; }

  public TpWorkingData analyzeDocument(String text) {
//    TpWorkingData workingData = new TpWorkingData();
//    TpDocument tpDocument = workingData.getTpDocument();
//    List<PhraseData> sequences = sequenceSplitter.split(new PhraseData(false, text, 0));
//
//    for (int i = 0; i < sequences.size(); i++) {
//      List<PhraseData> phraseNouns = nounExtractor.extract(sequences.get(i));
//      
////      System.out.println(" ta da thay cai ni "+ sequences.size());
//      
//      StringBuilder builder = new StringBuilder();
//      for(int j = 0; j < phraseNouns.size(); j++) {
//        PhraseData pphrase = phraseNouns.get(j);
////        System.out.println(pphrase.getValue() + " / "+keyFilter.isKey(pphrase));
//        if(pphrase.isAtomic()) {
//          String lowerValue = pphrase.getValue().toLowerCase();
//          pphrase.setNoun(true);
//          if(keyFilter.isKey(pphrase)) {
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            workingData.addKey(lowerValue);
////            System.out.println("key: "+ pphrase.getValue());
//            continue;
//          } 
//
//          if(!phraseFilter.isValid(pphrase)) {
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            continue;
//          } 
//          
//          if(hasWhitespace(pphrase.getValue())) {
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            tpDocument.addPhrase(lowerValue);
////            System.out.println("word: "+ pphrase.getValue());
//            continue;
//          } 
//          
//          if(builder.length() > 0) builder.append(' ');
//          builder.append(lowerValue);
//          continue;
//        } 
//        // end atomic phrase
//        
//        List<PhraseData> normalPhrases = wordSeparator.split(pphrase, phraseFilter);
//        for(int k = 0; k < normalPhrases.size(); k++) {
//          PhraseData nphrase = normalPhrases.get(k);
//          if(keyFilter.isKey(nphrase)) {
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            
//            workingData.addKey(nphrase.getValue());
////            System.out.println("key: "+ nphrase.getValue());
//            continue;
//          } 
//
////          System.out.println("phrase : "+ nphrase.getValue()+ " ==  >"+phraseFilter.isValid(nphrase));
//          
//          if(!phraseFilter.isValid(nphrase)){
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            continue;
//          }
//
//          if(hasWhitespace(nphrase.getValue())) {
//            if(builder.length() > 0) {
////              System.out.println("word 2 : "+ builder);
//              tpDocument.addPhrase(builder.toString());
//              builder.setLength(0);
//            }
//            tpDocument.addPhrase(nphrase.getValue());
////            System.out.println("word: "+ nphrase.getValue());
//            continue;
//          }
//          if(builder.length() > 0) builder.append(' ');
//          builder.append(nphrase.getValue());
//        }
//
//      }
//      
//    }
    
//    for(int i = 0; i < keys.size(); i++) {
//      System.out.println("noun: "+ keys.get(i).getValue());
//    }
//    return workingData;
    throw new RuntimeErrorException(new Error("Not support"));
  }

  private boolean hasWhitespace(String value) {
    int  index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) return true;
      index++;
    }
    return false;
  }


}
