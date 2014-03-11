/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.NLPData;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2011  
 */
public class ActionBottom0Plugin  extends ActionAbstractPlugin {
  
  final static String[][] DOUBLE_LABELS = {
    {"nhà đẹp giá rẻ", "1,1"}, {"nhà giá rẻ", "1,1"},
    {"nhà", "giá", "sổ đỏ", "1,1"}, {"nhà", "giá", "sổ hồng", "1,1"},
    {"bán sổ đỏ", "đúc giả", "1,1"}, {"nhà container", "1,1"}, 
    {"bán khách sạn", "1,1"}, {"mt", "<1,1"},
    
//    {"mt", "xây dựng", "giá", "1,1"},
    //    {"mt", "hẻm", "trệt", "lầu", "gần", "sh", "1,1"},
    //    {"mt", "gần", /* "hướng", "sổ đỏ",*/ "1,1"},
    {"nền", "giá", "1,2"}, {"sở hữu", "nền", "1,2"}, /*{"ô", "giá rẻ", "1,2"},*/
    {"đất nền", "1,2"}, {"bán liền kề", "1,2"}, {"nền đất", "thổ cư", "1,2"},
    {"đất rẻ", "1,2"},  {"bán lk", "1,2"}, {"đất", "bán", "<1,2"}, 
    {"dt", "giá bán", "<1,2"}, {"diện tích", "giá bán", "<1,2"},
    {"đất biệt thự", "1,2"},  {"dự án", "đất nền", "1,2"},
    {"đất thổ cư", "1,2"}, {"lô đất", "<1,2"}, {"can ban", "lk", "1,2"}, 
    {"một số suất liền kề", "1,2"},  {"thuận tiện để làm nhà", "1,2"},
    {"đất sổ đỏ", "có thể xây", "1,2"}, {"nhượng qsd", "sổ đỏ", "1,2"},
    {"lô đất", "thích hợp xây", "1,2"}, {"đất nông nghiệp", "giấy tờ hợp lệ", "1,2"},
    {"san nền", "lk", "bt", "1,2"}, {"mt", "vào đất", "1,2"},
    
    {"ng.căn", "cho thuê", "2,1"},
    {"cho", "thuê nhà", "2,1"}, {"cho thuê nguyên căn", "2,1"},
    {"điện nước chính", "cọc", "2,1"}, {"cho thuê", "nhà mặt tiền", "2,1"}, 
    {"house", "for rent", "2,1"},
    
    {"cần thuê", "mặt phố", "3,1"}, {"cần tìm nhà cho thuê", "3,1"},
    
    {"đặt phòng", "2,6"}, {"phòng", "chủ nhà", "<2,6"}, 
    {"phòng chính chủ", "2,6"}, {"phòng", "nấu ăn", "2,6"},
    {"phòng trọ", "cho", "thuê", "2,6"}, {"ở ghép", "2,6"},
    {"phòng", "ct riêng", "2,6"},  {"phòng", "công tơ riêng", "2,6"}, 
    {"phòng", "đầy đủ", "truyền hình cáp", "<2,6"},
    {"o ghep", "2,6"}, {"thuê phòng", "2,6"}, {"thuê", "phòng trống", "2,6"},
    {"điện, nước", "sinh hoạt đầy đủ", "công tơ tính", "2,6"},
    
    {"cho thuê", "xưởng xây dựng", "2,10"},
    {"mb đường", "thuận lợi mở", "2,10"}, {"sang quán", "2,10"},
    
    {"cần thuê địa điểm", "3,10"}, {"tìm mặt bằng", "3,10"}, 
    
    {"sang sạp", "4,10"},
    
    {"diện tích đất biệt thự", "1,5"}, {"diện tích biệt thự", "1,5"}, {"suất biệt thự", "1,5"},
    {"biệt thự", "giá rẻ", "1,5"},  {"dự án", "khu biệt thự", "1,5"}, {"biệt thự nghỉ dưỡng", "1,5"}, 
    
    /*{"dự án thành phố mới", "1,7"},*/ {"dự án", "giá gốc", "1,7"}, 
    
    {"tòa nhà cao ốc văn phòng", "2,3"}, {"diện tích văn phòng cho thuê", "2,3"},
    {"tòa văn phòng cho thuê", "2,3"}, {"văn phòng cho thuê", "2,3"}, 
    {"sàn văn phòng hạng", "2,3"}, 
    
    {"thuê văn phòng", "3,3"}, 
    
    {"cc", "<1,4"}, {"royal city", "tầng", "1,4"}, {"royal city", "ngoại giao", "1,4"},
    {"cc mimi", "1,4"}, {"hai sàn", "tầng", "1,4"}
    
  };
  
  final static String[][] TAGS = {
    {"nhà đẹp", "giá rẻ"}, {"nhà", "giá rẻ"},
    {"nhà", "sổ đỏ"}, {"nhà", "sổ hồng"},
    {"sổ đỏ", "đúc giả"}, {"nhà", "container"}, 
    {"bán khách sạn"}, {"mt", "mặt tiền"},
    
//    {"mt", "xây dựng", "giá"},
    //    {"mt", "hẻm", "trệt", "lầu", "gần", "sh"},
    //    {"mt", "gần", /* "hướng", "sổ đỏ",*/ "1,1"},
    {"nền"}, {"sở hữu", "nền"}, /*{"ô", "giá rẻ"},*/
    {"đất nền"}, {"liền kề"}, {"nền đất", "thổ cư"},
    {"đất rẻ"},  {"lk"}, {"đất"}, 
    {"dt"}, {"diện tích"},
    {"đất biệt thự"},  {"dự án", "đất nền"},
    {"đất thổ cư"}, {"lô đất"}, {"lk"}, 
    {"một số suất liền kề"},  {"thuận tiện để làm nhà"}, 
    {"đất sổ đỏ", "có thể xây"},  {"nhượng qsd", "sổ đỏ"},
    {"lô đất", "thích hợp xây"}, {"đất nông nghiệp", "giấy tờ hợp lệ"},
    {"san nền", "lk", "bt"}, {"mt", "vào đất"},
//    {"khu đất cần bán"},
    
    {"ng.căn", "nguyên căn"},
    {"nhà"}, {"cho thuê", "nguyên căn"},
    {"điện nước chính", "cọc"}, {"nhà mặt tiền"},
    {"floor house"},
    
    {"mặt phố"}, {"cần tìm nhà"},
    
    {"đặt phòng"}, {"phòng", "chủ nhà"}, 
    {"phòng chính chủ"}, {"phòng", "nấu ăn"},
    {"phòng trọ"}, {"ở ghép"},
    {"phòng", "ct riêng"},  {"phòng", "công tơ riêng"}, 
    {"phòng", "đầy đủ", "truyền hình cáp"},
    {"o ghep"}, {"phòng"}, {"phòng trống"},
    {"điện, nước", "sinh hoạt đầy đủ", "công tơ tính"},
    
    {"xưởng xây dựng"},
    {"mb đường", "thuận lợi mở"}, {"quán"},
    
    {"địa điểm"}, {"mặt bằng"}, 
    
    {"sạp"},
    
    {"diện tích đất biệt thự"}, {"diện tích biệt thự"}, {"suất biệt thự"},
    {"biệt thự", "giá rẻ"},  {"dự án", "khu biệt thự"}, {"biệt thự nghỉ dưỡng"}, 
    
    /*{"dự án thành phố mới", "1,7"},*/ {"dự án", "giá gốc"}, 
    
    {"tòa nhà cao ốc văn phòng"}, {"diện tích văn phòng"},
    {"tòa văn phòng"}, {"văn phòng"}, {"sàn văn phòng"},
    
    {"văn phòng"},/*{"văn phòng cho thuê", "3,3"},*/
    
    {"chung cư"}, {"royal city"}, {"royal city", "ngoại giao"},
    {"chung cư", "mini"}, {"hai sàn"}
  };

  public short type() { return -1; }

  public short process(TextElement element, ArrayList<ActionObject> list) {
    String lower = element.getLower();
    
    for(int i = 0; i < DOUBLE_LABELS.length; i++) {
      List<Point> points = exist(lower, i); 
      if(points == null) continue;
      // for test
//          for(int k = 0; k < DOUBLE_LABELS[i].length; k++) {
//            System.out.print(DOUBLE_LABELS[i][k] + " , ");
//          }
      //    ActionExtractor.print();

//      ActionExtractor.print("double label " + DOUBLE_LABELS[i][0]);
      add(list, new ActionObject(DOUBLE_LABELS[i][DOUBLE_LABELS[i].length - 1], TAGS[i]));
      for(Point p : points) element.putPoint(NLPData.ACTION_OBJECT, p);
    }

    if(list.size() > 0) return _BREAK;
    
    return _CONTINUE;
  }
  
  private List<Point> exist(String text, int i) {
    List<Point> points = new ArrayList<Point>();
    for(int j = 0; j < DOUBLE_LABELS[i].length-1; j++) {
//      int idx = text.indexOf(DOUBLE_LABELS[i][j]);
      int idx = AOTextProcessor.indexOf(text, DOUBLE_LABELS[i][j]);
      if(idx < 0) return null;
      createPoint(points, idx, DOUBLE_LABELS[i][j].length());
    }
    return points;
  }
}
