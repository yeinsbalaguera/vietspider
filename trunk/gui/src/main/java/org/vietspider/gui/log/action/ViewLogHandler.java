/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.gui.log.ITextViewer;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2009  
 */
public class ViewLogHandler {

  private ITextViewer viewer;

  public ViewLogHandler(ITextViewer _viewer, int page) {
    this.viewer = _viewer;

    File file = getCachedFile(page);
    String logText = "";
    try {
      byte [] bytes = RWData.getInstance().load(file);
      logText = new String(bytes, Application.CHARSET);
    } catch (Exception e) {
      ClientLog.getInstance().setException(viewer.getShell(), e);
      return ;
    } 
    
    //    logText = logText.replaceAll("\n", "<br/>");
//    int start = 0;
//    int end = 0;
    //old format, will remove 2 follow code
    logText = logText.replaceAll("<a>", ""); 
    logText = logText.replaceAll("</a>", "");
    
    StringBuilder builder = new StringBuilder();
    StringReader reader = new StringReader(logText);
    BufferedReader bufferedReader = new BufferedReader(reader);
    String line = null;
    try {
      while((line = bufferedReader.readLine()) != null) {
         line = line.trim();
         if(line.startsWith("SOURCE:")) {
           int s = line.indexOf(':');
           int e = line.indexOf(':', s+1);
           if(s > 0 && e > 0) {
             builder.append(line.subSequence(0, s));
             String fullName = line.substring(s+2, e).trim();
             builder.append("<a href=\"#").append(fullName).append("\">");
             builder.append(fullName).append("</a>").append(line.substring(e));
             continue;
           }
         } 
         builder.append(line).append('\n');
      }
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(viewer.getShell(), e);
    }
    
    
    
//    StringBuilder builder = new StringBuilder();
////    builder.append("<html><head>");
////    builder.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
////    builder.append("</head><body>");
//    while(start < logText.length()) {
//      start = logText.indexOf("SOURCE:", end);
//      if(start < 0) break;
//      builder.append(logText.subSequence(end, start+8));
//      end = logText.indexOf(':', start+8);
//      if(end < 0) break;
//      builder.append("<a href=\"#");
//      String sourceName = logText.substring(start+8, end);
//      builder.append(sourceName).append("\">").append(sourceName).append("</a> - ");
//
//      String link =  searchLink(logText, end);
//      if(link != null) {
//        builder.append("<a href=\"#");
//        builder.append(sourceName).append('#').append(link).append("\">");
//        builder.append("Test</a> ");
//      }
//
//      /* int start2 = logText.indexOf("http://", end);
//      if(start2 > 0) {
//        int end2 = logText.indexOf(':', start2+10);
//        if(end2 > start2) {
//           builder.append(logText.subSequence(end, start2));
//           builder.append("<a href=\"#");
//           String url  = logText.substring(start2, end2);
//           builder.append(sourceName).append('#').append(url);
//           builder.append("\">").append(url).append("</a>");
//           start = end2;
//           end = end2;
//           continue;
//        }
//      } */
//
//      start = end+1;  
//
//    }
//
//    if(end  > -1 && end < logText.length()) {
//      builder.append(logText.subSequence(end, logText.length())); 
//    }

//    builder.append("</body>").append("</html>");
    
    File logFile  = new File(ClientConnector2.getCacheFolder("logs/temp/"), String.valueOf(page)+".html");
    try {
      RWData.getInstance().save(logFile, builder.toString().getBytes("utf-8"));
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(viewer.getShell(), e);
    } 

    try {
      viewer.setFile(logFile);
    } catch (Exception e) {
      ClientLog.getInstance().setException(viewer.getShell(), e);
    }

    //    new Thread() {
    //      public void run() {
    //        try {
    //          Thread.sleep(10*1000);
    //          logFile.delete();
    //        } catch (Exception e) {
    //        }
    //      }
    //    }.start();
  }

//  private String searchLink(String logText, int from) {
//    int start2 = logText.indexOf("http://", from);
//    if(start2 < 1) return null;
//    int end2 = logText.indexOf(':', start2+10);
//    if(end2 <= start2) return null; 
//    return logText.substring(start2, end2);
//  }

  private File getCachedFile(int page) {
    String log = "log" + String.valueOf(page-1);
    return ClientConnector2.getCacheFile("server/"+viewer.getFolder(), log);
  }
}
