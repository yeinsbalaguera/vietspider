/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.index;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.vietspider.db.ContentIndex;
import org.vietspider.db.dict.VietnameseDictionary;
import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.analytics.ViIndexAnalyzer2;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 26, 2009  
 */
public class QueryParser {

  public BooleanQuery createQuery(String pattern) {
    BooleanQuery booleanQuery = new BooleanQuery();

    ViIndexAnalyzer2 analyzer2 = new ViIndexAnalyzer2(/*new ConjuntionFilter()*/);
    List<PhraseData> phrases = analyzer2.analyze(pattern);

    String sequence = analyzer2.buildQuery(phrases);
    if(isWhitespace(sequence)) {
      Query query = createPhraseQuery(ContentIndex.FIELD_SEQUENCE, sequence, 2.5f);
      booleanQuery.add(query, BooleanClause.Occur.SHOULD);

      String n_sequence =  VietnameseConverter.toTextNotMarked(sequence);
      Query n_query = createPhraseQuery(ContentIndex.FIELD_SEQUENCE, n_sequence, 2.0f);
      booleanQuery.add(n_query, BooleanClause.Occur.SHOULD);
    }

    for(int i = 0; i < phrases.size(); i++) {
      String value = phrases.get(i).getValue();
      Query query = createBooleanQuery(value);
      booleanQuery.add(query, BooleanClause.Occur.SHOULD);
    }

    //    System.out.println(" boolean query "+ booleanQuery);

    return booleanQuery;
  }

  protected Query createPhraseQuery(String fieldName, String pattern, float boost) {
    String [] elements = pattern.split(" ");
    PhraseQuery query2 = new PhraseQuery();
    for(int i = 0; i < elements.length; i++) {
      if(!hasCharacter(elements[i])) continue;
      query2.add(new Term(fieldName, elements[i]));
    }
    query2.setBoost(boost);
    query2.setSlop(2);

    return query2;
  }

  protected BooleanQuery createBooleanQuery(String pattern) {
    BooleanQuery booleanQuery = new BooleanQuery();
    String n_pattern = VietnameseConverter.toTextNotMarked(pattern); 
    Query query = null;
    Query n_query = null;

    boolean isPhrase = isPhrase(pattern);
    if(isPhrase) {
      query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, pattern, 2.0f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 1.5f);
    } else {
      query = new TermQuery(new Term(ContentIndex.FIELD_TITLE_WORD, pattern));
      query.setBoost(pattern.indexOf(' ') > 0 ? 1.0f : 0.5f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 0.8f);
    }

    BooleanClause booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    if(isPhrase) {
      query = createPhraseQuery(ContentIndex.FIELD_DESC_WORD, pattern, 1.5f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 1.0f);
    } else {
      query = new TermQuery(new Term(ContentIndex.FIELD_DESC_WORD, pattern));
      query.setBoost(pattern.indexOf(' ') > 0 ? 0.8f : 0.5f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 0.5f);
    }
    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    if(isPhrase) {
      query = createPhraseQuery(ContentIndex.FIELD_CONTENT_WORD, pattern, 1.0f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 0.8f);
    } else {
      query = new TermQuery(new Term(ContentIndex.FIELD_CONTENT_WORD, pattern));
      query.setBoost(pattern.indexOf(' ') > 0 ? 0.7f : 0.5f);
      n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 0.3f);
    }
    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    return booleanQuery;
  }

  protected BooleanQuery createPhraseQuery(String pattern) {
    BooleanQuery booleanQuery = new BooleanQuery();
    String n_pattern = VietnameseConverter.toTextNotMarked(pattern); 
    Query query = null;
    Query n_query = null;

    query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, pattern, 2.0f);
    n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 1.5f);

    BooleanClause booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    query = createPhraseQuery(ContentIndex.FIELD_DESC_WORD, pattern, 1.5f);
    n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 1.0f);

    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    query = createPhraseQuery(ContentIndex.FIELD_CONTENT_WORD, pattern, 1.0f);
    n_query = createPhraseQuery(ContentIndex.FIELD_TITLE_WORD, n_pattern, 0.8f);

    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    return booleanQuery;
  }

  public BooleanQuery createMustQuery(String pattern) {
    BooleanQuery booleanQuery = new BooleanQuery();
    String n_pattern = VietnameseConverter.toTextNotMarked(pattern); 

    Query  query = new TermQuery(new Term(ContentIndex.FIELD_TITLE_WORD, pattern));
    query.setBoost(0.0f);
    Query n_query = new TermQuery(new Term(ContentIndex.FIELD_TITLE_WORD, pattern));
    n_query.setBoost(0.0f);

    BooleanClause booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    query = new TermQuery(new Term(ContentIndex.FIELD_DESC_WORD, pattern));
    query.setBoost(0.0f);
    n_query =new TermQuery(new Term(ContentIndex.FIELD_DESC_WORD, n_pattern));
    n_query.setBoost(0.0f);

    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    query = new TermQuery(new Term(ContentIndex.FIELD_CONTENT_WORD, pattern));
    query.setBoost(0.0f);
    n_query = new TermQuery(new Term(ContentIndex.FIELD_CONTENT_WORD, n_pattern));
    n_query.setBoost(0.0f);

    booleanClause = new BooleanClause(query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);
    booleanClause = new BooleanClause(n_query, BooleanClause.Occur.SHOULD);
    booleanQuery.add(booleanClause);

    return booleanQuery;
  }

  protected boolean hasCharacter(String text) {
    int  index = 0;
    while(index < text.length()) {
      if(Character.isLetterOrDigit(text.charAt(index))) return true;
      index++;
    }
    return false;
  }

  protected boolean isPhrase(String value) {
    if(VietnameseDictionary.getInstance().contains(value)) return false;
    return isWhitespace(value);
  }

  protected boolean isWhitespace(String value) {
    int  index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isWhitespace(c) || Character.isSpaceChar(c)) return true;
      index++;
    }
    return false;
  }

}
