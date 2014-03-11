/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.common.Application;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class DataContentHandler {
  
  public ArticleCollection loadNewContents() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.new.list.metas")
    };
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.DATA_CONTENT_HANDLER, new byte[0], headers);
    try {
      return XML2Object.getInstance().toObject(ArticleCollection.class, bytes);
    } catch (Throwable e) {
//      File file = new File("D:\\java\\test\\xml\\article-collections.xml");
//      org.vietspider.common.io.RWData.getInstance().save(file, bytes);
      return null;
    }
  }
  
  public String [] loadArticle(String id) {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.article")
    };
    try {
      ClientConnector2 connector = ClientConnector2.currentInstance();
      byte [] bytes = connector.post(URLPath.DATA_CONTENT_HANDLER, id.getBytes(), headers);
      String value = new String(bytes, Application.CHARSET);
      return value.split("\n");
    } catch (Exception e) {
      return new String[]{e.toString()};
    }
  }
  
}
