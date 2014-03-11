/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 16, 2009  
 */
public class ArticleLoader {

  private DataClientService client;
  
  public ArticleLoader() {
    SystemProperties properties = SystemProperties.getInstance();
    
    String host = properties.getValue("load.data.server");
    if(host == null || host.trim().isEmpty()) return;
    client = new DataClientService(host);
  }
  
  public Article loadArticleById(String group, String url)  {
    if(client == null) return null;
    Header [] headers = new Header[] {
      new BasicHeader("action", "load.article.by.url")
    };
    
    try {
      byte [] bytes = (group+"\n"+url).getBytes(Application.CHARSET);

      bytes = client.post(URLPath.DATA_CONTENT_HANDLER, bytes, headers);
      if(bytes.length < 10) return null;
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
      ObjectInputStream objectInputStream = null;
      objectInputStream = new ObjectInputStream(byteInputStream);
      return (Article)objectInputStream.readObject();
    } catch (HttpHostConnectException e) {
      return null;
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      return null;
    }
  }
  
  
}
