/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common.source;

import java.io.Serializable;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2009  
 */
public abstract class BasicSourceClientHandler 
    extends SimpleSourceClientHandler implements Serializable {

  protected ClientConnector2 connector;
  protected String group;

  public BasicSourceClientHandler(String group) throws Exception {
    this.group = group;
    connector = getConnector();
  }

  public void abort()  {
    connector.abort();
    ClientConnector2.currentInstance().abort();
  }
  
  protected ClientConnector2 getConnector() throws Exception {
    if(group == null)  {
      return ClientConnector2.currentInstance();
    }
    
    Groups groupCollection = loadGroups();
    if(groupCollection  == null) {
      return ClientConnector2.currentInstance();
    }

    Group [] groups = groupCollection.getGroups();
    for(int i = 0; i < groups.length; i++) {
      if(!groups[i].getType().equalsIgnoreCase(group)) continue;
      String remote = groups[i].getRemote();
      if(remote == null || (remote = remote.trim()).isEmpty()) {
        return ClientConnector2.currentInstance();
      }
      ClientConnector2 connector_ = new ClientConnector2();
      ClientConnector2 current  = ClientConnector2.currentInstance();
      connector_.ping(current.getUsername(), current.getPassword());
      connector_.setURL(remote);
      return connector_;
    }

    return ClientConnector2.currentInstance();
  }
  

}
