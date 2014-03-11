/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.crepo;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.vietspider.browser.form.Param;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.MD5Hash;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.link.LinkCreator;
import org.vietspider.crawl.plugin.ProcessPlugin;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 7, 2007  
 */
public final class SessionStore {

  private int linkCounter = 0;

  private ConcurrentLinkedQueue<Link> homepageQueue;
  private ConcurrentLinkedQueue<Link> visitQueue;
  private ConcurrentLinkedQueue<Link> dataQueue;
  
  private CopyOnWriteArrayList<LinkLog> downloadedQueue;
  private AtomicInteger done;
  
  protected volatile Set<MD5Hash> codes;

  protected Source source;

  protected volatile long lastAccess = System.currentTimeMillis();
  protected volatile LinkStorage storage;

  protected volatile ProcessPlugin processor;

  SessionStore(CrawlingSession executor, Source _source)  throws Throwable {
    this.source = _source;

    this.storage = new LinkStorage(source.getFullName());

    homepageQueue = new ConcurrentLinkedQueue<Link>();
    visitQueue = new ConcurrentLinkedQueue<Link>();
    dataQueue = new ConcurrentLinkedQueue<Link>();
    codes = new ConcurrentSkipListSet<MD5Hash>(new Comparator<MD5Hash>() {
      @Override
      public int compare(MD5Hash m1, MD5Hash m2) {
        return m1.compareTo(m2);
      }
    });
    
    downloadedQueue = new CopyOnWriteArrayList<LinkLog>();
    done = new AtomicInteger();

    WebClientInit webInit = new WebClientInit();
    webInit.init(executor, this, source);
    linkCounter = storage.load(source, visitQueue, dataQueue, LinkStorage.MAX_SIZE);
  }
  
  public void addDownloadedLink(Link link) {
    if(downloadedQueue.size() > 100) return;
    downloadedQueue.add(LinkLogIO.createLinkLog(
        link, "{link.downloaded}", LinkLog.PHASE_DOWNLOAD));
  }
  
  public void addLinkLog(LinkLog link) {
    if(downloadedQueue.size() > 100) return;
    downloadedQueue.add(link);
  }
  
  public int doneLink() {
    return done.incrementAndGet(); 
  }

  protected void addHomepages(String host, String refer, String [] addresses) {
    if(linkCounter >= (LinkStorage.MAX_SIZE/5)) return;
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    LinkPatterns dataTempl = linkCreator.getDataPatterns();
    URLEncoder encoder = new URLEncoder();
    for(String address : addresses) {
//      System.out.println("truoc do "+ address);
      address = encoder.encode(address);
      Link link = linkCreator.create(host, address, 0);
      if(link == null || codes.contains(link.getUrlId())) continue;
//            System.out.println("=== > "+ link.getAddress());

      link.setIsLink(true);
      link.setReferer(refer);
      link.setIsData(dataTempl == null || dataTempl.match(link.getAddress()));

      codes.add(link.getUrlId());
      homepageQueue.add(link);
      if(link.isData()) dataQueue.add(link);
    }
  }

  //  public LinkQueue getQueue() { return linkQueue; }


  void endSession() {
//    System.out.println(source.getFullName());
//    System.out.println(" end session "+ done.get() +  " :  "+ downloadedQueue.size());
    if(done.get() < 10) {
      for(int i = 0; i < downloadedQueue.size(); i++) {
//        System.out.println("log download " + downloadedQueue.get(i).getUrl());
        LinkLogStorages.getInstance().save(downloadedQueue.get(i));
      }
    } 
    done.set(0);
    downloadedQueue.clear();
    
    codes.clear();
    storage.store1(processor, 0);
    if(storage.totalFile() > 10) {
      LinkLogStorages.getInstance().save(source, "{inefficient.configuration.channel}");
      //      System.out.println(" quet khong hieu qua roi "+ source.getFullName());
      //      LogSource.getInstance().setMessage(source, null,  "LinkStorage : {inefficient.configuration.channel}.");
    }
    //    LinkCacher cacher = LinkCacherService.getCacher(source);
    //    if(cacher != null) cacher.save(linkQueue.clone());
  }

  public void push(String host, Link referer, List<Link> list) {
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    int localCounter = 0;
    for(int i = 0; i < list.size(); i++) {
      Link link = list.get(i);
      if(link == null) continue;

      String address = link.getAddress();
      List<Param> params = link.getParams();

      link = linkCreator.create(host, address, referer.getLevel() + 1);
      if(link == null) continue;
      link.setParams(params);
      if(codes.contains(link.getUrlId())) continue;

      LinkPatterns dataTempl = linkCreator.getDataPatterns();
      LinkPatterns visitTempl = linkCreator.getVisitPatterns();

      if(source.getDepth() > 1) {
        link.setIsLink(visitTempl == null || visitTempl.match(address));
      } else {
        link.setIsLink(false);
      }
      link.setIsData(dataTempl == null || dataTempl.match(address));
//      System.out.println(link.getAddress());
//      System.out.println("is data "+link.isData()+ ", is link "+ link.isLink());
      if(!link.isLink() && !link.isData()) continue;

      link.setReferer(referer.getAddress());

      codes.add(link.getUrlId());
      if(linkCounter >= LinkStorage.MAX_SIZE) {
        storage.add(processor, link);
        continue;
      }
      
      if(link.isData()) {
        dataQueue.add(link);
      } else {
        visitQueue.add(link);
      }
      linkCounter++;
      localCounter++;
    }
    
    if(localCounter > 0) return;
    
    for(int i = 0; i < list.size(); i++) {
      LinkLogIO.saveLinkLog(list.get(i), "{invalid.link}", LinkLog.PHASE_DOWNLOAD);
    }
    
  }

  public void push(List<Link> list) { 
    for(int i = 0; i < list.size(); i++) {
      Link link = list.get(i);
      if(codes.contains(link.getUrlId())) continue;
      codes.add(link.getUrlId());

      if(linkCounter >= LinkStorage.MAX_SIZE) {
        storage.add(processor, link);
        continue;
      }

      if(link.isData()) {
        dataQueue.add(link);
      } else {
        visitQueue.add(link);
      }
      linkCounter++;
    }
  }

  void setLastAccess() { lastAccess = System.currentTimeMillis(); }
  boolean isTimeout() { return System.currentTimeMillis() - lastAccess >= 1*60*1000; }

  public int size() { 
    return homepageQueue.size() + visitQueue.size() + dataQueue.size(); 
  }

  public boolean contains(MD5Hash code) { return codes.contains(code); }

  public void addCode(MD5Hash code) { codes.add(code); }

  public boolean isEmpty() {
    //    System.out.println(" thay co "+ homepageQueue.size()+  " / "+ linkQueue.size());
    return homepageQueue.isEmpty() && visitQueue.isEmpty() && dataQueue.isEmpty(); 
  }

  public Link poll() {
    if(!dataQueue.isEmpty())  {
      return dataQueue.poll();
    }
    if(!visitQueue.isEmpty()) return visitQueue.poll();
    if(!homepageQueue.isEmpty()) return  homepageQueue.poll();
    return null;
  }

  void checkDownloaded() {
    //    long start = System.currentTimeMillis();
    Iterator<Link> iterator = dataQueue.iterator();
    int counter = 500;
    while(iterator.hasNext()) {
      Link link =  iterator.next();
      if(processor.checkDownloaded(link, false))  {
        //        System.out.println(" da bi loai bo "+ link.getAddress());
        iterator.remove();
      }
      counter--;
      if(counter <= 0) break;
    }
    //    System.out.println("mat tong cong "+ (System.currentTimeMillis() - start));

  }

  public void setProcessor(ProcessPlugin _processor) {
    if(processor != null && processor.getClass() == _processor.getClass()) return;
    this.processor = _processor;
  }

}
