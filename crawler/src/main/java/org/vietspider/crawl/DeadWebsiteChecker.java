/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.crawl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 10, 2012
 */
//thuannd update code
public class DeadWebsiteChecker {
  
  private volatile static DeadWebsiteChecker CHECKER;
  
  static synchronized DeadWebsiteChecker getInstance() {
    if(CHECKER == null) CHECKER = new DeadWebsiteChecker();
    return CHECKER;
  }
  
  private List<String> deadMessages = new ArrayList<String>();
  private long lastModified = -1;
  
  boolean check(WebClient webClient, Link link) {
    loadMessage();
    if(deadMessages.size() < 1) return false;
    int idx = link.getAddress().indexOf('/', 10);
    if(idx < 0) idx = link.getAddress().indexOf('?', 10);
    String address = link.getAddress();
    if(idx > 0) address = address.substring(0, idx);
    try {
      HttpMethodHandler httpHandler = new HttpMethodHandler(webClient);
      /*HttpResponse response = */httpHandler.execute(address, link.getAddress());
      byte [] data = httpHandler.readBody();
      String html = new String(data).toLowerCase();
      for(int i = 0; i < deadMessages.size(); i++) {
        if(html.indexOf(deadMessages.get(i)) < 0) continue;
        LinkLogIO.saveLinkLog(link, "Website Dead: " + deadMessages.get(i), LinkLog.PHASE_DOWNLOAD);
        Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
        SourceIO.getInstance().savePriority(source, 24);
        //SourceIO.getInstance().putDisableSource(link.getSource());
        return true;
      }
    } catch (Exception e) {
      LinkLogIO.saveLinkLog(link, e.toString(), LinkLog.PHASE_DOWNLOAD);
    }
    return false;
  }
  
  void loadMessage() {
    File file = new File(UtilFile.getFolder("sources/type"), "404.message.txt");
    try {
      if(!file.exists() || file.length() < 1) {
        StringBuilder builder = new StringBuilder();
        builder.append("To change this page, upload your website into the public_html directory\n");
        builder.append("website đang bảo trì\n");
        builder.append("This Account Has Been Suspended\n");
        builder.append("Suspended Domain\n");
        builder.append("Apache is functioning normally\n");
        builder.append("If you are seeing this page instead of the site you expected\n");
        builder.append("may be pending for final\n");
        builder.append("Not Acceptable\n");
        builder.append("googlesyndication.com\n");
        builder.append("Invalid Hostname\n");
        builder.append("It may be possible to restore access to this site\n");
        builder.append("/cgi-sys/defaultwebpage.cgi\n");
        builder.append("Directory Listing Denied\n");
        builder.append("this domain may be for sale\n");
        builder.append("no Web site at this address\n");
//        builder.append("\n");
//        builder.append("\n");
//        builder.append("\n");
//        builder.append("\n");
//        builder.append("\n");
//        builder.append("\n");
//        builder.append("\n");
        RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
      }
      if(file.lastModified() <= lastModified) return;
      lastModified = file.lastModified();
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim().toLowerCase();
        if(elements[i].length() < 2) continue;
        deadMessages.add(elements[i]);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public static void main(String[] args) {
    String address = "http://www.timnhanhcanho.com/";
    try {
      WebClient webClient = new WebClient();
      webClient.setURL(address, new java.net.URL(address));
      
      HttpMethodHandler httpHandler = new HttpMethodHandler(webClient);
      /*HttpResponse response = */httpHandler.execute(address, address);
      byte [] data = httpHandler.readBody();
      String html = new String(data).toLowerCase();
      System.out.println(html);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
