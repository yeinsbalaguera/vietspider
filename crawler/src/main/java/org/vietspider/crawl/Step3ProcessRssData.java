/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.parser.rss2.EntryItem;
import org.vietspider.parser.rss2.IMetaItem;
import org.vietspider.parser.rss2.MetaDocument;
import org.vietspider.parser.rss2.MetaLink;
import org.vietspider.pool.Task;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public final class Step3ProcessRssData extends Task<Link> {

  public Step3ProcessRssData cloneInstance() { return new Step3ProcessRssData(); }

  public Link execute(Link link)  {
    if(link == null) return null;
//    counter = 0;
    
    if(!link.isRss())  return link; 
    
//    CrawlExecutor executor = (CrawlExecutor) worker.getExecutor();

//  System.out.println("step 3 " + link.getAddress());
    String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
    ProcessPlugin plugin = worker.<ProcessPlugin>getResource(pluginName);
    if(plugin == null) {
      LinkLogIO.saveLinkLog(link, "{plugin.not.found}", LinkLog.PHASE_RSS);
      return null;
    }
//  LogWebsite.getInstance().setMessage(link.getSource(), pluginName + " : plugin not found!");
//  return null;

//  *****************************************handle rss data ***************************************

    MetaDocument metaDocument = link.getDocument();
    if(metaDocument == null) {
      LinkLogIO.saveLinkLog(link, "{document.not.found}",LinkLog.PHASE_RSS);
//      Source source = link.getSource();
//      if(source.isDebug()) {
//        LogSource.getInstance().setMessage(source, null,  link.getAddress() + ": {rss.document.not.found}."); 
//      }
      return null;
    }
    List<IMetaItem> metaItems = metaDocument.getItems();
    RefsDecoder decoder = new RefsDecoder();
    
//  LinkStore store = (LinkStore)worker.getExecutor().getStore();
    HTMLParser2 htmlParser2 = new HTMLParser2(); 
    for(IMetaItem metaItem : metaItems) {
      Link itemLink = createLink(link, metaItem);
      if(itemLink == null) {
        LinkLogIO.saveLinkLog(link, "{item.not.found}",LinkLog.PHASE_RSS);
        continue;
      }
    
    if(plugin.checkDownloaded(itemLink, true)) {
      LinkLogIO.saveLinkLog(link, "{item.downloaded}",LinkLog.PHASE_RSS);
      continue;
    }
    

      String content = null;
      if(metaItem instanceof EntryItem) content = ((EntryItem)metaItem).getContent();
      if(content == null || content.trim().isEmpty()) content = metaItem.getDesc();

      char [] chars = decoder.decode(content.toCharArray());
      content = new String(decoder.decode(content.toCharArray()));
      HTMLDocument htmlDocument = null;
      try { 
        htmlDocument = htmlParser2.createDocument(chars);
      } catch (Exception e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_RSS);
//        LogWebsite.getInstance().setThrowable(link.getSource(), e);
      }
      if(htmlDocument == null) {
        if(plugin.checkDownloaded(itemLink, true)) {
          LinkLogIO.saveLinkLog(link, "{document.not.found}", LinkLog.PHASE_RSS);
          continue;
        }
        
        continue;
      }
      HTMLNode root = htmlDocument.getRoot();
      if(root.totalOfChildren() < 1) {
        LinkLogIO.saveLinkLog(link, "{document.is.empty}", LinkLog.PHASE_RSS);
        continue;
      }
//      decode(htmlDocument);

      itemLink.setDocument(htmlDocument);

      PluginData pluginData = plugin.createPluginData(itemLink, -1);
      if(pluginData == null) {
        LinkLogIO.saveLinkLog(link, "{plugin.data.not.found}", LinkLog.PHASE_RSS);
        continue;
      }

      pluginData.getMeta().setTitle(metaItem.getTitle());
      pluginData.getMeta().setSource(metaItem.getImage());
      try {
        plugin.handle(pluginData);
      } catch (Throwable e) {
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_RSS);
      }
    }
    
    return null;
  }
  
  private Link createLink(Link link, IMetaItem metaItem) {
    List<MetaLink> metaLinks = metaItem.getLinks();
    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    
    String refer = link.getAddress();
    String host = executor.getResource(WebClient.class).getHost();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return null;
    
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    
    if(metaLinks == null || metaLinks.size() < 1) {
      return linkCreator.create(host, refer, 1/*, link.getRootCode()*/);
    }
    
    for(MetaLink metaLink : metaLinks) {
      if(metaLink == null) continue;
      String href = metaLink.getRealHref();
      if(href == null || href.trim().isEmpty()) continue;
      return linkCreator.create(host, href, 1/*, link.getRootCode()*/);
    }

    return linkCreator.create(host, refer, 1/*, link.getRootCode()*/);
  }
  
  public void abort() {
    ProcessPlugin plugin = worker.<ProcessPlugin>getResource("WEBPAGE");
    if(plugin != null) plugin.abort();
    
    String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
    plugin = worker.<ProcessPlugin>getResource(pluginName);  
    if(plugin != null) plugin.abort();
  }
  
}
