/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.content.index3.monitor.SearchMonitor;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.SearchQuery;
import org.vietspider.server.WebRM;
import org.vietspider.webui.search.ParameterParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public class SolrIndexSearchHandler extends MetaListHandler {
  
  public SolrIndexSearchHandler(String type) {
    super(type); 
    name = "SEARCH"; 
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
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

    SearchQuery query = null;
    if(parameters.length > 0) {
      query = new SearchQuery(parameters[0][1]);
//      for(int i = 1; i < parameters.length; i++) {
//        if("region".equalsIgnoreCase(parameters[i][0])) {
//          query.setRegion(parameters[i][1]);
//        }
//      }
    } else {
      throw new IndexOutOfBoundsException("Incorrect parammeter");
    }
    
    query.setHighlightStart("<b style=\"color: black; background-color: rgb(255, 255, 102);\">");
    query.setHighlightEnd("</b>");
    
    DatabaseService.getLoader().search(metas, query);
    
    if(metas.getCurrentPage() == 1) {
      SearchMonitor.getInstance().savePattern(query.getPattern(), (int)metas.getTotalData());
    }
    
    WebRM resources = new WebRM();
    StringBuilder  builder = new StringBuilder(resources.getLabel("search"));
    metas.setTitle(builder.toString());
    metas.setUrl(params[1]); 
    
    return write(request, response, context, metas, params);
  }

}
