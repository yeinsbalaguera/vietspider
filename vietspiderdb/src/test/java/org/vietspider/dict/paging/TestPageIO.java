/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.paging;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.bean.Article;
import org.vietspider.paging.Entry;
import org.vietspider.paging.MetaIdEntry;
import org.vietspider.paging.PageIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class TestPageIO extends TestCase {

  @Test
  public void testData() throws Exception {
    File file = new File("D:\\Temp\\entry\\entries.txt");
    if(file.exists()) file.delete();
    file.createNewFile();
    
    MetaIdEntry entry = new MetaIdEntry(0, 0);
    
    PageIO<MetaIdEntry> pageIO = entry.createIO(file);
    pageIO.write(entry);
    for(int i = 1; i < 103; i++){
      pageIO.write(new MetaIdEntry(i, 0));
      if(i%10 != 0) continue;
      pageIO.commit();
    }
    pageIO.commit();
    
    assertEquals(pageIO.getTotalPage(10), 11);
    
    
    List<MetaIdEntry> list = pageIO.loadPageByDesc(8, 10, null);
    assertEquals(list.size(), 10);
    
    
    assertEquals(list.get(0).getMetaId(), 32);
    assertEquals(list.get(4).getMetaId(), 28);
    assertEquals(list.get(9).getMetaId(), 23);
    
    list = pageIO.loadPageByDesc(11, 10, null);
    assertEquals(list.size(), 3);
    
    list = pageIO.loadPageByDesc(8, 10, null);
    entry = list.get(4);
    entry.setType(Entry.UPDATE);
    entry.setStatus(Article.EDITED);
    pageIO.write(entry);
    pageIO.commit();
    
    list = pageIO.loadPageByDesc(8, 10, null);
    assertEquals(list.get(4).getStatus(), Article.EDITED);
    
    System.out.println(file.length());
    pageIO.write(new MetaIdEntry(12, 5, Entry.UPDATE));
    pageIO.write(new MetaIdEntry(78, 5, Entry.UPDATE));
    pageIO.write(new MetaIdEntry(34, 5, Entry.UPDATE));
    pageIO.write(new MetaIdEntry(98, 5, Entry.UPDATE));
    
    pageIO.commit();
    pageIO.optimize(5);
    System.out.println(file.length());
    
//    file.delete();
  }
  
}
