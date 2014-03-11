/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 12, 2008  
 */
public class UserAction {
  
  private String user;
  private String time;
  private String action;
  private String data;
  
  public UserAction() {    
  }
  
  public UserAction(String user, String time, String action, String data) {
    this.user = user;
    this.time = time;
    this.action = action;
    this.data = data;
  }
  
  public String getUser() { return user;   }
  public void setUser(String user) { this.user = user; }
  
  public String getTime() { return time; }
  public void setTime(String time) { this.time = time; }
  
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  
  public String getData() { return data; }
  public void setData(String data) { this.data = data;   }
  
  public String toReportFormat(int index) {
    StringBuilder builder = new StringBuilder("<tr>\n");
    builder.append("<td  style=\"padding: 5px;\">").append(String.valueOf(index)).append("</td>\n");
    builder.append("<td style=\"padding: 5px;\">").append(time).append("</td>\n");
    String actionValue  = action;
    try {
      actionValue  = UserLogFilter.RESOURCES.getLabel(action);
    } catch (Exception e) {
      actionValue  = action;
    }
    builder.append("<td style=\"padding: 5px;\">").append(actionValue).append("</td>\n");
    String dataValue = data;
    int idIndex = dataValue.indexOf("article.id=");
    if(idIndex > 0) dataValue = dataValue.substring(0, idIndex-1);
    builder.append("<td style=\"padding: 5px;\">").append(dataValue).append("</td>\n");
    builder.append("</tr>\n");
    return builder.toString();    
  }

}
