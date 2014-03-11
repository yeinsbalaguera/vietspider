/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.content.tp.TpWorkingData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 7, 2011  
 */
public class TestTpDocAnalyzer2 {
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    PluginData2TpDocument pluginData2TpDocument = new PluginData2TpDocument();
    
    File folder = new File("D:\\Temp\\tp\\unit\\");
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      String content = new String(RWData.getInstance().load(files[i]), Application.CHARSET);
      TpWorkingData workingData = pluginData2TpDocument.convert(null, files[i].getName(), content);
    }
  }
}
