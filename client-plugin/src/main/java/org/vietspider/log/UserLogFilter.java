/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 11, 2008  
 */
final class UserLogFilter {
  
  private FileOutputStream output; 
  private FileChannel channel;
  
  final static String LABEL = "USER:";
  static ClientRM RESOURCES;
  
  private HashMap<String, UserActions> collections;
  
  private File folder;
  
  UserLogFilter() {
    RESOURCES = new ClientRM("LogPlugin");
    collections = new HashMap<String, UserActions>();
    folder = ClientConnector2.getCacheFolder("logs/plugin/");
  }

  File filter(File file) throws Exception {
    InputStreamReader inputReader = null;
    BufferedReader bufferedReader = null;

    
    File fileOutput = new File(folder, "user_report.html");
    if(fileOutput.exists()) fileOutput.delete();
    
    TreeSet<Integer> codes = new TreeSet<Integer>();
    try {
      inputReader = new InputStreamReader(new FileInputStream(file), Application.CHARSET);
      bufferedReader = new BufferedReader(inputReader);
      
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        if(!line.startsWith(LABEL)) continue;
//        int hashCode = line.hashCode();

        UserAction userAction = createUserAction(line);
        StringBuilder buildCode = new StringBuilder();
        buildCode.append(userAction.getUser()).append(' ');
        String data = userAction.getData();
        int idIndex = data.indexOf("article.id="); 
        if(idIndex > 0) data = data.substring(0, idIndex-1);
        buildCode.append(userAction.getAction()).append(' ').append(data);
        int hashCode = buildCode.toString().hashCode();
        if(codes.contains(hashCode)) continue;
        
        if(userAction == null) continue;
        String username = userAction.getUser();
        UserActions userActions = collections.get(username);
        if(userActions == null) {
          userActions = new UserActions(folder, username);
          collections.put(username, userActions);
        } 
//        System.out.println(userAction);
        userActions.add(userAction);
        
        codes.add(hashCode);
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    
    try {
      fileOutput.createNewFile();
      output = new FileOutputStream(fileOutput, true);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    startFile();
    
    Iterator<String> keys = collections.keySet().iterator();
    int index = 1;
    while(keys.hasNext()) {
      String key = keys.next();
      UserActions userActions = collections.get(key);
      append(userActions.toReportFormat(index));
      userActions.endFile();
      index++;
    }
    
    endFile();
    
    return fileOutput;
  }
  
  private UserAction createUserAction(String line) {
    int idx = line.indexOf(LABEL);
    if(idx < 0) return null;
    line = line.substring(idx+LABEL.length());
    
    idx = line.indexOf(": ");
    if(idx < 0) return null;
    String time = line.substring(0, idx).trim();
    line  = line.substring(idx+1).trim();
    
    idx = line.indexOf(' ');
    if(idx < 0) return null;
    String user = line.substring(0, idx).trim();
    line  = line.substring(idx+1).trim();
    
    idx = line.indexOf(' ');
    if(idx < 0) return null;
    String action = line.substring(0, idx).trim();
    String data = line.substring(idx+1).trim();
    
    return new UserAction(user, time, action, data);
  }
  
  void startFile() {
    try { 
      channel = output.getChannel();
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
      return;
    }
    
    StringBuilder builder = new StringBuilder("<html>\n");
    builder.append("<head>\n");
    builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> \n");
    String title = UserLogFilter.RESOURCES.getLabel("report.title");
    builder.append("<title>").append(title).append("</title>\n");
    builder.append("</head>\n");
    builder.append("<body>\n");
    builder.append("<center>\n");
    builder.append("<table ")  
           .append("style=\"border-collapse: collapse; font-family: Tahoma; font-size: 12px;\"")
           .append(" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">\n");
    
    builder.append("  <tr>");
    builder.append("    <th colspan=\"4\" scope=\"colgroup\" ")
           .append(" style=\"background-color: #ffffff; font-family: Verdana; font-weight: bold; font-size: 15px; color: #ffffff; border-bottom: 1px solid #E6EEFA; background-color: rgb(0, 102, 153); height: 22px;\">");
    builder.append(title);
    builder.append("    </th>");
    builder.append("  </tr>");
    
    String order  = UserLogFilter.RESOURCES.getLabel("order");
    String time  = UserLogFilter.RESOURCES.getLabel("action.time");
    String user  = UserLogFilter.RESOURCES.getLabel("user");
    String action  = UserLogFilter.RESOURCES.getLabel("action");
    
    builder.append("  <tr align=\"left\" style=\"background-color: rgb(0, 102, 153); height: 22px; padding: 5px;\">");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\" >").append(order).append("</th>");
    builder.append("    <th  style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\">").append(user).append("</th>");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\">").append(action).append("</th>");
    builder.append("    <th style=\"font-family: tahoma; font-weight: bold; font-size: 13px; color: rgb(255, 255, 255); padding: 5px;\">").append(time).append("</th>");
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
  
}
