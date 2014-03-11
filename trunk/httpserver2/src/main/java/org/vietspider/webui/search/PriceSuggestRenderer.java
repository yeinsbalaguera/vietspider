/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.LogService;
import org.vietspider.index.SearchQuery;
import org.vietspider.nlp.impl.Price;
import org.vietspider.nlp.impl.ao.ActionObject;
import org.vietspider.nlp.query.QueryAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 1, 2011  
 */
class PriceSuggestRenderer {
  @SuppressWarnings("unchecked")
  void render(StringBuilder builder, SearchQuery query) {
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    Map<Short, Collection<?>> records = analyzer.process(query.getPattern());
    
    List<Price> nlpPrices = (List<Price>)records.get(NLPData.PRICE);
    if(nlpPrices != null && nlpPrices.size() > 0) return;
    
    List<ActionObject> aos = (List<ActionObject>)records.get(NLPData.ACTION_OBJECT);
    if(aos == null || aos.size() != 1) return;
    String region = region(query);
    String ao = aos.get(0).getData();
    String [][] prices = new String[0][0];
    if(ao.equals("1,1") || ao.equals("1,2")
       || ao.equals("5,1") || ao.equals("5,2")) {// land, house
       if("Hà Nội".equals(region)) {
         prices = new String[][]{ {"-999_1", "Dưới 1 tỷ"},
                              {"1000-3000_1", "1 - 3 tỷ"}, 
                              {"3000-5000_1", "3 - 5 tỷ"}, 
                             {"5000-10000_1", "5 - 10 tỷ"},
                            {"10000-20000_1", "10 - 20 tỷ"}, 
                                 {"20000.000001-_1", "Trên 20 tỷ"}};
       } else if("Hồ Chí Minh".equals(region)) {
         prices = new String[][]{ {"-499_1", "Dưới 500 triệu"},
                              {"500-1000_1", "500 triệu - 1 tỷ"}, 
                             {"1000-3000_1", "1 - 3 tỷ"}, 
                             {"3000-5000_1", "3 - 5 tỷ"}, 
                            {"5000-10000_1", "5 - 10 tỷ"},
                           {"10000-20000_1", "10 - 20 tỷ"}, 
                                {"20000.000001-_1", "Trên 20 tỷ"}};
       } else {
         prices = new String[][]{ {"-199_1", "Dưới 200 triệu"},
                               {"200-500_1", "200 - 500 triệu"},
                              {"500-1000_1", "500 triệu - 1 tỷ"}, 
                             {"1000-3000_1", "1 - 3 tỷ"}, 
                             {"3000-5000_1", "3 - 5 tỷ"}, 
                                 {"5000.000001-_1", "Trên 5 tỷ"}};
       }
    } else if(ao.equals("1,5") || ao.equals("5,5")) {//villa
      if("Hà Nội".equals(region) || "Hồ Chí Minh".equals(region)) {
        prices = new String[][]{ {"-4999_1", "Dưới 5 tỷ"},
                            {"5000-10000_1", "5 - 10 tỷ"}, 
                           {"10000-20000_1", "10 - 20 tỷ"}, 
                           {"20000-50000_1", "20 - 50 tỷ"}, 
                                {"50000.000001-_1", "Trên 50 tỷ"}};
      } else {
        prices = new String[][]{ {"-5999_1", "Dưới 5 tỷ"},
                            {"5000-10000_1", "5 - 10 tỷ"}, 
                           {"10000-20000_1", "10 - 20 tỷ"},
                                {"20000.000001-_1", "Trên 20 tỷ"}};
      }
    } else if(ao.equals("1,4")|| ao.equals("5,4")) {//apartment
      if("Hà Nội".equals(region) || "Hồ Chí Minh".equals(region)) {
        prices = new String[][]{{"-999_1", "Dưới 1 tỷ"},
                            {"1000-3000_1", "1 - 3 tỷ"}, 
                            {"3000-5000_1", "3 - 5 tỷ"}, 
                                {"5000.000001-_1", "Trên 5 tỷ"}};
      } else {
        prices = new String[][]{{"-999_1", "Dưới 1 tỷ"},
                           {"1000-3000_1", "1- 3 tỷ"}, 
                        {"3000.000001-_1", "Trên 3 tỷ"}};
      }
    } else if(ao.equals("2,1") || ao.equals("3,1")) {//hourse
      if("Hà Nội".equals(region) || "Hồ Chí Minh".equals(region)) {
        prices = new String[][]{{"-0.999999_3", "Dưới 1 triệu"}, 
                               {"1-2_3", "1 - 2 triệu"}, 
                               {"2-5_3", "2 - 5 triệu"},
                              {"5-10_3", "5 - 10 triệu"}, 
                             {"10-20_3", "10 - 20 triệu"},
                             {"20-50_3", "20 - 50 triệu"}, 
                               {"50.000001-_3", "Trên 50 triệu"}};
      } else {
        prices = new String[][]{{"-0.499999_3", "Dưới 500000"}, 
                               {"0.5-1_3", "500000 - 1 triệu"}, 
                                 {"1-3_3", "1 - 3 triệu"},
                                 {"3-5_3", "3 - 5 triệu"},  
                                {"5-10_3", "5 - 10 triệu"},
                                 {"10.000001-_3", "Trên 10 triệu"}};
      }
    } else if(ao.equals("2,4") || ao.equals("3,4")) {//apartment
      if("Hà Nội".equals(region) || "Hồ Chí Minh".equals(region)) {
        prices = new String[][]{{"-1.999999_3", "Dưới 2 triệu"}, 
                               {"2-5_3", "2 - 5 triệu"}, 
                              {"5-10_3", "5 - 10 triệu"},
                             {"10-20_3", "10 - 20 triệu"}, 
                               {"20.000001-_3", "Trên 20 triệu"}};
      } else {
        prices = new String[][]{{"-1.999999_3", "Dưới 2 triệu"}, 
                               {"2-5_3", "2 - 5 triệu"}, 
                              {"5-10_3", "5 - 10 triệu"},
                               {"10.000001-_3", "Trên 10 triệu"}};
      }
    } else if(ao.equals("2,5") || ao.equals("3,5")) {//villa
        prices = new String[][]{{"-4.999999_3", "Dưới 5 triệu"}, 
                              {"5-10_3", "5 - 10 triệu"}, 
                             {"10-20_3", "10 - 20 triệu"},
                             {"20-50_3", "20 - 50 triệu"}, 
                               {"50.000001-_3", "Trên 50 triệu"}};
//    } else if(ao.equals("2,3") || ao  .equals("3,3")) {//office
//      if(region.equals("Hà Nội")) {
//        prices = new String[]{"dưới 1 triệu", 
//            "1 triệu - 2 triệu", "2 triệu - 5 triệu",
//            "5 triệu - 10 triệu", "10 triệu - 20 triệu",
//            "20 triệu - 50 triệu", "trên 50 triệu"};
//      } else if(region.equals("Hồ Chí Minh")) {
//        prices = new String[]{"dưới 1 triệu", 
//            "1 triệu - 2 triệu", "2 triệu - 5 triệu",
//            "5 triệu - 10 triệu", "10 triệu - 20 triệu",
//            "20 triệu - 50 triệu", "trên 50 triệu"};
//      } else {
//        prices = new String[]{"dưới 500000", 
//            "500000 - 1 triệu", "1 triệu - 3 triệu",
//            "3 triệu - 5 triệu", "5 triệu - 10 triệu",
//            "trên 10 triệu"};
//      }
    } else if(ao.equals("2,6") || ao.equals("3,6")) {//room
        prices = new String[][]{{"-0.999999_3", "Dưới 1 triệu"}, 
                               {"1-2_3", "1 - 2 triệu"}, 
                               {"2-5_3", "2 - 5 triệu"},
                                {"5.000001-_3", "Trên 5 triệu"}};
    }
    
    if(prices.length < 1) return;
    
    builder.append("<div class=\"menu\">&nbsp;&nbsp;&nbsp;");
    builder.append("<a href=\"/site/search/1/?query=");
    try {
      builder.append(URLEncoder.encode(query.getPattern(), "utf-8"));
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    if(query.getDate() > 0) builder.append("&sdate=").append(query.getDate());
    builder.append("\" class=\"menu\">Tất cả</a></div>");
    
    for(int i = 0; i < prices.length; i++) {
      builder.append("<div class=\"menu\">&nbsp;&nbsp;&nbsp;");
      boolean link = !prices[i][0].equals(query.getPrice());
      if(link) {
        builder.append("<a href=\"/site/search/1/?query=");
        try {
          builder.append(URLEncoder.encode(query.getPattern(), "utf-8"));
        } catch (Exception e) {
          LogService.getInstance().setMessage(e, e.toString());
        }
        builder.append("&price=").append(prices[i][0]);
        if(query.getDate() > 0) builder.append("&sdate=").append(query.getDate());
        builder.append("\" class=\"menu\">");
      }
      builder.append(prices[i][1]);
      if(link) builder.append("</a>");
      builder.append("</div>");
    }
  }
  
  private String region(SearchQuery query)  {
    String text = query.getRegion();
    if(text == null) return null;
    TextSpliter spliter = new TextSpliter();
    List<String> elements = spliter.toList(text, ',');
    if(elements.size() != 1) return null;
    return elements.get(0);
  }
}
