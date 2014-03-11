/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class TestMatching {
  
  private static void match(Tags tags, File file) throws Exception {
    if(file.isDirectory()) {
      File [] files = file.listFiles();
      for(int i = 0; i < files.length; i++) {
        match(tags, files[i]);
      }
      return;
    }
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
    Document document = new Document(value);
    document.setId(file.getName());
    String tag = tags.tag(document);
    if(tag == null) {
      File folder = new File("D:\\Temp\\categorization\\output\\unknown\\");
      folder.mkdirs();
      RWData.getInstance().copy(file, new File(folder, file.getName()));
      return;
    }
    File folder = new File("D:\\Temp\\categorization\\output\\" + tag + "\\");
    folder.mkdirs();
    RWData.getInstance().copy(file, new File(folder, file.getName()));
//    System.out.println(file.getName() + " : " + score);
  }
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
//    Tags tags = new Tags(new File("D:\java\test\vsnews\data\sources\type\tags"));
    Tags tags = Tags.getInstance("kinh-te");
    
    file  = new File("D:\\Temp\\categorization\\template\\");
    if(file.isDirectory()) {
      UtilFile.deleteFolder(new File("D:\\Temp\\categorization\\output\\"), false);
    }
    tags.setDefault("kinh_te");
//    file  = new File("D:\\Temp\\categorization\\temp.txt");
    match(tags, file);
  }
}
