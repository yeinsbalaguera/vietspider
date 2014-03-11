/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.vietspider.bean.website.Website;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.db.database.WebsiteSaveSiteService;
import org.vietspider.db.website.WebsiteDatabases;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.locale.vn.VietnameseDataChecker;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 14, 2009  
 */
class WebsiteDetector implements Runnable {

  protected final static long  TIMEOUT = 3*60*1000;

  protected volatile long start = System.currentTimeMillis();

  protected VietnameseDataChecker langChecker;
  protected WebsiteDatabases database;

  protected Website website;

  protected volatile Thread thread = null;
  protected HttpMethodHandler httpMethodHandler;

  protected String [] vietnamLabels = {
      "vietnam", "viet name", "vietnamese", 
      "ha noi", "da nang", "ho chi minh", "vung tau", "ha phong", "can tho", "da lat"  
  };

  public WebsiteDetector(WebsiteDatabases database, VietnameseDataChecker langChecker) {
    this.database = database;
    this.langChecker = langChecker;
  }

  public void start(Website website_) {
    this.website = website_;
    if(website == null) return;

    thread = new Thread(this);
    thread.start();
  }

  public void run() {
    try {
      start = System.currentTimeMillis();

//      LogService.getInstance().setMessage(null, "detect "+ website.getAddress());
//      System.out.println(" detect "+ website.getAddress());

      String html = null;
      List<NodeImpl> tokens = detect(website.getAddress());
      if(website == null) return;
      if(tokens != null) html = new TokenHtmlRenderer().buildHtml(tokens);
      
//      System.out.println(" ducot html "+ html);

      website.setTimeDownload(website.getTimeDownload() + 1);
      if(html == null) {
        if(website.getTimeDownload() >= 3) {
          website.setHtml("no content");
          website.setLanguage("en");
          website.setStatus(Website.UNAVAILABLE);
        }
        WebsiteStorage.getInstance().save(website);
        return;
      }

      String text = html.toLowerCase();
      if(text.indexOf("sponsored listings") > -1 
          || text.indexOf("buy this domain") > -1) {
        website.setStatus(Website.UNAVAILABLE);
      } else if(text.indexOf("under construction") > -1) {
        website.setStatus(Website.UNAVAILABLE);
      } else if (text.indexOf("advertise@dotvn.com") > -1) {
        website.setStatus(Website.UNAVAILABLE);
      } else if(text.indexOf("invalid hostname") > -1 
          || text.indexOf("directory listing denied") > -1
          || text.indexOf("forbidden") > -1 
      ) {
        website.setStatus(Website.UNAVAILABLE);
      }

      if(website.getStatus() == Website.NEW_ADDRESS) {
        for(int i = 0; i < vietnamLabels.length; i++) {
          if(text.indexOf(vietnamLabels[i]) > -1) {
            website.setDesc("vietnam");
            break;
          }
        }
      }

      if(website.getLanguage().equalsIgnoreCase("en")) {
        text = html.toLowerCase();
        if(text.indexOf("sponsored listings") > -1 
            && text.indexOf("buy this domain") > -1) {
        }
        WebsiteSaveSiteService.save(website.getAddress(), html);
      }

      //    System.out.println(html);

      website.setHtml(html);
      WebsiteStorage.getInstance().save(website);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  protected List<NodeImpl> detect(String address) {
    List<String> links = new ArrayList<String>();
    links.add(address);

    int level = 0;
    int counter = 0;
    List<NodeImpl> tokens = null;
    while(true) {
      List<String> newLinks = new ArrayList<String>();
      for(int i = 0; i < links.size(); i++) {
//        System.out.println(" co na "+ links.get(i));
        tokens = detect(links.get(i), 0, newLinks);
        if(website == null) return tokens;
        if("vn".equals(website.getLanguage())) return tokens;
        counter++;
        if(counter > 5) break;
      }
      links = newLinks;

      level++;
      if(level > 2 || counter > 5) break;
    }
    //    System.out.println(" vay la " + website.getLanguage() + "  : "+ website.getAddress());
    return tokens;
  }

  private List<NodeImpl> detect(String address, int time, List<String> links) {
    //  String address = website.getAddress();
    WebClient webClient = new WebClient();
    WebsiteRedirectHandler redirect = new WebsiteRedirectHandler();
    webClient.setRedirectHandler(redirect);
    InputStream webInput = null;

    try {
      URL url =  new URL(address);
      webClient.setURL(null, url);

//            System.out.println(" chuan bi download "+ address);
      httpMethodHandler = new HttpMethodHandler(webClient);
      HttpResponse response = httpMethodHandler.execute(address, address);

      if(response == null)  return null;

      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      //    System.out.println(" === >"+ address+ " : "+ statusCode);
      switch (statusCode) {
      case HttpStatus.SC_NOT_FOUND:
      case HttpStatus.SC_SERVICE_UNAVAILABLE:
        String host = url.getHost();
        if(time < 1)  return detect("http://"+host, time+1, links);
        //      System.out.println(" === >"+ address+ " : "+ statusCode);
        return null;
      }

      HttpEntity httpEntity = response.getEntity();
      if(httpEntity == null) return null;
//        setUnvailable(website, "no http entity response");
//        return null;
//      }

      byte [] bytes = httpMethodHandler.readBody();

      //    if(level > 0) System.out.println(" chuan bi detect "+ address + " : "+ bytes.length);

      if(bytes == null || bytes.length < 1) return null; 
//      {
//        setUnvailable(website, "empty data response");
//        return null;
//      }
      
      website.setLanguage("en");
      
      HTMLParserDetector parserDetector = new HTMLParserDetector(website.getCharset());
      parserDetector.setDecode(false);

      List<NodeImpl> tokens = parserDetector.createTokens(bytes);

      //      System.out.println(document.getTextValue());
      //    System.out.println("charset is " + parserDetector.getCharset());

      HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
      URLUtils urlUtils = new URLUtils();

      String text = new TokenTextRenderer().buildTextValue(tokens);
      RefsDecoder decoder = new RefsDecoder();
      text = new String(decoder.decode(text.toCharArray()));

      if(redirect.getURI() != null)  url = redirect.getURI().toURL();
      
      if(CharacterUtils.isVietnamese(text)) {
        website.setLanguage("vn");
        hyperLinkUtil.createFullNormalLink(tokens, url);
        return tokens;
      } else if (langChecker.checkTextData(text)) {
        website.setLanguage("vn");
        hyperLinkUtil.createFullNormalLink(tokens, url);
        return tokens;
      }

      List<String> values = hyperLinkUtil.scanSiteLink(tokens);

      for(int i = 0; i < Math.min(values.size(), 10); i++) {
        String link = values.get(i);
        if(link == null) continue;
        //      System.out.println(link);
        link = urlUtils.createURL(address, link).trim();
        link = urlUtils.getCanonical(link);
        if(!SWProtocol.isHttp(link)) link = "http://" + url.getHost() + link;
        //      System.out.println(" co na "+ link + "    "+ link .length());
        links.add(link);
      }
      hyperLinkUtil.createFullNormalLink(tokens, url);
      return tokens;
    } catch (MalformedURLException e) {
      try {
        database.remove(website.getHost());
      } catch (Throwable e1) {
        LogService.getInstance().setThrowable(e1);
      }
    } catch (MalformedChunkCodingException e) {
      return null;
//      setUnvailable(website, "Chunked stream ended unexpectedly");
    } catch (NoHttpResponseException e) {
//      setUnvailable(website, "no http response");
      return null;
    } catch (SocketException e) {
//      setUnvailable(website, "socket closed");
      return null;
    } catch (UnknownHostException e) {
      return null;
//      setUnvailable(website, "unknown host");
    } catch (Exception e) {
//      setUnvailable(website, e.toString());
      LogService.getInstance().setThrowable(e);
      return null;
    } finally {
      webClient.shutdown();
      try {
        if(webInput != null) webInput.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return null;
  }

  boolean isTimeout() {
    if(thread == null 
        || thread.isInterrupted() || !thread.isAlive()) return true;
    if(System.currentTimeMillis() - start >= TIMEOUT) {
      //    System.out.println(" bi timeout khi  download "+ website.getAddress());
      httpMethodHandler.abort();
      thread.interrupt();
      return true;
    }
    return false;
  }
}
