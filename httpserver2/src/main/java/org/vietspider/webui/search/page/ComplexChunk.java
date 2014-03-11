/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 26, 2010  
 */
public class ComplexChunk {
  
  private String fileName;
  private long lastModified;
  private List<Object> elements = new ArrayList<Object>();
  
  public ComplexChunk(String name) {
    this.fileName = name;
  }

  public void write(OutputStream output, Map<String, String> properties) {
    loadData();
    if(elements.size() < 1) return;

    for(int i = 0; i < elements.size(); i++) {
      Object ele = elements.get(i);
      if(ele instanceof byte[]) {
        try {
          output.write((byte[])ele);
        } catch (Exception e) {
          LogService.getInstance().setMessage("SERVER", e, null);
        }
      } else {
        String key = (String)ele;
        String value = properties.get(key);
        if(value == null) {
          value  = "$" + key;
        } 
        try {
          output.write(value.getBytes(Application.CHARSET));
        } catch (Exception e) {
          LogService.getInstance().setMessage("SERVER", e, null);
        }
      }
    }
    
    try {
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }

  private void loadData() {
    File file  = UtilFile.getFile("system/cms/search/", fileName);
    if(file.lastModified() == lastModified) return;
    lastModified = file.lastModified();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      int index = text.indexOf('$');
      int start = 0;
      while(index > -1) {
        if(index > start) {
          String subText = text.substring(start, index);
          elements.add(subText.getBytes(Application.CHARSET));
        }
        int end = index;
        index++;
        start = index;
        while(index <  text.length()) {
          char c = text.charAt(index);
          if(!Character.isLetterOrDigit(c)) {
            end = index;
            break;
          }
          index++;
        }
        if(end > start) {
          elements.add(text.substring(start, end));
        }
        start = end;
        index = text.indexOf('$', start);
      }
      
      if(start < text.length()) {
        String subText = text.substring(start);
        elements.add(subText.getBytes(Application.CHARSET));
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
  }
}
