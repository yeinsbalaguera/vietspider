/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.browser.Browser;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.ui.services.ClientRM;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 18, 2008  
 */
public class UnreadContentFilterPlugin extends ClientPlugin {

  protected String label;
  protected int type = DOMAIN;
  protected String filter = "/filter:0";

  public UnreadContentFilterPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    Class<?>  clazz = UnreadContentFilterPlugin.class;
    label = resources.getLabel(clazz.getName()+".label");
  }
  
  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type_) {
    this.type = type_;
    return type == CONTENT || type == DOMAIN;
  }

  public void invoke(Object... objects) {
    Browser browser = (Browser) objects[1];
    
    Pattern pattern = null;
    if(type == DOMAIN) {
      pattern = Pattern.compile("\\b\\d{1,2}\\s*[.]\\s*\\d{1,2}\\s*[.]\\s*\\d{4}\\b");
    } else {
      pattern = Pattern.compile("\\d{4}\\d{2}\\d{2}");
    }
    String date  = null;
    try {
      String url = browser.getUrl();
      Matcher matcher = pattern.matcher(url);
      if(matcher.find()) {
        date = url.substring(matcher.start(), matcher.end());
      }
      if(type == CONTENT) {
        Date dateInstance = new SimpleDateFormat("yyyyMMdd").parse(date); 
        date = CalendarUtils.getParamFormat().format(dateInstance);
      }
    } catch (Exception e) {
    }
    
    if(date == null){
      Calendar cal = Calendar.getInstance();    
      date = CalendarUtils.getParamFormat().format(cal.getTime());
    } else {
      date = date.replace('/', '.');
    }
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    StringBuilder builder = new StringBuilder();
    builder.append(connector.getRemoteURL()).append('/');
    builder.append(connector.getApplication()).append('/').append("DOMAIN");
    builder.append("/1/").append(date).append(filter);    
    browser.setUrl(builder.toString());
  }
}
