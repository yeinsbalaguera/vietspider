/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;

import java.io.File;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.Source;
import org.vietspider.model.XML2Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2008  
 */
class SourceReader {
  
  Source readSource(String line) {
    if(line == null) return null;
    String [] elements = line.split("\\.");
    if(elements.length < 3) return null;

    String group = NameConverter.encode(CharsUtil.cutAndTrim(elements[1]));
    String category = NameConverter.encode(CharsUtil.cutAndTrim(elements[2]));
    String name  = category+"."+NameConverter.encode(CharsUtil.cutAndTrim(elements[0]));
    
    return readSource(group, category, name);
  }
  
  Source readSource(String group, String category, String name) {
    File folder = UtilFile.getFolderNotCreate("sources/sources/"+group+"/"+category+"/");
    if(folder == null || !folder.exists()) return null;
    File fileSource = new File(folder, name);
    if(!fileSource.exists()) return null;
    
    return readSource(fileSource);
  }
  
  public Source readSource(File fileSource) {
    try {
      
      XML2Source xml2Source = new XML2Source();
      return xml2Source.toSource(RWData.getInstance().load(fileSource));
      
     /* String xml = new String(RWData.getInstance().load(fileSource), Application.CHARSET);
      if(xml == null || xml.trim().length() < 1) return null;
      
      XMLDocument document = XMLParser.createDocument(xml, null);
      return XML2Object.getInstance().toObject(Source.class, document);*/
    } catch (Exception e) {
      LogService.getInstance().setThrowable(fileSource.getName(), e);
    }
    return null;
  }
 
}
