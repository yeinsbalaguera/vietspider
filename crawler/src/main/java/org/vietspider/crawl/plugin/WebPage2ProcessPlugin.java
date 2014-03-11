/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.handler.CompleteDataHandler;
import org.vietspider.crawl.plugin.handler.MergeTextNode;
import org.vietspider.crawl.plugin.handler.WebAutoExtractor;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.js.JsHandler;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.io.websites2.lang.TokenHtmlRenderer;
import org.vietspider.link.ContentFilters;
import org.vietspider.link.IPageChecker;
import org.vietspider.model.Source;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public class WebPage2ProcessPlugin extends ProcessPlugin {
  
//  private RemoveDescImage removeDescImage;
  protected MergeTextNode mergeTextNode;
  protected ViDateTimeExtractor timeExtractor;
  
  public WebPage2ProcessPlugin() {
    mergeTextNode = new MergeTextNode();
    
    URLUtils urlUtils = new URLUtils();
    HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
    completeDataHandler = new CompleteDataHandler(hyperLinkUtil, urlUtils) {
      
      public void completeURL(String addressHome, Link link, Meta meta) {
        URL home = null;
        try{
          home = new URL(addressHome);//link.getSource().getHome()[0]);  
        }catch(Exception exp){
          LogService.getInstance().setThrowable(link, exp);
        }
        if(home == null) return;

//        System.out.println("normal "+link.getNormalizeURL());
//        System.out.println("home "+ home.toString());
        String address = urlUtils.createURL(home, link.buildURL());
//        System.out.println("address "+address);
        meta.setSource(address);

        try {
          hyperLinkUtil.createFullNormalLink(link.getTokens(), new URL(address));
        }catch(Exception exp){
          LogService.getInstance().setThrowable(link, exp);
        }
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public WebPage2ProcessPlugin(Worker<?, Link> worker, Source source) {
    this();
    setWorker((Worker<String, Link>)worker);
    worker.putResource("WEBPAGE", this);
    setProcessRegion(source);
    
    timeExtractor = new ViDateTimeExtractor();
//    HTMLDataExtractor extractor = worker.getResource(HTMLDataExtractor.class);
//    ViDateTimeExtractor timeExtractor = new ViDateTimeExtractor(nodeHandler);
  }
 
  @Override
  public void handle(final PluginData pluginData) throws Throwable {
    if(pluginData == null) return;
    Link link = pluginData.getLink();
    CrawlingSession executor = worker.getExecutor();
    
    HTMLDocument document = link.<HTMLDocument>getDocument();
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;
    
    List<String> jsDocWriters = source.getJsDocWriters();
    if(jsDocWriters.size() > 0) {
      try {
        JsHandler.updateDocument(document, jsDocWriters);
      } catch(Exception exp) {
        LogService.getInstance().setThrowable(link.getSourceFullName(), exp);
      }
    }
    
    WebAutoExtractor autoExtractor = new WebAutoExtractor(timeExtractor);
    /*HTMLNode body = */autoExtractor.extractData(document, link.getAddress());
//    if(body == null) return ;
    
    List<NodeImpl> tokens = link.getTokens();
    if(tokens == null) return ;
    
    List<HTMLNode> textNodes = searchTextNodes(tokens);
    if(textNodes == null) return ;
    
    IPageChecker pageChecker = source.getPageChecker();
    if(pageChecker != null ) {
      if(!pageChecker.check(autoExtractor.getTextContent())) {
//        System.out.println(link.getAddress());
        return ; 
      }
    }

    if(textNodes == null || textNodes.size() < 1) return ;
    mergeTextNode.mergeText(textNodes);
    
    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(textNodes)) {
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      return ;
    }
    
//    document = new HTMLDocument(body);
    pluginData.getLink().setDocument(document);
    pluginData.setTextNodes(textNodes);
    
    pluginData.getMeta().setTitle(autoExtractor.getTitle());
//    if(!processTitle(pluginData)) return ;
    pluginData.getMeta().setDesc(autoExtractor.getDesc());
    
    //CONTENT FILTER
    if(contentFilters != null) contentFilters.mark(textNodes);
    
//    CompleteDataHandler completeHandler = worker.getResource(CompleteDataHandler.class);
//    pluginData.getMeta().setSource(completeHandler.completeURL(link.getAddress(), body));
    
    List<HTMLNode> nodes = pluginData.getCloneTextNodes();
    if(nodes != null) {
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < nodes.size(); i++) {
        builder.append(' ').append(nodes.get(i).getValue());
      }
      pluginData.getMeta().putProperty("temp.text", builder.toString());
    }
    
    //save data
    if(!saveWebPage(pluginData)) return;
    PageDownloadedTracker.saveUrl(pluginData.getLink());
    
//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    logSaver.updateTotalLinkAndData(link.getSource(), pluginData.getMeta().getCalendar(), 0, 1);
    
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    sourceLog.increaseTotalSavedDataSuccessfull();

    //for indexing
    textNodes = pluginData.getCloneTextNodes();
    if(textNodes == null) return;
    
    Iterator<HTMLNode> iterator = textNodes.iterator();
    while(iterator.hasNext()) {
      HTMLNode contentNode = iterator.next();
      if(isLinkNode(contentNode, 0)) iterator.remove();
    }
    pluginData.setWebPage(true);
    
//    TopicTrackingServices topicTrackingServices = TopicTrackingServices.getInstance();
//    if(topicTrackingServices != null) {
//      PluginData2TpDocument converter = new PluginData2TpDocument();
//      TpWorkingData workingData = converter.convert(pluginData);
//      if(workingData != null) topicTrackingServices.save(workingData);
//    } 
    
//    ContentIndex contentIndex = PluginData2TpDocument.toIndexContent(pluginData);
//    if(contentIndex != null) ContentIndexers.getInstance().index(contentIndex);
  }
  
  protected List<HTMLNode> searchTextNodes(List<NodeImpl> tokens) {
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl nodeImpl = tokens.get(i);
      if(nodeImpl.isNode(Name.CONTENT)) contents.add(nodeImpl);
    }
    return contents.size() < 2 ? null : contents;
  }

  
  public boolean isCheckTitle() { return false; }

  protected boolean saveWebPage(PluginData pluginData) throws Exception {
    Link link  = pluginData.getLink();
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    Article article = pluginData.getArticle();
    Meta meta = article.getMeta();
    Calendar calendar = meta.getCalendar();
    
    meta.setTime(CalendarUtils.getDateTimeFormat().format(calendar.getTime()));
    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
    
    Domain domain = new Domain(date, source.getGroup(), source.getCategory(), source.getName());
    meta.setDomain(domain.getId());
  
    completeDataHandler.completeURL(source.getHome()[0], link, meta);
    contentMetaHandler.cutMetaData(meta, null);
    
    String html = new TokenHtmlRenderer().buildHtml(link.getTokens());
    Content content = new Content(meta.getId(), date, html);
    
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
    
    try {
      EIDFolder2.write(domain, meta.getId(), Article.WAIT);
      //      IDTracker.getInstance().append(new EntryID(domain, meta.getId()));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
    }
    return true;
  }
  
  public List<HTMLNode> searchContents(HTMLNode body) {
    List<HTMLNode> list =  new ArrayList<HTMLNode>();
    list = searchTextNode(body, list);
    return list;
  }
  
  private List<HTMLNode> searchTextNode(HTMLNode root, List<HTMLNode> contents){
    if(root == null) return contents;
    
    switch (root.getName()) {
    case SCRIPT:
      return contents;
    case STYLE:
      return contents;
    case UNKNOWN:
      return contents;
      
    case MARQUEE:
      return contents;

    case OPTION:
      return contents;
    case OPTGROUP:
      return contents;
    case SELECT:
      return contents;

    default:
      break;
    }
    
    if(root.isNode(Name.CONTENT)) {
      HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
      if(verify.isValid(root.getValue())) contents.add(root);
      return contents;
    }
    
    List<HTMLNode> childen = root.getChildren();
    if (childen == null)  return  contents;
    for(HTMLNode ele : childen) {
      searchTextNode(ele, contents);      
    }
    return contents;
  }
  
  public boolean isLinkNode(HTMLNode node, int time) {
    if(time  == 5 || node == null) return false;
    if(node.isNode(Name.A) || node.isNode(Name.MARQUEE)) return true;
    return isLinkNode(node.getParent(), time+1);
  }
  
}
