/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.content.nlp.CityCodeDetector;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 24, 2009  
 */
public class TestCityDetector extends TestCase {
  
  private CityCodeDetector detector = new CityCodeDetector();
  
  private String detect(String name) throws Exception {
    URL url = TestNLPExtractor.class.getResource(name);
    File contentFile = new File(url.toURI());
    String text = new String(RWData.getInstance().load(contentFile), "utf-8");
    
    return detector.detect(text);
  }

  @Test
  public void testTreeMap() throws Exception {
    Assert.assertEquals(detect("article.txt"), "hà nội");
    Assert.assertEquals(detect("article2.txt"), "thành phố hồ chí minh");
    Assert.assertEquals(detect("article3.txt"), "bình thuận");
    Assert.assertEquals(detect("article4.txt"), "hà nội");
    Assert.assertEquals(detect("article5.txt"), "hà nội");
  }
}
