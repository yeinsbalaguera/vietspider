/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class TestClassifiedTags extends TestCase {
  
  private File folder;
  private Tags tags;
  
  @Override
  protected void setUp() throws Exception {
    Document.print = true;
    tags = new Tags(new File("D:\\Program\\vsnews\\data\\sources\\type\\tags\\"), "bat-dong-san");
    tags.setMinScore(0);
    tags.setMinRate(0.1);
    tags.setMinDefaultRate(1.0);
    folder = new File("D:\\Temp\\classified\\");
  }
  
  private String tag(String name) throws Exception {
    File file  = null;
    if(name.endsWith(".txt")) {
      file  = new File(folder, "test2\\" + name);
      if(!file.exists()) {
        file  = new File(folder, "test3\\" + name);
      }
      if(!file.exists()) {
        file  = new File(folder, "test4\\" + name);
      }
    } else {
      file  = new File(folder, "test2\\" + name +".txt");  
      if(!file.exists()) {
        file  = new File(folder, "test3\\" + name +".txt"); 
      }
      if(!file.exists()) {
        file  = new File(folder, "test4\\" + name +".txt"); 
      }
    }
    
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
//    System.out.println(value);
    Document document = new Document(value);
    document.setId(file.getName());
    return tags.tag(document);
  }
  
  private void method1() throws Exception {
    assertEquals("mua-ban", tag("201101300500220040"));
    assertEquals("mua-ban", tag("201101300500250003"));
    assertEquals("thue", tag("201101300500560043"));
    assertEquals("mua-ban", tag("201101300500270006"));
    assertEquals("mua-ban", tag("201101300500290077.txt"));
    assertEquals("thue", tag("201101300502010018.txt"));
    assertEquals("mua-ban", tag("201101300545110000.txt"));
    assertEquals("sang-nhuong", tag("201101300506540003.txt"));
    assertEquals("bat-dong-san", tag("201101300507040081.txt"));
    assertEquals("sang-nhuong", tag("201101300520350052.txt"));
    assertEquals("sang-nhuong", tag("201101300502520010.txt"));
    assertEquals("mua-ban", tag("201101261921150012.txt"));
    assertEquals("bat-dong-san", tag("201101260608100036.txt"));
    assertEquals("thue", tag("201101171117360003.txt"));
    assertEquals("mua-ban", tag("201101270750320086.txt"));
    assertEquals("sang-nhuong", tag("201101310314030075.txt"));
    assertEquals("thue", tag("201101310547460099.txt"));
    assertEquals("sang-nhuong", tag("201101272020060007.txt"));
    assertEquals("thue", tag("201101160622250030.txt"));
    assertEquals("mua-ban", tag("201101142245350040.txt"));
    assertEquals("bat-dong-san", tag("201101270935560010.txt"));
    assertEquals("mua-ban", tag("201101150929020010.txt"));
    assertEquals("mua-ban", tag("201101152054050072.txt"));
    assertEquals("bat-dong-san", tag("201101150508470092.txt"));
    assertEquals("bat-dong-san", tag("201101150822430064.txt"));
    assertEquals("mua-ban", tag("201101170944490078.txt"));
    assertEquals("mua-ban", tag("201101171050310064.txt"));
    assertEquals("mua-ban", tag("201101171017130075.txt"));
    assertEquals("mua-ban", tag("201101170839320017.txt"));
    assertEquals("mua-ban", tag("201101171709290020.txt"));
    assertEquals("mua-ban", tag("201101142229240068.txt"));
    assertEquals("mua-ban", tag("201101151018540058.txt"));
    assertEquals("sang-nhuong", tag("201101160733180036.txt"));
    assertEquals("thue", tag("201101170550280066.txt"));
    assertEquals("thue", tag("201101161620430067.txt"));
    assertEquals("mua-ban", tag("201101151454320044.txt"));
    assertEquals("mua-ban", tag("201101270840370025.txt"));
    assertEquals("mua-ban", tag("201101271000390075.txt"));
    assertEquals("mua-ban", tag("201101271001150089.txt"));
    assertEquals("mua-ban", tag("201101271036050010.txt"));
    assertEquals("mua-ban", tag("201101271038490024.txt"));
    assertEquals("mua-ban", tag("201101271041570027.txt"));
    assertEquals("mua-ban", tag("201101272018340007.txt"));
    assertEquals("mua-ban", tag("201101272100100029.txt"));
    assertEquals("mua-ban", tag("201101272031030017.txt"));
    assertEquals("thue", tag("201101310536100015.txt"));
    assertEquals("thue", tag("201101271015380035.txt"));
    assertEquals("mua-ban", tag("201101271041130001.txt"));
    assertEquals("mua-ban", tag("201101271431380086.txt"));
    assertEquals(null, tag("201101161450150041.txt"));
    assertEquals(null, tag("201101161422350053.txt"));
    assertEquals(null, tag("201101161621180097.txt"));
    assertEquals(null, tag("201101172124370059.txt"));
    assertEquals(null, tag("201101270917240052.txt"));
    assertEquals(null, tag("201101270919520047.txt"));
    assertEquals("mua-ban", tag("201101170956130086.txt"));
    assertEquals("bat-dong-san", tag("201101270935470012.txt"));
    assertEquals("bat-dong-san", tag("201101272041270029.txt"));
    assertEquals("sang-nhuong", tag("201101310313070075.txt"));
    assertEquals(null, tag("1.txt"));
  }

  @Test
  public void test() throws Exception {
    Document.print = false;
//    method1();
    Document.print = true;
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
//    assertEquals("", tag(""));
  }

}
