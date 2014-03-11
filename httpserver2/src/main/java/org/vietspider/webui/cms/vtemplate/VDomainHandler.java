/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.MergeEntryDomains;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.server.handler.cms.DataNotFound;
import org.vietspider.webui.cms.RequestUtils;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class VDomainHandler extends VTemplateHandler {
  
  private VMetaListRenderer renderer;
  
  public VDomainHandler(String type) {
    super(type); 
    name = "domain"; 
    
    renderer = new VMetaListRenderer();
  }

  public void handle(final HttpRequest request, final HttpResponse response,
                     final HttpContext context) throws HttpException, IOException {
    String path = request.getRequestLine().getUri();
    if(path.trim().length() < 2) {
      try {
        List<String> dates = DatabaseService.getLoader().loadDateFromDomain();
        if(dates.size() < 1)  throw new DataNotFound();
        
        HttpRequestData hrd = HttpUtils.createHRD(request, type);
        hrd.setParams(new String[]{"1", dates.get(0).replace('/', '.')});
        
        render(hrd);
        response.setEntity(hrd.createEntity());
      } catch (Exception e) {
        throw new HttpException(e.getMessage());
      }
      return;
    }

    super.handle(request, response, context);
  }
  

  public void render(HttpRequestData hrd) throws Exception {
    if(RequestUtils.isInvalidBot(hrd.getAgent())) {
      invalid(hrd);
      return;
    }
    
    if(RequestUtils.isBot(hrd.getAgent())) {
      LogService.getInstance().setMessage("WEB_CLIENT", null,
          "User Agent: " + hrd.getAgent() + ", is mobile? " + String.valueOf(hrd.isMobile()));
    } else {
      log.setMessage("USER_SEARCH", null,hrd.getUri());
      log.setMessage("USER_SEARCH", null, "User Agent: " + hrd.getAgent());
    }
    
    String page = "1";
    String [] params = hrd.getParams();
    
    if(params.length > 0) page = params[0];
    
    
    int length  = params.length;
    
    String date = null;
    String category = null;
    String source = null;
    
    Calendar calendar = Calendar.getInstance();
    
    if(params.length > 1 && "today".equals(params[1])) {
      params[1] = CalendarUtils.getParamFormat().format(calendar.getTime());
    } else if(params.length > 1 && "yesterday".equals(params[1])) {
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
      params[1] = CalendarUtils.getParamFormat().format(calendar.getTime());
    }
    
    if(params.length > 1 && HttpUtils.isDateFormat(params[1])) {
      date = params[1].replace('.', '/'); 
      if(length > 3){
        category = params[2];
        source  = params[3];
      } else if(length > 2){
        category = params[2];
      }
    } else {
      if(length > 2) {
        category = params[1];
        source  = params[2];
      } else if(params.length > 1){
        category = params[1];
      }
    }
    
   
//    System.out.println("thay co "+title);
//    System.out.println(" hihi "+ entryDomain.getFile());
    
    MetaList metas = new MetaList(name);
    metas.setPageSize(20);
    EntryReader entryReader = new EntryReader();
    
    try {
      metas.setCurrentPage(Integer.parseInt(page));
    } catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }
    
    for(int i = 0; i < 10; i++) {
      //working with entry
      IEntryDomain entryDomain = null;
      if(date != null) {
        entryDomain = new SimpleEntryDomain(date, category, source);
      } else {
        entryDomain = MergeEntryDomains.getInstance().getIterator(category, source);
      }

      entryReader.read(entryDomain, metas, -1);
      
      StringBuilder title = new StringBuilder();
      if(date != null) title.append(params[1]);
      
      if(category != null) {
        if(title.length() > 0) title.append('/');
        title.append(category);
      }
      
      if(source != null) {
        if(title.length() > 0) title.append('/');
        title.append(source);
      }
      metas.setTitle("Nik tin tức - " + title.toString());
      metas.setUrl(title.toString());
      
      System.out.println(title);
      
      if(metas.getData().size() > 0) break;
      
      calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
      params[1] = CalendarUtils.getParamFormat().format(calendar.getTime());
      
      date = params[1].replace('.', '/'); 
      category = null;
      source = null;
      metas.setCurrentPage(1);
      
    }
    
//    int pageValue = metas.getCurrentPage();
//    System.out.println(pageValue + " : " + metas.getTotalPage() );
    
    renderer.write(hrd, metas);
  }

}
