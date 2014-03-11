/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;
import java.sql.SQLException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.webui.cms.vietspider.TranslatorRendererImpl;
import org.vietspider.webui.search.ParameterParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class TranslateHandler extends CMSHandler<Article> {
  
  private TranslatorRendererImpl renderer;

  public TranslateHandler(String type) {
    super(type);
    renderer = new TranslatorRendererImpl();
    name = "TRANSLATOR"; 
  }

  public String handle(final HttpRequest request, 
              final HttpResponse response,
              final HttpContext context, String...params) throws Exception {
    if(params.length < 1) throw new Exception("Incorrect parameters");
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    Article article = null;
    try {
      article = loadDatabase(params[0]);      
    } catch (Exception e) {
      throw e;
    }
    
//    System.out.println("ta co " + params[1]);
    
    String [] newParams = new String[3];
    if(params.length > 1) {
      String [][] parameters = ParameterParser.getParameters(params[1]);
      for(int i = 0; i < parameters.length; i++) {
        if(parameters[i][0].charAt(0) == '?') {
          parameters[i][0] = parameters[i][0].substring(1);
        }
//        System.out.println(parameters[i][0] + " : " + parameters[i][1]);
        if(parameters[i][0].equals("from")) {
          newParams[0] = parameters[i][1];
        } else if(parameters[i][0].equals("to")) {
          newParams[1] = parameters[i][1];
        } 
      }
    }
    newParams[2] = request.getRequestLine().getUri();
    
    if(article == null) {
      redirect(request, response);
      return "text/html";
    }
    
    return write(request, response, context, article, newParams);
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
  

  public String render(OutputStream output, Article article, String [] cookies, String...params) throws Exception {
    try {
      return renderer.write(output, article, params[0], params[1], params[2]);
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }
  }
}
