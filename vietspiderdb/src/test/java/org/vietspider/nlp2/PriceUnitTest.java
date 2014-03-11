/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.bean.NLPData;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 16, 2011  
 */
public class PriceUnitTest extends TestCase {

  private NlpProcessor filder = NlpProcessor.getProcessor();
  private File folder = new File("D:\\Temp\\price_testcase\\");
  
  @Override
  protected void setUp() throws Exception {
    File file  = new File("D:\\java\\test\\vscrawler\\data");

    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
  }

  @SuppressWarnings("unchecked")
  public List<String> extract(String name) throws Exception {
    File file = new File(folder, name);
    String text = new String(RWData.getInstance().load(file), "utf-8");
    Map<Short, Collection<?>> map = filder.process(name, text);
    return (List<String>)map.get(NLPData.PRICE);
  }

  @Test
  public void test() throws Exception {
    List<String> values = extract("201012292356480058.txt");
    assertEquals(values.size(), 40);
    assertEquals("650 triệu/tổng", values.get(0).toString());
    //
    values = extract("201012292343150034.txt");
    assertEquals("680 triệu/tổng", values.get(0).toString());

    values = extract("201012300006560077.txt");
    assertEquals("120 tỷ/tổng", values.get(0).toString());

    values = extract("201012300002480005.txt");
    assertEquals("63 triệu/m2", values.get(0).toString());

    values = extract("201012292350510099.txt");
    assertEquals("50.379 triệu/m2", values.get(0).toString());

    values = extract("201012292342490012.txt");
    assertEquals("210000.0 đồng/m2", values.get(0).toString());
    //    assertEquals("70.000 đồng/m2", values.get(4).toString());
    //
    values = extract("201012292344090056.txt");
    assertEquals("16 triệu/m2", values.get(0).toString());

    values = extract("201012300002130033.txt");
    assertEquals("16 triệu/m2", values.get(0).toString());

    values = extract("201101031434590085.txt");
    assertEquals("18.9 triệu/tháng", values.get(0).toString());

    values = extract("201012292349280051.txt");
    assertEquals("580 triệu/tổng", values.get(0).toString());

    values = extract("201012292344010032.txt");
    assertEquals("4.2 tỷ/tổng", values.get(0).toString());

    values = extract("201012292345370037.txt");
    assertEquals("22 triệu/m2", values.get(0).toString());

    values = extract("201101031434590080.txt");
    assertEquals("35 triệu/tháng", values.get(0).toString());

    values = extract("201012292349420051.txt");
    assertEquals("2.25 tỷ/tổng", values.get(0).toString());

    values = extract("201012292342490051.txt");
    assertEquals("750000.0 đồng/tháng", values.get(0).toString());

    values = extract("201101031437510062.txt");
    assertEquals("515 triệu/tổng", values.get(0).toString());

    values = extract("201101031438190050.txt");
    assertEquals("1.4 tỷ/tổng", values.get(0).toString());

    values = extract("201012292343080022.txt");
    assertEquals("16.9 triệu/m2", values.get(0).toString());

    values = extract("201012300003110093.txt");
    assertEquals("2 triệu/tháng", values.get(0).toString());

    values = extract("201012300004080093.txt");
    assertEquals("2 triệu/tháng", values.get(0).toString());

    values = extract("201012300003390098.txt");
    assertEquals("510000.0 đồng/tháng", values.get(0).toString());

    values = extract("201012300006240094.txt");
    assertEquals("400000.0 đồng/tháng", values.get(0).toString());
    assertEquals("500000.0 đồng/tháng", values.get(1).toString());

    values = extract("201012292343530018.txt");
    assertEquals("2.5 triệu/tháng", values.get(0).toString());

    values = extract("201012300003190012.txt");
    assertEquals("9.3 triệu/m2", values.get(0).toString());
    assertEquals("10 triệu/m2", values.get(1).toString());
    assertEquals("9.5 triệu/m2", values.get(2).toString());

    values = extract("201012300003190013.txt");
    assertEquals("8.5 triệu/m2", values.get(0).toString());
    assertEquals("9.5 triệu/m2", values.get(1).toString());

    values = extract("201012292345240035.txt");
    assertEquals("1.5 triệu/tháng", values.get(0).toString());

    values = extract("201012292345090034.txt");
    assertEquals(null, values);

    values = extract("201101141759370026.txt");
    assertEquals("93 triệu/tổng", values.get(0).toString());

    values = extract("201101142201540058.txt");
    assertEquals("3.5 triệu/tháng", values.get(0).toString());

    values = extract("201101090841250001.txt");
    assertEquals(22, values.size());
    assertEquals("50.4 triệu/m2", values.get(0).toString());
    assertEquals("63 triệu/m2", values.get(21).toString());

    values = extract("201101090842390087.txt");
    assertEquals("3.919 tỷ/tổng", values.get(0).toString());

    values = extract("201101090933310067.txt");
    assertEquals("8 triệu/tháng", values.get(0).toString());
    assertEquals("3 tỷ/tổng", values.get(1).toString());

    values = extract("201101090930160085.txt");
    assertEquals("850 triệu/tổng", values.get(0).toString());

    values = extract("201101090847580090.txt");
    assertEquals(null, values);

    values = extract("201101090933590069.txt");
    assertEquals("72 triệu/m2", values.get(0).toString());

    values = extract("201101090933250069.txt");
    assertEquals("14 triệu/tháng", values.get(0).toString());

    values = extract("201101090930490062.txt");
    assertEquals("48.3 triệu/tháng", values.get(0).toString());

    values = extract("201101090907420099.txt");
    assertEquals("7.8 tỷ/tổng", values.get(0).toString());

    values = extract("201101090840290036.txt");
    assertEquals("525000.0 đồng/m2", values.get(0).toString());

    values = extract("201101090933450067.txt");
    assertEquals("21 triệu/tháng", values.get(0).toString());

    values = extract("201101031437540016.txt");
    assertEquals("42 triệu/tháng", values.get(0).toString());

    values = extract("201101090856100014.txt");
    assertEquals("400000.0 đồng/m2", values.get(0).toString());

    values = extract("201101090851370004.txt");
    assertEquals("42 triệu/m2", values.get(0).toString());

    values = extract("201101090922060035.txt");
    assertEquals("900000.0 đồng/m2", values.get(0).toString());

    values = extract("201101090842110019.txt");
    assertEquals("1.2 triệu/tháng", values.get(0).toString());
    assertEquals("1.3 triệu/tháng", values.get(1).toString());

    values = extract("201101101706430020.txt");
    assertEquals("3.5 triệu/m2", values.get(0).toString());
    assertEquals("8.6 triệu/m2", values.get(3).toString());

    values = extract("201101102117100070.txt");
    assertEquals("9.62 tỷ/tổng", values.get(0).toString());

    values = extract("201101102218410075.txt");
    assertEquals("4.995 tỷ/tổng", values.get(0).toString());

    values = extract("201101110017210049.txt");
    assertEquals("370 triệu/m2", values.get(0).toString());

    values = extract("201101101716180055.txt");
    assertEquals("18.5 triệu", values.get(0).toString());

    values = extract("201101102115380080.txt");
    assertEquals("2.3 tỷ/tổng", values.get(0).toString());

    values = extract("201101102014150060.txt");
    assertEquals("4.44 tỷ/tổng", values.get(0).toString());

    values = extract("a2.txt");
    assertEquals("22 triệu/tháng", values.get(0).toString());
    assertEquals("23.688 triệu/tháng", values.get(1));

    values = extract("201101142238590085.txt");
    assertEquals("4.4 triệu/m2", values.get(0).toString());
    assertEquals("5.7 triệu/m2", values.get(3));

    values = extract("201101142240320034.txt");
    assertEquals("560 triệu/tổng", values.get(0).toString());

    values = extract("201101142240390030.txt");
    assertEquals("240 triệu/tổng", values.get(0).toString());

    values = extract("201101150508220040.txt");
    assertEquals("4.6 tỷ/tổng", values.get(0).toString());
    assertEquals("2 tỷ/tổng", values.get(6).toString());

    values = extract("201101150553300034.txt");
    assertEquals("48 triệu/m2", values.get(0).toString());

    values = extract("201101150554000056.txt");
    assertEquals("3.9 tỷ/tổng", values.get(0).toString());

    values = extract("201101150600110072.txt");
    assertEquals("12.5 triệu/m2", values.get(0).toString());

    values = extract("201101150832200090.txt");
    assertEquals("15 triệu/m2", values.get(0).toString());
    assertEquals("42 triệu/m2", values.get(2).toString());

    values = extract("201101142229220062.txt");
    assertEquals("44 triệu/m2", values.get(0).toString());

    values = extract("201101142236050047.txt");
    assertEquals("12 triệu/m2", values.get(0).toString());

    values = extract("201101142239140088.txt");
    assertEquals("3.7 triệu/m2", values.get(0).toString());

    values = extract("201101142258230038.txt");
    assertEquals("8.925 triệu", values.get(0).toString());

    values = extract("201101150519260082.txt");
    assertEquals("88 triệu/tổng", values.get(0).toString());
    assertEquals("220 triệu/tổng", values.get(1).toString());

    values = extract("201101150751060068.txt");
    assertEquals("63 triệu/tháng", values.get(0).toString());

    values = extract("201101150756350018.txt");
    assertEquals("400 triệu/tổng", values.get(0).toString());

    values = extract("201101150905390000.txt");
    assertEquals("10 triệu/m2", values.get(0).toString());

    values = extract("201101150954310019.txt");
    assertEquals("520 triệu/tổng", values.get(0).toString());

    values = extract("201101151038530068.txt");
    assertEquals("14 triệu/m2", values.get(0).toString());

    values = extract("201101152230410072.txt");
    assertEquals("3.2 triệu/m2", values.get(0).toString());

    values = extract("201101160550230061.txt");
    assertEquals("3 triệu/m2", values.get(0).toString());

    values = extract("201101160623140040.txt");
    assertEquals("5.3 triệu/m2", values.get(0).toString());
    
    values = extract("201101161347580005.txt");
    assertEquals("5 tỷ/tổng", values.get(0));
    
    values = extract("201101161455240080.txt");
    assertEquals("47 triệu/m2", values.get(0));

    values = extract("201101161607460066.txt");
    assertEquals("15.35 triệu/m2", values.get(0).toString());

    values = extract("201101170511130003.txt");
    assertEquals("1.2 triệu/tháng", values.get(0).toString());
    assertEquals("1.8 triệu/tháng", values.get(3).toString());

    values = extract("201101170528110045.txt");
    assertEquals(null, values);

    values = extract("201101170826490096.txt");
    assertEquals(null, values);

    values = extract("201101170840120099.txt");
    assertEquals("1.35 tỷ/tổng", values.get(0).toString());

    values = extract("201101170848130000.txt");
    assertEquals(0, values.size());

    values = extract("201101170904330022.txt");
    assertEquals("15 triệu/m2", values.get(0).toString());

    values = extract("201101310539350089.txt");
    assertEquals("900 triệu/tổng", values.get(0).toString());

    values = extract("a3.txt");
    assertEquals(0, values.size());

    values = extract("a4.txt");
    assertEquals("550 triệu/tổng", values.get(0).toString());

    values = extract("a5.txt");
    assertEquals("4.5 triệu/tháng", values.get(0).toString());

    values = extract("a6.txt");
    assertEquals("19.8 tỷ/tổng", values.get(0).toString());

    values = extract("a7.txt");
    assertEquals("2.1 tỷ/tổng", values.get(0).toString());

    values = extract("a8.txt");
    assertEquals("11 tỷ/tổng", values.get(0).toString());

    values = extract("a9.txt");
    assertEquals("380 triệu/tổng", values.get(0).toString());

    values = extract("a10.txt");
    assertEquals("3.75 tỷ/tổng", values.get(0).toString());

    values = extract("a11.txt");
    assertEquals("40.887 triệu/m2", values.get(0).toString());
    assertEquals("4.285 tỷ/tổng", values.get(1).toString());
    
    values = extract("a12.txt");
    assertEquals("18 triệu/m2", values.get(0).toString());

    values = extract("a13.txt");
    assertEquals("9 triệu/m2", values.get(0).toString());
    assertEquals("1.079 tỷ/tổng", values.get(5).toString());

    values = extract("a14.txt");
    assertEquals("2.3 tỷ/tổng", values.get(0).toString());

    values = extract("a15.txt");
    assertEquals("12.3 triệu/m2", values.get(0).toString());

    values = extract("a16.txt");
    assertEquals("2.3 tỷ/tổng", values.get(0).toString());

    values = extract("a17.txt");
    assertEquals("2.3 tỷ/tổng", values.get(0).toString());

    values = extract("a18.txt");
    assertEquals("5.6 tỷ/tổng", values.get(0).toString());

    values = extract("a20.txt");
    assertEquals("8.3 tỷ/tổng", values.get(0).toString());
    
    values = extract("a21.txt");
    assertEquals("250 triệu/m2", values.get(0).toString());

    values = extract("a22.txt");
    assertEquals("7.5 tỷ/tổng", values.get(0).toString());

    values = extract("a23.txt");
    assertEquals("260 triệu/m2", values.get(0).toString());

    values = extract("a24.txt");
    assertEquals("300 triệu/m2", values.get(0).toString());

    values = extract("a25.txt");
    assertEquals("16 tỷ/tổng", values.get(0).toString());

    values = extract("a26.txt");
    assertEquals("5.2 tỷ/tổng", values.get(0).toString());

    values = extract("a27.txt");
    assertEquals("21 triệu/m2", values.get(0).toString());
    assertEquals("714 triệu/tổng", values.get(2).toString());

    values = extract("a28.txt");
    assertEquals("20 triệu/m2", values.get(0).toString());

    values = extract("a29.txt");
    assertEquals("1.55 tỷ/tổng", values.get(0).toString());

    values = extract("a30.txt");
    assertEquals("1.45 tỷ/tổng", values.get(0).toString());

    values = extract("a31.txt");
    assertEquals("28.2 triệu/m2", values.get(0).toString());

    values = extract("a32.txt");
    assertEquals("1 tỷ/tổng", values.get(0).toString());
    assertEquals("20 triệu/m2", values.get(2).toString());

    values = extract("a34.txt");
    assertEquals("1.2 tỷ/tổng", values.get(0).toString());
    assertEquals("26 triệu/m2", values.get(5).toString());

    values = extract("a35.txt");
    assertEquals("1.33 tỷ/tổng", values.get(0).toString());

    values = extract("a36.txt");
    assertEquals("1.55 tỷ/tổng", values.get(0).toString());

    values = extract("a37.txt");
    assertEquals("15 triệu/m2", values.get(0).toString());

    values = extract("a38.txt");
    assertEquals("8 tỷ/tổng", values.get(0).toString());

    values = extract("a39.txt");
    assertEquals("8 tỷ/tổng", values.get(0).toString());

    values = extract("a40.txt");
    assertEquals("54.348 triệu/m2", values.get(1).toString());

    values = extract("a41.txt");
    assertEquals("42 tỷ/tổng", values.get(0).toString());

    values = extract("a42.txt");
    assertEquals("380 triệu/tổng", values.get(0).toString());

    values = extract("a44.txt");
    assertEquals("50 triệu/m2", values.get(8).toString());

    values = extract("a45.txt");
    assertEquals("5 triệu/m2", values.get(0).toString());

    values = extract("a46.txt");
    assertEquals("38.85 triệu/m2", values.get(0).toString());
    assertEquals(2, values.size());

    values = extract("a47.txt");
    assertEquals("43.029 triệu/m2", values.get(0).toString());

    values = extract("a48.txt");
    assertEquals("8 triệu/m2", values.get(1).toString());

    values = extract("a49.txt");
    assertEquals("8.7 tỷ/tổng", values.get(0).toString());

    values = extract("b1.txt");
    assertEquals("5 tỷ/tổng", values.get(1).toString());

    values = extract("b2.txt");
    assertEquals("350 triệu/tổng", values.get(0).toString());

    values = extract("b3.txt");
    assertEquals("77.778 triệu/m2", values.get(1).toString());

//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());


    if(values == null) return; 
    for(String ele : values) {
      System.out.println(ele);
    }

  }


}
