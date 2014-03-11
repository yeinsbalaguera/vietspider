/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.index;

import java.util.Comparator;

import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.word.PhraseFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 26, 2009  
 */
public class ClassifiedConjuntionFilter implements PhraseFilter {
  
  private final static String [] PHRASES = {
    "cùng","của", "các", "có", "cũng", "chứ", "cho", "chưa", "cả",
    "do", "để", "đã",
    "hoặc",
    "khác",
    "là",
    "ở",
    "mà", "mặc dù", 
    "nhưng", "nên", "nếu", "này",
    "sao cho",
    "thì", "thế mà", "tuy nhiên", "tuy", "trong", "tại",
    "và", "vẫn", "vì", "với", "về", 
    "người đăng", "khách vãng lai", "ngày đăng", "lượt xem", "nội dung",
    "quay lại", "xem tiếp", "lh", "liên hệ", "nơi rao", "địa chỉ", "email", "số lần xem",
    "ngày hết hạn", "thông tin chung", "thông tin về người đăng", "người đăng tin",
    "địa chỉ", "nơi rao", "địa điểm", "liên lạc", "tin rao vặt tại", "nơi đăng tin"
//    ,"thông tin về phiên đấu giá", "thông tin hàng hóa", "thông tin vận chuyển",
//    "Phương pháp bán", "Người bán", "Bắt đầu", "Kết thúc", "Thời gian còn", "Phí dịch vụ người mua",
//    "Tình trạng", "Kích cỡ lô hàng", "Mô tả chi tiết hàng hóa "
  };
  
  private Comparator<String> comparator = new Comparator<String>() {
    public int compare(String arg0, String arg1) {
      return arg0.compareToIgnoreCase(arg1);
    }
  };
  
  public ClassifiedConjuntionFilter() {
    java.util.Arrays.sort(PHRASES, comparator);
  }

  public boolean isValid(PhraseData phrase) {
    String value  = phrase.getValue();
    if(java.util.Arrays.binarySearch(PHRASES, value) > -1) return false;
    
    if(value.length() > 3) return true;
    for(int i = 0; i < value.length(); i++) {
      if(Character.isLetter(value.charAt(i))) return true;
    }
    
    return false;
  }
  
}
