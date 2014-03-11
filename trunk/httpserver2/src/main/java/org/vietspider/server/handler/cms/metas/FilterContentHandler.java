/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.File;
import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.article.index.ArticleIndexStorage;
import org.vietspider.article.index.ArticleSearchQuery;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public class FilterContentHandler extends MetaListHandler {
  
  public FilterContentHandler(String type) {
    super(type); 
    name = "FILTER"; 
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    String pattern = params[1];
    int idx = pattern.indexOf('=');
    if(idx < 0  || idx >= pattern.length()-1) { 
      throw new IndexOutOfBoundsException("Incorrect parammeter");
    }
    
    String filter = URLDecoder.decode(pattern.substring(idx+1), "UTF-8");
    
    MetaList metas = new MetaList();
    metas.setAction("FILTER");
    metas.setCurrentPage(Integer.parseInt(params[0]));
    
    File file = UtilFile.getFile("system/plugin/filter", NameConverter.encode(filter));
    if(file.exists() && file.length() > 0) {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      
      int start = 0;
      while(start < text.length()) {
        char c = text.charAt(start);
        if(Character.isLetterOrDigit(c)) break;
        start++;
      }
      
      int end = text.length()-1;
      while(end > 0) {
        char c = text.charAt(end);
        if(Character.isLetterOrDigit(c)) break;
        end--;
      }
      
      StringBuilder builder = new StringBuilder("\"");
      for(int i = start; i <= end; i++) {
        char c = text.charAt(i);
        if(c == ',') {
          builder.append("\" AND \"");
        } else if (c == '\n') {
          builder.append("\" OR \"");
        } else if(Character.isLetterOrDigit(c)) {
          builder.append(c);
        } else if(Character.isSpaceChar(c) || Character.isWhitespace(c)) {
          builder.append(' ');
        }
      }
      builder.append("\"");
      
//      System.out.println(" thay co filter "+ builder);

      ArticleSearchQuery query = new ArticleSearchQuery();
      query.setPattern(builder.toString());

      if(Application.LICENSE != Install.PERSONAL) {
        query.setHighlightStart("no");
        ArticleIndexStorage.getInstance().search(metas, query);
      }
    }
    
    metas.setTitle(filter);
    metas.setUrl("?text=" + pattern.substring(idx+1)); 
    
    return write(request, response, context, metas, params);
  }

}
