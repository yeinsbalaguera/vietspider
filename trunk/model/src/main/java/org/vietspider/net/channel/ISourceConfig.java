/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.channel;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.vietspider.browser.FastWebClient;
import org.vietspider.html.HTMLDocument;
import org.vietspider.link.explorer.SiteExplorer;
import org.vietspider.link.generator.UpdateDocument;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2011  
 */
public interface ISourceConfig extends Serializable {
  
  public String getReferer();
  
  public String getCharset();
  public void setCharset(String charset);
  
  public String getHomepage();
  
  public Properties getProperties();
  
  public Properties getSystemProperties();
  
  public void log(Exception e);
  
  public FastWebClient getWebClient() ;
  
  public LoginWebsite getLoginWebsite();
  
  public List<String> getJsDocWriters();
  
  public String getSourceProxy() ;
  
  public boolean isDecode() ;
  
  public void setDocType(short docType);
  public short getDocType();
  
  public String getMessage() ;
  public void setMessage(String message) ;
  
  public SiteExplorer getSiteExplorer();
  public void setSiteExplorer(SiteExplorer siteExplorer) ;
  
  public List<UpdateDocument> getUpdateDocs();
  
  public void abort(String url);
  
  public String getName();
  
  public HTMLDocument getDoc() ;
  public void setDoc(HTMLDocument doc) ;
  
}
