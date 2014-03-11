/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.homepage;

import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.gui.creator.SendWorker;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2008  
 */
public class SendHomepageWorker extends SendWorker {
  
  public SendHomepageWorker(String group, String category, String...names) {
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

      StringBuilder builder = new StringBuilder();
      String key = group+"."+category+"."+names[0];
      HomepagesClientHandler client = new HomepagesClientHandler();
      int total = client.loadTotalHomepages(group, category, names[0]);
      
      for(int i = 0; i < total; i++) {
        builder.setLength(0);
        String homepage = client.loadHomepages(group, category, names[0], i+1);
        builder.append(key).append('\n').append(homepage);
        client.saveHomepages(connector, builder.toString());
      }
      
    } catch (Exception e) {
      if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
        return e.getMessage();
      } 
      return e.toString();
    }
    return null;
  }

}
