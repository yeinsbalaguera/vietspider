/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.crawl;

import java.util.List;

import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.crepo.WebClientInit;
import org.vietspider.crawl.io.access.SourceTrackerService;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.crawl.plugin.CommonProcessPlugin;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.io.CrawlSourceLog;
import org.vietspider.io.model.GroupIO;
import org.vietspider.link.generator.LinkGeneratorInvoker;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.pool.Session;
import org.vietspider.pool.Task;
import org.vietspider.pool.Worker;
//8,9,10,12,14,16,17,18,19,20,21,22,23
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 10, 2007  
 */
public final class CrawlingSession extends Session<String, Link> {
  
  public CrawlingSession(CrawlingPool pool, int index) throws Exception {
    super(pool, index);
  }
  
  public void newSession(String sourceFullName) throws Throwable {
    if(sourceFullName == null) return;
    
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    if(source == null) return;
    
    this.value = null;

    PageDownloadedTracker.getInstance().registry(source);
    
    WebClient webClient = new WebClient();
    WebRedirectHandler redirectHandler = new WebRedirectHandler();
    webClient.setRedirectHandler(redirectHandler);
    putResource(webClient);
    
    CrawlingSetup.setup(source);
    
    Group group = GroupIO.getInstance().loadGroups().getGroup(source.getGroup());
    putResource(group);
    
    String pluginClassName = source.getGroup().toLowerCase().trim();
    Class<?> clazz = CrawlingSetup.lookPluginClass(pluginClassName);
//    System.out.println(" bebeb  "+ clazz);
    
    //create source resource
//    putResource(new SourceResource(source));
    putResource(new CrawlSourceLog());
    
    // set link store
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) {
      store = SessionStores.getInstance().create(this, source);
    } else {
//      System.out.println(" da thay crawl "+ store);
      WebClientInit webInit = new WebClientInit();
      webInit.init(this, store, source);
    }
    
    redirectHandler.setCodes(store);
    putResource(store);
    
    for(Worker<String, Link> worker : workers) {
      worker.putResource(new HTMLDataExtractor());
      
      ProcessPlugin plugin = null;
      try {
        plugin = (ProcessPlugin) clazz.newInstance();
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(source, e.toString());
        plugin = new CommonProcessPlugin();
      }
      
      plugin.setWorker(worker);
      worker.putResource(CrawlingSetup.PLUGIN_NAME, pluginClassName);
      worker.putResource(pluginClassName, plugin);
      plugin.setProcessRegion(source);
      
      store.setProcessor(plugin);
      
      worker.getResource(HTMLDataExtractor.class).newSession(source);
    }
    
    this.value = sourceFullName;
    
    SourceTrackerService.getInstance().write(source);
  }
  
  public synchronized void endSession(boolean abort) {
    WebClient webClient = getResource(WebClient.class);
    if(webClient != null) webClient.shutdown();
    
    if(value == null) return;
    
    for(Worker<String, Link> worker : workers) {
      if(abort) worker.abort();
      String pluginName = worker.getResource(CrawlingSetup.PLUGIN_NAME);
      ProcessPlugin plugin = worker.<ProcessPlugin>getResource(pluginName);
      if(plugin != null) plugin.endSession();
      worker.clearResources();
    }
    
    Source source = CrawlingSources.getInstance().getSource(value);
    if(value != null) {
      LinkGeneratorInvoker.invokeEndSession(source.getLinkGenerators());
      SessionStores.remove(source.getCodeName());
      
      if(abort) LinkLogStorages.getInstance().save(source, "{crawling.stop.by.user}");
    }
    
    value = null;
    clearResources();
    
    Runtime.getRuntime().gc();
  }
  
  public boolean isEndSession() {
    if(value == null) return true;
    
    Source source = CrawlingSources.getInstance().getSource(value);
    if(source == null) return true;
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
//    System.out.println(value.getFullName() + " : "+ store.hashCode());
    if(store == null) {
      LinkLogStorages.getInstance().save(source, "{crawling.end.by.store}");
//      System.out.println("end session by store null");
      return true;
    }
    
//    System.out.println(value.getFullName() + " : "+ store.hashCode() +"  timeout "+ sourceResource.isTimeout());
    if(source.isTimeout()) {
      LinkLogStorages.getInstance().save(source, "{crawling.end.by.timeout}");
//      System.out.println("end session by timeout");
      return true;
    }

//    System.out.println("end session by no link  : "+ !store.isEmpty());
    if(!store.isEmpty()) return false;
    
    for(int i = 1; i < workers.length; i++) { 
      if(!workers[i].isFree()) return false;
    }
    
    if(value != null) {
      try {
        Link link = new Link(source.getHome()[0], 0, null, value);
        WebClient  webClient = getResource(WebClient.class);
        if(!DeadWebsiteChecker.getInstance().check(webClient, link)) {
          LinkLogStorages.getInstance().save(source, "{crawling.end}");  
        }
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(source, e.toString());
      }
    } 
    return true;
  }
  
  public boolean isWorking(String key) { 
    if(value == null) return false;
    return value.equals(key);
  }
  
  public synchronized Link nextElement() {
    if(value  == null) return null;
    
    Source source = CrawlingSources.getInstance().getSource(value);
    if(source == null) return null;
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return null;
    
    if(source.isTimeout()) {
      LinkLogStorages.getInstance().save(source, "{source.timeout}");
      return null;
    }
//    SessionStore store = getResource(SessionStore.class);
    Link link  = store.poll();
    return link;
  }
  
  public void addElement(List<Link> list, Link referer) {
//    System.out.println("referer " + referer.getAddress());
//    System.out.println(" co duoc "+ list.size());
    if(value == null || !value.equals(referer.getSourceFullName())) {
//      Source source = referer.getSource();
      LinkLogIO.saveLinkLog(referer, "{invalid.source}", LinkLog.PHASE_DOWNLOAD);
      return;
    }
    
    WebClient webClient = getResource(WebClient.class);
    if(list.size() < 1) {
      //check website died or not
      if(!DeadWebsiteChecker.getInstance().check(webClient, referer)) {
        LinkLogIO.saveLinkLog(referer, "{empty.link}", LinkLog.PHASE_DOWNLOAD);
      }
    }
   
    Source source = CrawlingSources.getInstance().getSource(value);
    SessionStore store = SessionStores.getStore(source.getCodeName());
//    System.out.println(" add item 2 "+ value.getFullName() + " : "+ store.hashCode());
    if(store != null) store.push(webClient.getHost(), referer, list);
//    System.out.println(" add item 2 "+ value.getFullName() + " : "+ isEndSession());
  }
  
  public void addElement(List<Link> list, String sourceFullName) {
    if(value == null || !value.equals(sourceFullName)) return;
    Source source = CrawlingSources.getInstance().getSource(value);
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store != null) store.push(list);
  }
  
  public Task<?> [] createTasks() {
    return new Task<?>[] {
        new Step2VisitLink(),
        new Step3ProcessRssData(),
        new Step3ProcessHtmlData()
    };
  }
  
}
