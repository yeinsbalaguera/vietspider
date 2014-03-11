/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.channel;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.vietspider.ClientProperties;
import org.vietspider.browser.FastWebClient;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.link.MockLink;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.js.JsHandler;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.link.explorer.SiteExplorer;
import org.vietspider.link.generator.UpdateDocument;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.model.SourceProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 13, 2009  
 */
public class DocumentLoader {

  protected DefaultSourceConfig config;

  public DocumentLoader(ISourceConfig iconfig) {
    this.config = (DefaultSourceConfig)iconfig;
  }
  
  public void load() throws Exception {
    config.setDoc(getDocument());
  }

  private HTMLDocument getDocument() throws Exception {
    FastWebClient webClient = config.getWebClient();
    Properties properties = config.getProperties();
    webClient.setUserAgent(properties.getProperty(SourceProperties.USER_AGENT));
    
    String referer = config.getReferer();
    if(webClient.getHost() == null) {
      String homepage = config.getHomepage();
      String proxy = config.getSourceProxy();
      ClientProperties client  = ClientProperties.getInstance();
      if("true".equals(client.getValue("proxy.enable"))) {
        String proxyHost = client.getValue(Application.PROXY_HOST);
//                  System.out.println(" client proxy host "+ proxyHost);
        if(proxyHost != null && !proxyHost.trim().isEmpty()) {
          webClient.setURL(referer, new URL(homepage));
          try {
            int proxyPort = Integer.parseInt(client.getValue(Application.PROXY_PORT).trim());
            String proxyUser = client.getValue(Application.PROXY_USER);
            if(proxyUser != null && proxyUser.trim().isEmpty()) proxyUser = null;
            String proxyPassword = client.getValue(Application.PROXY_PASSWORD);
            webClient.registryProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
          } catch (Exception e) {
            LogService.getInstance().setMessage(null, "Proxy not found!");
          }
        } else {
          LogService.getInstance().setMessage(null, "Proxy not found!");
        }
      }  else if(proxy != null && proxy.trim().startsWith("blind")) {
        webClient.setURL(referer, new URL(homepage), proxy);
        //      if(HttpSessionUtils.isMultiProxies(proxy)) {
        //        webClient.setURL(referer, new URL(homepage), proxy);
      } else if(proxy != null && !proxy.trim().isEmpty()){
        webClient.setURL(referer, new URL(homepage));
        setSourceProxy(webClient, proxy);
        //        webClient.registryProxy(proxy);
      } else {
        webClient.setURL(referer, new URL(homepage));
        Properties system = config.getSystemProperties();
        /*        if(system == null) {
//          URLWidget txtHome = iSourceInfo.<URLWidget>getField("txtHome");
          DataClientHandler handler = new DataClientHandler();
          system = handler.getSystemProperties();
//          loadSystemProperties(txtHome.getTextComponent());
        }
         */        
        if(system != null) {
          String proxyEnable = system.getProperty(Application.PROXY_ENABLE);
          if("true".equalsIgnoreCase(proxyEnable)) {
            String proxyHost = system.getProperty(Application.PROXY_HOST);
            //          System.out.println(" proxy host "+ proxyHost);
            if(proxyHost != null && !proxyHost.trim().isEmpty()) {
              try {
                int proxyPort = Integer.parseInt(system.getProperty(Application.PROXY_PORT).trim());
                String proxyUser = system.getProperty(Application.PROXY_USER);
                if(proxyUser != null && proxyUser.trim().isEmpty()) proxyUser = null;
                String proxyPassword = system.getProperty(Application.PROXY_PASSWORD);
                webClient.registryProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
              } catch (Exception e) {
                config.log(e);
              }
            } else {
              LogService.getInstance().setMessage(null, "Proxy not found!");
            }
          }
        }
      }
    }

    try {
      config.getLoginWebsite().login();
    } catch (Exception e) {
      config.setMessage(e.toString());
    }

    HTMLDocument document =  null;
    SiteExplorer siteExplorer = config.getSiteExplorer();
    if(siteExplorer != null) {
      siteExplorer.setCharset(config.getCharset());
      siteExplorer.setDecode(config.isDecode());
      siteExplorer.explore();
      if(config.getDocType() == JSPattern.LINK) {
        if(siteExplorer.getVisit() != null) {
          return siteExplorer.getVisit().getDocument(); 
        } 
        return siteExplorer.getHomepage().getDocument();
      } else if(config.getDocType() == JSPattern.DATA) {
        if(siteExplorer.getData() != null) {
          return siteExplorer.getData().getDocument(); 
        }
        return null;
      }
    }  else {
      String address = config.getAddress();
      if(address == null || address.trim().length() == 0) return null;
      HTMLParserDetector detector = new HTMLParserDetector();
      detector.setDecode(config.isDecode());
      detector.setCharset(config.getCharset());
      document = webClient.createDocument(referer, address, config.isCache(), detector);
      
      if(config.getCharset() == null 
          || config.getCharset().length() == 0) {
        config.setCharset(detector.getCharset());
      }
    }
    List<String> jsDocWriters = config.getJsDocWriters();
    if(jsDocWriters.size() > 0) JsHandler.updateDocument(document, jsDocWriters);

    String address = config.getAddress();
    MockLink mockLink = new MockLink(address, referer);
    List<UpdateDocument> updateDocs = config.getUpdateDocs();
    for(int i = 0; i < updateDocs.size(); i++) {
      updateDocs.get(i).generate(mockLink, webClient, document);
    }

    return document;
  }

  private void setSourceProxy(FastWebClient webClient, String proxy) {
    String [] elements = proxy.split(":");
    if(elements.length < 2) return;

    int proxyPort = Integer.parseInt(elements[1].trim());

    String proxyUser = elements.length < 3 ? null : elements[2].trim();
    String proxyPassword = elements.length < 4 ? null : elements[3].trim();
    webClient.registryProxy(elements[0].trim(), proxyPort, proxyUser, proxyPassword);
  }

  public void abort(String url) {
    config.getWebClient().abort(url);
  }

}
