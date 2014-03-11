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
@NodeMap("user")
public class User {
  
  public final static int ROLE_ADMIN = 3;
  
  public final static int APPLICATION_MONITOR = 4;
  
  public final static int DATA_READ_ONLY = 1;
  
  public final static int DATA_EDIT = 2;

  @NodeMap("full-name")
  private String fullName;
  
  @NodeMap("password")
  private String password;
  
  @NodeMap("username")
  private String userName;
  
  @NodeMap("email")
  private String email;
  
  @NodeMap("permission")
  private int permission = DATA_READ_ONLY;
  
  @NodesMap(value="groups", item="item")
  private List<String> groups = new ArrayList<String>();
  
  public int getPermission() { return permission; }
  public void setPermission(int data) { this.permission = data; }
  
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  
  public String getUserName() { return userName; }
  public void setUserName(String userName) { this.userName = userName; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public List<String> getGroups() { return groups; }
  public void setGroups(List<String> groups) { this.groups = groups; }

}
