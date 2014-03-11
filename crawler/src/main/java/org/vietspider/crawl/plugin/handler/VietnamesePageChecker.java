/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import org.vietspider.link.IPageChecker;
import org.vietspider.locale.vn.VietnameseDataChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2009  
 */
public class VietnamesePageChecker extends VietnameseDataChecker implements IPageChecker {
  
  private boolean checkData = false;
  private short checkMode = ALL;
  
  public VietnamesePageChecker() {
    super();
  }
  
  public boolean onlyCheckData() {  return checkData; }
  
  public void setOnlyCheckData(boolean checkData) { this.checkData = checkData; }
  
  public void setCheckMode(short mode) {
    checkMode = mode;
  }
  
  public boolean check(String value) {
    if(checkMode == ALL || checkMode == LOCALE) {
      if(checkLocalePattern(value)) return true;
    }
    
    if(checkMode == ALL || checkMode == TEXT) {
      if(checkTextData(value)) return true;
    }
    
    return false;
  }
  
  public static void main(String[] args) {
    VietnamesePageChecker checker = new VietnamesePageChecker();
//    String value = "  Huu duyen thien ly nag tuog ngo, a di đà fẹt. Im Thang. Nice 2 meet u [T]1MuR~. nice the end part of May nhe. ";
    String value = "Đểu thật đấy !~ Là lạ làm sao ý Mình dạO này như bấn loạn buỒn lắm mà saO vẫn cưỜi được ấynói rì thì nói nhưng mình vẫn cứ thấy trong người là lạ sao ấyTự nhiên lúk 1 mình thì cứ thẫn thờ, cố gắng quên đi 1 người không thể nhớcó khi nhận ra rằng mọi thứ như phản bội lại chính mình ấyNhớ qá !~ Vu vơ quá !~ Mệt mỏi quá !~ Muốn khóc quá !~ Có phải chưa quen với cuộc sống mới không mà mình thành ra như thế này nữa !~ Buồn cười thật, cố gắng làm rỳ rốt cuộc thì cũng không tìm được câu trả lờiThôi !~ Qên đi !~ Đi đi nhớ những kỉ niệm buồn";
    
    System.out.println(checker.check(value));
    
    value  = "vietnam#data#locale"; 
    System.out.println(value.indexOf("#data"));
  }
}
