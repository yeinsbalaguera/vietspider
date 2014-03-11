/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

import java.util.List;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2009  
 */
public class TestSplitWords {
  public static void main(String[] args) throws Exception {
    VietWordSplitter separator = new VietWordSplitter();
    //    System.out.println("|"+separator.trim("- %thuan ! ")+"|");
    //    String sentence = "Lỗi bảo mật đầu tiên được phát hiện trong Firefox 3.5 là một lỗi rất nguy hiểm có thể bị tin tặc lợi dụng tấn";
    String sentence = "Theo quy định của Bộ Thông tin và Truyền thông, mỗi cá nhân chỉ được sở hữu tối đa 3 số của mỗi mạng di động. Sẽ có";
//    String sentence = "Trưởng Công an xã Kỳ Giang, huyện Kỳ Anh (Hà Tĩnh) đã lên tiếng xin lỗi anh Nguyễn Văn Thuận và gia đình nhưng vẫn nhất quyết không thừa nhận việc mình tra tấn khiến anh Thuận bị thương.";
    List<Word> words = separator.split(sentence);
    for(int i = 0; i < words.size(); i++) {
      System.out.println(words.get(i).getValue());
    }
    
//    System.out.println(separator.startsWith("nhU dinh thuan", "Nhu"));
//    System.out.println(separator.startsWith("nhUng dinh thuan", "Nhu "));
    
//    List<String> list  = separator.searchStartWith("NGUY");
//    for(int i = 0; i < list.size(); i++) {
//      System.out.println(list.get(i));
//    }
  }
}
