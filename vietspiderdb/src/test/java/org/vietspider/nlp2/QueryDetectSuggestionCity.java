/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.nlp.query.QAddressDetector;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2011  
 */
public class QueryDetectSuggestionCity {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\data\\");

    //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;

//    String text = "cho thuê đống đa";
//    String text = "nha dong da";
    String text = "nhà nguyễn thị minh khai";
    text = "nhà nguyễn kiệm";
    text = "nhà nguyễn xí";
    text = "nhà hoàng diệu";
//    System.out.println(text);
    
    TextElement element = new TextElement(text);
    List<String> list = QAddressDetector.getInstance().detectSuggestion(element);

    for(int i = 0; i < list.size(); i++) {
      System.out.println(list.get(i));
    }
  }
}
