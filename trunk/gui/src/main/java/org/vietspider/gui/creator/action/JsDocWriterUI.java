/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.util.List;
import java.util.Properties;

import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.link.pattern.JsDocWriterGetter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2008  
 */
public class JsDocWriterUI  extends JsDocWriterGetter {
  
  protected ISourceInfo iSourceInfo;
  
  public JsDocWriterUI(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
  }
  
  public List<String> getJsDocWriters() {
    Properties properties = iSourceInfo.getField("properties");
    return super.getJsDocWriters(properties);
  }
}
