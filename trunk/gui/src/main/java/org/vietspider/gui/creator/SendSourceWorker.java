/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2008  
 */
public class SendSourceWorker extends SendWorker {
  
  public SendSourceWorker(Source source) {
    super(source);
  }
  
  public SendSourceWorker(String group, String category, String...names) {
    super(group, category, names);
  }

  public String send(String host, final String port, String username, String password) {
    try {
      connector.setURL(SWProtocol.HTTP+host+":"+port);
      int ping = connector.ping(username, password);
      ClientRM resources = new ClientRM("Connector");
      if(ping < -2) {
        return resources.getLabel("cannotConnect");
      } else if (ping == -2) {
        return resources.getLabel("incorrectUser");
      } else if (ping == -1) {
        return  resources.getLabel("incorrectPassword");
      } else  if(ping == 0) {
        return  resources.getLabel("incorrectMode");
      }

      if(source != null) {
        SimpleSourceClientHandler handler = new SimpleSourceClientHandler();
        handler.saveSource(connector, source);
      } else {
        for(String name : names) {
          SourcesClientHandler handler = new SourcesClientHandler(group);
          source = loadSource(handler, name);
          if(source != null) handler.saveSource(connector, source);
        }  
      }
      
    } catch (Exception e) {
      if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
        return e.getMessage();
      } 
      return e.toString();
    }
    return null;
  }

  private Source loadSource(SourcesClientHandler handler, String name) throws Exception {
    return handler.loadSource(category, name); 
  }
}
