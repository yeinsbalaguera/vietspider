/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.Article;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.chars.TextSpliter;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.nlp.impl.Price;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.query.ValueRange;
import org.vietspider.nlp.query.ValueRange.DoubleValueRange;
import org.vietspider.nlp.query.ValueRange.FloatValueRange;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2011  
 */
class NlpPresentationBuilder {

  private Map<Short, Collection<?>> nlpMap;
  private CommonSearchQuery query;

  NlpPresentationBuilder(CommonSearchQuery query) {
    this.query = query;
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    nlpMap = analyzer.process(query.getPattern());
  }
  
  void buildActionObject(Article article) {
    Collection<?> collection = nlpMap.get(NLPData.ACTION_OBJECT);
    
    String ao = null;
    if(collection != null && collection.size() > 0) {
      Iterator<?> iterator = collection.iterator();
      ao = iterator.next().toString();
    }
    
    NLPRecord record = article.getNlpRecord();
    StringBuilder builder = new StringBuilder();
    if(record == null) return;
    collection = record.getData(NLPData.ACTION_OBJECT);
    //      String value = meta.getPropertyValue("action_object");
    if(collection == null || collection.size() < 1)  return;
    Iterator<?> iterator = collection.iterator();
    int counter = 0;
    while(counter < 2 && iterator.hasNext()) {
      if(counter > 0) builder.append(", ");
      String temp = iterator.next().toString();
      if(temp.equals(ao)) {
        builder.append("<b style=\"color:#000\">");
        builder.append(NLPData.action_object(temp));
        builder.append("</b>");
      } else {
        builder.append(NLPData.action_object(temp));
      }
      counter++;
    }
    article.getMeta().putProperty("nlp.action_object", builder.toString());
  }

  void buildArea(Article article) {
    NLPRecord record = article.getNlpRecord();
    if(record == null) return;
    
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    FloatValueRange area = analyzer.getRange(nlpMap, ValueRange.AREA_RANGE);
    if(area != null) {
      List<String> list = record.getData(NLPData.AREA);
      TextSpliter spliter = new TextSpliter();
      for(int i = 0; i < list.size(); i++) {
        List<String> elements = spliter.toList(list.get(i), '-');
        for(String ele : elements) {
          String presentation = ele;
          if(ele.endsWith("m2")) {
            ele = ele.substring(0, ele.length()-2);
          } else {
            presentation += "m2";
          }
          if(!area.valid(ele)) continue;
          article.getMeta().putProperty("nlp.area", presentation);
          return;
        }
      }
    }

    String value = record.getOneData(NLPData.AREA_SHORT);
    //meta.getPropertyValue("nlp.area");
    if(value != null && value.length() > 0) {
      article.getMeta().putProperty("nlp.area", value);
    }
    
    Collection<?> collection = nlpMap.get(NLPData.AREA);
    if(collection == null || collection.size() < 1) {
      article.getMeta().putProperty("query.area", "no");
    }
  }

  void buildPrice(Article article) {
    NLPRecord record = article.getNlpRecord();
    if(record == null) return;
    
    DoubleValueRange rfilter = (DoubleValueRange)query.getProperties().get("filter.price");
    
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    DoubleValueRange price = analyzer.getRange(nlpMap, ValueRange.PRICE_TOTAL_RANGE);
    if(price == null && rfilter != null 
        && rfilter.getType() == ValueRange.PRICE_TOTAL_RANGE) price = rfilter;
    if(price != null) {
      List<String> list = record.getData(NLPData.PRICE_TOTAL);
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i);
        if(!price.valid(value)) continue;
        String presentation = Price.toText(Double.valueOf(value))+ "/tổng";
        article.getMeta().putProperty("nlp.price", presentation);  
        return;
      }
    }
    
    price = analyzer.getRange(nlpMap, ValueRange.PRICE_M2_RANGE);
    if(price == null && rfilter != null 
        && rfilter.getType() == ValueRange.PRICE_M2_RANGE) price = rfilter;
    if(price != null) {
      List<String> list = record.getData(NLPData.PRICE_UNIT_M2);
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i);
        if(!price.valid(value)) continue;
        String presentation = Price.toText(Double.valueOf(value))+ "/m2";
        article.getMeta().putProperty("nlp.price", presentation);  
        return;
      }
    }
    
    price = analyzer.getRange(nlpMap, ValueRange.PRICE_MONTH_RANGE);
    if(price == null && rfilter != null 
        && rfilter.getType() == ValueRange.PRICE_MONTH_RANGE) price = rfilter;
    if(price != null) {
      List<String> list = record.getData(NLPData.PRICE_MONTH);
      for(int i = 0; i < list.size(); i++) {
        String value = list.get(i);
        if(!price.valid(value)) continue;
        String presentation = Price.toText(Double.valueOf(value))+ "/tháng";
//        System.out.println("chay thu " + presentation);
        article.getMeta().putProperty("nlp.price", presentation);  
        return;
      }
    }
    
    Collection<?> collection = nlpMap.get(NLPData.PRICE);
    if(collection == null || collection.size() < 1) {
      article.getMeta().putProperty("query.price", "no");
    }
    
    collection = record.getData(NLPData.PRICE);
    if(collection != null && collection.size() > 0) {
      Iterator<?> iterator = collection.iterator();
      int counter = 0;
      StringBuilder builder = new StringBuilder();
      while(counter < 2 && iterator.hasNext()) {
        if(counter > 0) builder.append(", ");
        builder.append(iterator.next().toString());
        counter++;
      }
      article.getMeta().putProperty("nlp.price", builder.toString()); 
    }
    
  }
  
  void buildAddress(Article article) {
    NLPRecord record = article.getNlpRecord();
    if(record == null) return;
    List<String> list = record.getData(NLPData.ADDRESS);
    if(list == null || list.size() < 1) return;
    
    List<String> regions = query.getRegions();
    if(regions == null || regions.size() < 1) {
      article.getMeta().putProperty("nlp.address", list.get(0)); 
      return;
    }
    
    String address = null;
    for(int i = 0; i < list.size(); i++) {
      if(endWith(list.get(i))) {
        address = list.get(i);
        break;
      }
    }
    
    if(address == null) return;
//    if(address == null) address = list.get(0);
    
    article.getMeta().putProperty("nlp.address", address); 
  }
  
  private boolean endWith(String text) {
    List<String> regions = query.getRegions();
    for(int j = 0; j < regions.size(); j++) {
      String region = regions.get(j);
//      System.out.println(region + " : "+ text);
      if(region.length() > text.length()) return false;
      int index1 = region.length() - 1;
      int index2 = text.length() - 1;
      boolean end = true;
      while(index1 > -1) {
        char c1 = Character.toLowerCase(region.charAt(index1));
        char c2 = text.charAt(index2);
        index1--;
        index2--;
        if(c1 == c2) continue;
//        System.out.println(c1 +  " : "+ c2);
        end = false;
        break;
      }
//      System.out.println(end);
      if(end) return true;
    }
    return false;
  }
}
