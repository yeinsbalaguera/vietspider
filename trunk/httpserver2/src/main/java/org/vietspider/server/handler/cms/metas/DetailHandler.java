/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;
import java.sql.SQLException;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.ArticleRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class DetailHandler extends CMSHandler<Article> {
  
  private String referer = null;
  private String appName = "/vietspider/";

  public DetailHandler(String type) {
    super(type);
    SystemProperties properties = SystemProperties.getInstance();
    appName = properties.getValue(Application.APPLICATION_NAME);
    name = "DETAIL"; 
  }

  public String handle(final HttpRequest request, 
              final HttpResponse response,
              final HttpContext context, String...params) throws Exception {
    referer = null;
    if(params.length < 1) throw new Exception("Incorrect parameters");
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    Article article = null;
    try {
      article = loadDatabase(params[0]);      
    } catch (Exception e) {
      throw e;
    }
    
    if(article == null) {
      redirect(request, response);
      return "text/html";
    }
    
    if(request.getRequestLine().getUri().startsWith("/"+appName+"/")) {
      referer = getReferer(request);
      
      if(CrawlerConfig.EXCUTOR_SIZE <= 20) { 
        int status = article.getStatus() != 0 ? article.getStatus() : Article.READ;
        EIDFolder2.write(article.getDomain(), params[0], status);
      }
      
//      IDTracker.getInstance().update(params[0], status);
      return write(request, response, context, article, params);
    }

    if(article.getDomain() == null) {
      referer = getReferer(request);
      return write(request, response, context, article, params);
    }
    
    String username = CMSService.INSTANCE.getUsername(getCookie(request));
    if(username == null) username = "guest";
//    AccessChecker accessChecker = AccessCheckerService.getInstance().getAccessChecker(username);
//    boolean accessable = accessChecker.isPermitAccess(article.getDomain().getCategory(), false);
//    if(accessable) {
      referer = getReferer(request);
      int status = article.getStatus()>0 ? article.getStatus() : Article.READ;
      
      EIDFolder2.write(article.getDomain(), params[0], status);
//      IDTracker.getInstance().update(params[0], status);
      return write(request, response, context, article, params);
//    }
//    
//    redirect(request, response);
//    return "text/html";
  }
  
  private String getReferer(HttpRequest request) {
    Header header = request.getFirstHeader("Referer");
    return header == null ? null : header.getValue();
  }
  
  private Article loadDatabase(String metaId) {
    if(metaId == null || metaId.trim().isEmpty()) return null;
    Article article = null;
    try {
      article = DatabaseService.getLoader().loadArticle(metaId);
    } catch (SQLException e) {
      return null;
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
      return null;
    }
    if(article == null) return null;

    if(article.getMeta() == null) return  null;

    return article;
  }
  
  protected void logAction(String username, String action, String message) throws Exception {
    StringBuilder builder = new StringBuilder(username);
    action = action.replace(' ', '.');
    builder.append(' ').append(action).append(' ').append(message);
    LogService.getInstance().setMessage("USER", null, builder.toString());
  }

  public String render(OutputStream output, Article article, String [] cookies, String...params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    ArticleRenderer render = cms.createRender(ArticleRenderer.class);
    render.setParams(params);
    return render.write(output, type, cookies, article, referer);
  }

}
