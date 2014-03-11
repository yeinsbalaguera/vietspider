/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.bean.Content;
import org.vietspider.bean.IDGenerator;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;
import org.vietspider.index.ClassifiedSearchQuery;
import org.vietspider.index.SearchQuery;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.nlp.NlpProcessor;
import org.vietspider.webui.cms.RequestUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
//sửa máy tính tu khoa test spam
public class UserPostHandler extends SearchHandler<String> {

  private TopMetasHandler topHandler;
  private DataClientService crawler;
  private String crawlerUsername;

  public UserPostHandler(TopMetasHandler topHandler) {
    super("site"); 
    name = "user"; 
    this.topHandler = topHandler;
    Regions.getInstance();
    
    String remoteCrawler = SystemProperties.getInstance().getValue("remote.crawler.address");
    crawlerUsername = SystemProperties.getInstance().getValue("remote.crawler.username");
    if(remoteCrawler != null && !remoteCrawler.trim().isEmpty()) {
      crawler = new DataClientService(remoteCrawler);
    }
  }

  public String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {

    Header headerAgent = request.getFirstHeader("User-Agent");
    if(RequestUtils.isBot(headerAgent)) {
      return topHandler.handle(request, response, context, params);
    }

    Header headerCookie = request.getFirstHeader("Cookie");
    long session = RequestUtils.getSession(headerCookie);
    if(headerAgent == null) {
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

    ClassifiedSearchQuery query = new ClassifiedSearchQuery("");

    String text = "";
    if(request instanceof BasicHttpEntityEnclosingRequest) {
      BasicHttpEntityEnclosingRequest basicRequest = (BasicHttpEntityEnclosingRequest)request;
      ByteArrayOutputStream arrayOutput = RWData.getInstance().loadInputStream(basicRequest.getEntity().getContent());
      text = new String(arrayOutput.toByteArray(), Application.CHARSET);
      int start = text.indexOf('=');
      int end = text.indexOf('&', start+1);
      //    System.out.println(text);
      //    System.out.println(start +  " : "+ end);
      if(start < 0 || end < 0) {
        text = "";
      } else {
        text = text.substring(start + 1, end).trim();
        text = URLDecoder.decode(text, Application.CHARSET);
      }

      if(text.length() > 0) {
        Article article = createArticle(text);
        if(article == null) {
          return write(request, response, context, text, query);
        }
        
//        File file = UtilFile.getFile("track/temp/user/", fileName);
//        org.vietspider.common.io.RWData.getInstance().save(file, text.getBytes(Application.CHARSET));
        
//        DatabaseService.getSaver().save(article);
        postArticleToCrawler(article); 
        
        LogService.getInstance().setMessage(null, "User post " + article.getId() + " to server.");
        
//        System.out.println("===== >  chay thu "+ DatabaseService.getSaver());

        return redirect(request, response, context, buildRedirect(), query);
      }
    }
    //    System.out.println(text);
    return write(request, response, context, text, query);
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, String text, String cookies[], SearchQuery query) throws Exception {
    UserPostRendererImpl render = new UserPostRendererImpl();
    try {
      render.write(output, text, query);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return "text/html";
  }

  private Article createArticle(String text) throws Exception {
    Article article = new Article();

    Calendar calendar = Calendar.getInstance();
    IDGenerator idGenerator = IDGenerator.currentInstance();
    if(idGenerator == null) {
      IDGenerator.createIDGenerator();
      idGenerator = IDGenerator.currentInstance();
    }
    String id = idGenerator.generateId(90, calendar);
    if(id == null) return null;
    Meta meta = new Meta();
    meta.setId(id);
    meta.setCalendar(calendar);
    meta.setTime(CalendarUtils.getDateTimeFormat().format(calendar.getTime()));

    NlpProcessor processor = NlpProcessor.getProcessor();
    Map<Short, Collection<?>> map = processor.process(meta.getId(), text);

    NLPRecord record = NlpHandler.process(meta, map);
    String action_object = meta.getPropertyValue("action_object");
    if(action_object == null) return null;
//    if(!action_object.startsWith("3,") 
//        && !action_object.startsWith("5,")) return null;

    StringBuilder builder = new StringBuilder();
    builder.append(NLPData.action_object(action_object));

    Collection<?> _values = map.get(NLPData.ADDRESS);
    if(_values != null && _values.size() > 0) {
      builder.append(' ').append(_values.iterator().next().toString());
    }

    meta.setTitle(builder.toString());

    _values = map.get(NLPData.EMAIL);

    if(_values != null && _values.size() > 0) {
      builder.append(". Liên hệ:");
      builder.append(_values.iterator().next().toString());
    }

    meta.setDesc(builder.toString());
    meta.setDomain("nik.vn");
    meta.setSource("http://nik.vn");
    article.setMeta(meta);

    Content content = new Content();
    builder.setLength(0);
    TextSpliter spliter = new TextSpliter();
    List<String> elements = spliter.toList(text, '\n');
    for(String ele : elements) {
      if(builder.length() > 0) builder.append("<br/>");
      builder.append("<p>").append(ele).append("</p>");
    }
    content.setContent(builder.toString());

    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    if(meta.getTime() != null) {
      content.setDate(dateFormat.format(calendar.getTime()));
    }
    content.setMeta(meta.getId());
    article.setContent(content);

    article.setNlpRecord(record);

    article.setStatus(Article.USER);

    return article;
  }

  public String buildRedirect() throws Exception {
    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append("<head>");
    builder.append("<meta http-equiv=\"Refresh\" content=\"10;URL=/\">");
    builder.append("</head>");
    builder.append("<body> Đã lưu nội dung thành công!</body>");
    builder.append("</html>");
    return builder.toString();
  }
  
  private void postArticleToCrawler(Article article) {
    ArticleCollection dataCollection = new ArticleCollection();
    dataCollection.get().add(article);
    article.setSaveType(Article.SAVE_NEW);
    
    List<Header> lheaders = new ArrayList<Header>();
    lheaders.add(new BasicHeader("action", "add.article"));
    lheaders.add(new BasicHeader("username", crawlerUsername));
    Header [] headers = lheaders.toArray(new Header[0]);
    try {
      crawler.postAsXML(URLPath.REMOTE_DATA_HANDLER, dataCollection, headers);
//      System.out.println(" da post duoc "+ article.getId());
//      return crawler.readAsObject(URLPath.REMOTE_DATA_HANDLER, metaId.getBytes(), headers);
    } catch (HttpHostConnectException e) {
      LogService.getInstance().setMessage(e, e.toString());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}

