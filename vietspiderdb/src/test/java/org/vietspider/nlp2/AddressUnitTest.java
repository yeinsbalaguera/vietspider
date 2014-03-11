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
 * Jan 12, 2011  
 */
public class AddressUnitTest extends TestCase {

  private File folder = new File("D:\\Temp\\address_testcase\\");

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
    NlpProcessor processor = NlpProcessor.getProcessor();
    Map<Short, Collection<?>> map = processor.process(file.getName(), text);
    Collection<?> values = map.get(NLPData.ADDRESS);
    return (List<String>)values;
  }

  @Test
  public void test() throws Exception {
    List<String> values = extract("201012292345480032.txt");
    assertEquals(values.size(), 2);
    assertEquals("láng hạ, hà nội", values.get(1));

    values = extract("1.txt");
    assertEquals("sư vạn hạnh, phường 12, quận 10, thành phố hồ chí minh", values.get(0));

    values = extract("2.txt");
    assertEquals("định công, hà nội", values.get(0));

    values = extract("3.txt");
    assertEquals(1, values.size());
    assertEquals("lâm văn bền, phường tân kiểng, quận 7, thành phố hồ chí minh", values.get(0));

    values = extract("4.txt");
    assertEquals("phan huy ích, quận gò vấp, thành phố hồ chí minh", values.get(0));
//    assertEquals("phường 12, quận gò vấp, thành phố hồ chí minh", values.get(0));

    values = extract("5.txt");
    assertEquals("phường 12, quận gò vấp, thành phố hồ chí minh", values.get(0));

    values = extract("201101090841530012.txt");
    assertEquals("lê văn lương, huyện nhà bè, thành phố hồ chí minh", values.get(0));

    values = extract("201101031434210090.txt");
    assertEquals("lê đại hành, bà triệu, thái phiên, hà nội", values.get(0));

    values = extract("6.txt");
    assertEquals("bạch mai, hà nội", values.get(0));

    values = extract("7.txt");
    assertEquals("thành phố hồ chí minh", values.get(0));


    values = extract("8.txt");
    assertEquals("bạch mai, hà nội", values.get(0));

    values = extract("9.txt");
    assertEquals("thụy khuê, hà nội", values.get(0));

    values = extract("201101090840500030.txt");
    assertEquals("mễ trì, huyện từ liêm, hà nội", values.get(0));

    values = extract("10.txt");
    assertEquals("trần phú, phường 4, lê hồng phong, quận 5, thành phố hồ chí minh", values.get(0));

    values = extract("11.txt");
    assertEquals(1, values.size());
    assertEquals("quận hai bà trưng, hà nội", values.get(0));

    values = extract("12.txt");
    assertEquals("quận 4, thành phố hồ chí minh", values.get(0));

    values = extract("13.txt");
    assertEquals("nguyễn sơn, thoại ngọc hầu, quận tân phú, thành phố hồ chí minh", values.get(0));

    values = extract("14.txt");
    assertEquals("nguyễn văn bứa, xã xuân thới thượng, huyện hóc môn, thành phố hồ chí minh", values.get(0));

    values = extract("15.txt");
    assertEquals("đào tấn, hà nội", values.get(0));


    values = extract("201101310313300020.txt");
    assertEquals("thị trấn lái thiêu, huyện thuận an, bình dương", values.get(0));

    values = extract("201101310314080033.txt");
    assertEquals("sóng thần, bình dương", values.get(0));

    values = extract("16.txt");
    assertEquals("thanh vinh, quận liên chiểu, đà nẵng", values.get(0));

    values = extract("17.txt");
    assertEquals("phan tứ, mỹ an, quận ngũ hành sơn, đà nẵng", values.get(0));

    values = extract("18.txt");
    assertEquals("thành phố nha trang, khánh hòa", values.get(0));

    values = extract("19.txt");
    assertEquals("vĩnh hòa, thành phố nha trang, khánh hòa", values.get(0));

    values = extract("20.txt");
    assertEquals("sơn thủy, tháp bà, thành phố nha trang, khánh hòa", values.get(0));

    values = extract("201101142228520061.txt");
    assertEquals("quận bình thạnh, thành phố hồ chí minh", values.get(0));

    values = extract("201101150905410002.txt");
    assertEquals("nam hòa, phường phước long a, quận 9, thành phố hồ chí minh", values.get(0));


    values = extract("201101151201420084.txt");
    assertEquals("thị trấn mỹ phước, huyện bến cát, bình dương", values.get(0));

    values = extract("201101170756240005.txt");
    assertEquals("nguyễn thị tần, quận 8, thành phố hồ chí minh", values.get(0));

    values = extract("23.txt");
    assertEquals("cách mạng tháng 8, quận 10, thành phố hồ chí minh", values.get(0));

    values = extract("24.txt");
    assertEquals("dịch vọng, quận cầu giấy, hà nội", values.get(0));

    values = extract("25.txt");
    assertEquals(4, values.size());

    values = extract("26.txt");
    assertEquals("quận 12, thành phố hồ chí minh", values.get(0));

    values = extract("27.txt");
    assertEquals("nguyễn ngọc vũ, hà nội", values.get(0));

    values = extract("28.txt");
    assertEquals("minh khai, hà nội", values.get(0));

    values = extract("29.txt");
    assertEquals("thành công, hà nội", values.get(0));

    values = extract("30.txt");
    assertEquals("hoàng quốc việt, hà nội", values.get(0));

    values = extract("201101310526250098.txt");
    assertEquals("trịnh đình trọng, quận tân phú, thành phố hồ chí minh", values.get(0));

    values = extract("201101310628220056.txt");
    assertEquals("điện biên phủ, quận bình tân, thành phố hồ chí minh", values.get(0));

    values = extract("201101310628250058.txt");
    assertEquals("thành phố hồ chí minh", values.get(0));

    values = extract("201101310628370059.txt");
    assertEquals("quận 9, thành phố hồ chí minh", values.get(0));

    values = extract("201101310629310057.txt");
    assertEquals("xã phước thạnh, huyện củ chi, thành phố hồ chí minh", values.get(0));

    values = extract("201101310629420054.txt");
    assertEquals("trần quang đạo, xã bình khánh, huyện cần giờ, thành phố hồ chí minh", values.get(0));

    values = extract("201101310629470056.txt");
    assertEquals("bình dương", values.get(0));
    
    values = extract("a3.txt");
    assertEquals("hải phòng", values.get(0));
    assertEquals(1, values.size());

    values = extract("a4.txt");
    assertEquals("huyện mê linh, hà nội", values.get(0));
    assertEquals(1, values.size());
      
    values = extract("a5.txt");
    assertEquals("lê trọng tấn, di trạch, lê văn lương, văn phú, quận hà đông, hà nội", values.get(0));
    assertEquals(1, values.size());

    values = extract("a6.txt");
    assertEquals("xã kim chung, huyện hoài đức, hà nội", values.get(0));

      values = extract("a7.txt");
      assertEquals("đắk lắk", values.get(0));

      values = extract("a8.txt");
      assertEquals("an dương vương, hà nội", values.get(0));

      values = extract("a9.txt");
      assertEquals("an dương vương, phường 11, quận 5, thành phố hồ chí minh", values.get(0));    
      
      values = extract("a10.txt");
      assertEquals("an dương vương, phường 11, quận 5, thành phố hồ chí minh", values.get(0));

      values = extract("a11.txt");
      assertEquals("hải phòng", values.get(0));

      values = extract("a13.txt");
      assertEquals("an xá, phúc xá, quận ba đình, hà nội", values.get(0));

      values = extract("a14.txt");
      assertEquals("thanh hóa", values.get(0));

      values = extract("a15.txt");
      assertEquals("lâm đồng", values.get(0));   
      
      values = extract("a16.txt");
      assertEquals("mễ trì hạ, hà nội", values.get(0));

      values = extract("a17.txt");
      assertEquals("bùi xương trạch, xã đàn, quận thanh xuân, hà nội", values.get(0));

      values = extract("a18.txt");
      assertEquals("bạch mai, hà nội", values.get(0));

     values = extract("a19.txt");
      assertEquals("bạch mai, hà nội", values.get(0));

      values = extract("a20.txt");
      assertEquals("trường chinh, đường giải phóng, hà nội", values.get(0));   
      
      values = extract("a21.txt");
      assertEquals("quảng ninh", values.get(0));

      values = extract("a22.txt");
      assertEquals("hải phòng", values.get(0));

      values = extract("a23.txt");
      assertEquals("bắc ninh", values.get(0));

      values = extract("a24.txt");
      assertEquals("huyện gia lâm, hà nội", values.get(0));

      values = extract("a25.txt");
      assertEquals("hải phòng", values.get(0));

      values = extract("a26.txt");
      assertEquals("đường số 8, quận bình tân, thành phố hồ chí minh", values.get(0));

      values = extract("a27.txt");
      assertEquals("quận bình tân, thành phố hồ chí minh", values.get(0));

      values = extract("a28.txt");
      assertEquals("cù chính lan, hà nội", values.get(0));

      values = extract("a29.txt");
      assertEquals("nghệ an", values.get(0));

//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));
//
//      values = extract("a.txt");
//      assertEquals("", values.get(0));     


    if(values != null) {
      for(String ele : values) {
        System.out.println(ele);
      }
    }
  }

}
