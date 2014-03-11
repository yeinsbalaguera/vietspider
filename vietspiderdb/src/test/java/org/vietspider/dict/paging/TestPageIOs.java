/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.paging;

import java.io.File;

import org.vietspider.paging.MetaIdEntry;
import org.vietspider.paging.PageIOs;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class TestPageIOs {
  
  public static void main(String[] args) throws Throwable {
    File file = new File("D:\\jav\\Temp");
    if(file.exists()) file.delete();
    file.createNewFile();
    
    MetaIdEntry entry = new MetaIdEntry(13434233, 0);
    System.out.println(entry.getMetaId());
    
    PageIOs.getInstance().write(file, entry);
    PageIOs.getInstance().write(file, new MetaIdEntry(3454344, 0));
    PageIOs.getInstance().write(file, new MetaIdEntry(34, 0));
    PageIOs.getInstance().write(file, new MetaIdEntry(12344, 0));
    try {
      Thread.sleep(10*1000);
    } catch (Exception e) {
    }
    
    PageIOs.getInstance().write(file, new MetaIdEntry(6324, 0));
    PageIOs.getInstance().write(file, new MetaIdEntry(875332, 0));
    PageIOs.getInstance().write(file, new MetaIdEntry(99824, 0));
    
    try {
      Thread.sleep(15*1000);
    } catch (Exception e) {
    }
  }
  
}
