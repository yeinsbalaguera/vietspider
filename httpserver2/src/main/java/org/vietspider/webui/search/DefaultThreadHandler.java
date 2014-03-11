/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.ClassifiedSearchQuery;
import org.vietspider.index.SearchQuery;
import org.vietspider.webui.cms.FileWriterImpl;
import org.vietspider.webui.cms.RequestUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class DefaultThreadHandler extends SearchHandler<MetaList> {
  
  private FileWriterImpl writerImpl = new FileWriterImpl();
  
  public DefaultThreadHandler() {
    super("site"); 
    name = "thread"; 
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    Header headerCookie = request.getFirstHeader("Cookie");
    long session = RequestUtils.getSession(headerCookie);
    
    if(params == null || params.length < 2
        || (session > -1 && ClientSessionDetector.getInstance().isVeryFast(session))) {
      return write(request, response, context, null, null);
    } 
    
    MetaList metas = new MetaList();
    metas.setAction("thread");
    try {
      metas.setCurrentPage(Integer.parseInt(params[0]));
      
      String [][] parameters = ParameterParser.getParameters(params[1]);
      
      
      ClassifiedSearchQuery query = null;
      if(parameters.length > 0) {
        query = new ClassifiedSearchQuery(parameters[0][1]);
        List<String> actions = new ArrayList<String>();
        for(int i = 1; i < parameters.length; i++) {
//          if("region".equalsIgnoreCase(parameters[i][0])) {
//            query.setRegion(parameters[i][1]);
//          } else 
          if("sdate".equalsIgnoreCase(parameters[i][0])) {
            try {
              query.setDate(Integer.parseInt(parameters[i][1]));
            } catch (Exception e) {
            }
          } else if("action".equalsIgnoreCase(parameters[i][0])) {
            actions.add(parameters[i][1].trim());
          } else if("meta".equalsIgnoreCase(parameters[i][0])) {
            query.setArticleId(parameters[i][1]);
          }
        }
        if(actions.size() > 0) query.setActions(actions.toArray(new String[0]));
      } else {
        return write(request, response, context, null, null);
      }
      
      List<String> histories = RequestUtils.getHistory(headerCookie, query.getPattern());
      if(session < 0) session = ClientSessionDetector.getInstance().generateId();
      ClientSessionDetector.getInstance().put(session);
      response.addHeader(new BasicHeader("Set-Cookie", RequestUtils.toCookieValues(histories)));
      response.addHeader(new BasicHeader("Set-Cookie", "client=" + session + "; path=/;"));
      query.setHistory(histories);
      
//      String [] data =  getQuery(params[1]);
//      if(data == null) return write(request, response, context, null, null);
      
//      SearchQuery query = new SearchQuery(data[0]);
//      query.setArticleId(data[1]);
      try {
        metas.setCurrentPage(Integer.parseInt(params[0]));
      } catch (NumberFormatException e) {
        metas.setCurrentPage(1);
      }
      metas.setAction("thread");
      metas.setUrl(query.getPattern());
      
      ArticleThreadLoader loader = new ArticleThreadLoader(query.getPattern()); 
      
      Article article = loader.loadDatabase(query.getArticleId(), Article.NORMAL);      
      
      if(article == null) {
        return write(request, response, context, null, null);
      }

      String title  = article.getMeta().getTitle();
      article.getMeta().setTitle(title+" (*)");
      metas.getData().add(article);
      loader.load(metas, article.getMetaRelations());

      return write(request, response, context, metas, query);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      return write(request, response, context, null, null);
    }
  }
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, MetaList metas, String cookies[], SearchQuery query) throws Exception {
    if(metas == null) {
      writerImpl.write(output, "form.html");   
      return "text/html";
    }
    
    PageRendererImpl render = new PageRendererImpl(false);
    render.write(output, metas, query);
    return "text/html";
  }
  
 /* private String[] getQuery(String pattern) throws Exception {
    int idx = pattern.indexOf('=');
    if(idx < 0) return null;
    
    pattern = pattern.substring(idx+1);
    idx = pattern.indexOf('&');
    if(idx < 0) return null;
    String metaId = pattern.substring(0, idx);
    
    pattern = pattern.substring(idx);
    
    idx = pattern.indexOf('=');
    if(idx < 0) return null;
    String query = pattern.substring(idx+1);
    query = URLDecoder.decode(query, "UTF-8");
    
    return new String[]{metaId, query};
  }*/
  
}

