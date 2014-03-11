/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.browser.FastWebClient;
import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.html.HTMLDocument;
import org.vietspider.link.explorer.SiteExplorer;
import org.vietspider.link.generator.UpdateDocument;
import org.vietspider.link.pattern.JsDocWriterGetter;
import org.vietspider.link.pattern.model.JSPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2011  
 */
public class DefaultSourceConfig implements ISourceConfig {
  
  private final static long serialVersionUID = -10l;
  
  protected String name;
  protected String referer;
  protected String charset;
  protected String homepage;
  
  protected Properties properties;
  protected Properties system;
  
  protected transient FastWebClient webClient;
  protected LoginWebsite login;
  
  protected boolean decode;
  
  protected short docType = JSPattern.LINK;
  
  private String message = null;
  
  private HTMLDocument doc;
  protected SiteExplorer siteExplorer = null ;
  
  private String address;
  private boolean cache = false;
  
  private boolean server = false;
  
  private List<UpdateDocument> updateDocs = new ArrayList<UpdateDocument>();
  
  public DefaultSourceConfig() {
  }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  private List<Exception> exceptions  = new ArrayList<Exception>();

  public String getReferer() { return referer; }
  public void setReferer(String referer) { this.referer = referer; }

  public void setCharset(String charset) { this.charset = charset; }
  public String getCharset() { return charset; }
  
  public void setHomepage(String homepage) { this.homepage = homepage; }
  public String getHomepage() { return homepage; }
  
  public Properties getProperties() { return properties; }
  public void setProperties(Properties properties) { this.properties = properties; }
  
  public boolean isDecode() { return decode; }
  public void setDecode(boolean decode) { this.decode = decode; }
  
  public void setDocType(short docType) { this.docType = docType; }
  public short getDocType() { return docType; }
  
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  
  public Properties getSystemProperties() { return system; }
  public void setSystem(Properties system) {
    this.system = system;
  }
  
  public SiteExplorer getSiteExplorer() { return siteExplorer; }
  public void setSiteExplorer(SiteExplorer siteExplorer) {
    this.siteExplorer = siteExplorer;
  }
  
  public HTMLDocument getDoc() { return doc; }
  public void setDoc(HTMLDocument doc) { this.doc = doc; }
  
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public boolean isCache() { return cache; }
  public void setCache(boolean cache) { this.cache = cache; }
  
  public boolean isServer() { return server; }
  public void setServer(boolean server) { this.server = server; }

  public void abort(String url) {
    webClient.abort(url);
  }
  
  public void log(Exception e) {
    exceptions.add(e);
  }
  
  public FastWebClient getWebClient() { return webClient; }
  public void setWebClient(FastWebClient webClient) {
    this.webClient = webClient;
  }
  
  public List<UpdateDocument> getUpdateDocs() { return updateDocs; }
  
  public LoginWebsite getLoginWebsite() {
    if(login != null) return login;
    login = new LoginWebsite(this);
    return login;
  }
  
  public List<String> getJsDocWriters() {
    return new JsDocWriterGetter().getJsDocWriters(properties);
  }
  
  public String getSourceProxy() {
    return properties.getProperty(HttpSessionUtils.PROXY);
  }
  
}
