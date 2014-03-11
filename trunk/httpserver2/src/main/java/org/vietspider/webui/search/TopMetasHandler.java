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
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.ClassifiedSearchQuery;
import org.vietspider.index.SearchQuery;
import org.vietspider.io.LogDataImpl;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;
import org.vietspider.webui.cms.FileWriterImpl;
import org.vietspider.webui.cms.RequestUtils;
import org.vietspider.webui.search.seo.LastAccessData;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class TopMetasHandler extends SearchHandler<MetaList> {

  private FileWriterImpl writerImpl = new FileWriterImpl();
  private LogDataImpl log;

  private String username;
  protected DataClientService client;  
  protected  ContentMapper mapper = new ContentMapper();

  public TopMetasHandler(String type) {
    super(type); 
    name = "browse"; 
    log = new LogDataImpl("track/logs/bot/");
    //    SEOPageGenerator.createInstance();

    String remote = SystemProperties.getInstance().getValue("remote.crawler.address");
    username = SystemProperties.getInstance().getValue("remote.crawler.username");
    if(remote != null && !remote.trim().isEmpty()) client = new DataClientService(remote);

  }

  public String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
    //    if(logAgent) log.setMessage(null, userAgent);
    
    Header headerAgent = request.getFirstHeader("User-Agent");
    String uri = request.getRequestLine().getUri();
    if(!RequestUtils.isBot(headerAgent)) {
      int index = uri.indexOf("/browse/");
      if(index > -1) {
        uri = uri.substring(0, index) + "/search/" + uri.substring(index + "/browse/".length());
        return redirectURL(request, response, context, uri, null);
      }
    }
    
    int index = uri.indexOf('?');
    if(index > -1) {
      uri = uri.substring(0, index);
//      System.out.println(" =====  > chuyen lan 1 " +uri);
      response.addHeader("Location", uri);
      response.setStatusCode(HttpStatus.SC_MOVED_PERMANENTLY);
      return write(request, response, context, null, (SearchQuery)null);
    }
    
    index = uri.indexOf("/search/");
    if(index > -1) {
      uri = uri.substring(0, index) + "/browse/" + uri.substring(index + "/search/".length());
      response.addHeader("Location", uri);
      response.setStatusCode(HttpStatus.SC_MOVED_PERMANENTLY);
      return write(request, response, context, null, (SearchQuery)null);
    }
    
//    response.setStatusCode()
    
    log.setMessage("USER_SEARCH", null, request.getRequestLine().getUri());
    log.setMessage("USER_SEARCH", null, "User Agent: " + headerAgent.getValue());

//    if(client == null || params == null || params.length < 2) {
//      String fileName = request.getRequestLine().getUri();
//      if(fileName != null && fileName.length() > 1) {
//        if(fileName.charAt(0) == '/') fileName = fileName.substring(1);
//        ClassifiedSearchQuery query = new ClassifiedSearchQuery(fileName);
//        return write(request, response, context, null, query);
//      }
//      return write(request, response, context, null, (SearchQuery)null);
//    } 

    //only for test
    //    File folder = UtilFile.getFolder("content/temp/search/");
    //    UtilFile.deleteFolder(folder);
    
    if(params.length < 1) {
      if(uri.length() > 1 && uri.charAt(0) == '/') {
        uri = uri.substring(1);
        ClassifiedSearchQuery query = new ClassifiedSearchQuery(uri);
        return write(request, response, context, null, query);
      }
      return write(request, response, context, null, null);
    }
 
    try {
      MetaList metas = load(params[0]);
      if(metas == null) {
        return write(request, response, context, null, (SearchQuery)null);
      }
      
      if(metas.getCurrentPage() > 1
          && (metas.getData() == null
              || metas.getData().size() < 1)) {
        response.addHeader("Location", "/site/browse/1/");
        response.setStatusCode(HttpStatus.SC_MOVED_PERMANENTLY);
        return write(request, response, context, null, (SearchQuery)null);
      }

      metas.setAction("browse");

//      ClassifiedSearchQuery query = new ClassifiedSearchQuery();
//      String [][] parameters = ParameterParser.getParameters(params[1]);
//      query.setPattern(parameters[0][1]);

      //      System.out.println(" ====  >"+ metas.getData().size());

      //  ContentSearcher2 searcher = new ContentSearcher2();
      //  searcher.search(metas, query);
      //      CachedEntry2 pageIO = articleHandler2.getPageIO();
      //      query.setRefresh(!pageIO.isFromFile() && size > 0 );

      return write(request, response, context, metas, null);
    } catch (Exception e) {
      log.setMessage(e, e.toString());
      return write(request, response, context, null, null);
    }
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, MetaList metas, String cookies[], SearchQuery query) throws Exception {
    if(metas == null) {
      if(query != null && writerImpl.isExists(query.getPattern())) {
        String fileName = query.getPattern();
        writerImpl.write(output, fileName);
        if(fileName.endsWith(".png"))  return "image/png";
        if(fileName.endsWith(".gif"))  return "image/gif";
        if(fileName.endsWith(".jpg"))  return "image/jpg";
        if(fileName.endsWith(".css"))  return "text/css";
        if(fileName.endsWith(".js"))  return "application/x-javascript";
        if(fileName.endsWith(".txt")) return "text/plain";
        if(fileName.endsWith(".xml")) return "text/xml";
        //        if(file.endsWith(".jpg"))  return "image/jpg";
        //        if(file.endsWith(".jpg"))  return "image/jpg";
        //        if(file.endsWith(".jpg"))  return "image/jpg";
        return "text/html";
      }

      writerImpl.write(output, "form.html");   
      return "text/html";
    }

    PageRendererImpl render = new PageRendererImpl(true);
    try {
      render.write(output, metas, query);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return "text/html";
  }

  public MetaList load(String page) {
    MetaList metas = new MetaList("browse");
    metas.setPageSize(30);
    try {
      metas.setCurrentPage(Integer.parseInt(page));
    } catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }
    
    try {
      List<Header> lheaders = new ArrayList<Header>();
      lheaders.add(new BasicHeader("action", "load.metas.as.raw.text"));
      lheaders.add(new BasicHeader("page.size", "30"));
      lheaders.add(new BasicHeader("username", username));
      Header [] headers = lheaders.toArray(new Header[0]);
      
      byte [] bytes = client.post(URLPath.REMOTE_DATA_HANDLER, page.getBytes(), headers);
      bytes = new GZipIO().unzip(bytes);
      String text = new String(bytes, Application.CHARSET);
      int idx = text.indexOf('\n');
      
      if(idx > 0) {
//        try {
//          metas.setTotalPage(Integer.parseInt(text.substring(0, idx)));
//        } catch (Exception e) {
//        }
        metas.setTotalPage(1000);
        
        text = text.substring(idx + 1);
      }
      
      String [] elements = text.split(Article.RAW_TEXT_ARTICLE_SEPARATOR);
      for(int i = 0; i < elements.length; i++) {
        Meta meta = null;
        if(elements[i].indexOf(ContentMapper.SEPARATOR) > -1) {
          Article article = new Article();
          ContentMapper.text2MetaData(article, elements[i]);
          meta = article.getMeta();
        } else {
          meta = ContentMapper.text2Meta(elements[i]);
        }
        if(meta == null) continue;
//        System.out.println(" bebebe ===  >" + meta.getId() + " : "+ meta);
        Article article = new Article();
        article.setMeta(meta);
        metas.getData().add(article);
      }
      return metas;
      
//      MetaList metaList = client.readFromXML(MetaList.class, 
//          URLPath.REMOTE_DATA_HANDLER, page.getBytes(), headers);
//      return metaList;
    } catch (Exception e) {
      log.setMessage(e, "Load " + page + " error: "+e.toString());
    } 

    try {
      LastAccessData.getInstance().loadArticles(metas);
    } catch (Exception e) {
      log.setMessage(e, "Load " + page + " error: "+e.toString());
    } 
    return null;
  }

}

