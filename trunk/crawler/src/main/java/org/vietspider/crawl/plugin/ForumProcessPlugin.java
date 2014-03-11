/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.HTMLDataExtractor;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.image.DocumentImageDownloader;
import org.vietspider.crawl.image.DocumentImageHandler;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.HTMLLinkExtractor;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.handler.PostForumHandler;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
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
 * Dec 21, 2007  
 */
public final class ForumProcessPlugin extends CommonProcessPlugin {

  private final static String THREAD_PAGE = "thread:page";
  private final static String POST_USER = "post:user";
  private final static String POST_CONTENT = "post:content";

  private NodePath [] pagePaths = null;
  private NodePath [] titlePaths = null;
  private NodePath [] userPaths = null;
  private NodePath [] contentPaths = null;

  private PostForumHandler postHandler;
  
  public ForumProcessPlugin() {
    super();
    postHandler = new PostForumHandler(completeDataHandler);
  }
  
  @Override
  public void setProcessRegion(Source source) {
    Region [] Regions = source.getProcessRegion();
    
//    PageSizeTracker.getInstance().registry(source);

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
    
   /* Group group = worker.getExecutor().getResource(Group.class);
    ImageHandler imageLoader = null;
    if(group.isDownloadImage()) {
      imageLoader = new DocumentImageDownloader(httpMethodHandler ); 
    } else {
      imageLoader = new DocumentImageHandler(httpMethodHandler, extractor);
    }
    worker.putResource(ImageHandler.class.getName(), imageLoader);*/

    titleExtractor = null;
    descExtractor = new DescAutoExtractor2(extractor, nodeHandler);
//  ArticleDescExtractor descExtractor = worker.getResource(ArticleDescExtractor.class);
//  descExtractor.setDescPath(null);
    pagePaths = null;
    userPaths = null;
    contentPaths = null;
    
    NodePathParser pathParser = new NodePathParser();
    if(Regions != null) { 
      for(Region path : Regions) {
        if(path == null || !path.hasData()) continue;
        if(path.getName().indexOf(Region.DATA_TITLE) > -1) {
          try {
            titlePaths = pathParser.toNodePath(path.getPaths());
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
            titlePaths = null;
          }
//        if(titlePaths != null && titlePaths.length > 0) {
//        titleExtractor = new TitlePathExtractor(extractor, nodeHandler, titlePaths);
//        }
        } else if(path.getName().equals(THREAD_PAGE) ) {
          try {
            pagePaths = pathParser.toNodePath(path.getPaths());
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
          }
          if(pagePaths != null && pagePaths.length < 1) pagePaths = null;
        } else if(path.getName().equals(POST_USER) ) {
          try {
            userPaths = pathParser.toNodePath(path.getPaths());
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
            userPaths = null;
          }
          if(userPaths != null && userPaths.length < 1) userPaths = null;
        } else if(path.getName().equals(POST_CONTENT) ) {
          try {
            contentPaths = pathParser.toNodePath(path.getPaths());
          } catch (Exception e) {
            LogService.getInstance().setThrowable(source, e);
            contentPaths = null;
          }
          if(contentPaths != null && contentPaths.length < 1) contentPaths = null;
        }  
      }
    }

    if(userPaths == null && contentPaths == null) super.setProcessRegion(source);
    
//  if(titleExtractor == null) {
//  titleExtractor = new  TitleAutoExtractor(extractor, nodeHandler);
//  }
    //end method
  }

  @Override
  public void handle(final PluginData pluginData) throws Throwable {
    if(pluginData == null) return ;
    Link link = pluginData.getLink();
    CrawlingSession executor = (CrawlingSession)worker.getExecutor();
//  if(executor.getValue() != link.getSource()) return ;
    // search next
    if(pagePaths != null && pagePaths.length > 0) searchNext(executor, link);
    //extract posts
    if(userPaths == null && contentPaths == null) {
      super.handle(pluginData);
      return;
    }

    HTMLDataExtractor extractor = worker.getResource(HTMLDataExtractor.class);
    HTMLNode root  = link.<HTMLDocument>getDocument().getRoot();

    List<HTMLNode> userNodes = extractor.matchNodes(root, userPaths);
    List<HTMLNode> contentNodes = extractor.matchNodes(root, contentPaths);

    if(userNodes == null || contentNodes == null ) {
//    String message = "Posts or Users cann't extract!";
//    LogService.getInstance().setMessage(link.getSource(), null, message);
      return ;
    }
//  System.out.println(" \n == vao roi  : "+ link.getAddress());
    pluginData.getMeta().setTitle(extractTitle(extractor, root));

    HTMLNode html = HTMLParser2.clone(root);
    int post = Math.min(userNodes.size(), contentNodes.size());

    if(post < 1) {
//    String message = "Total of Users not equals Total of Posts!";
//    LogService.getInstance().setMessage(link.getSource(), null, message);
      return ;
    }
    
    postHandler.build(html, userNodes, contentNodes);

   /* for(int i = 0; i < post; i++) {
      if(i > 0) {
        HTMLNode hrNode = new NodeImpl(hrNodeValue, Name.HR, TypeToken.SINGLE); 
        html.addChild(hrNode);
//      hrNode.setParent(html);
      }

      HTMLNode userNode = userNodes.get(i);
      if(userNode != null) {
        userNode =  completeDataHandler.completeTable(userNode);
        html.addChild(userNode);
//      userNode.setParent(html);
      }

      HTMLNode brNode = new NodeImpl(brNodeValue, Name.BR, TypeToken.SINGLE); 
      html.addChild(brNode);
//    brNode.setParent(html);

      HTMLNode contentNode = contentNodes.get(i);
      if(contentNode != null) {
        contentNode =  completeDataHandler.completeTable(contentNode);
        html.addChild(contentNode);
//      contentNode.setParent(html);
      }
    }*/
    
    HTMLDocument newDocument = new HTMLDocument(html);
    link.setDocument(newDocument);
    
//    ForumDataService.getInstance().add(pluginData, post);

//    int addressCode = link.getAddressCode();
    
    /*int savePost = 0;
    try {
      savePost = ForumTrackerDatabaseService.getInstance().read(sourceName, addressCode);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(link.getSource(), e);
      return ;
    }
//  System.out.println(" thay co save "+savePost);
    if(post == savePost) return;*/

    //search text contents
//  System.out.println(" thay "+ link.getAddress());
    List<HTMLNode> textNodes = searchTextNodes(root);

    if(textNodes == null) return;
    pluginData.setTextNodes(textNodes);

    // extract description
//  ArticleDescExtractor descExtractor = worker.getResource(ArticleDescExtractor.class);
    pluginData.getMeta().setDesc(descExtractor.extract(root, textNodes));
//  if(nodeHandler.isShortContents(textNodes, 50)) return false;

    //download images
    List<HTMLNode> imageNodes = new ArrayList<HTMLNode>();
    NodeHandler nodeHandler = new NodeHandler();
    nodeHandler.searchImageNodes(root.iterator(), imageNodes);
    if(!imageLoader.download(root, imageNodes, pluginData)) return;

    //save data
    if(!saveData(pluginData)) return ;

    PageDownloadedTracker.saveCode(link, post);
    
//    PageSizeTracker.saveSize(link);
    
//    indexMiningData(pluginData);
//    indexSearchData(pluginData);

//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    logSaver.updateTotalLinkAndData(link.getSource(), pluginData.getMeta().getCalendar(), 0, 1);

    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    sourceLog.increaseTotalSavedDataSuccessfull();
  }

  @Override()
  @SuppressWarnings("unused")
  public boolean checkDownloaded(Link link, boolean downloaded) {
//  Source source = link.getSource();
//  System.out.println(link.getAddress()+ " : "+ link.getLevel()+ " : "+ source.getDepth());
    if(userPaths != null) {
      if(link.isData()) return false;  
    }
    
    try {
      return PageDownloadedTracker.searchUrl(link, true);
    } catch (Throwable e1) {
      LogService.getInstance().setThrowable(link.getSourceFullName(), e1);
//      worker.getExecutor().abortSession();
      return true;
    }
  }

  private String extractTitle(HTMLExtractor extractor, HTMLNode root) {
    if(titlePaths == null) return null;
    StringBuilder builder = new StringBuilder();
   
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();

    List<HTMLNode> titleNodes = extractor.matchNodes(root, titlePaths);

    NodeHandler nodeHandler = new NodeHandler();
    for(int i = 0; i < titleNodes.size(); i++) {
      HTMLNode node = titleNodes.get(i);
      if(node == null) continue;
      List<HTMLNode> contents  = new ArrayList<HTMLNode>(); 
      htmlText.searchText(contents, node, verify);
//    nodeHandler.searchTextNode(node, contents);
      for(int j = 0; j < contents.size(); j++) {
        builder.append(contents.get(j).getValue()).append(' ');
      }
      nodeHandler.removeNode(node);
    }
    return builder.toString();
  }

  private void searchNext(CrawlingSession executor, Link link) {
    //get page for this thread
    HTMLLinkExtractor htmlLinkExtractor = HTMLLinkExtractor.getInstance();
//  htmlLinkExtractor.setWorking(link);
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;

    List<Link> collection = htmlLinkExtractor.getLinks(link, /*link.getSource(),*/ pagePaths);
//  List<Link> collection = htmlLinkExtractor.getLinks(srResource);
    List<Link> nextLinks = createNextLink(link, collection);
    if(nextLinks.size() < 1) return;
    if(userPaths == null && contentPaths == null) {
      executor.addElement(nextLinks, link.getSourceFullName());
      return;
    }
    
     
    if(nextLinks.size() < 2) {
      executor.addElement(nextLinks, link.getSourceFullName());
    }
    
//    long start = System.currentTimeMillis();
    
    int [] posts = new int[nextLinks.size()];
    for(int i = 0; i < nextLinks.size(); i++) {
      try {
        posts[i] = PageDownloadedTracker.searchCode(nextLinks.get(i), true);
      } catch (Throwable e) {
        posts[i] = -1;
        LogService.getInstance().setThrowable(link.getSourceFullName(), e);
//        executor.abortSession();
//        return;
      }
    }
    
    int max = 1;
    for(int i = 0; i < posts.length; i++) {
      if(posts[i] > max) max = posts[i];
    }
    
//    System.out.println(" thay max post la "+ max);
    
    List<Link> updateLinks = new ArrayList<Link>();
    for(int i = 0; i < posts.length; i++) {
      if(posts[i] >= max) continue;
      updateLinks.add(nextLinks.get(i));
    }
    
//    long end = System.currentTimeMillis();
//    System.out.println("step. 4 "+ link.getUrl()+ " xu ly cai ni mat " + (end - start));
    
    executor.addElement(updateLinks, link.getSourceFullName());
    
    /*int minPost = -1;
    Link minLink  = null;
    List<Link> updateLinks = new ArrayList<Link>();
    for(int i = 0; i < nextLinks.size(); i++) {
      Link ele = nextLinks.get(i);
      int post = 0;
      try {
        post = PostForumTrackerService2.getInstance().read(ele.getAddressCode());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(link.getSource(), e);
      }
      if(post < 1) {
        updateLinks.add(ele);
        continue;
      } 

      if(minPost < 0 || post < minPost){
        minLink = ele;
        minPost = post;        
      } 
    }

    if(minLink != null) updateLinks.add(minLink);
    executor.addElement(updateLinks, link.getSource());*/
  }

  private List<Link> createNextLink(Link referer, List<Link> list) {
    List<Link> values = new ArrayList<Link>();
    if(list == null) return values;

    int level = referer.getLevel();
    CrawlingSession executor = worker.getExecutor();
    String host = executor.getResource(WebClient.class).getHost();
    
    Source source = CrawlingSources.getInstance().getSource(referer.getSourceFullName());
    if(source == null) return values;
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return values;
    
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    for(int i = list.size() - 1; i > -1; i--) {
      String address = list.get(i).getAddress();
      if(address == null) continue;
      Link link = linkCreator.create(host, address, level/*, referer.getRootCode()*/);
      if(link == null) continue;

      link.setIsData(true);
      link.setIsLink(false);
      link.setReferer(referer.getAddress());

      values.add(link);
    }

    return values;
  }
  
//  @Override()
  /*public boolean isValidSize(Link link, HttpResponse response) {
//    if(!isValidLink(link)) return true;
    if(!link.isData()) return true;
    return PageSizeTracker.isValidSize(link, response);
  }
  @Override()
  public boolean isValidSize(Link link, byte[] data) {
//    if(!isValidLink(link)) return true;
    if(!link.isData()) return true;
    return PageSizeTracker.isValidSize(link, data);
  }*/
  
  /*@Override()
  public void saveLinkSize(Link link) {
//    if(!isValidLink(link)) return;
    if(!link.isData()) return;
    PageSizeTracker.saveSize(link);
  }*/
  
  /*private boolean isValidLink(Link link) {
    if(link.isLink()) {
      if(link.getLevel() <= 1) {
        return false;
      } else if(link.getLevel() < link.getSource().getDepth() - 2) {
        return false;
      }
    }
    return true;
  }*/
  
}
