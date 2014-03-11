/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.url;

import org.vietspider.common.io.HttpURL;
import org.vietspider.common.io.MD5Hash;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 15, 2009  
 */
public class UrlData {
  
  public static final short INSERT  = 1;
  public static final short REMOVE  = -1;
  
  private MD5Hash md5Hash;
  private String url;
  private String folder;
  
  private short type  = INSERT;
  

  public UrlData(){
  }
  
  public UrlData(String url, String folder){
    this.url = url;
    this.folder = folder;
    this.md5Hash = MD5Hash.digest(new HttpURL(url).getNormalizeURL());
  }
  
  public UrlData(String url, String folder, short t){
    this(url, folder);
    type = t;
  }
  
  public short getType() { return type; }
  public void setType(short type) { this.type = type; }

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  
  public MD5Hash getMd5Hash() { return md5Hash; }
  public void setMd5Hash(MD5Hash md5Hash) { this.md5Hash = md5Hash; }
  
  public String getFolder() { return folder;  }
  public void setFolder(String folder) { this.folder = folder; }

}
