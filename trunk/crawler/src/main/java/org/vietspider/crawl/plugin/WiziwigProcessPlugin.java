/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import static org.vietspider.crawl.plugin.WiziwigUtils.createFromTime;
import static org.vietspider.crawl.plugin.WiziwigUtils.createToTime;
import static org.vietspider.crawl.plugin.WiziwigUtils.getScheduled;
import static org.vietspider.crawl.plugin.WiziwigUtils.hasURL;
import static org.vietspider.crawl.plugin.WiziwigUtils.loadDictionary;
import static org.vietspider.crawl.plugin.WiziwigUtils.preProcess;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.content.IDatabases;
import org.vietspider.db2.content.ArticleBabuDatabases;
import org.vietspider.handler.XMLResource;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */

public class WiziwigProcessPlugin extends XmlProcessPlugin {

  private Properties dictionary;

  @Override
  public void setWorker(Worker<String, Link> worker) {
    super.setWorker(worker);

    File file = UtilFile.getFolder("system/dictionary");
    file = new File(file, "wiziwig.properties");
    dictionary = loadDictionary(file);
  }

  @Override
  public void handle(final PluginData pluginData) throws Throwable {
    if(pluginData == null) return ;
    Link link = pluginData.getLink();

    //    System.out.println("trong plugin "+ link.getUrl() + link.isLink());

    HTMLDocument document = link.<HTMLDocument>getDocument();
    if(document == null) return ;
    
    HTMLNode root = document.getRoot();

    //search text nodes
    List<HTMLNode> textNodes = searchTextNodes(root);
    //    System.out.println(" no text nodes "+ textNodes.size());
    if(textNodes == null) return ;

    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    if(source == null) return;
    // CONTENT FILTER
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;

    String xmlContent = documentHandler.handle(pluginData); 
    //    System.out.println(" xml content "+xmlContent);
    if(xmlContent == null) return;
    
    Meta meta = pluginData.getMeta();
    xmlContent = preProcess(meta, dictionary, xmlContent, 5*60*60*1000l);

    if(meta.getTitle()  == null || meta.getTitle().isEmpty()) {
      meta.setTitle("default title: " + meta.getId());
    }

    //    System.out.println(" test "+ meta.getTitle() + " : "+ meta.getDesc());

    if((meta.getTitle() == null || meta.getTitle().startsWith("default title"))
        && (meta.getDesc() == null || meta.getDesc().startsWith("empty description"))) {
      LogService.getInstance().setMessage(null, "Ignore "+ link.getAddress() +" - Error: Empty data!");
      return;
    }

    List<XMLResource> resourceNodes = documentHandler.getResources();
    Group group = worker.getExecutor().getResource(Group.class);
    if(group.isDownloadImage()) downloader.download(resourceNodes, pluginData);

    
    IDatabases idatabase = IDatabases.getInstance();
    if((idatabase instanceof ArticleBabuDatabases)) {
      ArticleBabuDatabases databases = (ArticleBabuDatabases) idatabase;
      String id = databases.loadIdByURL(link.getAddress());
      if(id != null) {
        Article article = pluginData.getArticle();
        article.setId(id);
        article.getMeta().setId(id);
        
        LogService.getInstance().setMessage(null,
            "Wiziwig re-download "+ article.getId() + " from " + link.getAddress());
      }
    }
    
    //save data
    if(saveData(pluginData, xmlContent)) handleSuccessfullData(pluginData);
  }
  
  public boolean checkDownloaded(Link link, boolean downloaded) {
    IDatabases idatabase = IDatabases.getInstance();
    if(!(idatabase instanceof ArticleBabuDatabases)) {
      return super.checkDownloaded(link, downloaded);
    }
    ArticleBabuDatabases databases = (ArticleBabuDatabases) idatabase;
    String address = link.getAddress();
    String id = databases.loadTempIdByURL(address);
    if(id != null) return true;
    id = databases.loadIdByURL(address);
    if(id == null) return false;
    Article article = databases.loadArticle(id);
    if(article == null) return false;
    Content content = article.getContent();
    if(content == null) return false;
    String xml = content.getContent();
    try {
      XMLDocument xmlDoc = XMLParser.createDocument(xml, null);
      
      XMLNode root = xmlDoc.getRoot();
      if(root.getChildren() == null
          || root.getChildren().size() < 1) return false;
      
      if(hasURL(xmlDoc)) return true;
      Calendar scheduled = getScheduled(xmlDoc);
      if(scheduled == null) return true;
      
      Calendar current = Calendar.getInstance();
      
//      System.out.println(" thay co "+ id 
//          + " : " + scheduled.getTime() + " : "
//          + (current.getTimeInMillis() - scheduled.getTimeInMillis())
//          + " : " + (scheduled.getTimeInMillis() - current.getTimeInMillis()
//              <= 20*60*1000l));
      
      if(scheduled.getTimeInMillis() - current.getTimeInMillis()
          > 20*60*1000l) return true;
      
      return false;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return true;
  }
  


  public static void main(String[] args) throws Exception {
//    File file = new File("D:\\Temp\\wiziwig.xml");
//    byte [] bytes = RWData.getInstance().load(file);
//    String xml  = new String(bytes, Application.CHARSET);
//    file = new File("D:\\Temp\\wiziwig.properties");
//    Properties dictionary = loadDictionary(file);
//    Meta meta = new Meta();
//    xml = preProcess(meta, dictionary, xml);
//    file = new File("D:\\Temp\\wiziwig2.xml");
//    RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    
//    SimpleDateFormat scheduledFormat = new SimpleDateFormat("E, MMMM dd 'from' HH:mm yyyy");
//    Date date = scheduledFormat.parse("Thu May 03 12:00:00 GMT+07:00 2012");
//    Calendar calendar = Calendar.getInstance();
//    System.out.println(date);
//    System.out.println(calendar.getTime());
    
    String time = "Monday, May 7th from 01:00 to 03:00";
    String diff = "04:05:48";
    
    Calendar from = createFromTime(time, 5*60*60*1000l);
    String to = createToTime(time, 5*60*60*1000l);
    if(from != null && to != null) {
      SimpleDateFormat newScheduledFormat =
          new SimpleDateFormat("E, MMMM dd 'from' HH:mm");
      String value = newScheduledFormat.format(from.getTime());
      value += " to " + to;
      System.out.println(value);
    }
  }


}
