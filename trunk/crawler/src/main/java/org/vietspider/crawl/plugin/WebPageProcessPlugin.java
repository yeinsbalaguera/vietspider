/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
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
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.HTMLDataExtractor;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.handler.MergeTextNode;
import org.vietspider.crawl.plugin.handler.WebPageMetaExtractor;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.js.JsHandler;
import org.vietspider.html.parser.HTMLTokenUtils;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.link.ContentFilters;
import org.vietspider.model.Source;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public final class WebPageProcessPlugin extends ProcessPlugin {
  
  private WebPageMetaExtractor metaExtractor;
//  private RemoveDescImage removeDescImage;
  private MergeTextNode mergeTextNode;
  
  @SuppressWarnings("unchecked")
  public WebPageProcessPlugin(Worker<?, Link> worker, Source source) {
    setWorker((Worker<String, Link>)worker);
    worker.putResource("WEBPAGE", this);
    setProcessRegion(source);
    
    HTMLDataExtractor extractor = worker.getResource(HTMLDataExtractor.class);
    ViDateTimeExtractor timeExtractor = new ViDateTimeExtractor();
    metaExtractor = new WebPageMetaExtractor(extractor, timeExtractor);
    
//    removeDescImage = new RemoveDescImage(nodeHandler);
    mergeTextNode = new MergeTextNode();
  }
 
  @Override
  public void handle(final PluginData pluginData) throws Throwable {
    if(pluginData == null) return ;
    Link link = pluginData.getLink();
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());

    HTMLDocument document = link.<HTMLDocument>getDocument();
    List<String> jsDocWriters = source.getJsDocWriters();
    if(jsDocWriters.size() > 0) {
      try {
        JsHandler.updateDocument(document, jsDocWriters);
      } catch(Exception exp) {
        LogService.getInstance().setThrowable(link.getSourceFullName(), exp);
      }
    }
    
    HTMLNode root = document.getRoot();
    List<HTMLNode> contentNodes = metaExtractor.searchContents(root);
    if(contentNodes == null || contentNodes.size() < 1) return ;
    
    mergeTextNode.mergeText(contentNodes);
    
    //CONTENT FILTER
    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(contentNodes)) {
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      return ;
    }
    
    pluginData.setTextNodes(contentNodes);
    
    cleanImageDesc(root, contentNodes);
    cleanLink(root, contentNodes, link.getAddress());
//    removeDescImage.removeDescImageNode(root, contentNodes);
    
    String desc = metaExtractor.extractDesc(contentNodes);
    pluginData.getMeta().setDesc(desc);
    
    String title  = metaExtractor.extractTitle(root, contentNodes);
    pluginData.getMeta().setTitle(title);
    
   /* if(pluginData.getGroup().isCheckTitle()) {
      StringBuilder builderCode = new StringBuilder(source.getName()).append('.').append(title); 
      int titleCode = builderCode.toString().hashCode();
      link.setTitleCode(titleCode);
      if(DownloadedTracker.search(source.getGroup(), titleCode, true)) return ;
    }*/
    
    List<NodeImpl> tokens = link.getTokens();
    if(tokens == null) return ;
    
    //CONTENT FILTER
    if(contentFilters != null) contentFilters.mark(contentNodes);
    
    pluginData.getMeta().setSource(completeDataHandler.completeURL(link.getNormalizeURL(), tokens));
    
    //save data
    if(saveData(pluginData, tokens)) handleSuccessfullData(pluginData);
    
    /*PageDownloadedTracker.saveUrl(pluginData.getLink());
    
    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
    logSaver.updateTotalLinkAndData(link.getSource(), pluginData.getMeta().getCalendar(), 0, 1);
    
    CrawlExecutor executor = (CrawlExecutor) worker.getExecutor();
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    sourceLog.increaseTotalSavedDataSuccessfull();

    //for indexing
    contentNodes = pluginData.getCloneTextNodes();
    if(contentNodes == null) return;
    
    Iterator<HTMLNode> iterator = contentNodes.iterator();
    while(iterator.hasNext()) {
      HTMLNode contentNode = iterator.next();
      if(metaExtractor.isLinkNode(contentNode, 0)) iterator.remove();
    }
    pluginData.setWebPage(true);
    
    TpDatabases tpDatabases = TpDatabases.getInstance();
    ContentIndex contentIndex = null;
    if(CrawlerConfig.INDEX_CONTENT) {
      contentIndex = PluginData2TpDocument.toIndexContent(pluginData);
    }
    if(tpDatabases != null) {
      tpDatabases.index(contentIndex);
      
      PluginData2TpDocument converter = new PluginData2TpDocument();
      TpWorkingData workingData = converter.convert(pluginData);
      if(workingData != null) tpDatabases.save(workingData);
    } else if(CrawlerConfig.INDEX_CONTENT) {
      DbIndexers.getInstance().index(contentIndex);
    }*/
  }
  
  public boolean isCheckTitle() { return false; }

  protected boolean saveData(PluginData pluginData, List<NodeImpl> tokens) throws Exception {
    Link link  = pluginData.getLink();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    Article article = pluginData.getArticle();
    Meta meta = article.getMeta();
    Calendar calendar = meta.getCalendar();
    HTMLNode root = link.<HTMLDocument>getDocument().getRoot();
    
    if(!root.hasChildren()) return false;
    
    meta.setTime(CalendarUtils.getDateTimeFormat().format(calendar.getTime()));
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    
    Domain domain = new Domain(date, source.getGroup(), source.getCategory(), source.getName());
    meta.setDomain(domain.getId());
    
    Content content = new Content(meta.getId(), date, HTMLTokenUtils.buildContent(tokens));
    
    if(meta.getTitle() == null || meta.getTitle().trim().isEmpty()) {
      meta.setTitle("...");
    }
    
    article.setContent(content);
    article.setDomain(domain);
    try {
      DatabaseService.getSaver().save(article);
//      LinkedList<Image> images = pluginData.getImages();
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
      return false;
    } catch (Exception e) {
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      LogService.getInstance().setMessage(link.getSourceFullName(), e, address);
    }
    
    //save id tracker
    try {
      EIDFolder2.write(article.getDomain(), article.getMeta().getId(), Article.WAIT);

      //      IDTracker.getInstance().append(new EntryID(domain, meta.getId()));
    } catch (Exception e) {
      LinkLogStorages.getInstance().save(source, e.toString());
//      if(source.isDebug()) {
//        LogSource.getInstance().setThrowable(source, e);
//      } else {
        LogService.getInstance().setThrowable(source, e);
//      }
    }
    
    return true;
  }
  
}
