/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.idm2;

import java.io.File;
import java.util.Date;

import org.vietspider.bean.Domain;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.text.NameConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 8, 2009  
 */
public class SimpleEntryDomain implements IEntryDomain {
  
  private File file;
  
  public File getFile() { return file; }
  
  public SimpleEntryDomain(Domain domain) throws Exception {
    this(domain.getDate(), domain.getGroup(), domain.getCategory(), domain.getName());
  }
  
  public SimpleEntryDomain(String date, String group, String category, String name) throws Exception {
    this(date, group + "." + category, name);
  }

  public SimpleEntryDomain(String date, String category, String name) throws Exception {
    if(date == null) throw new Exception("Unkown exception");
    if(date.indexOf('/') > -1) {
      Date dateValue = CalendarUtils.getDateFormat().parse(date);
      date = CalendarUtils.getFolderFormat().format(dateValue);
    }
    
    File folder = UtilFile.getFolder("content/summary/eid/" + date + "/");
//    String group = domain.getGroup();
//    System.out.println(group);
//    if(group == null) return  date + ".eid";
    
    if(category == null) {
      file = new File(folder, date + ".eid");
      return;
    }
    
    category = NameConverter.encode(category);
    
    if(name == null) {
      file = new File(folder, date + "."+ category + ".eid");
      return;
    }
    name = NameConverter.encode(name);
    file = new File(folder, date + "." + category + "." + name + ".eid");
  }
}
