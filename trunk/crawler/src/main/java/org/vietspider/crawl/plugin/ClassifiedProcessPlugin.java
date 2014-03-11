/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.IDGenerator;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.bean.Content.BigSizeException;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.content.nlp.classified.PostDateDetector;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.image.DocumentImageDownloader;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.search.SEOData;
import org.vietspider.db.search.SEODatabase;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.renderer.TextRenderer2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.link.ContentFilters;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.tags.Tags;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 2, 2009  
 */
// Thuannd update code
public class ClassifiedProcessPlugin extends CommonProcessPlugin {

  //  private CityCodeDetector regionDetector = new CityCodeDetector();
  private PostDateDetector dateDetector = new PostDateDetector();

  public ClassifiedProcessPlugin() {
    super();
//    TpDatabases.exitInstance();
    SystemProperties properties = SystemProperties.getInstance();
    nlp = "true".equalsIgnoreCase(properties.getValue("nlp"));
  }

  public void handle(final PluginData pluginData)  throws Throwable {
    if(pluginData == null) return;
    Link link = pluginData.getLink();

    if(link == null) return;

    HTMLDocument document = link.getDocument();
    if(document == null) {
      LinkLogIO.saveLinkLog(link, "{document.not.found}", LinkLog.PHASE_PLUGIN);
      return;
    }

    HTMLNode root = document.getRoot();
    //search text contents
    List<HTMLNode> textNodes = searchTextNodes(root);
    if(textNodes == null) {
      LinkLogIO.saveLinkLog(link, "{text.nodes.not.found}", LinkLog.PHASE_PLUGIN);
      return;
    }

    // CONTENT FILTER
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;
//    System.out.println(" check link ===> "+ link.getAddress());
    ContentFilters contentFilters = source.getContentFilters();
    if(contentFilters != null && !contentFilters.check(textNodes)) {
      LogService.getInstance().setMessage("CONTENT FILTER",  null, "Link "+ link.getAddress());
      PageDownloadedTracker.saveUrl(pluginData.getLink());
      LinkLogIO.saveLinkLog(link, "{stop.by.content.filter}", LinkLog.PHASE_PLUGIN);
      return;
    }
//    System.out.println(" *** lay link "+ link.getAddress());
//    System.out.println("data la " + pluginData.getArticle().getId());
    pluginData.setTextNodes(textNodes);
    
    Tags tags = Tags.getInstance("nha-dat");
    if(tags.hasTag()) {
      Article article = pluginData.getArticle();
      String tag = tags.tag(article.getId(), textNodes);
      pluginData.setTag(tag);
      //      System.out.println(" ===  > "+ pluginData.getArticle().getId() + " : "+ tag);
      if(tag != null) setTag(source, article, tag);
    }
    

    TextRenderer2 textRenderer = new TextRenderer2(root, TextRenderer.RENDERER);
    String text = textRenderer.getTextValue().toString();

    Meta meta = pluginData.getMeta();

    // extract title
    meta.setTitle(titleExtractor.extract(root, textNodes));

    String desc = descExtractor.extract(root, textNodes);
    meta.setDesc(desc);
    //    if(nodeHandler.isShortContents(textNodes, 50)) return false;

    //download images
    List<HTMLNode> imageNodes = new ArrayList<HTMLNode>();
    NodeHandler nodeHandler = new NodeHandler();
    nodeHandler.searchImageNodes(root.iterator(), imageNodes);
    if(!imageLoader.download(root, imageNodes, pluginData)) return;
    
    //CONTENT FILTER
    if(contentFilters != null && contentFilters.mark(textNodes)) {
      setTag(source, pluginData.getArticle(), contentFilters.getTag());
    }

    //detect data
    //    String city = regionDetector.detect(text);
    List<HTMLNode> nodes = pluginData.getCloneTextNodes();
    if(nodes != null) {
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < nodes.size(); i++) {
        builder.append(' ').append(nodes.get(i).getValue());
      }
      meta.putProperty("temp.text", builder.toString());
      //      System.out.println(" temp text "+ builder);
    }
    meta.setSourceTime(dateDetector.detectDate(text));
    
    
    //save data
    if(saveData(pluginData)) handleSuccessfullData(pluginData);
  }
  
  protected boolean saveData(PluginData data) throws Exception {
    Link link  = data.getLink();
    Article article = data.getArticle();
    Meta meta = article.getMeta();
    HTMLNode root = link.<HTMLDocument>getDocument().getRoot();
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return false;
    
    Tags tags = Tags.getInstance("nha-dat");
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
  
//  @Override()
//  public boolean saveData(Source source, Link link, Article article) throws Exception {
//   
//    return super.saveData(source, link, article);
//  }

  public boolean checkDownloaded(Link link, boolean downloaded) {
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return true;
    if(source.isRedown()) return false;

    if(!downloaded) {
      if(source.getExtractType() != ExtractType.NORMAL) return false;
    }

    try {
      //          System.out.println(" chuan bi search "+ link.getAddress() + PageDownloadedTracker.searchUrl(link, false));
      return PageDownloadedTracker.searchUrl(link, false);
    } catch (Throwable e1) {
      LogService.getInstance().setThrowable(source, e1);
      //      worker.getExecutor().abortSession();
      return true;
    }
  }

  public void handleSuccessfullData(PluginData pluginData) throws Throwable {
    if(pluginData == null) return;
    PageDownloadedTracker.saveUrl(pluginData.getLink());
    
//    IDVerifiedStorages.save("crawler", pluginData.getArticle());
    
//    TpDatabases tpDatabases = TpDatabases.getInstance();
    //    Map<String, Object> properties = pluginData.getMeta().getProperties();
//    String region = pluginData.getMeta().getPropertyValue("region");

    NLPRecord nlpRecord = pluginData.getArticle().getNlpRecord();

//    TpWorkingData workingData = null;
//    if(tpDatabases != null) {
//      ClassifiedPluginData2TpDocument converter = new ClassifiedPluginData2TpDocument();
//      workingData = converter.convert(pluginData);
//      if(workingData != null) tpDatabases.save(workingData);
//    }

    if(nlpRecord != null) {
//      List<String> nlpDatas = nlpRecord.getData(NLPData.TELEPHONE);
//      if(nlpDatas != null && workingData != null) {
//        for(int i = 0; i < nlpDatas.size(); i++) {
//          workingData.addKey(nlpDatas.get(i));
//        }
//      }

      List<String> nlpDatas = nlpRecord.getData(NLPData.MOBILE);
      if(nlpDatas != null) {
        for(int i = 0; i < nlpDatas.size(); i++) {
          addSEOData(/*workingData,*/ nlpDatas.get(i), SEOData.PHONE);
        }
      }

      nlpDatas = nlpRecord.getData(NLPData.EMAIL);
      if(nlpDatas != null) {
        for(int i = 0; i < nlpDatas.size(); i++) {
          addSEOData(/*workingData,*/ nlpDatas.get(i), SEOData.EMAIL);
        }
      }

//      List<String> addresses = nlpRecord.getData(NLPData.ADDRESS);
//      if(addresses != null && workingData != null) {
//        for(int i = 0; i < addresses.size(); i++) {
//          workingData.addKey(addresses.get(i));
//        }
//      }
    }


//    ContentIndex contentIndex = PluginData2TpDocument.toIndexContent(pluginData);
//    if(contentIndex != null) {
//      if(region != null) contentIndex.setRegion(region);
//      ContentIndexers.getInstance().index(contentIndex);
//    }

    //    DbIndexerService dbIndex = DbIndexerService.getInstance();
    //    if(dbIndex != null) dbIndex.getDbIndexEntry().save(pluginData);

//    Link link = pluginData.getLink();
//    MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//    logSaver.updateTotalLinkAndData(link.getSource(), pluginData.getMeta().getCalendar(), 0, 1);

    CrawlingSession executor = (CrawlingSession) worker.getExecutor();
    CrawlSourceLog sourceLog = executor.getResource(CrawlSourceLog.class);
    if(sourceLog != null) sourceLog.increaseTotalSavedDataSuccessfull();
  }


  private void addSEOData(/*TpWorkingData workingData, */String value, short type) {
    if(value == null) return;
    value  = value.trim();
    if(value.length() < 1) return;
    String [] elements = value.split(",");
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
//      if(workingData != null) workingData.addKey(elements[i]);
      //for SEO Database
      try {
        SEODatabase.getInstance().save(new SEOData(elements[i], type));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  //  private String buildAddress(NLPRecord record) {
  //    StringBuilder builder = new StringBuilder();
  //    String number = record.getData("Contact", "addressNumber");
  //    if(number == null || "unknown".equalsIgnoreCase(number)) return null;
  //    builder.append(number);
  //
  //    String street = record.getData("Contact", "street");
  //    if(street == null || !"unknown".equalsIgnoreCase(street)) return null;
  //    if(builder.length() > 0) builder.append(" ");
  //    builder.append(street);
  //
  //    String district = record.getData("Contact", "district");
  //    if(district == null || !"unknown".equalsIgnoreCase(district)) return null;
  //    if(builder.length() > 0) builder.append(" ");
  //    builder.append(district);
  //
  //    return builder.toString();
  //  }

  @Override()
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

      //      for(int i = 0; i < textNodes.size(); i++) {
      //        nodeImpl =  (NodeImpl)textNodes.get(i);
      //        String text = new String(nodeImpl.getValue());
      //        String newText = AbbreviateDictionary2.getInstance().compile(text);
      ////        if(text.length() != newText.length()) {
      ////          System.out.println("\n\n\n");
      ////          System.out.println(text);
      ////          System.out.println(newText);
      ////        }
      //        
      //       /* if(newText.toLowerCase().indexOf("cc") > -1) {
      ////        if(newText.length() != text.length()) {
      //          System.out.println("\n\n\n");
      //          System.out.println(text);
      //          System.out.println(newText);
      //        }*/
      //        nodeImpl.setValue(newText.toCharArray());
      //      }

      pluginData.setTextNodes(textNodes);

      if(imageLoader instanceof DocumentImageDownloader) {
        meta.setImage(null);
        //download images
        NodeHandler nodeHandler = new NodeHandler();
        List<HTMLNode> imageNodes = new ArrayList<HTMLNode>();
        nodeHandler.searchImageNodes(root.iterator(), imageNodes);
        //      List<HTMLNode> imageNodes = removeDescImage.getImageNodes();
        if(!imageLoader.download(root, imageNodes, pluginData)) return null;
      }

      StringBuilder buildContent = new StringBuilder();
      List<HTMLNode> children  = root.getChildren();
      for(HTMLNode ele : children) {
        ele.buildValue(buildContent);
      }
      content.setContent(buildContent.toString());

    } catch (Exception e) {
      return null;
    }

    return pluginData;
  }

  @Override
  public List<HTMLNode> searchTextNodes(HTMLNode node) {
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();

    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);
    //    nodeHandler.searchTextNode(node, contents);
    //    System.out.println("step 6 "+href.getUrl()+ " : "+ contentsNode.size());
    return contents;
  }
  
  private void setTag(Source source, Article article, String tag) {
    Meta meta = article.getMeta();
    Date dateInstance = meta.getCalendar().getTime();
    meta.setTime(CalendarUtils.getDateTimeFormat().format(dateInstance));
    String date = CalendarUtils.getDateFormat().format(dateInstance);

    Domain domain = new Domain(date, source.getGroup(), tag, source.getName());
    meta.setDomain(domain.getId());
    article.setDomain(domain);
  }

}
