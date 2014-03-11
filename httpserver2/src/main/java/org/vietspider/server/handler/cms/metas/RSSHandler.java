/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.MergeEntryDomains;
import org.vietspider.db.idm2.SimpleEntryDomain;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.RSSRenderer;
import org.vietspider.webui.search.ParameterParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 13, 2007
 */
public class RSSHandler extends CMSHandler<MetaList> {

  public RSSHandler(String type) {
    super(type);
    name = "RSS"; 
  }

  @Override
  public void handle(final HttpRequest request,
      final HttpResponse response,
      final HttpContext context) throws HttpException, IOException {
    String [] params = new String[0]; 
    String path  = request.getRequestLine().getUri();
    if(name != null && !name.trim().isEmpty()) {
      int idx = path.indexOf(name);
      if(idx > 0) {
        String component = path.substring(idx+name.length());
        component = component.trim();
        if(component.charAt(0) == '/') component = component.substring(1);
        component = URLDecoder.decode(component, Application.CHARSET);
        params = component.split("/");
      }
    }
    try {
      handle(request, response, context, params);
    }catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }

  public String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
    String page = "1";
    if(params.length > 0) page = params[0];

    MetaList metas = new MetaList(name);
    metas.setPageSize(getPageSize(params));
    
    try {
      metas.setCurrentPage(Integer.parseInt(page));
    }catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }

//    Domain domain = null;
    StringBuilder title = new StringBuilder();

    int length  = params.length;
    if(length > 0 && params[length-1].startsWith("filter:")) length--;  

    String date = null;
    String category = null;
    String source = null;
    
    if(params.length > 1 && DomainHandler.isDateFormat(params[1])) {
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

    //working with entry
    EntryReader entryReader = new EntryReader();
    IEntryDomain entryDomain = null;
    if(date != null) {
      entryDomain = new SimpleEntryDomain(date, category, source);
    } else {
      entryDomain = MergeEntryDomains.getInstance().getIterator(category, source);
    }
    entryReader.read(entryDomain, metas, -1);

   /* //working with database
    if(metas.getData().size() < 1) {
      int total  = 0;
      if(length > 3){
        total = menuInfo.getCategoryInfo(params[2]).getSource(params[3]).getData();
      } else if(length > 2){
        if(title.length() < 1) title.append(params[1]).append('/').append(params[2]);
        domain = new Domain();
        domain.setDate(date);
        domain.setCategory(params[2]);
        total = menuInfo.getCategoryInfo(params[2]).getTotalData();
      } else {
        if(length > 1) {
          if(title.length() < 1) title.append(params[1]); 
        } else {
          if(title.length() < 1) title.append(date.replace('/', '.'));
        }
        domain = new Domain();
        domain.setDate(date);
        total = menuInfo.getTotalData();
      }

      int totalPage = total / metas.getPageSize() ;
      if (total % metas.getPageSize() > 0) totalPage++ ;
      metas.setTotalPage(totalPage);

      if(metas.getCurrentPage()  < 1 
          || metas.getCurrentPage() > totalPage) metas.setCurrentPage(1);

      DatabaseService.getLoader().loadMetaFromDomain(domain, metas);
    }*/

    metas.setTitle(title.toString());
    metas.setUrl(title.toString());

//    System.out.println(" buoc truoc khi renderer "+ metas.getData().size());

    return write(request, response, context, metas, params);
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output_, MetaList metas, String cookies[], String... params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    RSSRenderer render = cms.createRender(RSSRenderer.class);
    render.write(output_, type, metas);
    return "text/xml";
  }

  private int getPageSize(String[] params) throws Exception {
    if(params.length < 2) return 50;
    String query = params[1];
    int idx = query.indexOf('?');
    if(idx < 0) return 50;
    query = query.substring(idx+1);

    String [][] parameters = ParameterParser.getParameters(query);
    if(parameters == null) return 50;
    for(int i = 0; i < parameters.length; i++) {
//      System.out.println(parameters[i][0]);
      if(!parameters[i][0].equalsIgnoreCase("pagesize")) continue;
      try {
        return Integer.parseInt(parameters[i][1]);
      } catch (Exception e) {
      }
    }
    return 50;
  }

}
