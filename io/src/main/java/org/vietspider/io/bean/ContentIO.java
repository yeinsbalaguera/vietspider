/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.io.bean;

import java.io.File;
import java.util.Date;

import org.vietspider.bean.Content;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 13, 2007
 */
public class ContentIO {

  private String catcheDate = "" ; 
  private File folder;

  public synchronized void saveConent(Content content) throws Exception {
    String date = content.getDate();
    if(!date.equals(catcheDate)) {
      catcheDate = date; 
      Date calendar = CalendarUtils.getDateFormat().parse(date);        
      folder  = UtilFile.getFolder("content/html/"+ CalendarUtils.getFolderFormat().format(calendar));     
    }
    StringBuilder builder  = new StringBuilder().append(content.getMeta()).append(".html");
    File file = new File(folder, builder.toString());
    RWData.getInstance().save(file, content.getContent().getBytes(Application.CHARSET));
    content.setContent("");
  }

  public synchronized  void loadContent(Content content) throws Exception {
    Date calendar = CalendarUtils.getDateFormat().parse(content.getDate());        
    File saveFolder  = UtilFile.getFolder("content/html/"+ CalendarUtils.getFolderFormat().format(calendar)); 
    StringBuilder builder  = new StringBuilder().append(content.getMeta()).append(".html");
    File file = new File(saveFolder, builder.toString());
    byte [] data = RWData.getInstance().load(file);
    content.setContent(new String(data, Application.CHARSET));
  }

}
