/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import static org.vietspider.link.generator.Generator.UPDATE_DOCUMENT_GENERATOR;
import static org.vietspider.link.generator.LinkGeneratorInvoker.constainGenerator;
import static org.vietspider.link.generator.LinkGeneratorInvoker.invoke;

import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.link.HTMLLinkExtractor;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.RSSHandler;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.crawl.plugin.handler.DocumentDecoder;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.db.url.HomepageDatabase;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.link.IPageChecker;
import org.vietspider.model.Source;
import org.vietspider.net.client.Proxies;
import org.vietspider.net.client.WebClient;
import org.vietspider.parser.rss2.MetaDocument;
import org.vietspider.parser.rss2.RSSParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 10, 2007  
 */
public final class Step2VisitLink extends Step2DownloadLink {

//  private ArticleLoader loader = new ArticleLoader();

  public Link execute(Link link)  {
    //    if(link.isData()) return null;
    if(link == null || link.getAddress() == null) return null;
    
//    System.out.println(link.getAddress() + " is data? "
//        + link.isData() + " /  is link? "+ link.isLink());
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    
    CrawlingSession executor = (CrawlingSession) worker.getExecutor(); 

    String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
    ProcessPlugin plugin = worker.<ProcessPlugin>getResource(pluginName);    
    if(plugin == null) {
      LinkLogIO.saveLinkLog(link, "{plugin.not.found}", LinkLog.PHASE_DOWNLOAD);
      return null;
    }

    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);

    boolean downloaded = true;

    if(!link.isLink() && link.isData()) {
      downloaded = plugin.checkDownloaded(link, false) ;
      sourceLog.increaseTotalDataLink();
      if(downloaded) {
        SessionStore store = SessionStores.getStore(source.getCodeName());
        store.addDownloadedLink(link);
//        LinkLogIO.saveLinkLog(link, "{link.downloaded}", LinkLog.PHASE_DOWNLOAD);
        return null;
      }
      
      /*Article article = loader.loadArticleById(source.getGroup(), link.getAddress());
      if(article != null && saveArticle(plugin, link, article)) {
        LinkLogIO.saveLinkLog(link, "{load.article}", LinkLog.PHASE_DOWNLOAD);
        return null;
      }
//        System.out.println(" thay co article cu "+ article.getMeta().getId());
      */
      
    } else if(link.isLink() && !link.isData()) {
      if(link.getLevel() >= source.getDepth()) {
        SessionStore store = SessionStores.getStore(source.getCodeName());
        store.addLinkLog(LinkLogIO.createLinkLog(
            link, "{out.of.depth}", LinkLog.PHASE_DOWNLOAD));
        return null;
      }
      if(link.getLevel() > 0)  sourceLog.increaseTotalVisitLink();
      downloaded = true;
    } else if(link.isLink() && link.isData()) {
      downloaded = plugin.checkDownloaded(link, false) ;
      sourceLog.increaseTotalDataLink();
      if(link.getLevel() > 0) sourceLog.increaseTotalVisitLink();
      if(downloaded) {
        SessionStore store = SessionStores.getStore(source.getCodeName());
        store.addDownloadedLink(link);
//        LinkLogIO.saveLinkLog(link, "{link.downloaded}", LinkLog.PHASE_DOWNLOAD);
        return null;
      } else if(link.getLevel() >= source.getDepth()) {
        LinkLogIO.saveLinkLog(link, "{out.of.depth.downloaded}", LinkLog.PHASE_DOWNLOAD);
        return null;
      } 
    }
    
    //  ************************************************* download data *********************************
    //    char [] chars = JdbmWebCacherService.getCacher(source).load(link.getAddressCode());
    char [] chars = null;
    if(link.getTokens() == null) {
      WebClient webClient = executor.getResource(WebClient.class);
      if(webClient == null) {
        LinkLogIO.saveLinkLog(link, "web.client.not.found", LinkLog.PHASE_DOWNLOAD);
        return null;
      }

      int time = link.getLevel() < 1 ? 6 : 1;

      for(int i = 0; i < time; i++) {
        webClient = executor.getResource(WebClient.class);
        if(webClient == null) {
          LinkLogIO.saveLinkLog(link, "web.client.not.found", LinkLog.PHASE_DOWNLOAD);
          return null;
        }

        try {
          chars = download(link);
        } catch (Exception e) {
          LinkLogStorages.getInstance().save(source, e);
        }

        if(chars != null) break;

        webClient.increaseBadRequest();
        worker.getTimer().startSession (); 

        if(!webClient.isBadClient()) continue;
        Proxies proxies = webClient.getProxies();
        if(proxies != null) webClient.registryProxy(proxies.next());
        webClient.resetBadRequestCounter();
      }

      if(chars == null) {
        LinkLogIO.saveLinkLog(link, "{not.found.data}", LinkLog.PHASE_DOWNLOAD);
        httpHandler.release();
        return null;
      }

      webClient.resetBadRequestCounter();

      RSSHandler rssHandler = new RSSHandler();
      if(rssHandler.isRssDocument(chars)) {
        return handleRSSLink(rssHandler, link, chars);
      }

      try {
        link.setTokens(new HTMLParser2().createTokens(chars));
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
      }

      if(link.getTokens() == null) {
        LinkLogIO.saveLinkLog(link, "{not.html.data}", LinkLog.PHASE_DOWNLOAD);
        return null;
      }
    }

    DocumentDecoder.decode(link.getTokens());

    List<Object> generators = source.getLinkGenerators(); 
    if(constainGenerator(generators, UPDATE_DOCUMENT_GENERATOR)) {
      HTMLDocument document = link.getDocument();
      //      System.out.println("chuan bi call roi "+ link.getAddress() + " / "+ link.isLink());
      try {
        WebClient webClient = executor.getResource(WebClient.class);
        if(link.getReferer() != null && link.isLink()) {
          webClient.getCacheData().putCachedObject(link.getAddress(), chars);
        } else {
          invoke(generators, UPDATE_DOCUMENT_GENERATOR, link, webClient, document);
        }
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
      }
    }

    //  ************************************************* Page Checker **********************************

    IPageChecker pageChecker = source.getPageChecker();
    if(pageChecker != null) {
      HTMLDocument document = link.getDocument();
      TextRenderer renderer = new TextRenderer(document.getRoot(), null);
      //      System.out.println(" Page checker "+ link.getAddress());
      if(!pageChecker.check(renderer.getTextValue().toString())) {
        if(link.getLevel() < 1 && CrawlingSetup.hasHomepageDatabase(source)) {
          HomepageDatabase db = new HomepageDatabase(source.getFullName(), true);
          db.removeUrl(link.getAddress());
        }
        if(!pageChecker.onlyCheckData()) {
          LinkLogIO.saveLinkLog(link, "{stop.by.page.checker}", LinkLog.PHASE_DOWNLOAD);
          return null; //{ //page checker (language)
        }
      }
    }

//    System.out.println(link.getAddress() + " link "+ link.isLink() 
//        + " ===  > "+ link.getLevel() + " / "+ source.getDepth());
    if(link.isLink() && link.getLevel() < source.getDepth()) {
//      if(link.getSource() == null) link.setSource(source);
//      HTMLLinkExtractor linkExtractor = new HTMLLinkExtractor(link);
      executor.addElement(HTMLLinkExtractor.getInstance().extractLink(worker), link);
    }

//    if(link.isLink() && link.getLevel() == 0) {
//      java.io.File file;
//      org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
//      java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//      file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".html");
//      System.out.println(link.getAddress() + " : "+ String.valueOf(link.hashCode())+ ".html");
//      try {
//        writer.save(file, link.<HTMLDocument>getDocument().getTextValue().getBytes("utf-8"));
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }

    return downloaded ? null : link;
  }


  private Link handleRSSLink(RSSHandler rssHandler, Link link, char [] chars) {
    CrawlingSession executor = (CrawlingSession) worker.getExecutor(); 
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());

    MetaDocument metaDocument = null;
    try {
      metaDocument = RSSParser.createDocument(new String(chars), new RefsDecoder());
    } catch (Exception e) {
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_DOWNLOAD);
    }
    
    if(metaDocument == null) {
      LinkLogIO.saveLinkLog(link, "{not.found.rss.document}", LinkLog.PHASE_DOWNLOAD);
      return null;
    }

    if(source.getPattern() == null || source.getPattern().trim().isEmpty()) {
      link.setDocument(metaDocument);
      link.setRss(true);
      return link;
    }

    executor.addElement(rssHandler.handle(metaDocument), link);
    return link;
  }
  
  /*private boolean saveArticle(ProcessPlugin plugin, Link link, Article article) {
    PluginData pluginData = plugin.createPluginData(link, article);
    if(pluginData == null) return false;
    List<HTMLNode> textNodes = pluginData.getCloneTextNodes();
    if(textNodes != null) {
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < textNodes.size(); i++) {
        builder.append(' ').append(textNodes.get(i).getValue());
      }
      article.getMeta().putProperty("temp.text", builder.toString());
    }
    
    try {
      if(plugin.saveDataToDatabase(pluginData)) {
        plugin.handleSuccessfullData(pluginData);
        //        LogWebsite.getInstance().setMessage(source, null, "sync data for " + link.getAddress());
        return true;
      }
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return false;
  }*/


}
