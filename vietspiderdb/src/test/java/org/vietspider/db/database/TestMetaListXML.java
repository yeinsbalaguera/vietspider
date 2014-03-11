/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.database;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2011  
 */
public class TestMetaListXML {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\xml\\meta_list1.xml");
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
    MetaList metas = XML2Object.getInstance().toObject(MetaList.class, xml);
    System.out.println(metas);
    System.out.println(metas.getData().hashCode() +  " : " + metas.getData());
    
    System.out.println(Object2XML.getInstance().toXMLDocument(metas).getTextValue());
    
    System.exit(0);
    
  }
}
