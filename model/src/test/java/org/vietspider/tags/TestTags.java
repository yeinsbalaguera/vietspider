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
public class TestTags extends TestCase {
  
  private File folder;
  private Tags tags;
  
  @Override
  protected void setUp() throws Exception {
    Document.print = true;
    tags = new Tags(new File("D:\\java\\test\\vsnews\\data\\sources\\type\\tags\\"), "kinh-te");
    folder = new File("D:\\Temp\\categorization\\unit_test\\");
  }
  
  private String tag(String name) throws Exception {
    File file  = new File(folder, name +".txt");
    String value  = new String(RWData.getInstance().load(file), Application.CHARSET);
    Document document = new Document(value);
    document.setId(file.getName());
    return tags.tag(document);
  }
  
  private void method1() throws Exception {
    Document.print = false;
    assertEquals("tai-chinh", tag("201107262006090015"));
    assertEquals("tai-chinh", tag("201107262006450014"));
    assertEquals("kinh-te", tag("201107262008010013"));
    assertEquals("kinh-te", tag("201107262006030000"));
    assertEquals("bat-dong-san", tag("201107262009560019"));
    
    assertEquals("kinh-te", tag("201107262006310009"));
    assertEquals("doanh-nghiep", tag("201107262005550003"));
    assertEquals("kinh-te", tag("201107262005490019"));
    
    assertEquals("tai-chinh", tag("201107262005030011"));
    assertEquals("bat-dong-san", tag("201107262004430009"));
    assertEquals(null, tag("201107262003310002"));
    assertEquals("kinh-te", tag("201107262036220002"));
    assertEquals("kinh-te", tag("201107262042120019"));
    assertEquals("kinh-te", tag("201107262055210017"));
    assertEquals("tai-chinh", tag("201107262055070000"));
    
    assertEquals("tai-chinh", tag("201107262055030008"));
    assertEquals("kinh-te", tag("201107262054490013"));
    assertEquals("bat-dong-san", tag("201107262054340009"));
    assertEquals("kinh-te", tag("201107262053480003"));
    assertEquals("tai-chinh", tag("201107262053430010"));
    assertEquals("kinh-te", tag("201107262053400009"));
    assertEquals("kinh-te", tag("201107262053350007"));
    assertEquals("tuyen-dung", tag("201107262053140006"));
    
    assertEquals("kinh-te", tag("201107262053030000"));
    assertEquals("kinh-te", tag("201107262053090012"));
    assertEquals("kinh-te", tag("201107262052490012"));
    assertEquals("kinh-te", tag("201107262052440008"));
    assertEquals(null, tag("201107262052410006"));
    assertEquals("kinh-te", tag("201107262052160000"));
    assertEquals("kinh-te", tag("201107262052130009"));
    assertEquals("tuyen-dung", tag("201107262052110007"));
    assertEquals("doanh-nghiep", tag("201107262051550013"));
    assertEquals("doanh-nghiep", tag("201107262050590017"));
    assertEquals("kinh-te", tag("201107262050470013"));
    assertEquals("kinh-te", tag("201107262049590011"));
    assertEquals("kinh-te", tag("201107262049440005"));
    assertEquals("kinh-te", tag("201107262048540015"));
    assertEquals("doanh-nghiep", tag("201107262048290007"));
    assertEquals("kinh-te", tag("201107262048160000"));
    assertEquals("kinh-te", tag("201107262047500010"));
    assertEquals("kinh-te", tag("201107262047460019"));
    assertEquals("kinh-te", tag("201107262047330004"));
    assertEquals("doanh-nghiep", tag("201107262047110005"));
    assertEquals("kinh-te", tag("201107262046550005"));
    assertEquals("kinh-te", tag("201107262046260000"));
    assertEquals("kinh-te", tag("201107262046130001"));
    assertEquals("kinh-te", tag("201107262046160014"));
    assertEquals("kinh-te", tag("201107262046070007"));
    assertEquals("kinh-te", tag("201107262046020013"));
    assertEquals("tai-chinh", tag("201107262045520019"));
    assertEquals("kinh-te", tag("201107262045500017"));
    assertEquals(null, tag("201107262045470004"));
    assertEquals("doanh-nghiep", tag("201107262045460013"));
    assertEquals("kinh-te", tag("201107262045420011"));
    assertEquals("kinh-te", tag("201107262045390000"));
    assertEquals("kinh-te", tag("201107262045550001"));
    assertEquals("kinh-te", tag("201107262045220001"));
    assertEquals("kinh-te", tag("201107262044520018"));
    assertEquals(null, tag("201107262044460015"));
    assertEquals("kinh-te", tag("201107262044430013"));
    assertEquals("kinh-te", tag("201107262044050001"));
    assertEquals("chung-khoan", tag("201107262043460005"));
    assertEquals("doanh-nghiep", tag("201107262043190004"));
    assertEquals("kinh-te", tag("201107262041450011"));
    assertEquals("kinh-te", tag("201107262041040012"));
    assertEquals("kinh-te", tag("201107262040250004"));
    assertEquals("doanh-nghiep", tag("201107262039520012"));
    assertEquals("kinh-te", tag("201107262039480011"));
    assertEquals(null, tag("201107262039230006"));
    assertEquals("kinh-te", tag("201107271021060007"));
    assertEquals("kinh-te", tag("201107271020370007"));
    assertEquals("kinh-te", tag("201107271019500002"));
    assertEquals("kinh-te", tag("201107271019290005"));
    assertEquals("tai-chinh", tag("201107271017270018"));
    assertEquals("bat-dong-san", tag("201107270956530011"));
    assertEquals("chung-khoan", tag("201107270954350015"));
    assertEquals("bat-dong-san", tag("201107270954050018"));
    assertEquals("kinh-te", tag("201107270953560000"));
    assertEquals(null, tag("1"));
    assertEquals("chung-khoan", tag("2"));
    assertEquals("bat-dong-san", tag("3"));
    assertEquals("kinh-te", tag("4"));
    assertEquals("bat-dong-san", tag("5"));
    assertEquals("bat-dong-san", tag("6"));
    assertEquals("bat-dong-san", tag("7"));
    assertEquals("kinh-te", tag("8"));
    assertEquals("bat-dong-san", tag("9"));
    assertEquals("bat-dong-san", tag("10"));
    assertEquals("kinh-te", tag("11"));
    assertEquals("doanh-nghiep", tag("12"));
    assertEquals("kinh-te", tag("13"));
    assertEquals("kinh-te", tag("14"));
    assertEquals("bat-dong-san", tag("15"));
    assertEquals("bat-dong-san", tag("16"));
    assertEquals("kinh-te", tag("17"));
    assertEquals("kinh-te", tag("18"));
    assertEquals("kinh-te", tag("19"));
    assertEquals("doanh-nghiep", tag("20"));
    assertEquals("bat-dong-san", tag("21"));
    assertEquals(null, tag("22"));
    assertEquals(null, tag("23"));
    assertEquals("kinh-te", tag("24"));
    assertEquals("tuyen-dung", tag("25"));
    assertEquals(null, tag("26"));
    assertEquals("kinh-te", tag("27"));
    assertEquals("kinh-te", tag("28"));
    assertEquals(null, tag("29"));
    assertEquals("kinh-te", tag("30"));
    assertEquals("bat-dong-san", tag("31"));
    assertEquals("bat-dong-san", tag("32"));
    assertEquals(null, tag("33"));
    assertEquals(null, tag("34"));
    assertEquals("bat-dong-san", tag("35"));
    assertEquals("bat-dong-san", tag("36"));
    assertEquals(null, tag("37"));
    assertEquals(null, tag("38"));
    assertEquals(null, tag("39"));
    assertEquals("doanh-nghiep", tag("40"));
    assertEquals("bat-dong-san", tag("41"));
    assertEquals("kinh-te", tag("42"));
    assertEquals(null, tag("43"));
    assertEquals("bat-dong-san", tag("44"));
    assertEquals(null, tag("45"));
    assertEquals("kinh-te", tag("46"));
    assertEquals("kinh-te", tag("47"));
    assertEquals("doanh-nghiep", tag("a2"));
    assertEquals("bat-dong-san", tag("a3"));
    assertEquals("bat-dong-san", tag("a4"));
    assertEquals(null, tag("a1"));
    assertEquals("bat-dong-san", tag("a5"));
    assertEquals("chung-khoan", tag("a6"));
    assertEquals("bat-dong-san", tag("a7"));
    assertEquals("bat-dong-san", tag("a8"));
    assertEquals(null, tag("a9"));
    assertEquals("tai-chinh", tag("a10"));
    assertEquals(null, tag("a11"));
    assertEquals(null, tag("a12"));
    assertEquals(null, tag("a13"));
    assertEquals(null, tag("a14"));
    assertEquals("bat-dong-san", tag("a15"));
    assertEquals("kinh-te", tag("a16"));
    assertEquals("bat-dong-san", tag("a17"));
    assertEquals("kinh-te", tag("a18"));
    assertEquals("kinh-te", tag("a19"));
    assertEquals(null, tag("b1"));
    assertEquals("kinh-te", tag("b2"));
    assertEquals(null, tag("b3"));
    assertEquals("bat-dong-san", tag("b4"));
    assertEquals("tai-chinh", tag("b5"));
    assertEquals("kinh-te", tag("b6"));
    assertEquals("kinh-te", tag("b7"));
    assertEquals("kinh-te", tag("b8"));
    assertEquals("kinh-te", tag("b9"));
    assertEquals("kinh-te", tag("b10"));
    assertEquals("tai-chinh", tag("b11"));
    assertEquals("tai-chinh", tag("b13"));
    assertEquals("tai-chinh", tag("b14"));
    assertEquals(null, tag("b15"));
    assertEquals("kinh-te", tag("b16"));
    assertEquals("doanh-nghiep", tag("b17"));
    assertEquals(null, tag("b18"));
    assertEquals("tuyen-dung", tag("b19"));
    assertEquals(null, tag("b21"));
    assertEquals(null, tag("b22"));
    assertEquals("kinh-te", tag("b23"));
    assertEquals("kinh-te", tag("b25"));
  }

  @Test
  public void test() throws Exception {
    method1();
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
//    assertEquals("kinh-te", tag(""));
  }

}
