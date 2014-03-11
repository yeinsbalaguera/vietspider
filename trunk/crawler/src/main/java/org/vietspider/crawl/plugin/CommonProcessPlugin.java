/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.link.ContentFilters;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public class CommonProcessPlugin extends ProcessPlugin {
  
//  File file ;
//  DataWriter dataWriter = new DataWriter();
  
  public CommonProcessPlugin() {
//    file = UtilFile.getFile("system", "hcm.txt");
  }

  @Override
  public void handle(final PluginData pluginData)  throws Throwable {
    if(pluginData == null) return;
    Link link = pluginData.getLink();
    
    if(link == null) return;
    
    HTMLDocument document = link.getDocument();
    if(document == null) return;
    HTMLNode root = document.getRoot();
    
    //search text contents
    List<HTMLNode> textNodes = searchTextNodes(root);
    if(textNodes == null) return;
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;
    
    // CONTENT FILTER
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;
    
    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(textNodes)) {
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      return;
    }
    
    pluginData.setTextNodes(textNodes);
    
    // extract title
    pluginData.getMeta().setTitle(titleExtractor.extract(root, textNodes));
    
//    String address = "\n"+pluginData.getMeta().getTitle();
//    dataWriter.append(file, address.getBytes(Application.CHARSET));
    
//    if(!processTitle(pluginData)) return;
    // extract description
    String desc = descExtractor.extract(root, textNodes);
    pluginData.getMeta().setDesc(desc);
//    if(nodeHandler.isShortContents(textNodes, 50)) return false;
    
    //download images
    List<HTMLNode> imageNodes = new ArrayList<HTMLNode>();
    NodeHandler nodeHandler = new NodeHandler();
    nodeHandler.searchImageNodes(root.iterator(), imageNodes);
    if(!imageLoader.download(root, imageNodes, pluginData)) return;

    //save data
    if(saveData(pluginData)) handleSuccessfullData(pluginData);
  }

}
