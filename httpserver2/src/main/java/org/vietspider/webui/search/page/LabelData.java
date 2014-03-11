/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 26, 2010  
 */
public class LabelData {
  
  protected String fileName;
  
  protected long lastModified;
  
  private String [] elements;
  
  public LabelData(String fileName) {
    this.fileName = fileName;
  }

  public String[] generate() {
    loadData();
    return elements;
  }

  public void loadData() {
    File file  = UtilFile.getFile("system/cms/search/", fileName);
    if(file.lastModified() == lastModified) return;
    elements = new String[0];
    lastModified = file.lastModified();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      elements = text.trim().split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
  }
  
}
