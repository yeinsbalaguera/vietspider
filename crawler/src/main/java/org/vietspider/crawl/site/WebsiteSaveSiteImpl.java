/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.site;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.IDGenerator;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.WebPage2ProcessPlugin;
import org.vietspider.crawl.plugin.handler.CompleteDataHandler;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner;
import org.vietspider.crawl.plugin.handler.WebAutoExtractor;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.WebsiteSaveSiteService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 14, 2009  
 */
public class WebsiteSaveSiteImpl 

  extends WebPage2ProcessPlugin implements WebsiteSaveSiteService.WebsiteSaveSite {
  
  private final String CATEGORY = "AUTO_SAVE";
  private final String GROUP = "SITE";
  
  private CompleteDataHandler completeHandler;
//  private volatile URLCodeGenerator codeGenerator;
  private DocumentFormatCleaner docCleaner;
  
  public WebsiteSaveSiteImpl() {
    timeExtractor = new ViDateTimeExtractor();
    
    HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
    URLUtils urlCreator = new URLUtils();
    completeHandler = new CompleteDataHandler(hyperLinkUtil, urlCreator);
    
//    codeGenerator = new URLCodeGenerator();
    docCleaner = new DocumentFormatCleaner();
  }
  
  public void save(String address, String html) throws Exception {
    String lower = html.toLowerCase();
    if(!isVietnam(lower)) return;
    
    URL url = null;
    try {
      url = new URL(address);
    } catch (Exception e) {
//      LogService.getInstance().setMessage(e, "1. Get link: ");
      return ;
    }
    
    Link link = new Link(address, null);
    Source source = new Source();
    source.setGroup(GROUP);
    source.setCategory("Website");
    source.setName("Webpage");
    link.setSourceFullName(source.getFullName());
    
    try {
      if(PageDownloadedTracker.searchUrl(link, true)) return ;
    } catch (Throwable e1) {
      LogService.getInstance().setThrowable(source, e1);
      return;
    }
    
    HTMLDocument document = new HTMLParser2().createDocument(html);
    WebAutoExtractor autoExtractor = new WebAutoExtractor(timeExtractor);
    HTMLNode body = autoExtractor.extractData(document, address);
    if(body == null) return ;
    
    if(!isVietnam(body.getTextValue().toLowerCase())) return;
    
    List<HTMLNode> textNodes = searchTextNodes(body);
    if(textNodes == null) return ;
    
    if(textNodes == null || textNodes.size() < 1) return ;
    mergeTextNode.mergeText(textNodes);
    
    document = new HTMLDocument(body);
    
    Calendar calendar = Calendar.getInstance();
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    
    String host = url.getHost();
    String name = hostToName(host);
    Domain domain = new Domain(date, GROUP, CATEGORY, name);
    
    Meta meta = new Meta();
    
    int executorId = CrawlService.getInstance().getThreadPool().getExecutors().size();
    String id = IDGenerator.currentInstance().generateId(executorId, calendar);
    if(id == null) return;
    meta.setId(id);
    
    meta.setTime(CalendarUtils.getDateTimeFormat().format(calendar.getTime()));
    meta.setDomain(domain.getId());
    meta.setSource(address);
    
    meta.setTitle(autoExtractor.getTitle());
    meta.setDesc(autoExtractor.getDesc());
    
    completeHandler.completeURL(address, body);
    docCleaner.handle(body);
     
    Content content = contentMetaHandler.buildContent(body.getChildren(), meta, date);    
    contentMetaHandler.cutMetaData(meta, content);
    
    try {
      DatabaseService.getSaver().save(new Article(domain, meta, content));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(address, e);
      return;
    }
    
    //save id tracker
    try {
      EIDFolder2.write(domain, meta.getId(), Article.WAIT);
      
//      IDTracker.getInstance().append(new EntryID(domain, meta.getId()));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(address, e);
    }
    
    try {
      PageDownloadedTracker.saveUrl(link);
    } catch (Throwable e1) {
      LogService.getInstance().setThrowable(source, e1);
    }
  }
  
  public List<HTMLNode> searchTextNodes(HTMLNode node) {
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);
//    nodeHandler.searchTextNode(node, contents);
//    System.out.println("step 6 "+href.getUrl()+ " : "+ contentsNode.size());
    return contents.size() < 2 ? null : contents;
  }
  
  private String hostToName(String value) {
    int index = 0;
    StringBuilder builder = new StringBuilder();
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else {
        builder.append(' ');
      }
      index++;
    }
    return builder.toString();
  }
  
  private boolean isVietnam(String text) {
    return text.indexOf("vietnamese") > -1 
          || text.indexOf("vietnam") > -1
          || text.indexOf("viet nam") > -1
          || text.indexOf("ha noi") > -1
          || text.indexOf("ho chi minh") > -1
          || text.indexOf("nha trang") > -1
          || text.indexOf("ha long") > -1;
  }

}
