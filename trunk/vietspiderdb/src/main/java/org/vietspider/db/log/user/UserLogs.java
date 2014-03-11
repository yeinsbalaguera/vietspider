/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.log.user;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 31, 2008  
 */
@NodeMap("article-user")
public class UserLogs {

  @NodeMap("article")
  private String articleId;
  @NodesMap(value = "users",  item = "item")
  private List<UserLog> actions;
  
  public UserLogs() {
    this.actions = new ArrayList<UserLog>();
  }
  
  public UserLogs(String articleId) {
    this.articleId = articleId;
    this.actions = new ArrayList<UserLog>();
  }
  
  public String getArticleId() {return articleId;}
  public void setArticleId(String articleId) {this.articleId = articleId;}

  public List<UserLog> getActions() { return actions; }
  public void setActions(List<UserLog> users) { this.actions = users; }
  public void addAction(String username, int action) {
    for(int i = 0 ; i < actions.size(); i++) {
      UserLog userLog = actions.get(i);
      if(userLog.getUserName().equals(username) 
          && userLog.getAction() == action) {
        userLog.setTime(System.currentTimeMillis());
        return ;
      }
    }
    actions.add(new UserLog(username, action));
  }
  
}
