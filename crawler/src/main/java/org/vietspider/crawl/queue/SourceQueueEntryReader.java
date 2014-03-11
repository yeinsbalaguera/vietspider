/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import static org.vietspider.model.SourceProperties.CONTENT_FILTER;
import static org.vietspider.model.SourceProperties.HOMEPAGE_FILE_DOWNLOADING;
import static org.vietspider.model.SourceProperties.MAX_EXECUTOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.io.CrawlHomepageDatabase;
import org.vietspider.crawl.link.CrawlingSetup;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.model.SourceProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public final class SourceQueueEntryReader  {
  
  public CrawlSessionEntry[] createSessionEntry(String line1) {
    /*String [] elements = line.split("\\.");
    if(elements.length < 3) return null;
    
    NameConverter converter = new NameConverter();
    String group = NameConverter.encode(CharsUtil.cutAndTrim(elements[1]));
    String category = NameConverter.encode(CharsUtil.cutAndTrim(elements[2]));
    String name  = category+"."+NameConverter.encode(CharsUtil.cutAndTrim(elements[0]));
    
    Source source = SourceIO.getInstance().loadSource(group, category, name);*/
    Source source = SourceIO.getInstance().loadSource(line1);
    if(source == null) {
      LinkLogStorages.getInstance().sourceNull(line1);
      return null;
    }
    return createSessionEntry(source);
  }
  
  public CrawlSessionEntry[] createSessionEntry(Source source) {
    String sourceFullName = source.getFullName();
    
    Properties properties = source.getProperties();
    
    try {
      String filter = properties.getProperty(CONTENT_FILTER);
      
      if(filter != null 
          && !(filter = filter.trim()).isEmpty() 
          && filter.charAt(0) != '#') {
        SourceQueueSearchEngine searchEngine = new SourceQueueSearchEngine();
        /*List<CrawlSessionEntry> entries = */
        searchEngine.create(source, filter);
//        entries.add(new CrawlSessionEntry(line, source, false));
//        return entries.toArray(new CrawlSessionEntry[entries.size()]);
        return new CrawlSessionEntry[]{new CrawlSessionEntry(sourceFullName, false)};
      }
      
      if(Application.LICENSE != Install.SEARCH_SYSTEM) {
        if(source.getPriority() > 48) source.setPriority(24);
        return new CrawlSessionEntry[]{new CrawlSessionEntry(sourceFullName, false)};
      }
      
      String hpTemplate = SourceProperties.getHomepageTemplate(properties);
      if(hpTemplate == null || hpTemplate.trim().isEmpty()) {
        return new CrawlSessionEntry[]{new CrawlSessionEntry(sourceFullName, false)};
      }
      
      String sourceName = NameConverter.encode(sourceFullName);
      
      if(!CrawlingSetup.hasHomepageDatabase(sourceName)) {
        return new CrawlSessionEntry[]{new CrawlSessionEntry(sourceFullName, false)};
      }
     
      if(!SourceQueueValidator.getInstance().shortValidate(source)) return null;
      if(!checkTotalExecutor(properties, sourceFullName)) return null;
      
      String homepages[] = loadHomepages(source/*, files*/);
      if(homepages == null || homepages.length < 1) return null;
      
      source.setHome(homepages);
      return new CrawlSessionEntry[]{new CrawlSessionEntry(sourceFullName, true)};
//      return copySources(source, line, loadHomepages(source/*, files*/));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  public String[] loadHomepages(Source source/*, File [] files*/) {
    String name  = source.getFullName();
    SourceQueueValidator validator =  SourceQueueValidator.getInstance();
    long timeout = validator.computeTimeout(source);
    CrawlHomepageDatabase database = new CrawlHomepageDatabase(name, timeout);
    
    if(!database.exists()) return new String[]{};
    
    Properties properties = source.getProperties();
    String downloading = properties.getProperty(HOMEPAGE_FILE_DOWNLOADING);
    int page = 1;
    try {
      page = Integer.parseInt(downloading);
    } catch (Exception e) {
      page = 1;
    }
    

    String [] elements = new String[0];
    try {
      List<String> list = new ArrayList<String>();
      page = database.loadCrawlList(list, page); 
      elements = list.toArray(new String[list.size()]);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
      elements = new String[0];
      page = 1;
    }
    
    downloading = String.valueOf(page);
    SourceIO.getInstance().saveProperty(source, HOMEPAGE_FILE_DOWNLOADING, downloading);
    return elements;
  }
  
  private boolean checkTotalExecutor(Properties properties, String key) {
    int maxExecutor = 5;
    try {
      if(properties.containsKey(MAX_EXECUTOR)) {
        maxExecutor = Integer.parseInt(properties.getProperty(MAX_EXECUTOR));
      }
    } catch (Exception e) {
      maxExecutor = 5;
    }
    
    int totalExecutor = CrawlService.getInstance().getThreadPool().countExecutor(key);
    return totalExecutor < maxExecutor; 
  }
  
}
