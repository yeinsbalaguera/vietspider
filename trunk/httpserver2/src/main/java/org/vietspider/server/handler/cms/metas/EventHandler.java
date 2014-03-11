/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class EventHandler extends MetaListHandler {
  
  public EventHandler(String type) {
    super(type);
    name = "EVENT"; 
  }
  
  public  String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    MetaList metas = new MetaList(name);
    String date = null;
    if(params.length > 1) date = params[1].replace('.','/');
    try {
      if(date != null) { 
        metas.setTitle(date); 
        metas.setUrl(params[1]);
        metas.setCurrentPage(Integer.parseInt(params[0]));
      } else {
        metas.setCurrentPage(1);
      }
    } catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }
    
    if(date != null) DatabaseService.getUtil().loadEvent(date, metas);
    return write(request, response, context, metas, params);
  }

}
