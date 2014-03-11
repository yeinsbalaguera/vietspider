/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class TestDateTimeDetector  extends TestCase {

  @Test
  public void testTreeMap() throws Exception {
    DateTimeDetector detector = new DateTimeDetector();
    
    String value = "2009-05-22 00:00:00.000";
    DetachDate date = detector.detect(value);
    Assert.assertEquals(date.getYear(), 2009);
    Assert.assertEquals(date.getMonth(), 05);
    Assert.assertEquals(date.getDate(), 22);
    
    value = "2009-05-03 00:00:00.000";
    date = detector.detect(value);
    Assert.assertEquals(date.getMonth(), 05);
    Assert.assertEquals(date.getDate(), 03);
    
  }
}
