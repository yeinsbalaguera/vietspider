/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.NLPData;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.ClassifiedSearchQuery;
import org.vietspider.index.SearchQuery;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.webui.cms.FileWriterImpl;
import org.vietspider.webui.cms.RequestUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
//sửa máy tính tu khoa test spam
public class DefaultSearchHandler extends SearchHandler<MetaList> {

  private FileWriterImpl writerImpl = new FileWriterImpl();
  
//  private TopMetasHandler topHandler;
  
  public DefaultSearchHandler(/*TopMetasHandler topHandler,*/ String type) {
    super(type); 
    name = "search"; 
//    this.topHandler = topHandler;
    Regions.getInstance();
//    SEOPageGenerator.createInstance();
  }
  

  @SuppressWarnings("unchecked")
  public String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
    
//    System.out.println(" ===================================== ");
//    Header [] headers = request.getAllHeaders();
//    for(Header header : headers) {
//      System.out.println(header.getName() + " : "+ header.getValue());
//    }
//    System.out.println(context.);
    
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    Header headerAgent = request.getFirstHeader("User-Agent");
    if(RequestUtils.isBot(headerAgent)) {
      return redirectURL(request, response, context, "/site/browse/1/", null);
//      System.out.println(" da xay ra tai day");
//      return topHandler.handle(request, response, context, params);
    }
    
    if(RequestUtils.isInvalidBot(headerAgent)) {
      return redirect(request, response, context, crawlPage(), null);
    }
    
    Header headerCookie = request.getFirstHeader("Cookie");
    long session = RequestUtils.getSession(headerCookie);
    if(headerAgent == null 
        || params == null || params.length < 2) {
      String fileName = request.getRequestLine().getUri();
      if(fileName != null && fileName.length() > 1) {
        if(fileName.charAt(0) == '/') fileName = fileName.substring(1);
        ClassifiedSearchQuery query = new ClassifiedSearchQuery(fileName);
        return write(request, response, context, null, query);
      }
      return write(request, response, context, null, (SearchQuery)null);
    } 
    
    if(session > -1 && ClientSessionDetector.getInstance().isVeryFast(session)) {
      return redirect(request, response, context, crawlPage(), null);
    }
    
//    //only for test
//    File folder = UtilFile.getFolder("content/temp/search/");
//    UtilFile.deleteFolder(folder);

    MetaList metas = new MetaList();
    metas.setAction("search");
    try {
      metas.setCurrentPage(Integer.parseInt(params[0]));
      
      String [][] parameters = ParameterParser.getParameters(params[1]);

//      System.out.println(" chay toi day roi " + parameters[0][1]);
      
      ClassifiedSearchQuery query = null;
      if(parameters.length > 0) {
        query = new ClassifiedSearchQuery(parameters[0][1]);
        List<String> actions = new ArrayList<String>();
        for(int i = 1; i < parameters.length; i++) {
          /*if("region".equalsIgnoreCase(parameters[i][0])) {
            query.setRegion(parameters[i][1]);
          } else*/ 
          if("price".equalsIgnoreCase(parameters[i][0])) {
              query.setPrice(parameters[i][1]);
          } else  if("sdate".equalsIgnoreCase(parameters[i][0])) {
            try {
              query.setDate(Integer.parseInt(parameters[i][1]));
            } catch (Exception e) {
            }
          } else if("action".equalsIgnoreCase(parameters[i][0])) {
            actions.add(parameters[i][1].trim());
          }
        }
        if(actions.size() > 0) query.setActions(actions.toArray(new String[0]));
      } else {
        return write(request, response, context, null, null);
      }
      
      if(query.getPattern() == null || query.getPattern().trim().length() < 1) {
        return write(request, response, context, null, null);
      }
      
      if(RequestUtils.isInValidKeyWord(query.getPattern())) {
        return write(request, response, context, null, null);
      }
      
      List<String> histories = RequestUtils.getHistory(headerCookie, query.getPattern());
      if(session < 0) session = ClientSessionDetector.getInstance().generateId();
      ClientSessionDetector.getInstance().put(session);
      response.addHeader(new BasicHeader("Set-Cookie", RequestUtils.toCookieValues(histories)));
      response.addHeader(new BasicHeader("Set-Cookie", "client=" + session + "; path=/;"));
      query.setHistory(histories);
      
      QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
      Map<Short, Collection<?>> records = analyzer.process(query.getPattern());
      
      List<String> cities = (List<String>)records.get(NLPData.CITY);
      if(cities != null && cities.size() > 0) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < cities.size(); i++) {
          if(builder.length() > 0) builder.append(',');
          builder.append(cities.get(i));
        }
//        String cookie = VietnameseConverter.toAlias(cities.get(0));
//        response.addHeader(new BasicHeader("Set-Cookie", "region=" + cookie + "; path=/;"));
//        cookie =  URLEncoder.encode(cities.get(0), "utf-8");
//        response.addHeader(new BasicHeader("Set-Cookie", "region_label=" + cookie + "; path=/;"));
        query.setRegion(builder.toString());
      } else {
        String cookie = RequestUtils.getCookie(headerCookie, "region");
        if(cookie != null) {
          String region = Regions.getInstance().getName(cookie);
//          System.out.println(keyRegion +  " : " + region);
          if(region != null) query.setRegion(region);
        }
      }
      
      
     String cookie = RequestUtils.getCookie(headerCookie, "owner");
     query.setOwner("true".equals(cookie));
      
//      HighlightBuilder highlighter = new HighlightBuilder(query.getPattern().toLowerCase());
      
//      System.out.println(" chay toi day roi " + query.getPattern());
      if(DatabaseService.isMode(DatabaseService.SEARCH)) {
        if(noCharacter(query.getPattern())){
          return write(request, response, context, null, (SearchQuery)null);
        }
        DatabaseService.getLoader().search(metas, query);
      }
      
      if(metas.getCurrentPage() == 1) {
        LogService.getInstance().setMessage("USER_SEARCH", null, "User Agent: " + headerAgent.getValue());
        LogService.getInstance().setMessage("USER_SEARCH", null, "Query Pattern: " + query.getPattern());
      }

      return write(request, response, context, metas, query);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      return write(request, response, context, null, null);
    }
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, MetaList metas, String cookies[], SearchQuery query) throws Exception {
    if(metas == null) {
      if(query != null && writerImpl.isExists(query.getPattern())) {
        return writeFile(output, query.getPattern());
      }
      
      writerImpl.write(output, "form.html");   
      return "text/html";
    }

    PageRendererImpl render = new PageRendererImpl(false);
    render.write(output, metas, query);
    return "text/html";
  }
  
  private String writeFile(OutputStream output, String fileName) throws Exception {
    writerImpl.write(output, fileName);
    if(fileName.endsWith(".png"))  return "image/png";
    if(fileName.endsWith(".gif"))  return "image/gif";
    if(fileName.endsWith(".jpg"))  return "image/jpg";
    if(fileName.endsWith(".css"))  return "text/css";
    if(fileName.endsWith(".js"))  return "application/x-javascript";
    if(fileName.endsWith(".txt")) return "text/plain";
    if(fileName.endsWith(".xml")) return "text/xml";
//    if(file.endsWith(".jpg"))  return "image/jpg";
//    if(file.endsWith(".jpg"))  return "image/jpg";
//    if(file.endsWith(".jpg"))  return "image/jpg";
    return "text/html";
  }

  /* private String getQuery(String pattern) throws Exception {
    int idx = pattern.indexOf('=');
    if(idx < 0  || idx >= pattern.length()-1) { 
      throw new IndexOutOfBoundsException("Incorrect parammeter");
    }

    pattern = pattern.substring(idx+1);
    idx = pattern.indexOf('&');
    System.out.println(" thay c "+ pattern + " / "+ idx);
    if(idx > 0) pattern = pattern.substring(0, idx);

    System.out.println(" thay c "+ pattern + " / "+ idx);

    return URLDecoder.decode(pattern, "UTF-8");
  }*/
  
  private boolean noCharacter(String text) {
    int idx = 0;
    while(idx < text.length()) {
      if(Character.isLetterOrDigit(text.charAt(idx))) return false;
      idx++;
    }
    return true;
  }
  
  
}

