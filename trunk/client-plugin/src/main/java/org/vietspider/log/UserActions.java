/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 12, 2008  
 */
public class UserActions {
  
//  private List<UserAction> list;
  private FileOutputStream output; 
  private FileChannel channel;
  private HashMap<String, Integer> actions;
  private String user;
  private File file ;
  private int index = 1;

  public UserActions(File folder, String user) {
    this.user = user;
    NameConverter converter = new NameConverter();
    String fileName  = NameConverter.encode(user)+"_report.html";
    file = new File(folder, fileName);
    if(file.exists()) file.delete();
    try {
      file.createNewFile();
      output = new FileOutputStream(file, true);
      startFile(user);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
//    list = new ArrayList<UserAction>();
    actions = new HashMap<String, Integer>();
  }
  
  
  public void add(UserAction userAction) {
//    if(!userAction.getUser().equalsIgnoreCase(user)) return;
    if(actions.containsKey(userAction.getAction())) {
      int counter = actions.get(userAction.getAction());
      actions.put(userAction.getAction(), counter+1);
    } else {
      actions.put(userAction.getAction(), 1); 
    }    
    
    try {
      append(userAction.toReportFormat(index));
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    index++;
  }
  
  void startFile(String userName) {
    try { 
      channel = output.getChannel();
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
      return;
    }
    
    StringBuilder builder = new StringBuilder("<html>\n");
    builder.append("<head>\n");
    builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> \n");
    String title = UserLogFilter.RESOURCES.getLabel("user.title");
    title  = title.replaceAll("\\{0\\}", userName);
    builder.append("<title>").append(title).append("</title>\n");
    builder.append("</head>\n");
    builder.append("<body>\n");
    builder.append("<center>\n");
    builder.append("<table ")
           .append("style=\"border-collapse: collapse; font-family: Tahoma; font-size: 12px;\"")
           .append(" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">\n");
    
    String order  = UserLogFilter.RESOURCES.getLabel("order");
    String time  = UserLogFilter.RESOURCES.getLabel("time");
    String data  = UserLogFilter.RESOURCES.getLabel("data");
    String action  = UserLogFilter.RESOURCES.getLabel("action");
    builder.append("  <tr>");
    builder.append("    <th colspan=\"4\" scope=\"colgroup\" ")
           .append(" style=\"background-color: #ffffff; font-family: Verdana; font-weight: bold; font-size: 15px; color: #ffffff; border-bottom: 1px solid #E6EEFA; background-color: rgb(0, 102, 153); height: 22px;\">");
    builder.append(title);
    builder.append("    </th>");
    builder.append("  </tr>");
    builder.append("  <tr align=\"left\" style=\"background-color: rgb(0, 102, 153); height: 22px; padding: 5px;\">");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\" >").append(order).append("</th>");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\" >").append(time).append("</th>");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\" >").append(action).append("</th>");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\" >").append(data).append("</th>");
    builder.append("  </tr>");
    
    try {
      append(builder.toString());
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
  }
  
  
  void endFile() {
    StringBuilder builder = new StringBuilder("</table>\n");
    builder.append("</center>\n");
    builder.append("</body>\n");
    builder.append("</html>\n");
    
    try {
      append(builder.toString());
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    
    try {
      if(channel != null) channel.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      if(output != null) output.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
  }
  
  private synchronized void append(String value) throws Exception {
    if(channel == null) return;
    byte[] data = value.getBytes(Application.CHARSET);
    ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
    buff.put(data);
    buff.rewind();
    if(channel.isOpen()) channel.write(buff);
    buff.clear(); 
  }
  
  
  public String toReportFormat(int index_) {
    StringBuilder builder = new StringBuilder("<tr>\n");
    builder.append("<td valign=\"top\" rowspan=\"").
           append(actions.size() + 4).append("\"  style=\"padding: 5px;\">").append(String.valueOf(index_)).append("</td>\n");
    builder.append("</tr>\n");

    builder.append("<td valign=\"top\" rowspan=\"").
            append(actions.size() + 4).append("\"  style=\"padding: 5px;\">").append(user).append("</td>\n");
    builder.append("</tr>\n");
    Iterator<String> iterator = actions.keySet().iterator();
    int total = 0;
    while(iterator.hasNext()) {
      String key  = iterator.next();
      int counter = actions.get(key);
      total += counter;
      String value = String.valueOf(counter);
      
      builder.append("<tr>\n");
      String keyLabel  = key;
      try {
        keyLabel  = UserLogFilter.RESOURCES.getLabel(key);
      } catch (Exception e) {
        keyLabel  = key;
      }
      builder.append("<td  style=\"padding: 5px;\">").append(keyLabel).append("</td>\n");
      builder.append("<td  style=\"padding: 5px;\">").append(value).append("</td>\n");
      builder.append("</tr>\n");
    }
    
    String actionTotal  = UserLogFilter.RESOURCES.getLabel("action.total");
    actionTotal += " " + String.valueOf(total);
    builder.append("<tr>\n");
    builder.append("<td colspan=\"3\" align=\"right\"  style=\"padding: 5px;\">").append(actionTotal).append("</td>\n");
    builder.append("</tr>\n");
    
    String detailReport = UserLogFilter.RESOURCES.getLabel("detail.report");
    builder.append("<tr>\n");
    builder.append("<td colspan=\"3\" align=\"right\"  style=\"padding: 5px;\">");
    try {
      builder.append("<a href=\"").append(file.toURI().toURL().toString()).append("\"")
             .append(" style=\"font-family: Verdana; font-size: 12px; font-weight: bold; color: #133971; text-decoration: none; line-height: 15px;\"")
             .append(">");
      builder.append(detailReport);      
      builder.append("</a>");
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    builder.append("</td>\n");
    builder.append("</tr>\n");
    
    return builder.toString();    
  }
  
}

