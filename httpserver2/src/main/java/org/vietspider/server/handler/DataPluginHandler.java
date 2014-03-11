/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.server.plugin.PluginHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2007  
 */
public class DataPluginHandler extends CommonHandler {

  private PluginHandler [] plugins = new PluginHandler[0];

  public DataPluginHandler() {
    new Thread() {
      public void run() {
//        System.out.println(" BEGIN INIT DATA HANDLER -->");
//        long time = System.currentTimeMillis();
        loadPlugins();
//        System.out.println("INIT DATA HANDLER FINISH! in" + ( System.currentTimeMillis() - time));
      }
    }.start();
  }
  
  public void loadPlugins() {
    File file  = new File(UtilFile.getFolder("system"), "plugin.config");
    if(file == null || !file.exists()) return;
    String value = null;
    try {
      byte [] bytes = RWData.getInstance().load(file);
      value = new String(bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(value == null) return;
    String [] elements = value.split("\n");
    final List<PluginHandler> list = new ArrayList<PluginHandler>(); 
    for(final String pluginClass : elements) {
      if(pluginClass == null 
          || pluginClass.trim().isEmpty()
          || pluginClass.charAt(0) == '#') continue;
      try {
        Class<?> clazz = Class.forName(pluginClass.trim());
        list.add((PluginHandler)clazz.newInstance());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    plugins = list.toArray(new PluginHandler[list.size()]);
  }

  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception  {
//    try {
    if(plugins.length < 1) {
      output.write("No Plugin or plugins were loading!".getBytes());
      return;
    }
    String value = null;
    
    Header header = request.getFirstHeader("action");
    String action = header == null? null : header.getValue();
    
    header = request.getFirstHeader("plugin.name");
    String pluginName = header == null? null : header.getValue();
    
    header = request.getFirstHeader("data.type");
    String dataType = header == null? "" : header.getValue();
    Boolean readValue = !"file".equals(dataType);
    
    Header debugHeader = request.getFirstHeader("debug");
    if(debugHeader != null && "true".equalsIgnoreCase(debugHeader.getValue())) {
      String log = "Invoke plugin " + pluginName+ " action " + action;
      LogService.getInstance().setMessage(getClass().getSimpleName(), null, log);
    }
    
    if(action == null || pluginName == null) {
      byte [] bytes = getRequestData(request);
      String bodyString = new String(bytes, "UTF-8");
      Map<String, String> params = URLUtils.getParams(bodyString);
      
      if(action == null || action.trim().isEmpty()) {
        action = params.get("action");
      }
      if(pluginName == null || pluginName.trim().isEmpty()) {
        pluginName = params.get("plugin.name");
      }
      
      value = bodyString;
      readValue = false;
      
      if(action == null || pluginName == null) {
        output.write("No Plugin or No Action".getBytes());
        return ;
      }
    }
    
    if(readValue){
	    value = new String(getRequestData(request), Application.CHARSET).trim();
    }
    
    
    for(PluginHandler plugin : plugins) {
//      System.out.println("=== >"+ pluginName+ " : "+plugin.getName()+ "  : " + plugin.getName().equals(pluginName));
      if(!plugin.getName().equals(pluginName)) continue;
      plugin.setRequest(request);
      plugin.setOutput(output);
      byte[] re = null;
      try {
        re = plugin.invoke(action, value);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        output.write(e.toString().getBytes(Application.CHARSET));
        return;
      }
      
      String log = plugin.getLogMessage();
      if(re != null && log != null) logAction(request, "", log);
      if(re != null && re.length > 0) output.write(re);
      return;
    }
    
    LogService.getInstance().setMessage("PLUGIN", null, "Cann't plugin "+ pluginName);
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
  }
  
}
