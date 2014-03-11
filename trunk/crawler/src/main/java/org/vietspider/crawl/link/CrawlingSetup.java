/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import static org.vietspider.model.SourceProperties.CONTENT_FILTER;
import static org.vietspider.model.SourceProperties.MIN_SIZE_OF_PAGE;
import static org.vietspider.model.SourceProperties.PAGE_CHECKER;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.crawl.CrawlSessionEntry;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.link.generator.LinkGeneratorBuilder;
import org.vietspider.crawl.plugin.CommonProcessPlugin;
import org.vietspider.crawl.plugin.handler.VietnamesePageChecker;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.link.ContentFilters;
import org.vietspider.link.IPageChecker;
import org.vietspider.link.pattern.JsDocWriterGetter;
import org.vietspider.model.Source;
import org.vietspider.net.client.Proxies;
import org.vietspider.net.client.ProxiesMonitor;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 8, 2008  
 */
public class CrawlingSetup {
  
  private final static String PLUGIN_PACKAGE = "org.vietspider.crawl.plugin.";
  
  public final static String PLUGIN_NAME = "PluginName";
  
  private final static String PLUGIN_EXT = "ProcessPlugin";
  
  public static Class<?> lookPluginClass(String className) {
    SystemProperties properties = SystemProperties.getInstance();
    String type = properties.getValue("crawl.plugin");
    if(type != null && (type = type.trim()).length() > 0) {
      try {
        return CrawlingSession.class.getClassLoader().loadClass(type);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    StringBuilder builder = new StringBuilder(PLUGIN_PACKAGE);
    builder.append(Character.toUpperCase(className.charAt(0)));
    className = builder.append(className.substring(1)).append(PLUGIN_EXT).toString();
    Class<?> clazz = CommonProcessPlugin.class;  
    try {
      clazz = CrawlingSession.class.getClassLoader().loadClass(className);
    } catch (Exception e) {
      clazz = CommonProcessPlugin.class;  
      className = CommonProcessPlugin.class.getName();
    }
    return clazz;
  }

  public static void setup(Source source) {
    Properties properties = source.getProperties();
//    String debug = properties.getProperty("Debug");
//    if(debug != null && debug.trim().length() > 0) {
//      try {
//        int sd = Integer.parseInt(debug);
//        if(sd > 0)  {
//          source.setDebug(sd);
//          properties.setProperty("Debug", String.valueOf(sd-1));
//        } else if(sd == 0) {
//          properties.setProperty("Debug", "");
//        } else {
//          source.setDebug(sd);
//          properties.setProperty("Debug", String.valueOf(sd+1));
//        }
//      } catch (Exception e) {
//      }
//      SourceIO.getInstance().save(source);
//    }
    

    //CONTENT FILTER
    // Thuannd update code
    loadContentFilter(source);
    
    source.setExpireCrawling(getExpireSession(source));
   

    String propertyValue = properties.getProperty(MIN_SIZE_OF_PAGE);
    if(propertyValue != null) {
      try {
        source.setMinSizeOfPage(Long.parseLong(propertyValue.trim()));
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(source, e);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else {
//          LogWebsite.getInstance().setThrowable(source, e);
//        }
      }
    }

    String pageCheckerValue = source.getProperties().getProperty(PAGE_CHECKER);
    if(pageCheckerValue != null && !(pageCheckerValue = pageCheckerValue.trim()).isEmpty()) {
      pageCheckerValue = pageCheckerValue.toLowerCase();
      if(pageCheckerValue.startsWith("vietnam")) {
        VietnamesePageChecker pageChecker = new VietnamesePageChecker();
        source.setPageChecker(pageChecker);
        if(pageCheckerValue.indexOf("#data") > -1) {
          pageChecker.setOnlyCheckData(true);
        } 
        if(pageCheckerValue.indexOf("#locale") > -1) {
          pageChecker.setCheckMode(IPageChecker.LOCALE);
        }
      }
    }

    source.setLinkBuilder(new LinkCreator(source));

    //set url matcher
    //    jsDoPostBack = null;

    if(source.getUpdateRegion() != null 
        && source.getUpdateRegion().getPaths() != null) {
      try {
        NodePathParser pathParser =  new NodePathParser();
        source.setUpdatePaths(pathParser.toNodePath(source.getUpdateRegion().getPaths()));
      } catch (Exception e) {
        LinkLogStorages.getInstance().save(source, e);
//        if(source.isDebug()) {
//          LogSource.getInstance().setThrowable(source, e);
//        } else {
//          LogWebsite.getInstance().setThrowable(source, e);
//        }
        source.setUpdatePaths(null);
      }
    }

    LinkGeneratorBuilder.buildLinkGenerator(source);

    source.setJsDocWriters(new JsDocWriterGetter().getJsDocWriters(properties));
  }


  private static long getExpireSession(Source source) {
    if(source.getPriority() == 0) return 24*60*60*1000l;
    int v = getExpireSession(source.getDepth());

    String proxy = source.getProperties().getProperty("proxy");
    if(proxy != null && proxy.trim().startsWith("blind")) {
      return 12*v*60*60*1000l;
    }

    String [] homepages = source.getHome();
    int h = 0;
    if(homepages != null && homepages.length > 0) {
      h = getExpireSession(homepages.length);
    }
    return h*v*60*60*1000l;
  }

  private static int getExpireSession(int value) {
    if(value < 10) return 3;
    if(value >= 10 && value < 20) return 5;
    if(value >= 20 && value < 50) return 7;
    if(value >= 50 && value < 100) return 12;
    if(value >= 100 && value < 500) return 15;
    return 2*24;
  }


  public final static boolean hasHomepageDatabase(Source source) {
    String name = NameConverter.encode(source.getFullName());
    return hasHomepageDatabase(name);
  }

  public final static boolean hasHomepageDatabase(String sourceName) {
    File folder = UtilFile.getFolder("sources/homepages/");
    File file  = new File(folder, sourceName +"/" + sourceName + ".url.idx");
    return file.exists();
  }
  
  public static CrawlSessionEntry[] duplicateEntries(
      CrawlingSession session, CrawlSessionEntry entry) {
    if(entry.getPointer() < -1) return null;
    
    WebClient webClient = session.getResource(WebClient.class);
    if(webClient == null) return null;
    String host = webClient.getHost();
    
    Proxies proxies = ProxiesMonitor.getInstance().getProxies(host);
    if(proxies == null || proxies.getTotalExcutor() < 2) return null;
    CrawlSessionEntry [] entries = new CrawlSessionEntry[proxies.getTotalExcutor()];
    for(int k = 0; k < entries.length; k++) {
      entries[k] = new CrawlSessionEntry(entry.getSourceFullName(), true);
      entries[k].setPointer(-2);
    }
//    System.out.println(" tai day ta co "+ entries.length);
    return entries;
//      executor.getPool().getQueueEntry().appendFirst(entries);
  }
  
  private static void loadContentFilter(Source source) {
    // Thuannd update code
    String filter = source.getProperties().getProperty(CONTENT_FILTER);
    if(filter == null || (filter = filter.trim()).isEmpty()) return;
    File folder = UtilFile.getFolder("sources/type");
    String tag = "Content Filters";
    if(filter.indexOf(',') < 0 && filter.indexOf('\n') < 0) {
      File file = UtilFile.getFile(folder, filter);
      if(file.exists()) {
        tag = file.getName();
        try {
//          System.out.println(file.getAbsolutePath());
          filter = new String(RWData.getInstance().load(file), Application.CHARSET);
//          System.out.println("ta co " + filter);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    }
    if("#decode#".equals(filter)) {
      source.setDecode(true);
      return;
    }
    
    if(filter.length() < 1 || filter.charAt(0) == '#') return;
    
//    System.out.println(" da co "+ filter);
    List<String> list = new ArrayList<String>(100);
    int start = 0;
    int idx = 1;
    while(idx < filter.length()) {
      char c = filter.charAt(idx);
      if(c == ',' || c == '\n') {
        String t = addData(list, filter.substring(start, idx));
        if(t != null) tag = t;
        start = idx+1;
        
        if(start < filter.length()  
            && filter.charAt(start) == '\"') {
          start++;
          idx = start+1;
          while(idx < filter.length()) {
            c = filter.charAt(idx);
            if(c == '\\') {
              idx += 2;
              continue;
            }
            if(c == '\"' || c == '\n') {
              t = addData(list, filter.substring(start, idx));
              if(t != null) tag = t;
              if(idx < filter.length() -1 
                  && filter.charAt(idx+1) == ',') {
                idx++;
              }
              start = idx+1;  
              break;
            }
            idx++;
          }
        }
      }
      idx++;
    }
    if(start < filter.length()) {
      addData(list, filter.substring(start));
    }
//    System.out.println(" ket qua ta co "+ list.size());
    String [] elements = list.toArray(new String[list.size()]);
//    for(int i = 0; i < elements.length; i++) {
//      System.out.println(elements[i]);
//    }
//    if(elements.length > 0) System.exit(0);
    
    ContentFilters filters = new ContentFilters(source, elements);
    filters.setTag(tag);
    source.setContentFilters(filters);
  }
  
  private static String addData(List<String> list, String data) {
    data = data.trim();
    String tag = null;
    if(data.indexOf("tag=") > -1) {
      tag = data.substring(data.indexOf('=')+1).trim();
    }
    list.add(data);
    return tag;
  }

}
