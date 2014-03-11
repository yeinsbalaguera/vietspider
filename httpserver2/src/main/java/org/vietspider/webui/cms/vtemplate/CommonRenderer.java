/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 10, 2011  
 */
final class CommonRenderer {

  private static final CommonRenderer COMMON = new CommonRenderer();
  
  final static CommonRenderer getInstance() {  return COMMON; }

  protected RIteratorRenderer iterator;
  protected RMenuRenderer menu;
  protected RRelationRenderer rel;
  
  private String host = "http://nik.vn/tin";

  CommonRenderer() {
    rel = new RRelationRenderer();
    iterator = new RIteratorRenderer();
    menu = new RMenuRenderer();
    
    File file = UtilFile.getFile("system/cms/vtemplate", "host.txt");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String [] elements = text.trim().split("\n");
      if(elements.length > 0) host = elements[0];
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  String renderHeader(HttpRequestData hrd, String title) {
    StringBuilder builder = new StringBuilder("<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\"");
    builder.append("\"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
    builder.append("<HTML  xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
    builder.append("<title>").append(title).append("</title>");  
//    String uriFolder = hrd.getUriFolder();

    builder.append("<link href=\"").append(host).append("/file/Common.css\" rel=\"stylesheet\" type=\"text/css\" />");
    builder.append("<link href=\"").append(host);
    if(hrd.isMobile()) {
      builder.append("/file/Mobile.css\" rel=\"stylesheet\" type=\"text/css\" />");
    } else {
      builder.append("/file/Main-Style.css\" rel=\"stylesheet\" type=\"text/css\" />");
    }
    builder.append("<meta http-equiv=\"content-type\" content=\"text/head; charset=utf-8\">");
    builder.append("<meta name=\"robots\" content=\"INDEX,FOLLOW\" />");
    builder.append("<script src=\"").append(host).append("/file/scripting_functions.js\"></script>");
    builder.append("</head>");
    builder.append("<body leftMargin=\"0\" topMargin=\"0\">");
    return builder.toString();
  }

  String renderBottom(HttpRequestData hrd)  {
    StringBuilder builder = new StringBuilder();
//    String uriFolder = hrd.getUriFolder();
    if(!hrd.isMobile()) {
      builder.append("<script src=\"").append(host).append("/file/load-page.js\"></script>");
    }
    builder.append("<script src=\"").append(host).append("/file/show-menu.js\"></script>");
    builder.append("<script type=\"text/javascript\">");
    builder.append("var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");");
    builder.append("document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));");
    builder.append("</script>");
    builder.append("<script type=\"text/javascript\">");
    builder.append("try {");
    builder.append("var pageTracker = _gat._getTracker(\"UA-739861-7\");");
    builder.append("pageTracker._trackPageview();");
    builder.append("} catch(err) {}</script>");
    builder.append("<script type=\"text/javascript\" src=\"https://apis.google.com/js/plusone.js\"></script>");
    builder.append("</body>");
    builder.append("</HTML>");
    return builder.toString();
  }




}
