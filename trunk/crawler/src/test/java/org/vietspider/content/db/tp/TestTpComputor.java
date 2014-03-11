/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class TestTpComputor extends TestCase {
  
  private File folder;
  private File folder2;
  private PluginData2TpDocument pluginData2TpDocument;
  private TpDocumentMatcher matcher;
  private org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
  
  @Override
  protected void setUp() throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    pluginData2TpDocument = new PluginData2TpDocument();
    matcher = new TpDocumentMatcher();
    
    folder = new File("D:\\Temp\\tp\\compute\\");
    folder2 = new File("D:\\Temp\\tp\\collection\\");
  }
  
  private TpWorkingData toTpData(String name) throws Exception {
    File file  = new File(folder, name);
    if(!file.exists()) file  = new File(folder, name + ".txt");
    if(!file.exists()) {
      file = new File(folder2, name);
      writer.copy(file, new File(folder, name));
    }
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
//    System.out.println(value.length());
    value = value.trim();
    return pluginData2TpDocument.convert(null, file.getName(), value);
  }
  
  private boolean compare(String name1, String name2, int rate) throws Exception {
    TpWorkingData data1 = toTpData(name1);
    TpWorkingData data2 = toTpData(name2);
    int _return = matcher.compare(data1, data2);
    if(matcher.isTest()) System.out.println("===========  > "+ _return + "%");
    return _return >= rate;
  }
  
  private void method1() throws Exception {
//    matcher.setTest(true);
    assertEquals(true, compare("1a", "1b", 10));
    assertEquals(false, compare("1a", "1c", 10));
    assertEquals(false, compare("1a", "1d", 10));
    assertEquals(false, compare("1a", "1e", 10));
    
    assertEquals(true, compare("2a", "2b", 10));
    
    assertEquals(true, compare("3a", "3b", 10));
    assertEquals(true, compare("3a", "3c", 10));
    assertEquals(true, compare("3b", "3c", 10));
    assertEquals(true, compare("3a", "3d", 10));
    
    assertEquals(false, compare("13.txt","201107262009090004.txt", 10));
    assertEquals(false, compare("15.txt","201107262036200000.txt", 10));
    assertEquals(false, compare("201107262003180013.txt","201107271057460010.txt", 10));
    assertEquals(false, compare("201107262008220007.txt","201107262009090004.txt", 10));
    assertEquals(true, compare("201107262003180013.txt","201107262004290010.txt", 10));
    assertEquals(false, compare("201107262009310001.txt","201107262041000019.txt", 10));
    assertEquals(true, compare("201107262009310001.txt","201107271107550012.txt", 10));
    assertEquals(true, compare("201107262009450004.txt","201107262009510008.txt", 10));
    assertEquals(true, compare("5a", "5b", 10));
    assertEquals(false, compare("5a", "5c", 10));
    assertEquals(true, compare("5a", "5d", 10));
    assertEquals(true, compare("5a", "5e", 10));
    assertEquals(true, compare("5a", "5f", 10));
    assertEquals(true, compare("6a", "6b", 10));
    assertEquals(true, compare("7a", "7b", 10));
    assertEquals(true, compare("8a", "8b", 10));
    assertEquals(true, compare("8a", "8c", 10));
    assertEquals(true, compare("9a", "9b", 10));
    assertEquals(true, compare("9a", "9c", 10));
    assertEquals(false, compare("10a", "10b", 10));
    assertEquals(false, compare("10a", "10c", 10));
    assertEquals(false, compare("10a", "10d", 10));
    assertEquals(false, compare("10a", "10e", 10));
    assertEquals(false, compare("13a", "13b", 10));
    assertEquals(false, compare("13a", "13c", 10));
    assertEquals(false, compare("13a", "13d", 10));
    assertEquals(true, compare("13a", "13e", 10));
    assertEquals(true, compare("14a", "14b", 10));
    assertEquals(true, compare("14a", "14c", 10));
    assertEquals(false, compare("15a", "15b", 10));
    assertEquals(false, compare("16a", "16b", 10));
    assertEquals(false, compare("16a", "16c", 10));
    assertEquals(false, compare("16b", "16c", 10));
    assertEquals(false, compare("17a", "17b", 10));
    assertEquals(false, compare("18a", "18b", 10));
    assertEquals(false, compare("19a", "19b", 10));
    assertEquals(false, compare("19a", "19c", 10));
    assertEquals(false, compare("19b", "19c", 10));
    assertEquals(false, compare("20a", "20b", 10));
    assertEquals(false, compare("20a", "20c", 10));
    assertEquals(false, compare("a1", "a11", 10));
    assertEquals(true, compare("a2", "a21", 10));
    
  }

  @Test
  public void test() throws Exception {
    method1();
    matcher.setTest(true);
    matcher.setPrintKey(true);
//    matcher.setPrintWord(true);
    
//    assertEquals(false, compare("a2", "a21", 10));
//    assertEquals(true, compare("8a", "8b", 10));
//    assertEquals(false, compare("", "", 10));
//    assertEquals(false, compare("", "", 10));
//    assertEquals(false, compare("", "", 10));
//    assertEquals(false, compare("", "", 10));
//    assertEquals(false, compare("", "", 10));
//    System.out.println("FDI".hashCode() +  " + " + "BÀI".hashCode());
  }

}
