/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.user;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 26, 2007  
 */
@NodeMap("group")
public class Group {
  
  @NodeMap("group-name")
  private String groupName;

  @NodesMap(value ="users", item = "item")
  private List<String> users = new ArrayList<String>();
  
  @NodesMap(value ="categories", item = "item")
  private List<String> workingCategories = new ArrayList<String>();

  public List<String> getUsers() { return users; }
  public void setUsers(List<String> users) { this.users = users; }

  public List<String> getWorkingCategories() { return workingCategories; }
  public void setWorkingCategories(List<String> workingCategories) {
    this.workingCategories = workingCategories;
  }
  
  public String getGroupName() { return groupName; }
  public void setGroupName(String groupName) { this.groupName = groupName; }
  
}
