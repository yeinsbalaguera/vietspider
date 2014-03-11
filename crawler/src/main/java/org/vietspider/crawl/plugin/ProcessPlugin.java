/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Content.BigSizeException;
import org.vietspider.bean.Domain;
import org.vietspider.bean.IDGenerator;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.HTMLDataExtractor;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.image.DocumentImageDownloader;
import org.vietspider.crawl.image.DocumentImageHandler;
import org.vietspider.crawl.image.ImageHandler;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.crawl.plugin.desc.DescExtractor;
import org.vietspider.crawl.plugin.desc.DescPathExtractor;
import org.vietspider.crawl.plugin.desc.TitleAutoExtractor;
import org.vietspider.crawl.plugin.desc.TitleExtractor;
import org.vietspider.crawl.plugin.desc.TitlePathExtractor;
import org.vietspider.crawl.plugin.handler.CompleteDataHandler;
import org.vietspider.crawl.plugin.handler.ContentMetaHandler;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.document.util.ImageDescRemover;
import org.vietspider.document.util.OtherLinkRemover2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.checker.LinkNodeChecker;
import org.vietspider.html.util.HTMLNodeUtil;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public abstract class ProcessPlugin {

  protected Worker<String, Link> worker;
  protected DescExtractor descExtractor;
  protected TitleExtractor titleExtractor;

  protected OtherLinkRemover2 linkRemover; 
  protected ImageDescRemover imageDescRemover;
  protected ContentMetaHandler contentMetaHandler;

  protected CompleteDataHandler completeDataHandler;
  protected DocumentFormatCleaner documentFormatCleaner;
  protected ImageHandler imageLoader;

  protected boolean nlp = false;

  public ProcessPlugin() {
    linkRemover = new OtherLinkRemover2();
    imageDescRemover = new ImageDescRemover();
    contentMetaHandler = new ContentMetaHandler();

    documentFormatCleaner = new DocumentFormatCleaner();

    URLUtils urlUtils = new URLUtils();
    HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
    completeDataHandler = new CompleteDataHandler(hyperLinkUtil, urlUtils);
  }

  public void setWorker(Worker<String, Link> worker) { 
    this.worker = worker;
  } 

  public void setProcessRegion(Source source) {
    //CONTENT FILTER
    if(source == null) return;

    Region [] regions = source.getProcessRegion();

    NodeHandler nodeHandler = new NodeHandler();
    HTMLDataExtractor extractor = worker.getResource(HTMLDataExtractor.class);

    if(imageLoader != null) imageLoader.abort();

    WebClient webClient = worker.getExecutor().getResource(WebClient.class);
    if(webClient == null) return ;
    Group group = worker.getExecutor().getResource(Group.class);
    if(group.isDownloadImage()) {
      imageLoader = new DocumentImageDownloader(webClient); 
    } else {
      imageLoader = new DocumentImageHandler(webClient, extractor);
    }

    titleExtractor = null;
    descExtractor = null;
    //    ArticleDescExtractor descExtractor = worker.getResource(ArticleDescExtractor.class);
    //    descExtractor.setDescPath(null);
    if(regions != null) {
      NodePathParser pathParser = new NodePathParser();
      for(Region path : regions) {
        if(path == null || !path.hasData() ) continue;
        if(path.getName().indexOf(Region.DATA_TITLE) > -1) {
          try {
            NodePath [] paths = pathParser.toNodePath(path.getPaths());
            if(paths != null && paths.length > 0) {
              titleExtractor = new TitlePathExtractor(extractor, nodeHandler, paths);
            }
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
        } else if(path.getName().indexOf(Region.DATA_DESCRIPTION) > -1) {
          try {
            NodePath [] paths = pathParser.toNodePath(path.getPaths());
            descExtractor = new DescPathExtractor(extractor, nodeHandler, paths);
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
        } else if(path.getName().indexOf(Region.DATA_IMAGE) > -1) {
          try {
            imageLoader.setNodePaths(pathParser.toNodePath(path.getPaths()));
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
        }
      }
    }

    if(titleExtractor == null) {
      titleExtractor = new TitleAutoExtractor(extractor, nodeHandler);
    }

    if(descExtractor == null) {
      descExtractor = new DescAutoExtractor2(extractor, nodeHandler);
    }
  }  

  protected boolean saveData(PluginData data) throws Exception {
    Link link  = data.getLink();
    Article article = data.getArticle();
    Meta meta = article.getMeta();
    HTMLNode root = link.<HTMLDocument>getDocument().getRoot();
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return false;
    
    if(link.getTitleId() != null) {
      try {
        if(PageDownloadedTracker.searchTitle(link, true)) {
          LinkLogIO.saveLinkLog(link, "{article.duplicate.title}", LinkLog.PHASE_PLUGIN);
          //          if(source.isDebug()) {
          //            String address = link.getAddress();
          //            LogSource.getInstance().setMessage(source, null,  address + ": duplecate title."); 
          //          }
          return false;
        }
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        //        } else {
        LogService.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        //        }
        return false;
      }
      
      try {
        PageDownloadedTracker.saveTitle(link);
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        //        if(source.isDebug()) {
        //          LogSource.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        //        } else {
        LogService.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
        //        }
        return false;
      }
    }
    
    documentFormatCleaner.handle(root);
    completeDataHandler.completeURL(source.getHome()[0], link, meta);
    if(!root.hasChildren()) {
      LinkLogIO.saveLinkLog(link, "{document.is.empty}", LinkLog.PHASE_PLUGIN);
      //      if(source.isDebug()) {
      //        String address = link.getAddress();
      //        LogSource.getInstance().setMessage(source, null,  address + ": html data is empty (root is empty)."); 
      //      }
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
      //      if(source.isDebug()) {
      //        LogSource.getInstance().setMessage(source, e, link.getAddress() + " " + e.getMessage());
      //      } else {
      LogService.getInstance().setMessage(source, e, link.getAddress() + " " + e.getMessage());
      //      }
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
    
    if(nlp) {
      //new implement nlp
//      NLPRecord nlpRecord = NLPExtractor.getInstance().extract(meta, domain, content);
      try {
        article.setNlpRecord(NlpHandler.process(meta, root));
      } catch (BigSizeException e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(source, e, link.getAddress() + " " + e.getMessage());
      }
    }

    return saveDataToDatabase(data);
  }

  public boolean saveDataToDatabase(PluginData data) throws Exception {
    Article article = data.getArticle();
    //save to databse
    try {
      DatabaseService.getSaver().save(article);
      
//      public void saveImageToDatabase() {
//      LinkedList<Image> images = data.getArticle().getImages();
//      while(!images.isEmpty()) {
//        Image image = images.poll();
//        try {
//          DatabaseService.getSaver().save(image);
//        }catch (Exception e) {
//          LogService.getInstance().setThrowable(e);
//        }
//      }
       
//      }
      
//      if(imageLoader != null) imageLoader.saveImageToDatabase();
      
    } catch (SQLException e) {
      Link link = data.getLink();
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e, address);

      try {
        DatabaseService.getDelete().deleteArticle(article.getMeta().getId());
      } catch (Exception e2) {
        LinkLogIO.saveLinkLog(link, e2, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(data.getLink().getSourceFullName(), e2);
      }

      return false;
    } catch (Exception e) {
      Link link = data.getLink();
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      Exception e1 = new Exception(address + " " + e.toString());
      e1.setStackTrace(e.getStackTrace());
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e1, address);
      try {
        DatabaseService.getDelete().deleteArticle(article.getMeta().getId());
      } catch (Exception e2) {
        LinkLogIO.saveLinkLog(link, e2, LinkLog.PHASE_PLUGIN);
      }
      return false;
    }

    //save id tracker
    try {
      EIDFolder2.write(article.getDomain(), article.getMeta().getId(), Article.WAIT);
    } catch (Exception e) {
      Link link = data.getLink();
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      LogService.getInstance().setThrowable(link.getSourceFullName(), e);
    }
    
    try {
      LinkLogIO.saveLinkLog(data.getLink(), "done", LinkLog.PHASE_PLUGIN, article.getId());
//      if(source != null) {
      Source source = CrawlingSources.getInstance().getSource(data.getLink().getSourceFullName());
      
      SessionStore store = SessionStores.getStore(source.getCodeName());
      if(store != null) store.doneLink();
//      }
    } catch (Exception e) {
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e, data.getLink().getAddress());
    }

    return true;
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

  public PluginData createPluginData(Link link, int index)  {
    Meta meta  = new Meta();
    Calendar calendar = Calendar.getInstance();
    int executorId = worker.getExecutor().getId();
    String id = IDGenerator.currentInstance().generateId(executorId, calendar);
    if(id == null) return null;
    meta.setId(id);
    meta.setCalendar(calendar);
    meta.putProperty("temp.url.code", link.getUrlId().toString());

    Group group = worker.getExecutor().getResource(Group.class);

    if(index > -1) {
      String address = link.getAddress();
      if(address.indexOf('?') > -1) {
        link.setAddress(link.getAddress() + "&vsid=" + String.valueOf(index));
      } else {
        link.setAddress(link.getAddress() + "?" + String.valueOf(index));
      }
    }

    return new PluginData(link, meta, group);
  }

  public PluginData createPluginData(Link link, Article article)  {
    Meta meta  = article.getMeta();
    Calendar calendar = Calendar.getInstance();
    int executorId = worker.getExecutor().getId();
    String id = IDGenerator.currentInstance().generateId(executorId, calendar);
    if(id == null) return null;
    meta.setId(id);
    meta.setCalendar(calendar);

    Content content = article.getContent();
    content.setMeta(id);

    Group group = worker.getExecutor().getResource(Group.class);

    PluginData pluginData = new PluginData(link, article, group);
    try {
      HTMLDocument document =  new HTMLParser2().createDocument(content.getContent());
      HTMLNode root = document.getRoot();
      List<HTMLNode> textNodes = searchTextNodes(root);

      NodeImpl nodeImpl = new NodeImpl(meta.getDesc().toCharArray(), Name.CONTENT);
      textNodes.add(0, nodeImpl);
      nodeImpl = new NodeImpl(meta.getTitle().toCharArray(), Name.CONTENT);
      textNodes.add(0, nodeImpl);

      pluginData.setTextNodes(textNodes);

      if(imageLoader instanceof DocumentImageDownloader) {
        meta.setImage(null);
        //download images
        HTMLNodeUtil nodeUtil = new HTMLNodeUtil();
        List<HTMLNode> imageNodes = nodeUtil.search(root, Name.IMG);
        //      List<HTMLNode> imageNodes = removeDescImage.getImageNodes();
        if(!imageLoader.download(root, imageNodes, pluginData)) return null;

        StringBuilder buildContent = new StringBuilder();
        List<HTMLNode> children  = root.getChildren();
        for(HTMLNode ele : children) {
          ele.buildValue(buildContent);
        }

        content.setContent(buildContent.toString());
      }
    } catch (Exception e) {
      return null;
    }

    return pluginData;
  }

  public boolean checkDownloaded(Link link, boolean downloaded) {
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return true;
    if(source.isRedown()) return false;

    if(!downloaded) {
      if(source.getExtractType() != ExtractType.NORMAL) return false;
    }

    try {
      //    System.out.println(" chuan bi search "+ link.getAddress() + " : "+ link.getAddressCode());
      return PageDownloadedTracker.searchUrl(link, true);
    } catch (Throwable e1) {
      LogService.getInstance().setThrowable(source, e1);
      //      worker.getExecutor().abortSession();
      return true;
    }
  }

  public abstract void handle(PluginData href) throws Throwable;

  public void handleSuccessfullData(PluginData pluginData) throws Throwable {
    PageDownloadedTracker.saveUrl(pluginData.getLink());

//    if(pluginData.getGroup().getType().equals(Group.ARTICLE)) {
//      TpServices tpServices = TpServices.getInstance();
//      if(tpServices != null) {
//        PluginData2TpDocument converter = new PluginData2TpDocument();
//        TpWorkingData workingData = converter.convert(pluginData);
//        if(workingData != null) tpServices.save(workingData);
//      }
//    }

//    ContentIndex contentIndex = PluginData2TpDocument.toIndexContent(pluginData);
//    if(contentIndex != null) ContentIndexers.getInstance().index(contentIndex);

    //    DbIndexerService dbIndex = DbIndexerService.getInstance();
    //    if(dbIndex != null) dbIndex.getDbIndexEntry().save(pluginData);

//    Link link = pluginData.getLink();
//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    Meta meta = pluginData.getArticle().getMeta();
//    Source cloneSource = link.getSource().clone();
//    cloneSource.setCategory(pluginData.getArticle().getDomain().getCategory());
//    System.out.println(" ==== source cate ===>  "+ link.getSource().getCategory());
//    System.out.println(" ==== clone cate ===>  "+ cloneSource.getCategory());
//    logSaver.updateTotalLinkAndData(cloneSource, meta.getCalendar(), 0, 1);

    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    if(sourceLog != null) sourceLog.increaseTotalSavedDataSuccessfull();
  }

  public void endSession() {
  }

  protected void cleanImageDesc(HTMLNode root, List<HTMLNode> contents) {
    List<HTMLNode> nodes = imageDescRemover.removeDesc(root);
    for(int i = 0; i < nodes.size(); i++) {
      contents.remove(nodes.get(i));
    }
  }

  protected void cleanLink(HTMLNode root, List<HTMLNode> contents, String url) {    
    List<HTMLNode> nodes = linkRemover.removeLinks(root, new LinkNodeChecker(url, 0));
    for(int i = 0; i < nodes.size(); i++) {
      contents.remove(nodes.get(i));
    }
  }

  public DescExtractor getDescExtractor() { return descExtractor; }

  public Worker<String, Link> getWorker() { return worker; }

  @SuppressWarnings("unused")
  public boolean isValidSize(Link link, HttpResponse response) { return true; }
  @SuppressWarnings("unused")
  public boolean isValidSize(Link link, byte[] data) { return true; }
  @SuppressWarnings("unused")
  public void saveLinkSize(Link link) { }

  public void abort() { 
    if(imageLoader != null) imageLoader.abort();
  }

}
