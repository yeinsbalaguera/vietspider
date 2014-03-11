/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.crawl.plugin.handler.DocumentHandler2;
import org.vietspider.crawl.plugin.handler.XMLResourceDownloader2;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.handler.XMLHandler3.Resource;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.link.ContentFilters;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public final class DocumentProcessPlugin extends ProcessPlugin {

  private DocumentHandler2 documentHandler;
  private XMLResourceDownloader2 downloader;

  @Override
  public void setWorker(Worker<String, Link> worker) {
    super.setWorker(worker);

    if(descExtractor instanceof DescAutoExtractor2) {
      DescAutoExtractor2 descAutoExtractor = (DescAutoExtractor2) descExtractor;
      descAutoExtractor.setMinDesc(50);
      descAutoExtractor.setMinDesc(100);
    }
  }

  public void setProcessRegion(Source source) {
    documentHandler = new DocumentHandler2(source);

    WebClient webClient = worker.getExecutor().getResource(WebClient.class);
    if(webClient == null) return ;
    downloader = new XMLResourceDownloader2(webClient);
  }

  @Override
  public void handle(final PluginData pluginData) throws Throwable {
    if(pluginData == null) return ;
    Link link = pluginData.getLink();

    HTMLDocument document = link.<HTMLDocument>getDocument();
    if(document == null) return ;

    HTMLNode root = document.getRoot();

    //search text nodes
    List<HTMLNode> textNodes = searchTextNodes(root);
    if(textNodes == null) return ;
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;

    // CONTENT FILTER
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;

    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(textNodes)) {
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      return ;
    }

    String xmlContent = documentHandler.handle(pluginData); 
    if(xmlContent == null) return;
    
    Meta meta = pluginData.getMeta();
    if((meta.getTitle() == null || meta.getTitle().startsWith("default title"))
        && (meta.getDesc() == null || meta.getDesc().startsWith("empty description"))) {
      LogService.getInstance().setMessage(null, "Ignore "+ pluginData.getMeta().getSource() +" - Error: Empty data!");
      return;
    }

    List<Resource> resourceNodes = documentHandler.getResources();
    downloader.download(resourceNodes, pluginData);

    //CONTENT FILTER
    if(contentFilters != null) contentFilters.mark(textNodes);

    //save data
    if(saveData(pluginData, xmlContent)) handleSuccessfullData(pluginData);
  }

  protected final boolean saveData(PluginData pluginData, String xmlContent) throws Exception {
    Link link  = pluginData.getLink();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return false;
    Article article = pluginData.getArticle();
    Meta meta = article.getMeta();
    Calendar calendar = meta.getCalendar();
    HTMLNode root = link.<HTMLDocument>getDocument().getRoot();

    documentFormatCleaner.handle(root);
    completeDataHandler.completeURL(source.getHome()[0], link, meta);
    if(!root.hasChildren()) return false;

    meta.setTime(CalendarUtils.getDateTimeFormat().format(calendar.getTime()));
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());

    Domain domain = new Domain(date, source.getGroup(), source.getCategory(), source.getName());
    meta.setDomain(domain.getId());

    Content content = new Content(meta.getId(), date, xmlContent);
    contentMetaHandler.cutMetaData(meta, content);

    article.setContent(content);
    article.setDomain(domain);
    //save to databse
    try {
      DatabaseService.getSaver().save(article);
//      LinkedList<Image> images = pluginData.getArticle().getImages();
//      while(!images.isEmpty()) {
//        Image image = images.poll();
//        try {
//          DatabaseService.getSaver().save(image);
//        }catch (Exception e) {
//          LogService.getInstance().setThrowable(e);
//        }
//      }
    } catch (SQLException e) {
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      LogService.getInstance().setMessage(link.getSourceFullName(), e, address);
      try {
        DatabaseService.getDelete().deleteArticle(meta.getId());
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(link.getSourceFullName(), e2);
      }
      return false;
    } catch (Exception e) {
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      Exception e1 = new Exception(address + " " + e.toString());
      e1.setStackTrace(e.getStackTrace());
      LogService.getInstance().setThrowable(link.getSourceFullName(), e1);
      try {
        DatabaseService.getDelete().deleteArticle(meta.getId());
      } catch (Exception e2) {
        LogService.getInstance().setThrowable(link.getSourceFullName(), e2);
      }
      return false;
    }

    //save id tracker
    try {
      EIDFolder2.write(domain, meta.getId(), Article.WAIT);

      //      IDTracker.getInstance().append(new EntryID(domain, meta.getId()));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(link.getSourceFullName(), e);
    }

    return true;
  }
  
  public void abort() { 
    if(imageLoader != null) imageLoader.abort();
    downloader.abort();
  }


}
