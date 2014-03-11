/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.locale.vn.VietWordSplitter;
import org.vietspider.locale.vn.Word;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 4, 2011  
 */
public class TestTpDocumentAnalyzer {
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vssearcher\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
  
    file = new File("D:\\Temp\\tp\\", "18" + ".txt"); 
    String content = new String(RWData.getInstance().load(file), Application.CHARSET);
    
//    TopicTrackingAnalyzer analyzer = new ViTopicTrackingAnalyzer();
//    TpWorkingData workingData = analyzer.analyzeDocument(content);
    
//    content = "Theo Thống kê từ UBND TP.HCM, hiện trên địa bàn thành phố có Sở Lao động  khoảng 1,6 triệu người tham gia BHXH bắt buộc, 1,5 triệu người tham gia BHTN và 4,4 triệu người tham gia BHYT";
    content = "Trước bối cảnh này, trong vài ngày qua tại địa bàn TP.HCM và Hà Nội đã liên tiếp rộ lên thông tin về việc điều chỉnh giá xăng dầu. Đặc biệt, khá nhiều cây xăng tại huyện Mê Linh, Hà Nội đã đóng cửa với đủ các lý do: mất điện, bảo dưỡng máy, hoặc thậm chí chẳng đưa ra lý do gì. ";
    
    file = new File("D:\\java\\vietspider\\model\\src\\main"
        +"\\java\\org\\vietspider\\locale\\vn\\full.vn.dict.cfg");
    VietWordSplitter separator = new VietWordSplitter(file);
//    System.out.println(separator.search("xung"));
    List<Word> words = separator.split(content);
    for(int i = 0; i < words.size(); i++) {
      if(i%12 == 0) System.out.println();
      System.out.print(words.get(i).getValue() + "; ");
    }
    System.out.println();
    
//    TreeSet<String> tree = workingData.getKeys();
//    Iterator<String> iterator = tree.iterator();
//    while(iterator.hasNext()) {
//      System.out.print(iterator.next()+ ", ");
//    }
//    System.out.println();
//    
//    
//    
//    VietTokenizer tokenizer = VietTokenizer.getInstance();
//
//    //    String text = "Những món quà tuy nhỏ nhưng mang ý nghĩa sẻ chia, tương trợ của bạn đọc báo điện tử Dân trí tiếp tục được gửi tới 20 nạn nhân trong vụ sập mỏ đá tại Lèn Cờ vào ngày 1/4 vừa qua.";
//    String[] elements = tokenizer.tokenize(content);
//    for(int i = 0; i < elements.length; i++) {
//      System.out.println("=== > "+elements[i]);
//    }
  }
  
}
