/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Source;
import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public class SaveSourceThread extends Thread {
  
  private Source source;
  
  public SaveSourceThread(Source s) {
    this.source = s;
    this.start();
  }

  public void run() {
    if(source == null) return;
    String category = NameConverter.encode(source.getCategory());
    String group = source.getGroup().toString();
    String name = category +"."+NameConverter.encode(source.getName());
    File file = UtilFile.getFile("sources/sources/"+group+"/"+category+"/", name);
    
    try {
      String xml = Object2XML.getInstance().toXMLDocument(source).getTextValue();
      byte [] bytes = xml.getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
    }
  }
}
