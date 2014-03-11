/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class ActionObjectTestFolder {

  private static File noDataFolder = new File("D:\\Temp\\no_mail\\");

  public static void main(String[] args) throws Exception {
    UtilFile.deleteFolder(noDataFolder, false);

    NlpProcessor processor = NlpProcessor.getProcessor();

    for(int i = 2; i < 3; i++) {
      File folder = new File("D:\\Temp\\test" + String.valueOf(i) + "\\"); 
      process(processor, folder);
    }

  }

  private static void process(NlpProcessor processor, File folder) throws Exception {
    File [] files = folder.listFiles();
    for(int i = 0; i< files.length; i++) {
      if(files[i].isDirectory()) continue;
      String text = new String(RWData.getInstance().load(files[i]), "utf-8");

      Map<Short, Collection<?>> map = processor.process(files[i].getName(), text);
      //    List<String> values = map.get(INlpFilter.PHONE);
      Collection<?> values = map.get(NLPData.ACTION_OBJECT);
      if(values == null || values.isEmpty()) {
        RWData.getInstance().copy(files[i], new File(noDataFolder, files[i].getName()));
        continue;
      }
      
      System.out.println(files[i].getName());
      System.out.println("result " + values);
      System.out.println(" total item "+ values.size());
      Iterator<?> iterator = values.iterator();
      while(iterator.hasNext()) {
        System.out.println(NLPData.action_object(iterator.next().toString()));
      }
      System.out.println("\n");
    }
  }

}
