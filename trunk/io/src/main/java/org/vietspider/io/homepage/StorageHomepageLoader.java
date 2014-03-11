/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.homepage;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2009  
 */
public class StorageHomepageLoader {
  
  private String data;
  private DataClientService client;

  public StorageHomepageLoader(String sourceName, String ...templates) {
    StringBuilder builder = new StringBuilder();
    builder.append(sourceName);
    for(int i = 0; i < templates.length; i++) {
      builder.append('\n').append(templates[i]);
    }
    data = builder.toString();
    
    SystemProperties properties = SystemProperties.getInstance();
    String store = properties.getValue("website.store");
    if(store == null || store.trim().isEmpty()) {
      store = "http://127.0.0.1:" + properties.getValue("port");
    }
    client = new DataClientService(store);
  }
  
  public String[] download(String lang) {
    if(client == null) return null;
    try {
      Header [] headers = new Header[] {
          new BasicHeader("action", "load.storage.homepage")
      };

      byte [] bytes = (lang + "\n" + data).getBytes(Application.CHARSET);
//      System.out.println(" luc nay ta co "+  (lang + "\n" + data));
      bytes = client.post(URLPath.DATA_HANDLER, bytes, headers);
     /* HttpResponse response = client.loadPostResponse(URLPath.DATA_HANDLER, bytes, headers);
      HttpResponseReader reader = new HttpResponseReader();
      InputStream inputStream = response.getEntity().getContent();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte [] buffer = new byte[16*1024];
      int read = 0;
      while((read = inputStream.read(buffer)) > -1) {
        outputStream.write(buffer, 0, read);
      }
      bytes = outputStream.toByteArray();*/
      String urls = new String(bytes, Application.CHARSET);
      if(urls.trim().isEmpty()) return null;
      return urls.split("\n");
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, " cann't load homepage");
      return null;
    }
  }
  
}
