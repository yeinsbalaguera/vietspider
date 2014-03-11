/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2011  
 */
public class QuerySimpleAOTest {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\data\\");

    //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;

//    String text = "cho thuê đống đa";
//    String text = "nha dong da";
    String text = "nhà phố vũ trọng phụng";
//    System.out.println(text);
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
   
    TextElement element = new TextElement(text);
    
//    NlpObject object = analyzer.getObjectExtractor().extract(element);
//    if(object != null) {
//      System.out.println(object.getLabel());
//    } else {
//      System.out.println("object is null ");
//    }
//    
//    NlpAction action = analyzer.getActionExtractor().extract(element);
//    if(action != null) {
//      System.out.println(action.getLabel());
//    } else {
//      System.out.println("object is null ");
//    }
   
    
    System.exit(0);
  }
}
