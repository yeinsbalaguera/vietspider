/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TestNLPFilterImpl2 {

  private static File folder = new File("D:\\Temp\\test2\\"); 

  public static String load(File file) throws Exception {
    return new String(RWData.getInstance().load(file), "utf-8");
  }
  
  public static void main(String[] args) throws Exception {
//    TextSplitter splitter = new TextSplitter();
    File [] files = folder.listFiles();
    NlpProcessor filder = NlpProcessor.getProcessor();
//    PriceFilter.TEST = true;

    File noMailFolder = new File("D:\\Temp\\no_mail\\");
    UtilFile.deleteFolder(noMailFolder, false);
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    for(int i = 0; i< files.length; i++) {
      if(files[i].isDirectory()) continue;
      String text = load(files[i]);
      System.out.println("\n==============================================");
      System.out.println(files[i].getName());
      Map<Short, Collection<?>> map = filder.process(files[i].getName(), text);
//      String [] values = map.get(INlpFilter.EMAIL);
//      if(text.indexOf('@') < 0) continue;
      
//      List<String> values = map.get(INlpFilter.PHONE);
      
//      Collection<?> values = map.get(NLPData.PRICE);
      Collection<?> values = map.get(NLPData.ADDRESS);
      if( values == null || values.size() < 1)  {
        writer.copy(files[i], new File(noMailFolder, files[i].getName()));
      } else {
        if(values != null) {
          System.out.println(" total item "+ values.size());
        }
        System.out.println("result " + values);
//        Iterator<?> iterator = values.iterator();
//        while(iterator.hasNext()) {
//          System.out.print(iterator.next() + " | ");
//        }
//        System.out.println("\n");
      }
    }
  }

}
