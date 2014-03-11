/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.website.IWebsiteDatabases;
import org.vietspider.model.SourceUtils;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
public class RWebsiteDatabases extends Thread implements IWebsiteDatabases {

  private volatile boolean execute = true;
  private volatile long sleep = 15*1000l;

  protected volatile java.util.Set<String> waitUrls = new ConcurrentSkipListSet<String>();

  protected DataClientService client;
  protected File folder;
  
  public RWebsiteDatabases() {
    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close URL Database";}
      public int getPriority() { return 2; }
      public void execute() {
        execute = false;
      }
    });
    this.start();
  }

  public void run() {
    String remote = SystemProperties.getInstance().getValue("website.store");
    if(remote == null || remote.trim().isEmpty()) return;
    client = new DataClientService(remote);
    folder = UtilFile.getFolder("sources/websites/temp/");
    execute  = false;
    try {
      InetAddress inetAddress = InetAddress.getLocalHost();
      Header [] headers = new Header[] {
          new BasicHeader("action", "website.client.host")
      };
      byte [] bytes = inetAddress.getHostAddress().getBytes(Application.CHARSET);
      client.postResponse(URLPath.DATA_HANDLER, bytes, headers);
      
      new SyncConfigSources(this);
      
      execute = true;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    while(execute) {
      commitUrls();
      commitWebsites();
      try {
        Thread.sleep(sleep);
      } catch (Exception e) {
      }
    }
  }

  public void saveWebsites(List<Website> websites) {
    if(client == null) return;
    for (int j = 0; j < websites.size(); j++) {
      save(websites.get(j));
    }
  }

  public void save(Website website) {
    if(client == null) return;
    File file = new File(folder, SourceUtils.getCodeName(website.getAddress()));
    try {
      String xml = Object2XML.getInstance().toXMLDocument(website).getTextValue();
      RWData.getInstance().save(file, xml.getBytes("utf-8"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public void save(String..._urls) {
    if(!WebsiteStorage.DETECT) return;
    Collections.addAll(waitUrls, _urls);
  }

  public void save(List<String> _urls) {
    if(!WebsiteStorage.DETECT) return;
    waitUrls.addAll(_urls);
  }

  public void load(Websites websites) {
    InputStream inputStream =  null;
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "load.website.list")
      };
      
      String xml = Object2XML.getInstance().toXMLDocument(websites).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      bytes = client.post(URLPath.DATA_HANDLER, bytes, headers);
    /*  HttpResponse response = client.loadPostResponse(URLPath.DATA_HANDLER, bytes, headers);
      inputStream = response.getEntity().getContent();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte [] buffer = new byte[16*1024];
      int read = 0;
      while((read = inputStream.read(buffer)) > -1) {
        outputStream.write(buffer, 0, read);
      }
      bytes = outputStream.toByteArray();*/
      
      Websites newWebsites = XML2Object.getInstance().toObject(Websites.class, bytes);
      websites.setList(newWebsites.getList());
    } catch (Exception e) {
    } finally {
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
      }
    }
  }

  public Website search(String host)  {
    InputStream inputStream =  null;
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "load.website.by.host")
      };
      
      byte [] bytes = host.getBytes("utf-8");
      bytes = client.post(URLPath.DATA_HANDLER, bytes, headers);
     /* HttpResponse response = client.loadPostResponse(URLPath.DATA_HANDLER, bytes, headers);
      inputStream = response.getEntity().getContent();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte [] buffer = new byte[16*1024];
      int read = 0;
      while((read = inputStream.read(buffer)) > -1) {
        outputStream.write(buffer, 0, read);
      }
      bytes = outputStream.toByteArray();*/
      return XML2Object.getInstance().toObject(Website.class, bytes);
    } catch (Exception e) {
    } finally {
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
      }
    }
    return null;
  }

  synchronized void commitUrls()  {
    if(waitUrls == null || waitUrls.size() < 1) return;
    if(!WebsiteStorage.DETECT) {
      waitUrls.clear();
      return;
    }
    
    Iterator<String> iterator = waitUrls.iterator();
    StringBuilder builder = new StringBuilder();
    while(iterator.hasNext()) {
      String url = iterator.next();
//      System.out.println(" chuan bi send  "+ url);
      iterator.remove();
      String host = Website.toHost(url);
      if(host == null) continue;
      
      if(builder.length() > 0) builder.append('\n');
      builder.append(url);
    }
    
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "save.website.urls")
      };
      
      byte [] bytes = builder.toString().getBytes(Application.CHARSET);
      client.post(URLPath.DATA_HANDLER, bytes, headers);
    } catch (Exception e) {    
    }
  }

  synchronized void commitWebsites()  {
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return;
    Websites websites = new Websites();
    for(int i = 0; i < Math.min(10, files.length); i++) {
      try { 
        byte [] bytes = (RWData.getInstance()).load(files[i]);
        Website website = XML2Object.getInstance().toObject(Website.class, bytes);
        websites.getList().add(website);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e); 
      } finally {
        files[i].delete();
      }
    }
    
    try {
      Header header = new BasicHeader("action", "save.website.list");
      String xml = Object2XML.getInstance().toXMLDocument(websites).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      client.post(URLPath.DATA_HANDLER, bytes, header);
    } catch (Exception e) {
      for(int i = 0; i < websites.getList().size(); i++) {
        save(websites.getList().get(i));
      }
    }
  }
  
  public void setSleep(long sleep) { this.sleep = sleep; }

}
