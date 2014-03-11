/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.bean.NLPData;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.NlpProcessor;
import org.vietspider.nlp.impl.ao.ActionObject;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 12, 2011  
 */
public class ActionObjectUnitTest extends TestCase {

  protected File folder = new File("D:\\Temp\\ao_testcase\\");
  protected NlpProcessor filder = NlpProcessor.getProcessor();

  public List<String> extract(String name) throws Exception {
    File file = new File(folder, name);
    String text = new String(RWData.getInstance().load(file), "utf-8");
    Map<Short, Collection<?>> map = filder.process(name, text);
    @SuppressWarnings("unchecked")
    List<ActionObject> list = (List<ActionObject>)map.get(NLPData.ACTION_OBJECT);
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < list.size(); i++) {
      values.add(NLPData.action_object(list.get(i).getData()));
    }
    return values;
  }

  @Test
  public void test() throws Exception {
    List<String> values = extract("1.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase().toLowerCase());

    values = extract("201101142223340022.txt");
    assertEquals("bán dự án", values.get(0).toLowerCase().toLowerCase());

    values = extract("201101161001390094.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101161045260047.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());
    assertEquals("bán dự án", values.get(1).toLowerCase());

    values = extract("201101310503450021.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101310620090050.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());
    assertEquals(1, values.size());

    values = extract("201101310312460061.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310629570041.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("201101310313170067.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310313540065.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310316240057.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310315440052.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310312140062.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310542460056.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());      

    values = extract("201101310522410010.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());

    values = extract("201101161828150061.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101151311300011.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101160610510039.txt");
    assertEquals("tư vấn thiết kế", values.get(0).toLowerCase());

    values = extract("201101170946350070.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101310316170056.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());
    //    assertEquals("tư vấn thiết kế", values.get(1));

    values = extract("201101151632570098.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());   

    values = extract("201101151311320017.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101161042180092.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101151632590090.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101310633240057.txt");
    assertEquals("bán dự án", values.get(0).toLowerCase());

    values = extract("201101311053510018.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("2.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101170924100009.txt");
    assertEquals("cho vay bất động sản", values.get(0).toLowerCase());

    values = extract("201101310640070043.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310541580053.txt");
    assertEquals("cho thuê nhà", values.get(0).toLowerCase());

    values = extract("201101151044190076.txt");
    assertEquals("đào tạo bất động sản", values.get(0).toLowerCase());

    values = extract("201101310313470069.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310932070084.txt");
    assertEquals("tư vấn bất động sản", values.get(0).toLowerCase());

    values = extract("201101310640160048.txt");
    assertEquals("bán dự án", values.get(0).toLowerCase());

    values = extract("201101311023100084.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310849110050.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310929340084.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310545090050.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310545110052.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101311053490016.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310313560067.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310313470058.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101151541410071.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101151606120079.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());

    values = extract("201101310314420055.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101311013460090.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101160609260083.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310503240026.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101311052590010.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101170505170021.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310314060050.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101311050160066.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101311059290099.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101310316060058.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310312310061.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310512480016.txt");
    assertEquals("bán bất động sản để kinh doanh", values.get(0).toLowerCase());
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(1).toLowerCase());

    values = extract("3.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101310620370056.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101311013530093.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310315310054.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101171227410046.txt");
    //    assertEquals("bán đất", values.get(0).toLowerCase());
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101311013430090.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101161834120050.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310605330042.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101170858430023.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101170616430068.txt");
    assertEquals("cho thuê nhà", values.get(0).toLowerCase());

    values = extract("201101310542270053.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101151406250024.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101161620450065.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101171231580095.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101142238550081.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101170537150069.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("201101310624410080.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101310813290035.txt");
    assertEquals("cần thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310518240041.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101171138000004.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310636430070.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101150606060096.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101172114380083.txt");
    assertEquals("cho thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310313570078.txt");
    assertEquals("cần thuê bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101171244070002.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("201101310641060076.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("4.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310313120055.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());

    values = extract("201101310948290007.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());

    values = extract("201101310312470093.txt");
    assertEquals("cho thuê văn phòng", values.get(0).toLowerCase());

    values = extract("201101310904100099.txt");
    assertEquals("cho thuê biệt thự", values.get(0).toLowerCase());

    values = extract("201101310637590056.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101311003060063.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("201101171345080018.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101151116310034.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101162137310076.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101310540410080.txt");
    assertEquals("cho thuê đất", values.get(0).toLowerCase());

    values = extract("201101310516370062.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310551160083.txt");
    assertEquals("bán căn hộ", values.get(0).toLowerCase());

    values = extract("201101171211580069.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101311003140060.txt");
    assertEquals("cho thuê phòng", values.get(0).toLowerCase());

    values = extract("201101170912100000.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310905370091.txt");
    assertEquals("bán biệt thự", values.get(0).toLowerCase());

    values = extract("201101310540350086.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310642470098.txt");
    assertEquals("nhượng phòng", values.get(0).toLowerCase());

    values = extract("201101310314040036.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310637010047.txt");
    assertEquals("giới thiệu dự án", values.get(0).toLowerCase());

    values = extract("201101310853070035.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310313060034.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101310523280017.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("201101310518030084.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101310313070075.txt");
    assertEquals("nhượng bất động sản để kinh doanh", values.get(0).toLowerCase());

    values = extract("201101170536180078.txt");
    assertEquals("cho thuê nhà", values.get(0).toLowerCase());

    values = extract("201101151125550013.txt");
    assertEquals("bán đất", values.get(0).toLowerCase());

    values = extract("5.txt");
    assertEquals("bán nhà", values.get(0).toLowerCase());

    values = extract("201101161220060094.txt");
    assertEquals("Mua đất", values.get(0));
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());
    //
    //      values = extract("");
    //      assertEquals("", values.get(0).toLowerCase());


    if(values == null) return;
    System.out.println("\n====================================");
    for(String ele : values) {
      System.out.println(ele);
    }
  }

}
