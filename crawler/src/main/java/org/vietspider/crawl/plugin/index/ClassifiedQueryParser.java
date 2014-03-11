/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import org.vietspider.index.QueryParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 9, 2009  
 */
public class ClassifiedQueryParser extends QueryParser {

//  @Override
//  protected BooleanQuery createBooleanQuery(String pattern) {
//    BooleanQuery booleanQuery = new BooleanQuery();
//    Query titleQuery = null;
//    Query descQuery = null;
//    Query contentQuery = null;
//    
//    boolean isPhrase =  VietnameseDictionary.getInstance().contains(pattern);
//    int counter = countWords(pattern);
//    
//    WordIndex wordIndex = ClassifiedKeyFilter.getInstance().getWordIndex();
//    float titleScore = 0.0f;
//    float descScore = 0.0f;
//    float contentScore = 0.0f;
//    if(wordIndex.contains(pattern)) {
////      System.out.println(" da cong them cho "+ pattern);
//      titleScore = 1.0f;
//      descScore = 1.0f;
//      contentScore = 1.0f;
//    }
//    
//    if(isPhrase && counter >= 2) {
//      if(counter >= 5 ) {
//        titleScore += 2.0f;
//        descScore += 1.8f;
//        contentScore += 1.5f;
//      } else if(counter >= 3 && counter < 5) {
//        titleScore += 1.8f;
//        descScore += 1.5f;
//        contentScore += 1.0f;
//      } else {
//        titleScore += 1.0f;
//        descScore += 0.8f;
//        contentScore += 0.7f;
//      }
//
//      titleQuery = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, pattern, titleScore);
//      descQuery  = createPhraseQuery(ContentIndex.FIELD_DESC_WORD, pattern, descScore);
//      contentQuery = createPhraseQuery(ContentIndex.FIELD_CONTENT_WORD, pattern, contentScore);  
//    } else {
//      if(counter > 1){
//        titleScore += 1.0f;
//        descScore += 0.8f;
//        contentScore += 0.7f;
//      } else {
//        titleScore += 0.8f;
//        descScore += 0.6f;
//        contentScore += 0.3f;
//      
//      }
//      
//      titleQuery = new TermQuery(new Term(ContentIndex.FIELD_TITLE_WORD, pattern));
//      titleQuery.setBoost(titleScore);
//      
//      descQuery = new TermQuery(new Term(ContentIndex.FIELD_DESC_WORD, pattern));
//      descQuery.setBoost(descScore);
//      
//      contentQuery = new TermQuery(new Term(ContentIndex.FIELD_CONTENT_WORD, pattern));
//      contentQuery .setBoost(contentScore);
//    }
//    
//    BooleanClause booleanClause = new BooleanClause( titleQuery, BooleanClause.Occur.SHOULD);
//    booleanQuery.add(booleanClause);
//    
//    booleanClause = new BooleanClause(descQuery, BooleanClause.Occur.SHOULD);
//    booleanQuery.add(booleanClause);
//
//    booleanClause = new BooleanClause(contentQuery, BooleanClause.Occur.SHOULD);
//    booleanQuery.add(booleanClause);
//
//    return booleanQuery;
//  }

}
