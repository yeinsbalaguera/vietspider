/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import java.io.Serializable;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.vietspider.db.ContentIndex;
import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.analytics.ViIndexAnalyzer2;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 1, 2009  
 */
@NodeMap(value = "classified-search-query", depth = 3)
public class ClassifiedSearchQuery extends SearchQuery {

  //  private Set<String> relations = new HashSet<String>();

  @NodeMap("action")
  private String action = null;
  
  @NodeMap(value  = "sub-query", depth = 3)
  private CommonSearchQuery subQuery;

  public ClassifiedSearchQuery() {
    this.parser = new ClassifiedQueryParser();
  }

  public ClassifiedSearchQuery(String pattern) {
    super(pattern);
    this.parser = new ClassifiedQueryParser();
  }

  private boolean isAction(String text) {
    String [] actions = getActions();
    if(actions == null) return false;
    for(int i = 0; i < actions.length; i++) {
      if(text.equals(actions[i])) return true;
    }
    return false;
  }
  
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public class ClassifiedQueryParser extends QueryParser implements Serializable {

    public BooleanQuery createQuery(String _pattern) {
      BooleanQuery booleanQuery = new BooleanQuery();

      //      SearchSequenceSplitter splitter = new SearchSequenceSplitter();
      //      SequenceSplitter splitter = new SequenceSplitter();
      //      List<PhraseData> phrases = splitter.split(new PhraseData(false, _pattern, 0));


      ViIndexAnalyzer2 analyzer2 = new ViIndexAnalyzer2();
      List<PhraseData> phrases = analyzer2.analyze(_pattern);

      String sequence = analyzer2.buildQuery(phrases);
      if(isWhitespace(sequence)) {
        //        CategoriesCodes.getInstance().match(relations, sequence.toCharArray());
        Query query = createPhraseQuery(ContentIndex.FIELD_SEQUENCE, sequence, 2.5f);
        booleanQuery.add(query, BooleanClause.Occur.SHOULD);

        String n_sequence =  VietnameseConverter.toTextNotMarked(sequence);
        Query n_query = createPhraseQuery(ContentIndex.FIELD_SEQUENCE, n_sequence, 2.0f);
        booleanQuery.add(n_query, BooleanClause.Occur.SHOULD);
      }

      /*if(action != null && !(action = action.trim().toLowerCase()).isEmpty()) {
        BooleanQuery actionQuery  = getParser().createMustQuery(action);
        actionQuery.setBoost(0.0f);
        BooleanClause booleanClause = new BooleanClause(actionQuery, BooleanClause.Occur.MUST);
        newQuery.add(booleanClause);
      }*/

      for(int i = 0; i < phrases.size(); i++) {
        String value = phrases.get(i).getValue();
        if(isAction(value)) {
          action = value;
          phrases.remove(i);
          break;
        }
      }

      BooleanQuery booleanQuery2 = new BooleanQuery();

      if(action == null) {
        for(int i = 0; i < phrases.size(); i++) {
          PhraseData phrase = phrases.get(i);
          String value = phrase.getValue();
          if(phrase.isAtomic()) {
            Query query = createPhraseQuery(value);
            booleanQuery2.add(query, BooleanClause.Occur.MUST);
          } else {
            Query query = createBooleanQuery(value);
            booleanQuery2.add(query, BooleanClause.Occur.MUST);
          }
        }
      } else {
        BooleanQuery actionQuery  = getParser().createMustQuery(action);
        actionQuery.setBoost(0.0f);
        BooleanClause booleanClause = new BooleanClause(actionQuery, BooleanClause.Occur.MUST);
        booleanQuery2.add(booleanClause);

        BooleanQuery booleanQuery3 = new BooleanQuery();
        for(int i = 0; i < phrases.size(); i++) {
          PhraseData phrase = phrases.get(i);
          String value = phrase.getValue();
          //          CategoriesCodes.getInstance().match(relations, value.toCharArray());
          if(phrase.isAtomic()) {
            Query query = createPhraseQuery(value);
            booleanQuery2.add(query, BooleanClause.Occur.MUST);
          } else {
            Query query = createBooleanQuery(value);
            booleanQuery2.add(query, BooleanClause.Occur.MUST);
          }
        }

        booleanQuery2.add(booleanQuery3, BooleanClause.Occur.MUST);
      }

      booleanQuery.add(booleanQuery2, BooleanClause.Occur.SHOULD);

      //            System.out.println(" boolean query "+ booleanQuery);

      return booleanQuery;
    }

  }


  public CommonSearchQuery getSubQuery() {
    return subQuery;
  }

  public void setSubQuery(CommonSearchQuery subQuery) {
    this.subQuery = subQuery;
  }
  
  
}
