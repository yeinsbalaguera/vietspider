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
public class ComplexLabelData {
  
  protected String fileName;
  
  private long lastModified;
  private String[][] lines;
  
  public ComplexLabelData(String fileName) {
    this.fileName = fileName;
  }

  public String[] generate(String pattern) {
    loadData();
    if(lines.length < 1) return new String[0];

    String [] elements = new String[lines.length]; 

    for(int i = 0; i < lines.length; i++) {
      if(lines[i][0] != null 
          && lines[i][0].toLowerCase().indexOf(pattern) > -1) return new String[0];

      if(lines[i][1] != null 
          && lines[i][1].toLowerCase().indexOf(pattern) > -1) return new String[0];

      if("pattern".equalsIgnoreCase(lines[i][1].trim())) {
        elements[i] = lines[i][0] + pattern;
      } else if("pattern".equalsIgnoreCase(lines[i][0].trim())) {
        elements[i] = pattern + lines[i][1];
      } else {
        elements[i] = lines[i][0];
      }
    }

    return elements;
  }

  public void loadData() {
    File file  = UtilFile.getFile("system/cms/search/", fileName);
    if(file.lastModified() == lastModified) return;
    lines = new String[0][0];
    lastModified = file.lastModified();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.trim().split("\n");
      lines = new String[elements.length][2];

      for(int i = 0; i < elements.length; i++) {
        int index = elements[i].indexOf('$');
        if(index < 0) {
          lines[i][0] = elements[i];
          lines[i][1] = null;
          continue;
        }

        if(index == 0) {
          index = elements[i].indexOf(' ');
          lines[i][0] = elements[i].substring(1, index);
          lines[i][1] = elements[i].substring(index+1);
          continue;
        }

        lines[i][0] = elements[i].substring(0, index);
        lines[i][1] = elements[i].substring(index+1);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
  }

}
