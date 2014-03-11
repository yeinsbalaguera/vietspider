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
import org.vietspider.nlp.impl.Unit;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class AreaUnitTest extends TestCase {

  private NlpProcessor filder = NlpProcessor.getProcessor();
  private File folder = new File("D:\\Temp\\area_testcase\\");

  @SuppressWarnings("unchecked")
  public List<Unit> extract(String name) throws Exception {
    File file = new File(folder, name);
    String text = new String(RWData.getInstance().load(file), "utf-8");
    Map<Short, Collection<?>> map = filder.process(name, text);
    return (List<Unit>)map.get(NLPData.AREA);
  }

  @Test
  public void test() throws Exception {
    List<Unit> values = extract("201101090851280019.txt");
    assertEquals(values.size(), 1);
    assertEquals("77.2m2-78.1m2-98.3m2-107.8m2", values.get(0).toString());

    values = extract("201012300005150027.txt");
    assertEquals("70m2-86m2-102m2", values.get(0).toString());
    //    assertEquals("70m2-86m2-102m2", values.get(1).toString());

    values = extract("201101090851290092.txt");
    assertEquals("115.17m2-118m2-211m2", values.get(0).toString());

    values = extract("201101101706430020.txt");
    assertEquals("68m2-72m2-80m2-90m2-100m2-150m2-161m2-170m2-200m2-300m2-400m2", values.get(0).toString());
    assertEquals("68m2->400m2", values.get(0).toShortString());

    values = extract("201012292343420030.txt");
    assertEquals("33.33m2-35m2-38m2-51m2-74m2-81m2-157m2-190m2-233m2", values.get(0).toString());
    //    assertEquals("33.33m2->233m2", values.get(1).toShortString());

    values = extract("201101031438100064.txt");
    assertEquals("75m2-121m2-175m2", values.get(0).toString());

    values = extract("201012300005250026.txt");
    assertEquals("100m2", values.get(0).toString());

    values = extract("201101090840320031.txt");
    assertEquals("12m2-48m2-60m2-100m2-105m2-120m2-150m2-151m2-155m2-155m2-165m2-170m2-200m2-1000m2", values.get(0).toString());

    values = extract("201101140048540051.txt");
    assertEquals("85m2", values.get(0).toString());

    values = extract("201101140049190097.txt");
    assertEquals("144m2", values.get(0).toString());

    values = extract("201101141530200079.txt");
    assertEquals("100m2-120m2-160m2", values.get(0).toString());

    values = extract("201101141530140081.txt");
    assertEquals("90.8m2", values.get(0).toString());

    values = extract("201101141530110081.txt");
    assertEquals("54m2-107m2", values.get(0).toString());

    values = extract("201101141530000073.txt");
    assertEquals("100m2", values.get(0).toString());

    values = extract("201101141530410080.txt");
    assertEquals("84m2-87.7m2-89.3m2-104m2", values.get(0).toString());

    values = extract("201101141530300074.txt");
    assertEquals("52m2-63m2", values.get(0).toString());

    values = extract("201101141531400043.txt");
    assertEquals("96m2", values.get(0).toString());

    values = extract("201101141531080040.txt");
    assertEquals("52.82m2", values.get(0).toString());

    values = extract("a4.txt");
    assertEquals("28.58m2-30m2", values.get(0).toString());

    values = extract("a7.txt");
    assertEquals("68m2-91m2-95m2-104m2-117m2", values.get(0).toString());

    values = extract("a8.txt");
    assertEquals("110.77m2-132.52m2", values.get(0).toString());

    values = extract("201101310834280048.txt");
    assertEquals("325m2", values.get(0).toString());


    values = extract("201101301806200020.txt");
    assertEquals("100m2", values.get(0).toString());


    values = extract("201102010554530034.txt");
    assertEquals("102m2", values.get(0).toString());

    values = extract("201102010554540035.txt");
    assertEquals("99m2", values.get(0).toString());

    //error
    values = extract("201102010556430087.txt");
    assertEquals("144m2", values.get(0).toString());

    values = extract("201102010618020074.txt");
    assertEquals("147.59m2", values.get(0).toString());

    values = extract("201101261309260015.txt");
    assertEquals("61.6m2", values.get(0).toString());

    values = extract("201101261341570012.txt");
    assertEquals("112.5m2-145.5m2", values.get(0).toString());

    values = extract("201101261437270070.txt");
    assertEquals("34m2", values.get(0).toString());

    values = extract("201101261549560075.txt");
    assertEquals("272m2", values.get(0).toString());

    values = extract("201101261752420093.txt");
    assertEquals("60m2", values.get(0).toString());

    values = extract("a9.txt");
    assertEquals("47m2-62m2-71.5m2-72m2-74m2-75m2-80m2-83m2-83.91m2-93m2-94m2-95m2-98m2-98m2-99m2-121m2-150m2-198m2-200m2-300m2-305m2", values.get(0).toString());

    values = extract("a10.txt");
    assertEquals("160m2", values.get(0).toString());

    values = extract("a11.txt");
    assertEquals("71.39m2", values.get(0).toString());

    values = extract("a12.txt");
    assertEquals("76.6m2-83.46m2-149.96m2", values.get(0).toString());

    values = extract("a13.txt");
    assertEquals("55.9m2", values.get(0).toString());

    values = extract("a14.txt");
    assertEquals("200m2", values.get(0).toString());      

    values = extract("a15.txt");
    assertEquals("200m2", values.get(0).toString());

    values = extract("a16.txt");
    assertEquals("162m2", values.get(0).toString());

    values = extract("a18.txt");
    assertEquals("45.5m2", values.get(0).toString());

    values = extract("a19.txt");
    assertEquals("35m2", values.get(0).toString());

    values = extract("a20.txt");
    assertEquals("120m2", values.get(0).toString());

    values = extract("a22.txt");
    assertEquals("71.5m2", values.get(0).toString());

    values = extract("a23.txt");
    assertEquals("3240m2", values.get(0).toString());

    values = extract("a24.txt");
    assertEquals("172m2", values.get(0).toString());

    values = extract("a25.txt");
    assertEquals("130m2", values.get(0).toString());      

    values = extract("a26.txt");
    assertEquals("71m2-73m2-88m2", values.get(0).toString());

    values = extract("a27.txt");
    assertEquals("105m2-189m2", values.get(0).toString());


    values = extract("a28.txt");
    assertEquals("50m2-100m2", values.get(0).toString());

    values = extract("a29.txt");
    assertEquals("64m2-64.8m2", values.get(0).toString());

    values = extract("a30.txt");
    assertEquals("52m2", values.get(0).toString());

    values = extract("a31.txt");
    assertEquals("92m2", values.get(0).toString());

    values = extract("a32.txt");
    assertEquals("111.55m2", values.get(0).toString());

    values = extract("a33.txt");
    assertEquals("50m2", values.get(0).toString());      

    values = extract("a34.txt");
    assertEquals("72m2", values.get(0).toString());

    values = extract("a35.txt");
    assertEquals("300m2", values.get(0).toString());

    values = extract("a40.txt");
    assertEquals("30m2", values.get(0).toString());

    values = extract("a41.txt");
    assertEquals("21m2", values.get(0).toString());

    values = extract("a42.txt");
    assertEquals("25m2", values.get(0).toString());

    values = extract("a43.txt");
    assertEquals("60m2-67m2", values.get(0).toString());

    values = extract("a44.txt");
    assertEquals("35m2", values.get(0).toString());

    values = extract("a45.txt");
    assertEquals("30m2", values.get(0).toString());

    values = extract("a46.txt");
    assertEquals("44m2", values.get(0).toString());

    values = extract("b3.txt");
    assertEquals("45m2", values.get(0).toString());

    values = extract("b9.txt");
    assertEquals("48m2", values.get(0).toString());

    values = extract("b16.txt");
    assertEquals("32m2", values.get(0).toString());

    values = extract("b18.txt");
    assertEquals("73.5m2", values.get(0).toString());

    values = extract("b19.txt");
    assertEquals("80m2", values.get(0).toString());

    values = extract("b21.txt");
    assertEquals("40m2", values.get(0).toString());

    values = extract("b22.txt");
    assertEquals("35m2", values.get(0).toString());

    values = extract("b26.txt");
    assertEquals("29.7m2-30m2", values.get(0).toString());

    values = extract("b28.txt");
    assertEquals("30m2", values.get(0).toString());

    values = extract("b30.txt");
    assertEquals("64m2", values.get(0).toString());

    values = extract("b49.txt");
    assertEquals("54.5m2-92m2", values.get(0).toString());

//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("m2", values.get(0).toString());


    for(Unit ele : values) {
      System.out.println(ele);
    }
  }

}
