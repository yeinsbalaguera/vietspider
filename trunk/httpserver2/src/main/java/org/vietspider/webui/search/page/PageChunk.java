/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.page;

import java.io.File;
import java.io.OutputStream;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 22, 2010  
 */
public class PageChunk extends Chunk {

  protected byte[] top;
  protected byte[] bottom;
  protected long lastModified;
  protected String fileName;
  protected String labelPattern ;
  
  public PageChunk(String fileName) {
    this(fileName, "$pattern");
  }

  public PageChunk(String fileName, String labelPattern) {
    this.fileName = fileName;
    this.labelPattern = labelPattern;
  }

  public void write(OutputStream output, String pattern) {
    loadData();
    if(top == null) return;
    try {
      output.write(top);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
    if(bottom == null) return;

    try {
      if(pattern != null) {
        if(pattern.indexOf('\"') > -1) {
          pattern = encode(pattern);
        }
        output.write(pattern.getBytes(Application.CHARSET));
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      output.write(bottom);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
  public void writeNoEncode(OutputStream output, String pattern) {
    loadData();
    if(top == null) return;
    try {
      output.write(top);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
    if(bottom == null) return;

    try {
      if(pattern != null) {
        output.write(pattern.getBytes(Application.CHARSET));
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    try {
      output.write(bottom);
      output.flush();
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
  protected void loadData() {
    File file  = UtilFile.getFile("system/cms/search/", fileName);
    if(file.lastModified() == lastModified) return;
    lastModified = file.lastModified();
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      int index = text.indexOf(labelPattern);
      if(index < 1) {
        top = text.getBytes(Application.CHARSET);
        return;
      }

      String subText = text.substring(0, index);
      top  = subText.getBytes(Application.CHARSET);

      subText = text.substring(index+ labelPattern.length());
      bottom = subText.getBytes(Application.CHARSET);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
  }
  
}
