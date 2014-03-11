/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.article.index.ArticleIndexStorage;
import org.vietspider.article.index.ArticleSearchQuery;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.MetaList;
import org.vietspider.server.WebRM;
import org.vietspider.webui.search.ParameterParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public class IndexSearchHandler extends MetaListHandler {
  
  public IndexSearchHandler(String type) {
    super(type); 
    name = "SEARCH"; 
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    String pattern = params[1];
    int idx = pattern.indexOf('=');
    if(idx < 0  || idx >= pattern.length()-1) { 
      throw new IndexOutOfBoundsException("Incorrect parammeter");
    }
    
    String search = URLDecoder.decode(pattern.substring(idx+1), "UTF-8");
    
    pattern = search;
        
    MetaList metas = new MetaList();
    metas.setAction("SEARCH");
    metas.setCurrentPage(Integer.parseInt(params[0]));
    
    String [][] parameters = ParameterParser.getParameters(params[1]);
    
    /*SearchQuery query = null;
    if(parameters.length > 0) {
      query = new SearchQuery(parameters[0][1]);
//      for(int i = 1; i < parameters.length; i++) {
//        if("region".equalsIgnoreCase(parameters[i][0])) {
//          query.setRegion(parameters[i][1]);
//        }
//      }
    } else {
      throw new IndexOutOfBoundsException("Incorrect parammeter");
    }*/
    
    
    ArticleSearchQuery query = new ArticleSearchQuery();
    for(int i = 0; i < parameters.length; i++) {
      if(parameters[i][0].charAt(0) == '?') {
        parameters[i][0] = parameters[i][0].substring(1);
      }
//      System.out.println(parameters[i][0] + " : "+ parameters[i][1]);
      if(parameters[i][0].equals("text")) {
        query.setPattern(parameters[i][1]);
      } else if(parameters[i][0].equals("source")) {
        query.setSource(parameters[i][1]);
      } else if(parameters[i][0].equals("dtstart")) {
        parameters[i][1] = parameters[i][1].replace('.', '/');
        try {
          SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
          Date date = dateFormat.parse(parameters[i][1]);
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          calendar.set(Calendar.HOUR, 0);
          calendar.set(Calendar.MINUTE, 0);
          calendar.set(Calendar.SECOND, 0);
          query.setIndexFrom(calendar.getTimeInMillis());
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
      } else if(parameters[i][0].equals("dtend")) {
        parameters[i][1] = parameters[i][1].replace('.', '/');
        try {
          SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
          Date date = dateFormat.parse(parameters[i][1]);
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          calendar.set(Calendar.HOUR, 0);
          calendar.set(Calendar.MINUTE, 0);
          calendar.set(Calendar.SECOND, 0);
          calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
          query.setIndexTo(calendar.getTimeInMillis());
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
      }
    }
    
    if(Application.LICENSE != Install.PERSONAL) {
//      ArticleSearchQuery query = new ArticleSearchQuery(parameters[0][1]);
      query.setHighlightStart("<b style=\"color: black; background-color: rgb(255, 255, 102);\">");
      query.setHighlightEnd("</b>");
      ArticleIndexStorage.getInstance().search(metas, query);
    }
    
//    System.out.println("thay co " + metas.getData().size());
    
//    System.out.println(parameters[0][1]);
    
//    File tempFolder = UtilFile.getFolder("content/temp/search/");
//    ContentSearcher2 searcher = new ContentSearcher2(ContentSearcher2.SIMPLE, tempFolder);
//    HighlightBuilder highlighter = new HighlightBuilder(query.getPattern().toLowerCase());
//    highlighter.setHighlightTag("<b style=\"color: black; background-color: rgb(255, 255, 102);\">", "</b>");
    //      query.setParser(new QueryParser());
//    DefaultArticleHandler articleHandler2 = new DefaultArticleHandler(highlighter);
//    searcher.search(metas, query, articleHandler2);
//    ContentSearcher searcher = new ContentSearcher();
//    searcher.search(metas, pattern);
    
    WebRM resources = new  WebRM();
    StringBuilder  builder = new StringBuilder(resources.getLabel("search"));
    metas.setTitle(builder.toString());
    metas.setUrl(params[1]); 
    
    return write(request, response, context, metas, params);
  }

}
