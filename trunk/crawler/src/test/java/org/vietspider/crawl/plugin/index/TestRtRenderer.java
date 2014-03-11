/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2010  
 */
public class TestRtRenderer {
  
  public static void main(String[] args) throws Exception {
    File folder = new File("D:\\Temp\\articles\\html\\");
    File input = new File(folder, "1.html");
    
    String text = new String(RWData.getInstance().load(input), "utf8");
    RtRenderder renderder = new RtRenderder();
    String result = renderder.build(text);
    
    File output = new File(folder, "output.txt");
    org.vietspider.common.io.RWData.getInstance().save(output, result.getBytes("utf8"));
    
  }
  
}
