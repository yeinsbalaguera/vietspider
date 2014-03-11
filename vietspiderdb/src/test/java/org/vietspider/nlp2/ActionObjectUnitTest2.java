/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.util.List;

import org.junit.Test;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class ActionObjectUnitTest2 extends ActionObjectUnitTest {

  @Test
  public void test() throws Exception {
    List<String> values = extract("201101301212070037.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101300538310030.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201102010759120040.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201102010500310000.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201102010650120033.txt");
    assertEquals("cần thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101300511470059.txt");
    assertEquals("bán bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101300606180030.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("12.txt");
    assertEquals("cho thuê căn hộ", values.get(0).toLowerCase());

    values = extract("6.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("7.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("8.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("9.txt");
    assertEquals("cần thuê phòng", values.get(0).toLowerCase());

    values = extract("10.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("11.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());
    //    assertEquals("cho thuê phòng", values.get(1).toLowerCase());

    values = extract("201101301438440051.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101300959450016.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102010759480042.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101300527420006.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101302012200034.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101300904040055.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101300636110075.txt");
    assertEquals("cần thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201102011121520059.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300538380027.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300519350060.txt.");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102010712150054.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300521330069.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300635580078.txt");
    assertEquals("cần thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201102010720120050.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102011121530050.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102010627070076.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300959450005.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101300959460019.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102010751390040.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201102010703230097.txt");
    assertEquals("cần thuê nhà", values.get(0).toLowerCase());

    values = extract("13.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("14.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("15.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("16.txt");
    assertEquals("bán phòng", values.get(1).toLowerCase());

    values = extract("17.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("18.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("19.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("20.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("21.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("22.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("23.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("24.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("25.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("26.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("27.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());
    assertEquals("cho thuê phòng", values.get(1).toLowerCase());

    values = extract("28.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("29.txt");
    assertEquals("cho thuê phòng", values.get(1).toLowerCase());

    values = extract("30.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("31.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("32.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("33.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("34.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("35.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("36.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("37.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("38.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("39.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("40.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("41.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("42.txt");
    assertEquals("cho thuê nhà", values.get(0).toLowerCase());

    values = extract("43.txt");
    assertEquals("nhượng đất", values.get(0).toLowerCase());

    values = extract("44.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("45.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("46.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("47.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("48.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("49.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("50.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("51.txt");
    assertEquals(0, values.size());

    values = extract("52.txt");
    assertEquals(0, values.size());

    values = extract("53.txt");
    assertEquals("bán bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("54.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("55.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("56.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("57.txt");
    assertEquals("cho thuê phòng", values.get(1).toLowerCase());

    values = extract("58.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("59.txt");
    assertEquals(9, values.size());

    values = extract("201101310312560093.txt");
    assertEquals("Cho thuê văn phòng", values.get(0));

    values = extract("201101310520530081.txt");
    assertEquals("Bán đất", values.get(0));

    values = extract("201101310544110082.txt");
    assertEquals("Bán phòng", values.get(0));

    values = extract("201101310908550039.txt");
    assertEquals("Bán bất động sản để kinh doanh", values.get(0));
    assertEquals("Cho thuê bất động sản để kinh doanh", values.get(1));

    values = extract("201101300603120047.txt");
    assertEquals("Nhượng bất động sản để kinh doanh", values.get(0));

    values = extract("201102010723120047.txt");
    assertEquals("Bán đất", values.get(0));

    values = extract("201101261655390094.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("201101261734590005.txt");
    assertEquals("Bán phòng", values.get(0));

    values = extract("201101142229220062.txt");
    assertEquals("Bán nhà", values.get(0));

    values = extract("a1.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a2.txt");
    assertEquals("Mua căn hộ", values.get(0));

    values = extract("a3.txt");
    assertEquals("Bán đất", values.get(0));

    values = extract("a4.txt");
    assertEquals("Bán đất", values.get(0));

    values = extract("201101160618420036.txt");
    assertEquals(0, values.size());
        
    values = extract("201103121720050080.txt");
    assertEquals(12, values.size());

    values = extract("201101161020340095.txt");
    assertEquals(0, values.size());
    
    values = extract("201101161451150045.txt");
    assertEquals(0, values.size());
    
    values = extract("201101161728580084.txt");
    assertEquals(0, values.size());
    
    values = extract("a5.txt");
    assertEquals("Cho thuê nhà", values.get(0));
    
    values = extract("201101142201540058.txt");
    assertEquals("Cho thuê nhà", values.get(0));

    values = extract("a6.txt");
    assertEquals("Cho thuê nhà", values.get(0));

    values = extract("a7.txt");
    assertEquals("Cho thuê bất động sản để kinh doanh", values.get(0));

    values = extract("a8.txt");
    assertEquals("Cần thuê nhà", values.get(0));

    values = extract("a9.txt");
    assertEquals("Cho thuê nhà", values.get(0));
    assertEquals("Cho thuê phòng", values.get(1));

    values = extract("a10.txt");
    assertEquals("Mua nhà", values.get(0));

    values = extract("a11.txt");
    assertEquals("Cho thuê nhà", values.get(0));

    values = extract("a12.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a13.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a14.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a15.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a16.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a17.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a18.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a19.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a21.txt");
    assertEquals("Bán nhà", values.get(0));

    values = extract("a22.txt");
    assertEquals("Cần thuê nhà", values.get(0));

    values = extract("a23.txt");
    assertEquals(0, values.size());

    values = extract("a24.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a25.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a26.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a27.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a28.txt");
    assertEquals("Cho thuê nhà", values.get(0));

    values = extract("a29.txt");
    assertEquals(0, values.size());

    values = extract("a30.txt");
    assertEquals(0, values.size());

    values = extract("a31.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a32.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a33.txt");
    assertEquals("Cần thuê nhà", values.get(0));

    values = extract("a34.txt");
    assertEquals("Bán căn hộ", values.get(0));
    
    values = extract("a35.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a26.txt");
    assertEquals("Bán căn hộ", values.get(0));

    values = extract("a37.txt");
    assertEquals("Bán đất", values.get(0));
    
    values = extract("a38.txt");
    assertEquals("Bán đất", values.get(0));

    values = extract("a39.txt");
    assertEquals("Bán đất", values.get(0));
    
    values = extract("a40.txt");
    assertEquals("Bán đất", values.get(0));

//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));
//
//    values = extract("");
//    assertEquals("", values.get(0));

    if(values == null) return;
    System.out.println("\n====================================");
    for(String ele : values) {
      System.out.println(ele);
    }
  }

}
