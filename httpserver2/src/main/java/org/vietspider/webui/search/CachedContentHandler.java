/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
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
import org.vietspider.bean.Content;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.AliveContentChecker;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.dict.non.TextTranslator;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.index.SearchQuery;
import org.vietspider.io.LogDataImpl;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;
import org.vietspider.server.handler.cms.metas.CMSHandler;
import org.vietspider.webui.cms.FileWriterImpl;
import org.vietspider.webui.cms.RequestUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 5, 2009  
 */
public class CachedContentHandler extends CMSHandler<Article> {

  private FileWriterImpl writerImpl = new FileWriterImpl();
  private LogDataImpl log;
  
  private String username;
  protected DataClientService client;  
  protected  ContentMapper mapper = new ContentMapper();
  private TopMetasHandler topMetasHandler;

  public CachedContentHandler(TopMetasHandler topMetasHandler) {
    super("site");
    name = "cached"; 
    log = new LogDataImpl("track/logs/bot/");
    
    String remote = SystemProperties.getInstance().getValue("remote.crawler.address");
    username = SystemProperties.getInstance().getValue("remote.crawler.username");
    if(remote != null && !remote.trim().isEmpty()) client = new DataClientService(remote);
    
    this.topMetasHandler = topMetasHandler;
  }

  public String handle(final HttpRequest request, 
      final HttpResponse response,
      final HttpContext context, String...params) throws Exception {
    
//    if(logAgent) LogService.getInstance().setMessage(null, userAgent);
    
    Header agentHeader = request.getFirstHeader("User-Agent");
    boolean bot = RequestUtils.isBot(agentHeader);
    
    if(RequestUtils.isInvalidBot(agentHeader)) {
      return redirect(request, response, context, crawlPage(), null);
    } else if(bot) {
      log.setMessage("USER_SEARCH", null, request.getRequestLine().getUri());
      log.setMessage("USER_SEARCH", null, "User Agent: " + agentHeader.getValue());
    }
    
    Header headerCookie = request.getFirstHeader("Cookie");
    long session = RequestUtils.getSession(headerCookie);
    
    if(params == null || params.length < 1
        || (session > -1 && ClientSessionDetector.getInstance().isVeryFast(session))) {
//    if(params == null || params.length < 1) {
      if(bot) return topMetasHandler.handle(request, response, context, params);
      return write(request, response, context, null);
    } 
    
    if(session < 0) session = ClientSessionDetector.getInstance().generateId();
    ClientSessionDetector.getInstance().put(session);
    response.addHeader(new BasicHeader("Set-Cookie", "client=" + session + "; path=/;"));
    
    Article article = null;
    String [][] parameters =null;
    try {
      article = loadDatabase(params[0]); 
      if(article == null && bot) {
        return topMetasHandler.handle(request, response, context, params);
      }
//      System.out.println(" return from database =====  >"+ params[0]+ " : "+article);

      parameters = ParameterParser.getParameters(params[1]);
      if(parameters.length > 1 
          &&  "true".equalsIgnoreCase(parameters[1][1])) {
        Meta meta = article.getMeta();
        Content content = article.getContent();
        TextTranslator translator = new TextTranslator();
        meta.setTitle(translator.compile(meta.getTitle()));
        meta.setDesc(translator.compile(meta.getDesc()));

        StringBuilder builder = new StringBuilder();
        HTMLParser2 parser = new HTMLParser2();
        HTMLDocument document = parser.createDocument(content.getContent());
        NodeIterator iterator = document.getRoot().iterator();
        while (iterator.hasNext()) {
          HTMLNode node = iterator.next();
          if(node.isNode(Name.CONTENT)) {
            String text  = new String(node.getValue()) ;
            node.setValue(translator.compile(text).toCharArray());
            builder.append(translator.compile(text));
          }else if(node.isNode(Name.STYLE) 
              || node.isNode(Name.SCRIPT)) {
            iterator.next();
          }

        }
        content.setContent(document.getTextValue());
      }
    } catch (Exception e) {
      if(bot) return topMetasHandler.handle(request, response, context, params);
      return write(request, response, context, null);
    }
    
    String content = null;
    if(article.getContent() != null) {
      content = article.getContent().getContent();
    }
    if(!bot && RequestUtils.isInValidKeyWord(content)) {
      return write(request, response, context, null);
    }
    
    AliveContentChecker.getInstance().add(article);
    
    if(bot) article.getMeta().putProperty("others", load());

    try {
      if(parameters.length >= 1){
        return write(request, response, context, article, parameters[0][1], String.valueOf(bot));
      } 
      return write(request, response, context, article, "");
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      if(bot) return topMetasHandler.handle(request, response, context, params);
      return write(request, response, context, null);
    }
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, Article article, String cookies[], String... params) throws Exception {
    if(article == null) {
      writerImpl.write(output, "form.html");   
      return "text/html";
    }
    
    boolean bot  = false;
    try {
      if(params.length > 1) {
        bot = Boolean.valueOf(params[1]);
      }
    } catch (Exception e) {
    }
    
    CachedRendererImpl render = new CachedRendererImpl(bot);
    render.write(output, article, params[0]);
    return "text/html";
  }

  private Article loadDatabase(String metaId) {
    Article article = null;
    try {
//      System.out.println(" loader " + DatabaseService.getLoader());
      article = DatabaseService.getLoader().loadArticle(metaId);
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
      return null;
    }
    if(article == null) return null;

    if(article.getMeta() == null
        || article.getMeta().getTitle() == null 
        || article.getContent() == null
        || article.getContent().getContent() == null) return  null;
    
    return article;
  }
  
  @SuppressWarnings("unused")
  protected String redirect(final HttpRequest request, 
                       final HttpResponse response,
                       final HttpContext context, String text, SearchQuery query) throws Exception {
    String contentType = "text/html";
    byte [] bytes = text.getBytes(Application.CHARSET);
    setOutputData(request, response, contentType, bytes);
    return contentType;
  }
  
  protected String crawlPage() {
    StringBuilder builder = new StringBuilder();
    builder.append("<html><head><title>Error...</title>");
    builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
    builder.append("</head><body>");
    builder.append("<h1>Bạn hiện đang truy cập quá nhanh.");
    builder.append(" Hãy đợi 5 giây sau rồi nhấn Refesh hoặc phím F5 để thử lại.</h1>");
    builder.append("</body></html>");
    return builder.toString();
  }

  /* 
  private String getQuery(String pattern) throws Exception {
    int idx = pattern.indexOf('=');
    if(idx < 0  || idx >= pattern.length()-1) return null;

    pattern = pattern.substring(idx+1);
    idx = pattern.lastIndexOf('&');
    if(idx > 0) pattern = pattern.substring(0, idx);

    return URLDecoder.decode(pattern, "UTF-8");
  }
   */
  
  public String load() {
//    MetaList metas = new MetaList("browse");
//    metas.setPageSize(15);
    int p = (int)(Math.random()*50);
    String page = String.valueOf(p);
//    try {
//      metas.setCurrentPage(p);
//    } catch (NumberFormatException e) {
//      metas.setCurrentPage(1);
//    }
    
    try {
      List<Header> lheaders = new ArrayList<Header>();
      lheaders.add(new BasicHeader("action", "load.metas.as.raw.text"));
      lheaders.add(new BasicHeader("page.size", "50"));
      lheaders.add(new BasicHeader("username", username));
      Header [] headers = lheaders.toArray(new Header[0]);
      
      byte [] bytes = client.post(URLPath.REMOTE_DATA_HANDLER, page.getBytes(), headers);
      bytes = new GZipIO().unzip(bytes);
      return new String(bytes, Application.CHARSET);
//      int idx = text.indexOf('\n');
//      
//      if(idx > 0) {
//        try {
//          metas.setTotalPage(Integer.parseInt(text.substring(0, idx)));
//        } catch (Exception e) {
//        }
//        
//        text = text.substring(idx + 1);
//      }
//      
//      String [] elements = text.split(Article.RAW_TEXT_ARTICLE_SEPARATOR);
//      for(int i = 0; i < elements.length; i++) {
//        Meta meta = null;
//        if(elements[i].indexOf(ContentMapper.SEPARATOR) > -1) {
//          Article article = new Article();
//          ContentMapper.text2MetaData(article, elements[i]);
//          meta = article.getMeta();
//        } else {
//          meta = ContentMapper.text2Meta(elements[i]);
//        }
//        if(meta == null) continue;
////        System.out.println(" bebebe ===  >" + meta.getId() + " : "+ meta);
//        Article article = new Article();
//        article.setMeta(meta);
//        metas.getData().add(article);
//      }
//      return metas;
      
//      MetaList metaList = client.readFromXML(MetaList.class, 
//          URLPath.REMOTE_DATA_HANDLER, page.getBytes(), headers);
//      return metaList;
    } catch (Exception e) {
      log.setMessage(e, "Load " + page + " error: "+e.toString());
    } 

//    try {
//      LastAccessData.getInstance().loadArticles(metas);
//    } catch (Exception e) {
//      log.setMessage(e, "Load " + page + " error: "+e.toString());
//    } 
    return null;
  }
  
}
