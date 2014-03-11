/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Content.BigSizeException;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.content.db.article.ArticlePluginSaver;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.HTMLNodeUtil;
import org.vietspider.html.util.HTMLText;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.link.ContentFilters;
import org.vietspider.model.Source;
import org.vietspider.pool.Worker;
import org.vietspider.tags.Tags;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public final class ArticleProcessPlugin extends ProcessPlugin {

  //  private RemoveDescImage removeDescImage;
  private ViDateTimeExtractor timeExtractor;

  @Override
  public void setWorker(Worker<String, Link> worker) {
    super.setWorker(worker);

    timeExtractor = new ViDateTimeExtractor();

    if(descExtractor instanceof DescAutoExtractor2) {
      DescAutoExtractor2 descAutoExtractor = (DescAutoExtractor2) descExtractor;
      descAutoExtractor.setMinDesc(10);
      descAutoExtractor.setMinDesc(20);
    }
  }

  @Override
  public void handle(final PluginData pluginData)  throws Throwable {
    if(pluginData == null) return;
    Link link = pluginData.getLink();

    HTMLDocument document = link.getDocument();
    HTMLNode root = document.getRoot();

    List<HTMLNode> textNodes = searchTextNodes(root);
    if(textNodes == null) {
      LinkLogIO.saveLinkLog(link, "{text.nodes.not.found}", LinkLog.PHASE_PLUGIN);
      return;
    }
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;

    //CONTENT FILTER
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) {
      LinkLogIO.saveLinkLog(link, "{session.store.not.found}", LinkLog.PHASE_PLUGIN);
      return;
    }

    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(textNodes)) {
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      LinkLogIO.saveLinkLog(link, "{stop.by.content.filter}", LinkLog.PHASE_PLUGIN);
      return;
    }

    //clean dirty data
    timeExtractor.removeDateTimeNode(textNodes, pluginData.getMeta());
    if(checkUpdateTime(pluginData)) {
      LinkLogIO.saveLinkLog(link, "{stop.by.data.update.time}", LinkLog.PHASE_PLUGIN);
      return;
    }

    pluginData.setTextNodes(textNodes);


    Tags tags = Tags.getInstance("kinh-te");
    if(tags.hasTag()) {
      Article article = pluginData.getArticle();
      String tag = tags.tag(article.getId(), textNodes);
      pluginData.setTag(tag);
      //      System.out.println(" ===  > "+ pluginData.getArticle().getId() + " : "+ tag);
      if(tag != null) {
        Meta meta = article.getMeta();
        Date dateInstance = meta.getCalendar().getTime();
        meta.setTime(CalendarUtils.getDateTimeFormat().format(dateInstance));
        String date = CalendarUtils.getDateFormat().format(dateInstance);

        Domain domain = new Domain(date, source.getGroup(), tag, source.getName());
        meta.setDomain(domain.getId());
        article.setDomain(domain);
        //        System.out.println(" chay thu " + tag);
      }
    }


    cleanImageDesc(root, textNodes);
    cleanLink(root, textNodes, link.getAddress());

    Meta meta = pluginData.getMeta();
    //extract title
    meta.setTitle(titleExtractor.extract(root, textNodes));
    //    if(!processTitle(pluginData)) return;

    meta.setDesc(descExtractor.extract(root, textNodes));
    meta.putProperty("desc.extractor.remove", String.valueOf(descExtractor.isRemove()));

    StringBuilder builder = new StringBuilder(meta.getTitle());
    builder.append(' ').append(meta.getDesc());
    link.setTitleId(MD5Hash.digest(builder.toString()));

    textNodes.clear();

    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(textNodes, root, verify);

    //download images
    HTMLNodeUtil nodeUtil = new HTMLNodeUtil();
    List<HTMLNode> imageNodes = nodeUtil.search(root, Name.IMG);
    if(!imageLoader.download(root, imageNodes, pluginData)) return;

    //CONTENT FILTER
    if(contentFilters != null) contentFilters.mark(textNodes);

    //save data
    saveData(pluginData);

    //    if(saveData(pluginData)) handleSuccessfullData(pluginData);
  }

  public boolean saveData(PluginData data) throws Exception {
    Link link  = data.getLink();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    Article article = data.getArticle();
    Meta meta = article.getMeta();
    HTMLNode root = link.<HTMLDocument>getDocument().getRoot();

    if(link.getTitleId() != null) {
      try {
        if(PageDownloadedTracker.searchTitle(link, true)) {
          LinkLogIO.saveLinkLog(link, "{article.duplicate.title}", LinkLog.PHASE_PLUGIN);
          return false;
        }
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        return false;
      }

      try {
        PageDownloadedTracker.saveTitle(link);
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        return false;
      }
    }
    
    
    Tags tags = Tags.getInstance("kinh-te");
    if(tags.hasTag() && data.getTag() == null) {
      LogService.getInstance().setMessage(null, "Ignore article:" +  meta.getTitle());
//      LogService.getInstance().setMessage(null, data.getLink().getAddress());
      try {
        PageDownloadedTracker.saveUrl(data.getLink());
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
      return true;
    }

    documentFormatCleaner.handle(root);
    completeDataHandler.completeURL(source.getHome()[0], link, meta);
    if(!root.hasChildren()) {
      LinkLogIO.saveLinkLog(link, "{document.is.empty}", LinkLog.PHASE_PLUGIN);
      return false;
    }

    Date dateInstance = meta.getCalendar().getTime();
    meta.setTime(CalendarUtils.getDateTimeFormat().format(dateInstance));
    String date = CalendarUtils.getDateFormat().format(dateInstance);

    Domain domain = article.getDomain();
    if(domain == null) {
      domain = new Domain(date, source.getGroup(), source.getCategory(), source.getName());
      meta.setDomain(domain.getId());
    }

    Content content = null;
    try {
      content = contentMetaHandler.buildContent(root.getChildren(), meta, date);
    } catch (BigSizeException e) {
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      LogService.getInstance().setMessage(source, e, link.getAddress() + " " + e.getMessage());
      return false;
    }

    contentMetaHandler.cutMetaData(meta, content);

    article.setContent(content);
    article.setDomain(domain);

    List<HTMLNode> nodes = data.getCloneTextNodes();
    if(nodes != null) {
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < nodes.size(); i++) {
        if(builder.length() > 0) builder.append('\n');
        builder.append(nodes.get(i).getValue());
      }
      meta.putProperty("temp.text", builder.toString());
    }

    ArticlePluginSaver.getInstance().add(data);

    try {
      PageDownloadedTracker.saveUrl(data.getLink());
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    if(sourceLog != null) sourceLog.increaseTotalSavedDataSuccessfull();

    return true;
  }

  private boolean checkUpdateTime(PluginData pluginData) {
    Meta meta  = pluginData.getMeta();

    if(CrawlerConfig.LIMIT_DATE < 1 || meta.getSourceTime() == null)  return false;

    Date date = null;
    try {
      date = CalendarUtils.getDateTimeFormat().parse(meta.getSourceTime());
    }catch (ParseException e) {
      LogService.getInstance().setMessage(pluginData.getLink(), e, null);
    }

    Calendar calendar = meta.getCalendar();
    if(date != null && calendar.getTimeInMillis() - date.getTime() > CrawlerConfig.LIMIT_DATE) {
      try {
        PageDownloadedTracker.saveUrl(pluginData.getLink());
      } catch (Throwable e1) {
        LogService.getInstance().setThrowable(pluginData.getLink().getSourceFullName(), e1);
        worker.getExecutor().endSession(true);
      }
      return true;
    }

    return false;
  }

  @Override()
  public List<HTMLNode> searchTextNodes(HTMLNode node) {
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();

    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);

    return contents.size() < 2 ? null : contents;
  }

}
