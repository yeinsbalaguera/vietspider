/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;

import org.vietspider.bean.ArticleCollection;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2011  
 */
public class TestArticleCollectionXML {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\xml\\article-collections.xml");
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
    ArticleCollection source = XML2Object.getInstance().toObject(ArticleCollection.class, xml);
    
    System.exit(0);
    
  }
}
