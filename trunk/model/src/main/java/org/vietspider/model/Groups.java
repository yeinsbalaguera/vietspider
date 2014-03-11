/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2007  
 */
@NodeMap("groups")
public class Groups {
  
  @NodesMap("list")
  private Group [] groups  = new Group[] {new Group()};
  
  public Groups() {
  }
  
  public String [] getNames() {
    String [] names = new String[groups.length];
    for(int i = 0; i < names.length; i++) {
      names[i] = groups[i].getType().toString();
    }
    return names;
  }

  public Group[] getGroups() { return groups; }
  
  public void setGroups(Group[] groups ) { this.groups = groups; }
  
  public Group getGroup(String type) {
    for(Group group : groups) {
      if(group.getType().equalsIgnoreCase(type)) return group;
    }
    return new Group();
  }
  
}