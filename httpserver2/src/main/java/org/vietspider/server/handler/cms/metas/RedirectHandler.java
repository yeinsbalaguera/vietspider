/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.RedirectRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2007  
 */
public class RedirectHandler extends CMSHandler <String> {

  public RedirectHandler(String type, String _name) {
    super(type);
    name  = _name;
  }
  
  @Override
  public String handle(HttpRequest request, 
      HttpResponse response, HttpContext context, String... params) throws Exception {
    
    CMSService cms = CMSService.INSTANCE;
    params = new String []{cms.getHost(), String.valueOf(cms.getWebPort())}; 
    String path =  "/" + type + "/" + name + "/1/";
//    SourceLogUtils log = new SourceLogUtils();
    String []dates = EIDFolder2.loadDates();//log.loadDate();
    if(dates.length > 0) {
      DateFormat dateFormat = CalendarUtils.getDateFormat();
      Date dateValue = dateFormat.parse(dates[0]);
      dateFormat = CalendarUtils.getParamFormat();
      path += dateFormat.format(dateValue);
    } else {
      Calendar calendar = Calendar.getInstance();
      DateFormat dateFormat = CalendarUtils.getParamFormat();
      path += dateFormat.format(calendar.getTime());
    }
    response.addHeader(new BasicHeader("Location", path));
    response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
    return write(request, response, context, path, params);
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, String value, String cookies[], String...params) throws Exception {
    RedirectRenderer render = new RedirectRenderer();
    render.write(output, type, "0", "", new String[]{value, value});
    return "text/html";
  }

}
