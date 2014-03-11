/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index.word;

import java.io.File;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class TestWordSeparator {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
   /* boolean contains = VietnameseDictDatabase.getInstance().contains("chính quyền");
    if(!contains) VietnameseDictDatabase.getInstance().save("chính quyền", "");
    System.out.println(VietnameseDictDatabase.getInstance().contains("chính quyền"));*/
    
    /*WordSeparator extractor = new WordSeparator();
    String text = "Nguyên phó chủ nhiệm Văn phòng Chính phủ sắp hầu tòa";
    text = "Đây là một trong các minh chứng giải thích được vì sao cần phải cho bé bú mẹ càng sớm càng tốt sau khi sinh, tạo điều kiện cho bé";
    List<String> nouns = new ArrayList<String>();
    extractor.split(nouns, text);
    
    for (int i = 0; i < nouns.size(); i++) {
      System.out.println(nouns.get(i));
    }*/
    
  }
}
