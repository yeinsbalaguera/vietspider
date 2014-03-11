/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 5, 2011  
 */
public class TimeQueryParser {
  
  private SolrQueryParser timeParser;
  private SolrQueryParser sourceTimeParser;
  
  public TimeQueryParser(QParser qparser) {
    timeParser  = new SolrQueryParser(qparser, "time");
    sourceTimeParser  = new SolrQueryParser(qparser, "source_time");
  }
  
  @SuppressWarnings("unused")
  List<Query> createShouldQueries(Map<Short, Collection<?>> records) throws ParseException {
    List<Query> list = new ArrayList<Query>();
    
    //today
    Calendar calendar = Calendar.getInstance();
    long max = calendar.getTimeInMillis();
    calendar.set(Calendar.HOUR, 0);
    long min = calendar.getTimeInMillis();
    StringBuilder builder = new StringBuilder();
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    Query timeQuery = timeParser.parse(builder.toString()); 
    timeQuery.setBoost(10.0f);
    list.add(timeQuery);
    
//    System.out.println(" boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    //yestoday
    max = min;
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = timeParser.parse(builder.toString()); 
    timeQuery.setBoost(8.0f);
    list.add(timeQuery);
    
//    System.out.println(" boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    max = min;
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = timeParser.parse(builder.toString()); 
    timeQuery.setBoost(7.0f);
    list.add(timeQuery);
    
//    System.out.println(" boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    max = min;
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = timeParser.parse(builder.toString()); 
    timeQuery.setBoost(4.0f);
    list.add(timeQuery);
    
//    System.out.println(" boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    max = min;
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 2);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = timeParser.parse(builder.toString()); 
    timeQuery.setBoost(2.0f);
    list.add(timeQuery);
    
//    System.out.println(" boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    // for source time
    calendar = Calendar.getInstance();
    max = calendar.getTimeInMillis();
    calendar.set(Calendar.HOUR, 0);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = sourceTimeParser.parse(builder.toString()); 
    timeQuery.setBoost(7.0f);
    list.add(timeQuery);
    
    
//    System.out.println(" source time boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
    calendar = Calendar.getInstance();
    max = calendar.getTimeInMillis();
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = sourceTimeParser.parse(builder.toString()); 
    timeQuery.setBoost(5.0f);
    list.add(timeQuery);
    
//    System.out.println(" source time boost  " + calendar.get(Calendar.DATE)  +  " : "+ timeQuery.getBoost() );
    
    calendar = Calendar.getInstance();
    max = calendar.getTimeInMillis();
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
    min = calendar.getTimeInMillis();
    builder.setLength(0);
    builder.append('[').append(min).append(" TO ").append(max).append(']');
    timeQuery = sourceTimeParser.parse(builder.toString()); 
    timeQuery.setBoost(2.0f);
    list.add(timeQuery);
    
//    System.out.println(" source time boost  " + calendar.get(Calendar.DATE) +  " : "+ timeQuery.getBoost());
    
//    TermRangeQuery tquery = new TermRangeQuery(
//        "time", String.valueOf(min), String.valueOf(max), true, true);
//    list.add(tquery);
    
    return list;
  }
}
