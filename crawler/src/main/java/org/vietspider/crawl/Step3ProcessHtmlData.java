/***************************************************************************
 * Copyright 2001-2003 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.crawl.plugin.WebPage2ProcessPlugin;
import org.vietspider.crawl.plugin.WebPageProcessPlugin;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.js.JsHandler;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.util.HTMLAnchorUtil;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.link.IPageChecker;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.pool.Task;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 13, 2007
 */
public final class Step3ProcessHtmlData extends Task<Link> {

  public Link execute(Link link) {
//    counter = 0;
    if(link == null) return null;
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
//    String address = link.getAddress();
    
    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    
    List<NodeImpl> tokens = link.getTokens(); 
    if(tokens == null) {
      LinkLogIO.saveLinkLog(link, "{tokens.not.found}", LinkLog.PHASE_EXTRACT);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {tokens.not.found}."); 
//      }
      return null;
    }
    
    
//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    logSaver.updateTotalLinkAndData(link.getSource(), Calendar.getInstance(), 1, 0);
    
    HTMLDataExtractor extractor = worker.getResource(HTMLDataExtractor.class);
    if(extractor.isNotExtract()) {
      ProcessPlugin plugin = worker.<ProcessPlugin>getResource("WEBPAGE");
      if(plugin == null) {
        if(Application.LICENSE == Install.SEARCH_SYSTEM) {
          plugin = new WebPage2ProcessPlugin(worker, source);
        } else {
          plugin = new WebPageProcessPlugin(worker, source);
        }
      }
//      decode(tokens);
      
      PluginData pluginData = plugin.createPluginData(link, -1);
      if(pluginData == null) {
        LinkLogIO.saveLinkLog(link, "{plugin.data.null}", LinkLog.PHASE_EXTRACT);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null,  address + ": {plugin.not.found}."); 
//        }
        return null;
      }
      
      try {
        plugin.handle(pluginData);
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_EXTRACT);
//        LogWebsite.getInstance().setThrowable(link.getSource(), e);
      }
      return null;
    }
    
    String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
    ProcessPlugin plugin = worker.<ProcessPlugin>getResource(pluginName);  
    if(plugin == null) {
      LinkLogIO.saveLinkLog(link, "{plugin.not.found}", LinkLog.PHASE_EXTRACT);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {plugin.not.found}."); 
//      }
      return null;
    }
//    System.out.println("step 3 " + link.getAddress());
//    LogWebsite.getInstance().setMessage(link.getSource(), pluginName + " : plugin not found!");
//    return null;
    
    HTMLDocument document = link.getDocument();
//    System.out.println(document + " : "+ srResource);
    if(document == null) {
      LinkLogIO.saveLinkLog(link, "{document.not.found}", LinkLog.PHASE_EXTRACT);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {document.not.found}."); 
//      }
      return null;
    }
    List<String> jsDocWriters = source.getJsDocWriters();
    if(jsDocWriters.size() > 0) {
      try {
        JsHandler.updateDocument(document, jsDocWriters);
      } catch(Exception exp) {
        LinkLogIO.saveLinkLog(link, exp, LinkLog.PHASE_EXTRACT);
//        LogWebsite.getInstance().setThrowable(link.getSource(), exp);
      }
    }
    
    if(link.getRef() != null) {
      HTMLAnchorUtil anchorUtil = new HTMLAnchorUtil();
      document = anchorUtil.searchDocument(document, link.getRef());
    }

//*****************************************hanlde normal data *************************************
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    sourceLog.increaseTotalExtractData();
    
    if(source.getExtractType() == ExtractType.ROW) { 
      HTMLDocument [] documents = extractor.extractRow(document);
      if(documents.length < 1) {
        LinkLogIO.saveLinkLog(link, "{row.extract.document.not.found}", LinkLog.PHASE_EXTRACT);
//        LinkCacher cacher = LinkCacherService.getCacher(source);
//        if(cacher != null) cacher.save(link);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null,  address + ": {row.extract.document.not.found}."); 
//        }
        return null;
      }

      int level = link.getLevel();
      String refer = link.getAddress();
      String host = executor.getResource(WebClient.class).getHost();

      LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
      for(int i = 0; i < documents.length; i++) {
        Link itemLink = linkCreator.create(host, refer, level/*, link.getRootCode()*/); 
        HTMLNode root = documents[i].getRoot();
        extractor.remove(root);
        
        if(root.totalOfChildren() < 1) {
//          LinkCacher cacher = LinkCacherService.getCacher(source);
//          if(cacher != null) cacher.save(link);
          continue;
        }
        sourceLog.increaseTotalExtractDataSuccessfull();
//        decode(documents[i]);

        itemLink.setDocument(documents[i]);
        PluginData pluginData = plugin.createPluginData(itemLink, i);
        if(pluginData == null || plugin.checkDownloaded(itemLink, true)) continue;
        sourceLog.increaseTotalSavedData();
        try {
          if(pluginData != null) plugin.handle(pluginData);
        } catch (Throwable e) {
          LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_EXTRACT);
//          LogWebsite.getInstance().setThrowable(link.getSource(), e);
        }
      }
      return null;
    } 
    
//    java.io.File file;
//    org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
//    java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//    file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".html");
//    System.out.println(link.getAddress() + " : "+ String.valueOf(link.hashCode())+ ".html");
//    try {
//      writer.save(file, document.getTextValue().getBytes("utf-8"));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
    document = extractor.extract(document);
    
//    java.io.File file;
//    org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
//    java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/");
//    file = new java.io.File(folder, String.valueOf(link.hashCode())+ ".2.html");
//    System.out.println(link.getAddress() + " : "+ String.valueOf(link.hashCode())+ ".2.html");
//    try {
//      writer.save(file, document.getTextValue().getBytes("utf-8"));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    
//    System.out.println("step  sang buoc thu 2 " + link.getAddress() + " doc "+ document.getTextValue());
    if(document == null) {
      LinkLogIO.saveLinkLog(link, "{document.not.found}", LinkLog.PHASE_EXTRACT);
//      LinkCacher cacher = LinkCacherService.getCacher(source);
//      if(cacher != null) cacher.save(link);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {document.not.found}."); 
//      }
      return null;
    }
    
    
    HTMLNode root = document.getRoot();
//    System.out.println("step  sang buoc thu 3 " + link.getAddress() + " doc "+ root.getTextValue());
    extractor.remove(root);
    
//    System.out.println("step  sang buoc thu 3 " + link.getAddress() + " doc "+ root.getTextValue());
    if(root.totalOfChildren() < 1) {
      LinkLogIO.saveLinkLog(link, "{document.is.empty}", LinkLog.PHASE_EXTRACT);
//      LinkCacher cacher = LinkCacherService.getCacher(source);
//      if(cacher != null) cacher.save(link);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {document.is.empty}."); 
//      }
      return null;
    }
    
//    decode(document);
    link.setDocument(document);
    sourceLog.increaseTotalExtractDataSuccessfull();
    
    IPageChecker pageChecker = source.getPageChecker();
    if(pageChecker != null && pageChecker.onlyCheckData()) {
      TextRenderer renderer = new TextRenderer(document.getRoot(), null);
      if(!pageChecker.check(renderer.getTextValue().toString())) {
        LinkLogIO.saveLinkLog(link, "{stop.by.content.filter}", LinkLog.PHASE_EXTRACT);
//        if(source.isDebug()) {
//          LogSource.getInstance().setMessage(source, null,  address + ": {stop.by.content.filter}."); 
//        }
//        System.out.println(link.getAddress());
        return null; 
      }
    }
    
//    System.out.println("step  sang buoc thu 4 " + link.getAddress() + " doc "+ root.getChildren().size());
    
    PluginData pluginData = plugin.createPluginData(link, -1);
    if(pluginData == null) {
      LinkLogIO.saveLinkLog(link, "{plugin.data.not.found}", LinkLog.PHASE_EXTRACT);
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  address + ": {plugin.not.found}."); 
//      }
      return null;
    }
    sourceLog.increaseTotalSavedData();
    try {
      if(pluginData != null) plugin.handle(pluginData);
    } catch (Throwable e) {
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_EXTRACT);
//      LogWebsite.getInstance().setThrowable(link.getSource(), e);
    }
    return null;
  }
  
  public void abort() {
    ProcessPlugin plugin = worker.<ProcessPlugin>getResource("WEBPAGE");
    if(plugin != null) plugin.abort();
    
    String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
    plugin = worker.<ProcessPlugin>getResource(pluginName);  
    if(plugin != null) plugin.abort();
  }

}
