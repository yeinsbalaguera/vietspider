/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.miming.vn;

import java.io.File;

import org.vietspider.content.tp.vn.ViTopicTrackingAnalyzer;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class TestDocumentComparator2 {
  
  public static void main(String[] args) throws Exception  {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
    ViTopicTrackingAnalyzer analyzer2 = new ViTopicTrackingAnalyzer();
    TpDocumentMatcher matcher = new TpDocumentMatcher();
    
    
  }
}
