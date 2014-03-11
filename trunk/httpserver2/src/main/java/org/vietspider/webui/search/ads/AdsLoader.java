/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.ads;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 4, 2010  
 */
public class AdsLoader {

  List<Advertise> load() {
    List<Advertise> advertises = new ArrayList<Advertise>();
    File file  = UtilFile.getFolder("system/cms/search/ads/");
    File [] files = UtilFile.listFiles(file, new FileFilter() {
      public boolean accept(File f) {
        return f.getName().endsWith(".adv");
      }
    });
    
    for(int i = 0; i < files.length; i++) {
      try {
        byte [] bytes = RWData.getInstance().load(files[i]);
        advertises.add(XML2Object.getInstance().toObject(Advertise.class, bytes));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return advertises;
  }
  
}
