/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms;



/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Mar 10, 2007
 */
public class CMSService {
  
  public final static CMSService INSTANCE = new CMSService();
  
  public String host = "127.0.0.1";
  public int webPort = 9245;
  public int appPort = 9245;
  
  public String siteViewer  ;
  public String appViewer ;
  
  public String viewer ;
  public String package_;
  
//  public static final  String ID = "CMS_Serivce";

  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }

  public int getWebPort() { return webPort; }
  public int getAppPort() { return appPort; }
  
  public void setWebPort(int port) { this.webPort = port; }
  public void setAppPort(int port) { this.appPort = port; }

  public String getSiteViewer() { return siteViewer; }
  public void setSiteViewer(String siteViewer) { this.siteViewer = siteViewer; }

  public String getAppViewer() { return appViewer; }
  public void setAppViewer(String appViewer) { this.appViewer = appViewer; }

  public String getViewer() { return viewer; }
  public void setViewer(String viewer) { this.viewer = viewer; }
  
  public String getPackage() { return package_; }
  public void setPackage(String package_) { this.package_ = package_; }

  public <T> T createRender(Class<T> clazz) throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    Class<?> clazz2 = classLoader.loadClass(package_+clazz.getSimpleName()+"Impl");
    return clazz.cast(clazz2.newInstance());
  }
  
  public String getUsername(String [] cookies) {
    if(cookies == null) return null;
    String username = null;
    for(String cookie : cookies) {
      int index = cookie.indexOf("userlogin=");
      if(index < 0) continue;
      index += "userlogin=".length();
      int dotIndex = cookie.indexOf('.');
      if(dotIndex > -1)  {
        username = cookie.substring(index, dotIndex);
      } else {
        username = cookie.substring(index , cookie.length());
      }
      break;
    }
    if(username == null) return null;
    if(username.equals("guest")) return null;
    return username;
  }
  
  
}
