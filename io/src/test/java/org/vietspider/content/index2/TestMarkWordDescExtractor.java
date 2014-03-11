/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index2;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2009  
 */
public class TestMarkWordDescExtractor {
  
  /*public static void testSplitWord(String pattern) {
//  String pattern = "xin chao  and toi di day \"dung roi \" or buon";
    
     MarkWordDescExtractor extractor = new MarkWordDescExtractor(pattern);
     List<String> list =  extractor.getHightWords();
     for(int i = 0; i < list.size(); i++) {
       System.out.println("=== > "+ list.get(i));
     }
     System.out.println("======================================");
     
     list =  extractor.getLowWords();
     for(int i = 0; i < list.size(); i++) {
       System.out.println("=== > "+ list.get(i));
     }
     System.out.println("======================================");
  }
  
  public static void testExtract() {
    String title = "Đác Lắc thí điểm dùng phân hữu cơ ami-ami";
    String pattern = "sở hữu";
    MarkWordDescExtractor extractor = new MarkWordDescExtractor(pattern);
//    System.out.println(extractor.buildTitle(title));
    
    String desc = "Lợi dụng dịch vụ hỗ trợ nạp tiền vào tài khoản game qua SMS, một số game thủ dụ người sử dụng di động nạp tiền vào tài khoản của mình. Các nạn nhân được \"cứu\" ở mức... nhận khiếu nại, cho cảnh báo.";
    String content = "Cuối tháng 6, đầu tháng 7/2009, thuê bao 01255050226 đã liên thu tục gửi tin nhắn tới hàng loạt các thuê bao khác trong mạng VinaPhone với nội dung: Chúc mừng bạn đã nhận được một chiếc điện thoại Ipod từ chương trình Quay số ngẫu nhiên của VinaPhone. Soạn tin CD ipod03 và gửi 3 lần đến 6769 để biết thêm chi tiết.";
    pattern = "thu";
    extractor = new MarkWordDescExtractor(pattern);
//    System.out.println(extractor.buildDesc(desc, content, 15));
    
    desc = "Một quan chức ngoại giao cấp cao của Mỹ bày tỏ lo ngại về tình hình hiện nay giữa Việt Nam và Trung Quốc trên Biển Đông, đồng thời khẳng định sẽ bảo vệ lợi ích của các công ty dầu khí Mỹ hoạt động trong khu vực.";
    content = "Trợ lý Bộ trưởng Ngoại giao Mỹ Scot Marciel cho biết Mỹ sẽ không đứng về bên nào trong tranh chấp các đảo và quần đảo giữa Trung Quốc và các nước láng giềng.";
    pattern = "trung quốc and biển đông";
    extractor = new MarkWordDescExtractor(pattern);
//    System.out.println(extractor.buildDesc(desc, content, 15));
    
    title  = "Phim truyền hình mới: Trường học điệp viên";
    extractor = new MarkWordDescExtractor(title);
    System.out.println(extractor.buildTitle(title));
  }
  
  public static void main(String[] args) {
//    testSplitWord("\"hà nội\"");
    testExtract();
  }*/
}
