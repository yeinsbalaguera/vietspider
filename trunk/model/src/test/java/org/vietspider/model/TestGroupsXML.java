/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2011  
 */
public class TestGroupsXML {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\vssearcher\\data\\sources\\type\\groups.xml");
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
    Groups groups = XML2Object.getInstance().toObject(Groups.class, xml);
    Group[] list = groups.getGroups();
    
    for(int i = 0; i < list.length; i++) {
      System.out.println(list[i].getType());
    }
    
    System.exit(0);
  }
}
