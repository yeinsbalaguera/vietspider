/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.search.seo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.SearchQuery;
import org.vietspider.io.LogDataImpl;
import org.vietspider.webui.cms.RequestUtils;
import org.vietspider.webui.search.SearchHandler;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 13, 2007
 */
public class RSSPageHandler extends SearchHandler<MetaList> {

  private  RSSPageFactory render;
  private LogDataImpl log;

  public RSSPageHandler() {
    super("site");
    name = "rss";
    render = new RSSPageFactory();
    log = new LogDataImpl("track/logs/bot/");
  }

  @Override
  public void handle(final HttpRequest request,
      final HttpResponse response,
      final HttpContext context) throws HttpException, IOException {

    //    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    Header agentHeader = request.getFirstHeader("User-Agent");
    log.setMessage("USER_SEARCH", null, request.getRequestLine().getUri());
    if(agentHeader != null) {
      log.setMessage("USER_SEARCH", null, "User Agent: " + agentHeader.getValue());
    }

    if(RequestUtils.isInvalidBot(agentHeader)) {
      try {
        redirect(request, response, context, crawlPage(), null);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }

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

    //    try {
    //      String [][] parameters = ParameterParser.getParameters(params[0]);
    //      render.setKeyword(parameters[0][1]);
    //    } catch (Exception e) {
    //      LogService.getInstance().setMessage("SERVER", e, e.toString());
    //    }

    try {
      handle(request, response, context, params);
    }catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, e.toString());
    }
  }

  @SuppressWarnings("unused")
  public String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
    //    System.out.println(" buoc truoc khi renderer "+ metas.getData().size());
    return write(request, response, context, null, (SearchQuery)null);
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, MetaList metas, String[] cookies, SearchQuery query) throws Exception {
    render.write(output, type, metas);
    return "text/xml";
  }

}
