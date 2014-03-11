/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.handler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 14, 2009  
 */
public class TestCutData {
  private static String cutData(StringBuilder value, StringBuilder append, int max, int from) {
    StringBuilder builder = new StringBuilder();
    if(value.length() <= max) return null;
    int index = from;
    builder.append(value.substring(0, from));
    while(index < max) {
      char c = value.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        index++;
        continue;
      }
      builder.append(value.substring(from, index));
      append.insert(0, ' ');
      append.insert(0, value.substring(index));
      return builder.toString();
    }
    append.insert(0, ' ');
    append.insert(0, value.substring(from));
    return builder.toString();
  }
  
  public static void main(String[] args) {
    StringBuilder builder = new StringBuilder("Vợ chồng Hải - Hằng từ nhiều năm nay chuyên tổ chức đám cưới, nhưng năm qua làm thêm nghề buôn bán cà phê, đã vay nợ nhiều người với số tiền hơn 3 tỉ đồng. Ông Nguyễn Văn Bình - Trưởng công an thị trấn Ia Kha nói: \"Chúng tôi đã can thiệp để vãn hồi trật tự và gọi một số người mang trả đồ đạc đã lấy trong nhà họ, đồng thời nhận đơn khiếu nại của bà con để tiếp tục điều tra xử lý\". Đây là vụ vỡ nợ lớn nhất từ trước đến nay ở thị trấn Ia Kha. ");
    StringBuilder append = new StringBuilder();
    String value = cutData(builder, append, 150, 80);
    System.out.println(value);
    System.out.println(append);
  }
}
