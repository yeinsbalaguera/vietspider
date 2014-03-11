/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import static org.vietspider.bean.NLPData.ACTION_ASSIGNMENT;
import static org.vietspider.bean.NLPData.ACTION_BUY;
import static org.vietspider.bean.NLPData.ACTION_FOR_RENT;
import static org.vietspider.bean.NLPData.ACTION_RENT;
import static org.vietspider.bean.NLPData.ACTION_SELL;
import static org.vietspider.bean.NLPData.OBJECT_APARTMENT;
import static org.vietspider.bean.NLPData.OBJECT_HOUSE;
import static org.vietspider.bean.NLPData.OBJECT_LAND;
import static org.vietspider.bean.NLPData.OBJECT_OFFICE;
import static org.vietspider.bean.NLPData.OBJECT_ROOM;
import static org.vietspider.bean.NLPData.OBJECT_VILLA;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.Application;
import org.vietspider.index.SearchQuery;
import org.vietspider.locale.vn.VietnameseConverter;
import org.vietspider.nlp.impl.ao.ActionObject;
import org.vietspider.nlp.impl.ao.NlpAction;
import org.vietspider.nlp.impl.ao.NlpObject;
import org.vietspider.nlp.query.QAddressDetector;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2011  
 */
final class SuggestionRenderer {

  @SuppressWarnings("unchecked")
  void renderRegion(StringBuilder builder, SearchQuery _query) {
    String query = null;
    String filter = null;
    if(_query != null) {
      query = _query.getPattern();
      filter = _query.getRegion();
    }
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    Map<Short, Collection<?>> records = analyzer.process(query);
    List<String> cities = (List<String>)records.get(NLPData.CITY);
    if(cities != null && cities.size() > 0) return;

    TextElement element = new TextElement(query);
    cities = QAddressDetector.getInstance().detectSuggestion(element);
    if(cities.size() < 1) return;

    for(int i = 0; i < cities.size(); i++) {
      String city = cities.get(i);
      if(city.startsWith("thành phố")) {
        cities.set(i, city.substring("thành phố".length()+1));
      }
    }

    if(filter != null) {
      Iterator<String> iterator = cities.iterator();
      while(iterator.hasNext()) {
        String value = iterator.next();
        if(filter.equalsIgnoreCase(value)) {
          iterator.remove();
          break;
        }
      }
    }

    if(cities.size() < 1) return;

    builder.append("<span class=\"suggestion_text\" style=\"color: rgb(204, 0, 0);\">");
    builder.append("Có phải bạn muốn tìm</span> \"");
    builder.append(query).append("\" <span class=\"suggestion_text\"");
    builder.append(" style=\"color: rgb(204, 0, 0);\">tại</span>:&nbsp;");
    for(int i = 0; i < cities.size(); i++) {
      String city = uppercase(cities.get(i));
      if(i > 0) builder.append(",&nbsp; &nbsp;");
      builder.append("<a href=\"javascript:setRegion('");
      builder.append(VietnameseConverter.toAlias(city));
      builder.append("', '").append(city).append("');\" class=\"suggestion_text\">");
      builder.append(city).append("</a>");
    }

  }

  @SuppressWarnings("unchecked")
  List<String> renderAction(StringBuilder builder, SearchQuery _query) {
    String query = null;
    if(_query != null) query = _query.getPattern();
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    Map<Short, Collection<?>> records = analyzer.process(query);
    List<ActionObject> list1 = (List<ActionObject>)records.get(NLPData.ACTION_OBJECT);

    if(list1 != null && list1.size() > 0) return null;
    
    Map<Short, Collection<?>> map  = analyzer.process(query);
    
    List<NlpAction> actions = (List<NlpAction>)map.get(NLPData.ACTION);
    List<NlpObject> objects = (List<NlpObject>)map.get(NLPData.OBJECT);
    
    if(objects != null && objects.size() > 0) {
      NlpObject object = objects.get(0);
      List<String> patterns = detect(query, object);
      if(patterns != null) render(builder, patterns);
      return patterns;
    }
    
    if(actions != null && actions.size() > 0) {
      NlpAction action =  actions.get(0);
      List<String> patterns = detect(query, action);
      if(patterns != null) render(builder, patterns);
      return patterns;
    }
    
    return null;
  }

  private List<String> detect(String query, NlpObject nlp) {
    switch (nlp.getType()) {
    case OBJECT_HOUSE:
    case OBJECT_VILLA:
    case OBJECT_APARTMENT:
      return insert(query, nlp.getStart(), 
          "bán ", "cho thuê ", "cần mua ", "cần thuê ");
    case OBJECT_LAND:
      return insert(query, nlp.getStart(), "bán ", "cần mua ");
    case OBJECT_OFFICE:
    case OBJECT_ROOM:
      return insert(query, nlp.getStart(), "cho thuê ", "cần thuê ");
    default:
      break;
    }
    return null; 
  }
  
  private List<String> detect(String query, NlpAction nlp) {
    switch (nlp.getType()) {
    case ACTION_SELL:
    case ACTION_BUY:
      return insert(query, nlp.getEnd(), 
          " nhà", " đất", " căn hộ", " biệt thự");
    case ACTION_FOR_RENT:
    case ACTION_RENT:
      return insert(query, nlp.getEnd(), 
          " nhà", " đất", " căn hộ", " biệt thự", " phòng", " xưởng");
    case ACTION_ASSIGNMENT:
      return insert(query, nlp.getEnd(),
          " gian hàng", " cửa hàng", " quán", " kho", " xưởng", " nhà hàng",
          " trang trại", " khách sạn", " mặt bằng", " shop",
          " kiốt", " sạp", " phòng net",
          " mỹ viện",  " trường mầm non", " tiệm");
    default:
      break;
    }
    return null; 
  }

  private List<String> insert(String query, int start, String...actions) {
    List<String> patterns = new ArrayList<String>();
    for(int i = 0; i < actions.length; i++) {
      StringBuilder pattern = new StringBuilder(query);
      pattern.insert(start, actions[i]);
      patterns.add(pattern.toString());
    }
    return patterns;
  }

  private void render(StringBuilder builder, List<String> patterns) {
    if(patterns.size() > 0) {
      builder.append("<span class=\"suggestion_text\" style=\"color: rgb(204, 0, 0);\">");
      builder.append("Thử tìm với</span>:<table><tr>");
      for(int i = 0; i < patterns.size(); i++) {
        builder.append("<td>");
        String encode = null;
        try {
          encode = URLEncoder.encode(patterns.get(i), "utf-8");
        } catch (Exception e) {
        }
        if(encode == null) continue;
        //          if(i > 0 && i < patterns.size()-1 ) {
        //            builder.append(", &nbsp;");
        //          } else if(i == patterns.size()-1){
        //            builder.append(" hoặc ");
        //          }
        builder.append("<a href=\"/site/search/1/?query=");
        builder.append(encode);
        builder.append("\" class=\"suggestion_text\">");
        builder.append(patterns.get(i)).append("</a>");
        builder.append("</td>");
        if(i%2 == 1) {
          builder.append("</tr>");
        } else {
          builder.append("<td width=\"15\"></td>");
        }
      }
      builder.append("</table>");
    }

  }

  private String uppercase(String text) {
    StringBuilder builder = new StringBuilder();
    int i = 0;
    builder.append(Character.toUpperCase(text.charAt(0)));

    for(i = 1; i < text.length(); i++) {
      if(Character.isWhitespace(text.charAt(i-1))) {
        builder.append(Character.toUpperCase(text.charAt(i)));
      } else {
        builder.append(text.charAt(i));
      }
    }
    return builder.toString();
  }

  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;
    
//    String text = "cho thuê đống đa";
//    String text = "quảng ninh nhượng kinh doanh";
//  String text = "nha dong da";
//  String text = "một căn hộ giá rẻ";
    
//    SuggestionRenderer renderer = new SuggestionRenderer();
//    StringBuilder builder = new StringBuilder();
//    List<String> patterns = renderer.renderAction(builder, text);
//    if(patterns == null) {
//      System.exit(0);
//      return;
//    }
//    for(String pattern : patterns) {
//      System.out.println(pattern);
//    }
    
    System.exit(0);
    
  }


}
