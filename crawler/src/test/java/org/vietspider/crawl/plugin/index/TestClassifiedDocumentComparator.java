/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class TestClassifiedDocumentComparator extends TestCase {
  
  static {
    File file  = new File("D:\\Temp\\articles\\data\\");
    try {
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
    } catch (Exception e) {
      e.printStackTrace();
    } 
    
    UtilFile.deleteFolder(new File("D:\\Temp\\articles\\data\\content\\"));
  }
  
//  private ClassifiedViTopicTrackingAnalyzer analyzer2 = new ClassifiedViTopicTrackingAnalyzer();
  private TpDocumentMatcher matcher = new TpDocumentMatcher();
  
  private TpWorkingData tpDocument1;
  private TpWorkingData tpDocument2;
  
  private TpWorkingData loadContent(String name) throws Exception {
    File file = new File("D:\\Temp\\articles\\temp\\", name);
    byte [] bytes = RWData.getInstance().load(file);
    String content = new String(bytes, "utf-8");
    if(file.getName().endsWith("html") || 
        file.getName().endsWith("htm")) {
      RtRenderder renderder = new RtRenderder();
      content = renderder.build(content);
      RefsDecoder decoder = new RefsDecoder();
      content = new String(decoder.decode(content.toCharArray()));
    }
    
//    TpWorkingData workingData = analyzer2.analyzeDocument/*(content);
//    workingData.getTpDocument().setId(name);*/
//    return workingData;
    return null;
  }
  
  private void printKeys(TpWorkingData working) {
    TreeSet<String> keys = working.getKeys();
    Iterator<String> iterator = keys.iterator();
    int i = 0;
    while(iterator.hasNext()) {
      System.out.print(iterator.next()+",");
      i++;
      if(i%10 == 0) System.out.println();
    }
    System.out.println("\n\n");
  }
  
  private void testDocument(String name1, String name2, boolean expect) throws Throwable {
    tpDocument1 = loadContent(name1); 
    tpDocument2 = loadContent(name2);
    double value = matcher.compare(tpDocument1, tpDocument2);
    if(expect) {
      System.out.println(matcher.getInfo());
    } else {
      System.out.println("###############"+matcher.getInfo());
    }
//    System.out.println(" ===> "+ value);
    System.out.println("================================================");
    
    System.out.println("==== > "+ value);
    try {
      Assert.assertEquals(value >= 15, expect);
    } catch (Throwable e) {
      printKeys(tpDocument1);
      printKeys(tpDocument2);
      throw e;
    }
    System.out.println("\n");
  }
  
  @Test
  public void testComparator() throws Throwable {
//    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
//    testDocument("170.txt", "171.txt", false);
//    testDocument("170.txt", "172.txt", true);
//    testDocument("171.txt", "172.txt", false);
//    
//    testDocument("173.txt", "174.txt", false);
//    testDocument("175.html", "176.html", false);
    testDocument("177.html", "178.html", true);
  }

}

