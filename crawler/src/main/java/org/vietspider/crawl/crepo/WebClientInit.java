/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.crepo;

import static org.vietspider.link.generator.Generator.HOMEPAGE_GENERATOR;
import static org.vietspider.link.generator.LinkGeneratorInvoker.invoke;

import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.MalformedChunkCodingException;
import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.browser.RefererFormHandler;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlerPoolPing;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.model.Source;
import org.vietspider.model.SourceProperties;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.pool.timer.TimerMonitor;
import org.vietspider.pool.timer.TimerWorker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 22, 2009  
 */
public class WebClientInit implements TimerWorker, Runnable {

  private Source source;
  private CrawlingSession executor;
  private SessionStore store;

  private boolean isLive = true;
  private HttpMethodHandler methodHandler;
//  private Thread thread;
  private volatile long timeout = 60*1000;

  public void init(CrawlingSession _executor, SessionStore _store, Source _source) {
    if(_source == null || _store == null) return;
    this.executor = _executor;
    this.store = _store;
    this.source = _source;
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
//      this.thread = new Thread(this);
//      this.thread.start();
//    } else {
    run();
//    }
  }

  public void run() {
    //     long time = System.currentTimeMillis();
//        System.out.println(" ====  >"+ hashCode()+ " started ");
    try {
      setup();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    isLive = false;
    //    time  = System.currentTimeMillis() - time;
    //    System.out.println(" ====  >"+ hashCode() + "( "+ time+"/"+timeout +" )"+ " finished ");
  }

  public void setup() throws Throwable {
    WebClient webClient = executor.getResource(WebClient.class);
    Properties properties = source.getProperties();

    methodHandler = new HttpMethodHandler(webClient);

    String userAgent = null;
    if(properties.containsKey(SourceProperties.USER_AGENT)) {
      userAgent = properties.getProperty(SourceProperties.USER_AGENT).trim();
      if(userAgent != null && userAgent.trim().isEmpty()) userAgent = null;
    }

    webClient.setUserAgent(userAgent);

    String [] addresses = source.getHome();
    
//    System.out.println(" step  1");
//    for(int i = 0; i < addresses.length; i++) {
//      System.out.println("home "+ addresses[i]);
//    }

    List<Object> linkGenerator = source.getLinkGenerators();
    List<String> listHomepage = new ArrayList<String>();
    invoke(linkGenerator, HOMEPAGE_GENERATOR, listHomepage);
    if(listHomepage.size() > 0) {
      addresses = listHomepage.toArray(new String[listHomepage.size()]);
    }
    
//    System.out.println(" step  2");
//    for(int i = 0; i < addresses.length; i++) {
//      System.out.println("home "+ addresses[i]);
//    }

    URL url = null; 
    try {
      url = new URL(addresses[0]);
    } catch (Exception e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
    }
    if(url == null) return;

    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    String referer = linkCreator.getRefererURL();
    String proxy = properties.getProperty(HttpSessionUtils.PROXY);
    RefererFormHandler refererFormHandler = new RefererFormHandler(webClient);

    if(referer != null && referer.trim().length() > 0) {
      timeout += 2*60*1000;
    }
    String loginValue  = properties.getProperty("Login");
    if(loginValue != null && !loginValue.trim().isEmpty()) timeout += 3*60*1000;

    TimerMonitor timerMonitor = new TimerMonitor(this);
    timerMonitor.startSession();

    try {
      if(proxy != null && proxy.trim().startsWith("blind")) {
        refererFormHandler .execute(referer, webClient.setURL(referer, url, proxy));
        proxy = null;
      } else { 
        refererFormHandler .execute(referer, webClient.setURL(referer, url));
      }

      if(referer != null && referer.trim().length() > 0) {
        methodHandler.execute(referer, "");
      }

    } catch (MalformedChunkCodingException e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
      return;
    } catch (IllegalStateException e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
      return;
    } catch (UnknownHostException e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
      CrawlerPoolPing.getInstance().increaTime();
    } catch (SocketException e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
      return;
    } catch (Exception e) {
      LinkLogStorages.getInstance().save(source, e, addresses[0]);
//      LogWebsite.getInstance().setMessage(source, e, addresses[0]);
      return;
    }
    
//    System.out.println(" step  2");
//    for(int i = 0; i < addresses.length; i++) {
//      System.out.println("home "+ addresses[i]);
//    }

    //set proxy and login to site 
    Properties systemProperties = SystemProperties.getInstance().getProperties();
    HttpSessionUtils httpSessionUtils = new HttpSessionUtils(methodHandler, source);
    httpSessionUtils.setProxy(systemProperties, proxy);
    try {
      if(!httpSessionUtils.login(loginValue, source.getEncoding(), url, referer)) {
        LinkLogStorages.getInstance().save(source, "Cann't login to website", url.toString());
//        LogWebsite.getInstance().setMessage(source, null, "Cann't login to website");
      }
    } catch (Exception e) {
      LinkLogStorages.getInstance().save(source, e, url.toString());
//      LogWebsite.getInstance().setMessage(source, e, "Cann't login to website");
    }

    String host = webClient.getHost();
    
//    System.out.println(" step  3");
//    for(int i = 0; i < addresses.length; i++) {
//      System.out.println("home "+ addresses[i]);
//    }

    //    store.loadFile(source);
    //    LinkQueue linkQueue = store.getQueue();
    //    System.out.println(" chuan bi load them mot mo "+ addresses);
    //    if(linkQueue.size() >= SessionTempLinkHandler.MAX_SIZE_LINK) return;
    //      System.out.println(" load vao "+ addresses.length);
    store.addHomepages(host, referer, addresses);
  }

  public void abort() {
    //    System.out.println(" ====  >"+ hashCode()+ " aborted ");
    methodHandler.abort();
    isLive = false;
  }

  public long getTimeout() { return timeout; }

  public boolean isFree() { return false; }

  public boolean isLive() { return isLive; }

  public boolean isPause() { return false; }

  public void newSession() { }

  public void destroy() { }
}
