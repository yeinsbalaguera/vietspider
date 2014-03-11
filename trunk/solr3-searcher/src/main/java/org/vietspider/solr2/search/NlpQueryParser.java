/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.vietspider.bean.NLPData.ACTION_ASSIGNMENT;
import static org.vietspider.bean.NLPData.ACTION_BUY;
import static org.vietspider.bean.NLPData.ACTION_FOR_RENT;
import static org.vietspider.bean.NLPData.ACTION_OBJECT;
import static org.vietspider.bean.NLPData.ACTION_RENT;
import static org.vietspider.bean.NLPData.ACTION_SELL;
import static org.vietspider.bean.NLPData.ADDRESS;
import static org.vietspider.bean.NLPData.AREA;
import static org.vietspider.bean.NLPData.CITY;
import static org.vietspider.bean.NLPData.NORMAL_TEXT;
import static org.vietspider.bean.NLPData.OBJECT_APARTMENT;
import static org.vietspider.bean.NLPData.OBJECT_BUSINESS;
import static org.vietspider.bean.NLPData.OBJECT_HOUSE;
import static org.vietspider.bean.NLPData.OBJECT_LAND;
import static org.vietspider.bean.NLPData.OBJECT_ROOM;
import static org.vietspider.bean.NLPData.OBJECT_VILLA;
import static org.vietspider.bean.NLPData.OWNER;
import static org.vietspider.bean.NLPData.PRICE;
import static org.vietspider.nlp.query.ValueRange.AREA_RANGE;
import static org.vietspider.nlp.query.ValueRange.PRICE_M2_RANGE;
import static org.vietspider.nlp.query.ValueRange.PRICE_MONTH_RANGE;
import static org.vietspider.nlp.query.ValueRange.PRICE_TOTAL_RANGE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.vietspider.bean.NLPData;
import org.vietspider.chars.TextSpliter;
import org.vietspider.nlp.impl.ao.NlpAction;
import org.vietspider.nlp.impl.ao.NlpObject;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.query.ValueRange.DoubleValueRange;
import org.vietspider.nlp.query.ValueRange.FloatValueRange;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 13, 2011  
 */
class NlpQueryParser {

  private SolrQueryParser regionParser;
  private SolrQueryParser addressParser;
  private SolrQueryParser actionObjectParser;
  private TextDataQueryParser textParser;

  private SolrQueryParser areaParser;
  private SolrQueryParser ownerParser;
  private SolrQueryParser priceTotalParser;
  private SolrQueryParser priceUnitM2Parser;
  private SolrQueryParser priceMonthParser;

  NlpQueryParser(QParser qparser, TextDataQueryParser textParser) {
    regionParser = new SolrQueryParser(qparser, "region");
    actionObjectParser = new SolrQueryParser(qparser, "action_object");
    addressParser = new SolrQueryParser(qparser, "address");
    areaParser = new SolrQueryParser(qparser, "area");
    priceTotalParser = new SolrQueryParser(qparser, "price_total");
    priceUnitM2Parser = new SolrQueryParser(qparser, "price_m2");
    priceMonthParser = new SolrQueryParser(qparser, "price_month");
    ownerParser = new SolrQueryParser(qparser, "owner");
    this.textParser = textParser;
  }

  SolrQueryParser getAddressParser() {
    return addressParser;
  }

  void setDefaultOperator(QueryParser.Operator operator) {
    regionParser.setDefaultOperator(operator);
    actionObjectParser.setDefaultOperator(operator);
  }

  BooleanQuery create(Map<Short, Collection<?>> records, 
      short type, /*String field, */float boost, BooleanClause.Occur clause) throws ParseException {
    Collection<?> collections = records.get(type);
    if(collections == null || collections.isEmpty()) return null;

    BooleanQuery _query = new BooleanQuery();

    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();

    if(type == AREA) {
      FloatValueRange range = analyzer.getRange(records, AREA_RANGE);
      if(range == null) return null;
      for(int i = 0; i < range.size(); i++) {
        _query.add(areaParser.parse(range.toQuery(i)), clause);
      }
      return _query;
    }

    if(type == PRICE) {
      DoubleValueRange range = analyzer.getRange(records, PRICE_TOTAL_RANGE);
      if(range != null) {
        for(int i = 0; i < range.size(); i++) {
          _query.add(priceTotalParser.parse(range.toQuery(i)), clause);
        }
      }

      range = analyzer.getRange(records, PRICE_M2_RANGE);
      if(range != null) {
        for(int i = 0; i < range.size(); i++) {
          _query.add(priceUnitM2Parser.parse(range.toQuery(i)), clause);
        }
      }

      range = analyzer.getRange(records, PRICE_MONTH_RANGE);
      if(range != null) {
        for(int i = 0; i < range.size(); i++) {
          _query.add(priceMonthParser.parse(range.toQuery(i)), clause);
        }
      }
      return _query;
    }

    boolean owner = false;
    Iterator<?> iterator = collections.iterator();

    while(iterator.hasNext()) {
      Object object = iterator.next();
      if(type == CITY) {
        String value = object.toString();
        _query.add(regionParser.parse("\"" + value + "\""), clause);
      } else if(type == ACTION_OBJECT) {
        String value = object.toString();
        _query.add(actionObjectParser.parse(value), clause);
        //        PhraseQuery eleQuery = new PhraseQuery();
        //        eleQuery.add(new Term("action_object", value));
        //        _query.add(eleQuery, clause);
      } else if(type == ADDRESS) {
        String value = object.toString();
        _query.add(addressParser.parse("\"" + value + "\""), clause);
      } else if(type == OWNER && "true".equals(object.toString())) {
        owner = true;
      }
      //      if(boost > 0) eleQuery.setBoost(boost);
    }

    if(owner) _query.add(ownerParser.parse("true"), clause);

    if(boost > 0) _query.setBoost(boost);
    return  _query;
  }

  BooleanQuery createText(Map<Short, Collection<?>> records,
      short type, BooleanClause.Occur clause, boolean quote) throws ParseException {
    Collection<?> collections = records.get(type);
    if(collections == null || collections.isEmpty()) return null;
    Iterator<?> iterator = collections.iterator();

    BooleanQuery _query = null;
    while(iterator.hasNext()) {
      String value = iterator.next().toString();
      value = QueryUtils.normalize(value);
      if(value.trim().length() < 1) continue;
      if(value.length() == 1 && value.charAt(0) == '\"') continue;
      if(value.length() == 2 && value.charAt(0) == '\"' && value.charAt(1) == '\"') continue;
      //      System.out.println("normal text: "  + value + " : " + value.trim().length());
      if(quote && value.charAt(0) != '\"') {
        value  = "\"" + value + "\"";
      }
      //      if(type == NORMAL_TEXT) System.out.println("\nnormal text: "  + value +"\n");
      //      if(boost > 0) eleQuery.setBoost(boost);
      if(_query == null)  _query = new BooleanQuery();
      _query.add(textParser.createQuery1(value, type), clause);
    }
    //    System.out.println(_query);
    return  _query;
  }

  List<BooleanQuery> createMustQueries(String pattern, Map<Short, 
      Collection<?>> records) throws ParseException {
    List<BooleanQuery> list = new ArrayList<BooleanQuery>();

    BooleanQuery _query = createText(records, NORMAL_TEXT, SHOULD, false);
    if(_query != null 
        && _query.getClauses().length > 0) list.add(_query);

    _query = create(records, ACTION_OBJECT, 10.0f, SHOULD);
    if(_query == null 
        || _query.getClauses().length < 1) {
      _query = createActionObjectQueries(pattern);
    }

    if(_query != null 
        && _query.getClauses().length > 0) {
      list.add(_query);
    } 

    _query = create(records, CITY, 1.0f, SHOULD);
    if(_query != null 
        && _query.getClauses().length > 0) list.add(_query);

    BooleanQuery _addressQuery = null;
    _query = create(records, ADDRESS, 10.0f, SHOULD);
    if(_query != null) {
      _addressQuery = new BooleanQuery();
      _addressQuery.add(_query, SHOULD) ;
    }
    _query = createText(records, ADDRESS, SHOULD, true);
    if(_query != null) _addressQuery.add(_query, SHOULD) ;
    if(_addressQuery != null 
        && _query.getClauses().length > 0 ) list.add(_addressQuery);

    _query = create(records, AREA, 10.0f, SHOULD);
    if(_query != null && _query.getClauses().length > 0) list.add(_query);

    _query = create(records, PRICE, 10.0f, SHOULD);
    if(_query != null && _query.getClauses().length > 0) list.add(_query);

    _query = create(records, OWNER, 10.0f, SHOULD);
    if(_query != null && _query.getClauses().length > 0) list.add(_query);

    return list;
  }

  Query  parseAddress(String pattern) throws ParseException {
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(pattern, ',');
    BooleanQuery mainQuery = new BooleanQuery();
    BooleanQuery mustQuery = new BooleanQuery();
    BooleanQuery shouldQuery = new BooleanQuery();
    //    List<Query> should = new ArrayList<Query>();
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
      if(elements[i].length() < 1) continue;
      Query query = addressParser.parse("\"" + elements[i] + "\"");
      if(elements[i].startsWith("Quận") 
          || elements[i].startsWith("quận")) {
        query.setBoost(100);
        shouldQuery.add(query, BooleanClause.Occur.SHOULD);
        continue;
      } 
      mustQuery.add(query, BooleanClause.Occur.MUST);
    }
    mainQuery.add(mustQuery, BooleanClause.Occur.MUST);
    if(shouldQuery.getClauses().length > 0) {
      mainQuery.add(shouldQuery, BooleanClause.Occur.SHOULD);
    }
    return mainQuery;
  }


  @SuppressWarnings("unchecked")
  private BooleanQuery createActionObjectQueries(String pattern) throws ParseException {
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();

    Map<Short, Collection<?>> map  = analyzer.process(pattern);
    List<NlpObject> objects = (List<NlpObject>)map.get(NLPData.OBJECT);

    if(objects == null || objects.size() < 1) {
      List<NlpAction> actions = (List<NlpAction>)map.get(NLPData.ACTION);
      if(actions == null || actions.size() < 1) return null;
      
      NlpAction action =  actions.get(0);
      BooleanQuery _query = new BooleanQuery();
      
      StringBuilder builder = new StringBuilder();
      builder.append(action.getType()).append(',').append(OBJECT_HOUSE);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

      builder.setLength(0);
      builder.append(ACTION_FOR_RENT).append(',').append(OBJECT_LAND);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

      builder.setLength(0);
      builder.append(ACTION_BUY).append(',').append(OBJECT_APARTMENT);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);


      builder.setLength(0);
      builder.append(ACTION_RENT).append(',').append(OBJECT_VILLA);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

      builder.setLength(0);
      builder.append(ACTION_ASSIGNMENT).append(',').append(OBJECT_ROOM);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);
      
      builder.setLength(0);
      builder.append(ACTION_ASSIGNMENT).append(',').append(OBJECT_BUSINESS);
      _query.add(actionObjectParser.parse(builder.toString()), SHOULD);
      
      return _query;
    }

    NlpObject object = objects.get(0);


    BooleanQuery _query = new BooleanQuery();

    StringBuilder builder = new StringBuilder();
    builder.append(ACTION_SELL).append(',').append(object.getType());
    _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

    builder.setLength(0);
    builder.append(ACTION_FOR_RENT).append(',').append(object.getType());
    _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

    builder.setLength(0);
    builder.append(ACTION_BUY).append(',').append(object.getType());
    _query.add(actionObjectParser.parse(builder.toString()), SHOULD);


    builder.setLength(0);
    builder.append(ACTION_RENT).append(',').append(object.getType());
    _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

    builder.setLength(0);
    builder.append(ACTION_ASSIGNMENT).append(',').append(object.getType());
    _query.add(actionObjectParser.parse(builder.toString()), SHOULD);

    return _query;

  }


}
