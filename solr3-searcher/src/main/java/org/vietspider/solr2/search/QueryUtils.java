/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.nlp.query.ValueRange;
import org.vietspider.nlp.query.ValueRange.DoubleValueRange;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 19, 2010  
 */
public class QueryUtils {

  private final static List<String> words = new ArrayList<String>();

  static {
    File file  = UtilFile.getFile("/system/dictionary/", "query.word.txt");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim().toLowerCase();
        if(elements[i].length() < 1) continue;
        words.add(elements[i]);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public static String normalize(String text) {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    int quote = 0;
    text = text.trim();
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
          || Character.isSpaceChar(c)) c = ' ';
      switch (c) {
      case '+':      
      case '-':
      case '&':
      case '|':
      case '!':
      case '(':
      case ')':
      case '{':
      case '}':
      case '[':
      case ']':
      case '^':
      case '~':
      case '*':
      case '?':
      case ':':
      case '\\':
      case '=':
      case '\'':
        builder.append('\\').append(c);
        break;
      case '\"':
        builder.append(c);
        quote++;
        break;
      default:
        /*if(Character.isLetterOrDigit(c)
            || Character.isWhitespace(c)
            || Character.isSpaceChar(c) || c < 256)*/ builder.append(c);
            break;
      }
      index++;
    }
    if(quote%2 == 1) builder.append('\"');

    //    System.out.println(" normalize text: "+ builder);

    if(builder.length() < 30 
        && words.size() > 0) {
      //      System.out.println("compile text : "+ compile(builder.toString()));
      return compile(builder.toString());
    }
    return builder.toString();
  }

  static String compile(String text) {
    StringBuilder builder = new StringBuilder(text);
    StringBuilder lower = new StringBuilder(text.toLowerCase());
    Iterator<String> iterator = words.iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      replace(builder, lower, key);
    }

    return builder.toString();
  }

  private static void replace(StringBuilder builder, StringBuilder lower, String key) {
    int index = lower.indexOf(key);
    //    System.out.println(" kiem key "+ key + " : "+ index);
    String value = new StringBuilder("\"").append(key).append("\"").toString();
    while(index > -1) {
      if(index > 0 && Character.isLetterOrDigit(lower.charAt(index-1))) {
        index = lower.indexOf(key, index + value.length());
        continue;
      }

      if((index + key.length()) < lower.length() - 1 
          && Character.isLetterOrDigit(lower.charAt(index+key.length()))) {
        index = lower.indexOf(key, index + value.length());
        continue;
      }
      builder.replace(index, index + key.length(),  value);
      lower.replace(index, index + key.length(),  value.toLowerCase());
      index = lower.indexOf(key, index + value.length());
    }
  }

  public static SolrQuery createQuery(CommonSearchQuery baseQuery, int start) {
    SolrQuery query = new SolrQuery();
    query = query.setQuery(baseQuery.getPattern());
    query = query.setFields("id", "score");
    query = query.setFacetMinCount(1);
    query = query.setFacetLimit(2).setStart(start);
    query = query.setHighlight(false);
    query = query.setQueryType("vshandler");
    
//    System.out.println(" ==== > " + baseQuery.getPattern());
    
    if(!baseQuery.getPattern().startsWith("field:")) {
      List<String> regions = baseQuery.getRegions();
      if(regions != null) {
        for(int i = 0; i < regions.size(); i++) {
          query = query.addFilterQuery("region:\"" + regions.get(i) + "\"");
        }
      }

      query = analyzePrice(baseQuery, query);

      if(baseQuery.isOwner()) {
        query = query.addFilterQuery("owner:\"true\"");
      }
    }

    if(baseQuery.getIndexFrom() > 0 && baseQuery.getIndexTo() > 0) {
      query = query.addFilterQuery("time:[" 
          + baseQuery.getIndexFrom() + " TO " + baseQuery.getIndexTo() + "]");
    } 

//    System.out.println(" ============ >"+ query);

    return query;
  }

  static Proximity buildProximity(String pattern){
    StringBuilder content = new StringBuilder();
    int counter = 1;

    int index = 0;
    int length = (pattern = pattern.trim()).length();
    boolean lastEmpty = true;
    while(index < length) {
      char c = pattern.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        content.append(c);
        lastEmpty = false;
      } else if(Character.isSpaceChar(c) || Character.isWhitespace(c)) {
        if(!lastEmpty) {
          counter++;
          content.append(' ');
          lastEmpty = true;
        }
      } else {
        return null;
      }

      index++;
    }
    content.insert(0,'\"');
    content.append("\"~");
    if(counter < 2) return null;
    if(counter == 2) {
      content.append('0');
    } else if(counter == 3) {
      content.append('1');
    } else if(counter == 4) {
      content.append('2');
    } else {
      content.append('3');
    }
    return new Proximity(content.toString(), counter);
  }

  private static SolrQuery analyzePrice(CommonSearchQuery baseQuery, SolrQuery query) {
    String price = baseQuery.getPrice();
    if(price == null) return query;

    try {
      DoubleValueRange range = null;

      int index = price.indexOf('_');
      int type = Integer.parseInt(price.substring(index+1));
      price = price.substring(0, index);
      TextSpliter spliter = new TextSpliter();
      List<String> elements = spliter.toList(price, '-');
      double dmin = 0;
      double dmax = 0;
      //      System.out.println(elements.size());
      if(elements.size() != 2) return query;
      String min = elements.get(0);
      if(min.length() == 0) {
        min = "*";
        dmin = 0;
      } else {
        dmin = Double.parseDouble(min)*1000*1000l;
        min = String.valueOf(dmin);
      }
      String max = elements.get(1);
      if(max.length() == 0) {
        max = "*";
        dmax = 1000*1000*1000*1000l;
      } else {
        dmax = Double.parseDouble(max)*1000*1000l;
        max = String.valueOf(dmax);
      }

      if(type == 1) {
        range = new DoubleValueRange(ValueRange.PRICE_TOTAL_RANGE);
        query = query.addFilterQuery("price_total:[" + min + " TO " + max + "]");
      } else if(type == 2) {
        range = new DoubleValueRange(ValueRange.PRICE_M2_RANGE);
        query = query.addFilterQuery("price_m2:[" + min + " TO " + max + "]");
      } else if(type == 3) {
        range = new DoubleValueRange(ValueRange.PRICE_MONTH_RANGE);
        query = query.addFilterQuery("price_month:[" + min + " TO " + max + "]");
      }

      range.addRange(dmin, dmax);

      baseQuery.getProperties().put("filter.price", range);

    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }

    return query;
  }

  public static void main(String[] args) {
    System.out.println(normalize("\"Nhà 02 mặt tiền, mới xây 2008"));
    System.out.println(normalize("\"Nhà 02 mặt tiền, mới xây 2008\""));
  }
}
