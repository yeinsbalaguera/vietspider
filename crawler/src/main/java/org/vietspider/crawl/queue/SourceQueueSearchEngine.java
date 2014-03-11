/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.queue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 14, 2008  
 */
class SourceQueueSearchEngine {
  
  void create(Source source, String filter) {
    String host = null;
    try {
      URL url = new URL(source.getHome()[0]);
      host = url.getHost();
      if(host.toLowerCase().startsWith("www")) {
        int index = host.indexOf('.');
        if(index > -1)  host = host.substring(index+1, host.length());
      }
    } catch (Exception e) {
    }
    filter = filter.replace('^', ',');  
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      createGoogle(source, host, filter.split(","));
    }
  }
  
//  void create(Source source, String filter) {
//    File folder = UtilFile.getFolder("sources/websites/se/");
//    List<CrawlSessionEntry> entries = new ArrayList<CrawlSessionEntry>();
//    if(!folder.exists()) return entries;
//    File [] files = folder.listFiles();
//    if(files == null || files.length < 1) return entries;
//    String host = null;
//    try {
//      URL url = new URL(source.getHome()[0]);
//      host = url.getHost();
//      if(host.toLowerCase().startsWith("www")) {
//        int index = host.indexOf('.');
//        if(index > -1)  host = host.substring(index+1, host.length());
//      }
//    } catch (Exception e) {
//      return entries;
//    }
//    String [] filters = filter.split(",");
//    String linkGenerator = source.getFullName();
//    
//    for(int i = 0; i < files.length; i++) {
//    try {
//      String xml = new String(RWData.getInstance().load(files[i]), Application.CHARSET);
//      if(xml == null || xml.trim().length() < 1) return entries;
//      XMLDocument document = XMLParser.createDocument(xml, null);
//      for(String element : filters) {
//        Source seSource = createGoogle(source);
//        Source seSource = XML2Object.getInstance().toObject(Source.class, document);

//        String homepage = seSource.getHome()[0];
//        homepage = homepage.replaceAll("vietspider_host", host);
//        homepage = homepage.replaceAll("vietspider_pattern", element.trim());
//        seSource.setHome(new String[]{homepage});
//
//        seSource.setGroup(source.getGroup());
//        seSource.setCategory(source.getCategory());
//
//        seSource.getProperties().setProperty(LINK_GENERATOR, linkGenerator);
//      System.out.println(homepage);
//
//        String line = seSource.getGroup() + "." + seSource.getCategory()+ "." + seSource.getName();
//        entries.add(new CrawlSessionEntry(line, seSource, true));
//      }
//
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(source, e);
//    }
//    }
//    return entries;
//  }
  
  private void createGoogle(Source source, String host, String [] filters) {
    String google = "http://www.google.com.vn/search?hl=vi&q=site:vietspider_host+\"vietspider_pattern\"&btnG=Tìm+kiếm&meta=";
//    String google = "http://www.google.com.vn/search?hl=vi&q=site%3Avietspider_host+%22vietspider_pattern%22&btnG=T%C3%ACm+v%E1%BB%9Bi+Google&meta=";
    List<String> list = new ArrayList<String>();
    
    for(String element : filters) {
//      Source seSource = XML2Object.getInstance().toObject(Source.class, document);
      String homepage = new String(google); 
      
      homepage = homepage.replaceAll("vietspider_host", host);
      homepage = homepage.replaceAll("vietspider_pattern", element.trim());
      
      list.add(homepage);
    }
    
    String [] addresses = source.getHome();
    for(String address : addresses) {
      list.add(address);
    }
    
    source.setHome(list.toArray(new String[list.size()]));
    
    String linkPattern = source.getProperties().getProperty("LinkPattern");
    if(linkPattern == null) return;
    linkPattern = linkPattern.trim();
    if(linkPattern.length() > 0) linkPattern += "\n";
    linkPattern += "http://www.google.com.vn/search?q=site:*&hl=vi&start=*&sa=N";
    source.getProperties().setProperty("LinkPattern", linkPattern) ;
  }
  
}
