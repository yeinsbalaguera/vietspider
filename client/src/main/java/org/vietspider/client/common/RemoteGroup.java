/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2009  
 */
public class RemoteGroup {
  
  private Groups groupContainer;
  
  public ClientConnector2 getConnector(String groupName) throws Exception {
    if(groupContainer == null) {
      SourcesClientHandler clientHandler = new SourcesClientHandler(null);
      groupContainer = clientHandler.loadGroups();
    }
    if(groupContainer  == null) return ClientConnector2.currentInstance();

    Group [] groups = groupContainer.getGroups();
    for(int i = 0; i < groups.length; i++) {
      if(groups[i].getType().equalsIgnoreCase(groupName)) {
        String remote = groups[i].getRemote();
        if(remote == null || (remote = remote.trim()).isEmpty()) {
          return ClientConnector2.currentInstance();
        }
        ClientConnector2 connector = new ClientConnector2();
        connector.setURL(remote);
        return connector;
      }
    }

    return ClientConnector2.currentInstance();
  }
  
}
