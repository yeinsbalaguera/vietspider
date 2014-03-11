/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.HTMLDataExtractor;
import org.vietspider.crawl.image.DocumentImageDownloader;
import org.vietspider.crawl.image.DocumentImageHandler;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.crawl.plugin.desc.DescPathExtractor;
import org.vietspider.crawl.plugin.desc.TitleAutoExtractor;
import org.vietspider.crawl.plugin.desc.TitlePathExtractor;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public final class ProductProcessPlugin extends ProcessPlugin {
  
  private final static String ADDRESS_PROPERTY = "Address";
  
  private String addressValue;

  public ProductProcessPlugin() {
  }
  
  @Override
  public void setProcessRegion(Source source) {
    String value = source.getProperties().getProperty(ADDRESS_PROPERTY); 
    if(value != null && !value.trim().isEmpty()) {
      addressValue = value;
    } else {
      addressValue = null;
    }
    
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
    
    Region [] regions = source.getProcessRegion();
    titleExtractor= null;
    descExtractor = null;
//    ArticleDescExtractor descExtractor = worker.getResource(ArticleDescExtractor.class);
//    descExtractor.setDescPath(null);  
    NodeHandler nodeHandler = new NodeHandler();
    if(regions != null) {
      NodePathParser pathParser = new NodePathParser();
      for(Region region : regions) {
        if(region == null || !region.hasData()) continue;
        if(region.getName().equalsIgnoreCase(Region.PRODUCT_NAME)) {
          try {
            NodePath [] paths = pathParser.toNodePath(region.getPaths());
            if(paths != null && paths.length > 0) {
              titleExtractor = new TitlePathExtractor(extractor, nodeHandler, paths);
            }
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
        } else if(region.getName().equalsIgnoreCase(Region.PRODUCT_DESCRIPTION)) {
          try {
            NodePath [] paths = pathParser.toNodePath(region.getPaths());
            descExtractor = new DescPathExtractor(extractor, nodeHandler, paths);
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
        } else if(region.getName().indexOf(Region.DATA_IMAGE) > -1) {
          try {
            imageLoader.setNodePaths(pathParser.toNodePath(region.getPaths()));
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
    //end method
  }    
  
  
  @Override
  public void handle(PluginData pluginData) throws Throwable {
    if(pluginData == null) return ;
    Link link = pluginData.getLink();
//    System.out.println("====>"+link);
    
    HTMLDocument document = link.getDocument();
    HTMLNode root = document.getRoot();
    
    //search text contents
    List<HTMLNode> textNodes = searchTextNodes(root);
//    System.out.println("====> 22 "+link+ " textNode "+ textNodes);
    if(textNodes == null) return ;
    pluginData.setTextNodes(textNodes); 
    
    //extract title
    pluginData.getMeta().setTitle(titleExtractor.extract(root, textNodes));
//    if(!processTitle(pluginData)) return ; 
    
    //extract description
//    ArticleDescExtractor descExtractor = worker.getResource(ArticleDescExtractor.class);
    String desc = descExtractor.extract(root, textNodes);
    pluginData.getMeta().setDesc(desc);
    
    //download image
    List<HTMLNode> imageNodes  = new ArrayList<HTMLNode>();
    NodeHandler nodeHandler = new NodeHandler();
    nodeHandler.searchImageNodes(root.iterator(), imageNodes);
    
    if(!imageLoader.download(root, imageNodes, pluginData)) return ;

    //save data
    if(!saveData(pluginData)) return ;
    
    PageDownloadedTracker.saveUrl(pluginData.getLink());
    
//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    logSaver.updateTotalLinkAndData(link.getSource(), pluginData.getMeta().getCalendar(), 0, 1);
    
    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    sourceLog.increaseTotalSavedDataSuccessfull();
  }
  
  protected Content buildContent(List<HTMLNode> children, Meta meta, String date)  throws Exception {
    StringBuilder buildContent = new StringBuilder();
    for(HTMLNode ele : children) ele.buildValue(buildContent);
    if(addressValue != null)  buildContent.append(addressValue);
    return new Content(meta.getId(), date, buildContent.toString());
  }
  
  @Override()
  public List<HTMLNode> searchTextNodes(HTMLNode node) {
//    NodeHandler nodeHandler = worker.getResource(NodeHandler.class);
//    List<HTMLNode> refsNode = new ArrayList<HTMLNode>();
//    nodeHandler.searchNodes(node.iterator(), refsNode, Name.A);
//    if(refsNode.size() > 20) return null;
    
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);
    
    return contents.size() < 1 ? null : contents;
  }
  
}
