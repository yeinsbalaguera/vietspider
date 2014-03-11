/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.model.Groups;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 18, 2007  
 */
public class GroupIO {
  
  private final static GroupIO instance = new GroupIO();
  
  public final static GroupIO getInstance() { return instance; }

  private Groups groups = null;
  private volatile long lastModified = System.currentTimeMillis();
  private GroupIO() {

  }
  
  public Groups loadGroups() {
    File file = UtilFile.getFile("sources/type", "groups.xml");
    if(file.lastModified() == lastModified) {
      if(groups != null) return groups;
    }
    
    lastModified = file.lastModified();
    if(file.exists())  {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        XMLDocument document = XMLParser.createDocument(xml, null);
        groups = XML2Object.getInstance().toObject(Groups.class, document);
        return groups;
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return new Groups();
    } 
    
    groups = new Groups();
    try {
      String xml = Object2XML.getInstance().toXMLDocument(groups).getTextValue();
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
      lastModified = file.lastModified();
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return groups;
  }
  
  public void saveGroups(Groups groups_) {
    this.groups  = groups_;
    try {
      Object2XML bean2XML = new Object2XML();
      XMLDocument document = bean2XML.toXMLDocument(groups);
      String xml = document.getTextValue();
      File file = UtilFile.getFile("sources/type", "groups.xml");
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
      lastModified = file.lastModified();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
 
}

