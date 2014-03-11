/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index.word;

import java.io.File;
import java.util.List;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.index.analytics.ViIndexAnalyzer2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class TestVietnameseAnalyzer2 {

  private static void print(List<String> values) {
    System.out.println("total: " + values.size());

    int counter = 0;
    for(String ele : values) {
      System.out.print(ele);
      System.out.print("; ");
      counter++;
      if(counter < 10) continue;
      counter = 0;
      System.out.println();
    }
  }

  public static void main(String[] args) throws Exception  {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());

    file = new File("D:\\Temp\\articles\\text\\200908181121498.txt");
    String text = new String(RWData.getInstance().load(file), "utf-8");
    
//    text = "một bệnh nhân quả quyết rằng bị đánh đập";
//    text = " CPS Việt Nam đã từng đạt giải thưởng Trí tuệ Việt Nam 2008 và giải thưởng về Khoa học Công nghệ trong đào tạo kỹ thuật sửa chữa ĐTDĐ do Liên hiệp các Hội Khoa học Kỹ thuật Việt Nam trao tặng năm 2009";

    ViIndexAnalyzer2 analyzer2 = new ViIndexAnalyzer2();

    text = analyzer2.extract(text);
    System.out.println(text);

//    print(nouns);
//    System.out.println("\n\n================================================\n\n");
//    print(words);

  }

}
