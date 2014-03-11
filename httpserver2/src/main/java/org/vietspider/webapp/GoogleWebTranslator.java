/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webapp;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.LogService;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 2, 2011  
 */
public class GoogleWebTranslator extends TranslatorMachine {

  private HttpHost httpHost;

  public GoogleWebTranslator(TranslateMode config) {
    super(config, "http://translate.google.com/");

    httpHost = webClient.createHttpHost("http://translate.google.com/");
  }

  String translate(String text, String from, String to) throws Exception {
    if(text.trim().length() < 1) return "";
    //  System.out.println("================================");
    //  System.out.println(text);
    text = URLEncoder.encode(text, "utf8");

    String uri = "http://translate.google.com/translate_a/t?client=t&text=" + text;
    uri += "&hl=en&";
    if(from != null && from.trim().length() > 0) {
      uri += "&sl=" +  from;
    }

    if(to != null && to.trim().length() > 0) {
      uri += "&tl=" + to;
    }
    uri += "&multires=1&otf=1&pc=1&ssel=4&tsel=0&sc=1";

    //    System.out.println("translator: " +uri);

    text = cached.getCachedObject(uri);
    if(text == null || text.trim().length() < 1) {
      try {
        HttpGet httpGet = webClient.createGetMethod(uri, null);
        HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
        byte [] bytes = reader.readBody(httpResponse);
        text = new String(bytes, "utf-8").trim();
        if(text.length() > 0) cached.putCachedObject(uri, text);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    if(text == null) return null;

    RefsDecoder decoder = new RefsDecoder();
    text = new String(decoder.decode(text.toCharArray()));

    return processText(text);
  }
  
  private static String processText(String text) {
    StringBuilder dich = new StringBuilder();
    StringBuilder phienam = new StringBuilder();
    
    int start = text.indexOf("[[[");
    int end = text.indexOf("]]");
    if(start < 0 || end < 0 || end < start) {
      throw new NullPointerException("Invalid data (start or end data)");
    }
    String newText = text.substring(start+2, end+1);
//    System.out.println(newText);
    start = newText.indexOf('[');
//    System.out.println(start);
    while(start > -1) {
      end = newText.indexOf(']', start);
      if(end < 0) end = newText.length();
      String value = newText.substring(start+1, end);
      String [] elements = split(value);
      if(elements.length > 0 
          && elements[0] != null
          && elements[0].trim().length() > 0) {
        if(dich.length() > 0) dich.append(' ');
//        System.out.println("truoc : "+ elements[0]);
        elements[0] = elements[0].replaceAll("\\\\\"", "\"");
//        System.out.println("sau : "+ elements[0]);
        dich.append(elements[0]);
      }
      if(elements.length > 3 
          && elements[3] != null
          && elements[3].trim().length() > 0) {
        if(phienam.length() > 0) phienam.append(' ');
        elements[3] = elements[3].replaceAll("\\\\\"", "\"");
        phienam.append(elements[3]);
      }
      
//      System.out.println("===================");
//      System.out.println(value);
//      System.out.println("======= >tach thanh");
//      for(String ele : elements) {
//        System.out.println("element : "+ ele);
//      }
      
      start = newText.indexOf('[', end+1);
    }
//    System.out.println(" google dich duoc ");
//    System.out.println("------------------------");
//    System.out.println(text);
//    System.out.println(dich);
//    System.out.println(phienam);
    
    if(dich.length() < 1) return null;
    
    if(phienam.length() > 0) {
      dich.append("&nbsp;&nbsp;&nbsp;[[Phiên âm: ").append(phienam).append("]]&nbsp;&nbsp;");
    }
    
    return dich.toString();
  }

  private static String[] split(String text) {
    List<String> list = new ArrayList<String>();
    int start = 0;
    if(text.charAt(start) == '\"') start++;
    int end = text.indexOf(',', start);
//    System.out.println(start +  " : "+ end + " : "+ text.length());
    while(end > 1 && end < text.length() - 1) {
//      System.out.println(" end - 1: "+ text.charAt(end-1));
      if(text.charAt(end-1) != '\"') {
        end = text.indexOf(',', end+1);
        continue;
      }
//      System.out.println(" end - 2: "+ text.charAt(end-2));
      if(end > 2 && text.charAt(end-2) == '\\') {
        end = text.indexOf(',', end+1);
        continue;
      }
      
//      System.out.println(" end + 1: "+ text.charAt(end+1));
      if(text.charAt(end+1) != '\"') {
        end = text.indexOf(',', end+1);
        continue;
      }
//      System.out.println(start +  " : "+ end);
      
      list.add(text.substring(start, end-1));
      
      start = end+1;
      if(text.charAt(start) == '\"') start++;
      end = text.indexOf(',', start);
    }
    
    if(start < text.length()) {
      if(text.charAt(text.length()-1) == '\"') {
        list.add(text.substring(start, text.length()-1));
      } else {
        list.add(text.substring(start, text.length()));
      }
    }
    
    return list.toArray(new String[0]);
  }
  
  public static void main(String[] args) {
    String text = "[[[\"Sự khan hiếm và suy thoái đất và nước \\\",\\\"ngày càng tăng mối đe dọa đối với an ninh lương thực\",\"Scarcity and degradation of land and water: growing threat to food security\",\"\",\"\"]],,\"en\",,[[\"Sự khan hiếm\",[4],0,0,808,0,3,0],[\"và suy thoái\",[5],1,0,725,3,6,0],[\"đất\",[6],1,0,840,6,7,0],[\"và nước\",[7],1,0,826,7,9,0],[\"ngày càng tăng\",[8],1,0,765,9,12,0],[\"mối đe dọa\",[9],1,0,779,12,15,0],[\"đối với an ninh\",[10],1,0,788,15,19,0],[\"lương\",[11],1,0,1000,19,20,0],[\"thực\",[12],1,0,1000,20,21,0]],[[\"Scarcity\",4,[[\"Sự khan hiếm\",808,0,0],[\"khan hiếm\",0,0,0],[\"tình trạng khan hiếm\",0,0,0],[\"sự khan hiếm về\",0,0,0]],[[0,8]],\"Scarcity and degradation of land and water: growing threat to food security\"],[\"and degradation\",5,[[\"và suy thoái\",725,1,0],[\"và sự xuống cấp\",0,1,0],[\"và thoái hóa\",0,1,0],[\"và thoái hoá\",0,1,0],[\"và xuống cấp\",0,1,0]],[[9,24]],\"\"],[\"land\",6,[[\"đất\",840,1,0],[\"đất đai\",0,1,0],[\"vùng đất\",0,1,0]],[[28,32]],\"\"],[\"and water\",7,[[\"và nước\",826,1,0],[\"nước và\",0,1,0],[\"và nước uống\",0,1,0]],[[33,42]],\"\"],[\"growing\",8,[[\"ngày càng tăng\",765,1,0],[\"phát triển\",0,1,0],[\"tăng trưởng\",0,1,0],[\"đang phát triển\",0,1,0]],[[44,51]],\"\"],[\"threat\",9,[[\"mối đe dọa\",779,1,0],[\"đe dọa\",0,1,0],[\"mối đe doạ\",0,1,0],[\"sự đe dọa\",0,1,0]],[[52,58]],\"\"],[\"to security\",10,[[\"đối với an ninh\",788,1,0],[\"đến an ninh\",0,1,0],[\"với an ninh\",0,1,0],[\"cho an ninh\",0,1,0]],[[59,61],[67,75]],\"\"],[\"food\",11,[[\"lương\",1000,1,0],[\"thực phẩm\",0,1,0],[\"thức ăn\",0,1,0],[\"lương thực\",0,1,0],[\"đồ ăn\",0,1,0]],[[62,66]],\"\"],[\"\",12,[[\"thực\",1000,1,0]],,\"\"]],,,[[\"en\"]],13]";
//    String text = "[[[\"- Suy thoái lan rộng và làm sâu sắc thêm tình trạng khan hiếm các nguồn tài nguyên đất và nước đã đặt một số hệ thống sản xuất thực phẩm quan trọng trên toàn cầu có nguy cơ, đặt ra một thách thức sâu sắc nhiệm vụ của việc nuôi một dân số thế giới dự kiến ​​sẽ đạt 9 tỷ người vào năm 2050 , theo một \",\"– Widespread degradation and deepening scarcity of land and water resources have placed a number of key food production systems around the globe at risk, posing a profound challenge to the task of feeding a world population expected to reach 9 billion people by 2050, according to a\",\"\",\"\"],[\"FAO báo cáo mới được công bố ngày hôm nay.\",\"new FAO report published today.\",\"\",\"\"]],,\"en\",,[[\"-\",[4],0,0,855,0,1,0],[\"Suy thoái\",[5],1,0,783,1,3,0],[\"lan rộng\",[6],1,0,940,3,5,0],[\"và\",[7],1,0,872,5,6,0],[\"làm sâu sắc thêm\",[8],1,0,967,6,10,0],[\"tình trạng khan hiếm\",[9],1,0,928,10,14,0],[\"các nguồn tài nguyên\",[10],1,0,724,14,18,0],[\"đất và nước\",[11],1,0,922,18,21,0],[\"đã đặt\",[12],1,0,666,21,23,0],[\"một số\",[13],1,0,1000,23,25,0],[\"hệ thống sản xuất\",[14],1,0,849,25,29,0],[\"thực\",[15],1,0,1000,29,30,0],[\"phẩm\",[16],1,0,1000,30,31,0],[\"quan trọng\",[17],1,0,579,31,33,0],[\"trên toàn cầu\",[18],1,0,813,33,36,0],[\"có nguy cơ,\",[19],1,0,1000,36,40,0],[\"đặt ra\",[20],1,0,511,40,42,0],[\"một thách thức\",[21],1,0,891,42,45,0],[\"sâu\",[22],1,0,1000,45,46,0],[\"sắc\",[23],1,0,1000,46,47,0],[\"nhiệm vụ\",[24],1,0,501,47,49,0],[\"của việc nuôi\",[25],1,0,1000,49,52,0],[\"một\",[26],1,0,1000,52,53,0],[\"dân số thế giới\",[27],1,0,862,53,57,0],[\"dự kiến ​​sẽ\",[28],1,0,836,57,60,0],[\"đạt\",[29],1,0,1000,60,61,0],[\"9\",[30],1,0,1000,61,62,0],[\"tỷ người\",[31],1,0,840,62,64,0],[\"vào năm 2050\",[32],1,0,822,64,67,0],[\", theo một\",[33],0,0,1000,67,70,0],[\"FAO\",[35],0,0,883,0,1,0],[\"báo cáo\",[36],1,0,836,1,3,0],[\"mới\",[37],1,0,1000,3,4,0],[\"được công bố\",[38],1,0,674,4,7,0],[\"ngày hôm nay.\",[39],1,0,747,7,11,0]],[[\"--\",4,[[\"-\",855,0,0]],[[0,1]],\"– Widespread degradation and deepening scarcity of land and water resources have placed a number of key food production systems around the globe at risk, posing a profound challenge to the task of feeding a world population expected to reach 9 billion people by 2050, according to a\"],[\"degradation\",5,[[\"Suy thoái\",783,1,0],[\"xuống cấp\",0,1,0],[\"sự xuống cấp\",0,1,0],[\"sự suy thoái\",0,1,0],[\"sự thoái hóa\",0,1,0]],[[13,24]],\"\"],[\"Widespread\",6,[[\"lan rộng\",940,1,0],[\"rộng rãi\",0,1,0],[\"diện rộng\",0,1,0],[\"trên diện rộng\",0,1,0],[\"lan tràn\",0,1,0]],[[2,12]],\"\"],[\"and\",7,[[\"và\",872,1,0],[\"và các\",0,1,0]],[[25,28]],\"\"],[\"deepening\",8,[[\"làm sâu sắc thêm\",967,1,0],[\"đào sâu\",0,1,0],[\"làm sâu sắc hơn\",0,1,0],[\"sâu sắc hơn\",0,1,0]],[[29,38]],\"\"],[\"scarcity\",9,[[\"tình trạng khan hiếm\",928,1,0],[\"khan hiếm\",0,1,0],[\"sự khan hiếm\",0,1,0],[\"trạng khan hiếm\",0,1,0]],[[39,47]],\"\"],[\"of resources\",10,[[\"các nguồn tài nguyên\",724,1,0],[\"tài nguyên\",0,1,0],[\"các nguồn lực\",0,1,0],[\"nguồn tài nguyên\",0,1,0]],[[48,50],[66,75]],\"\"],[\"land and water\",11,[[\"đất và nước\",922,1,0],[\"cạn và dưới nước\",0,1,0],[\"dụng đất và nước\",0,1,0]],[[51,65]],\"\"],[\"have placed\",12,[[\"đã đặt\",666,1,0],[\"đặt\",0,1,0],[\"có đặt\",0,1,0],[\"đã đặt các\",0,1,0]],[[76,87]],\"\"],[\"a number of\",13,[[\"một số\",1000,1,0],[\"nhiều\",0,1,0],[\"một số các\",0,1,0]],[[88,99]],\"\"],[\"systems production\",14,[[\"hệ thống sản xuất\",849,1,0],[\"các hệ thống sản xuất\",0,1,0],[\"hệ thống sản\",0,1,0],[\"hệ thống chăn\",0,1,0],[\"hệ thống nuôi\",0,1,0]],[[109,127]],\"\"],[\"food\",15,[[\"thực\",1000,1,0],[\"thực phẩm\",0,1,0],[\"thức ăn\",0,1,0],[\"lương thực\",0,1,0],[\"đồ ăn\",0,1,0]],[[104,108]],\"\"],[\"\",16,[[\"phẩm\",1000,1,0]],,\"\"],[\"key\",17,[[\"quan trọng\",579,1,0],[\"chính\",0,1,0],[\"chủ chốt\",0,1,0],[\"then chốt\",0,1,0],[\"chìa khóa\",0,1,0]],[[100,103]],\"\"],[\"around globe the\",18,[[\"trên toàn cầu\",813,1,0],[\"trên toàn thế giới\",0,1,0],[\"trên khắp thế giới\",0,1,0],[\"khắp toàn cầu\",0,1,0]],[[128,144]],\"\"],[\"at risk ,\",19,[[\"có nguy cơ,\",1000,1,0]],[[145,153]],\"\"],[\"posing\",20,[[\"đặt ra\",511,1,0],[\"giả\",0,1,0],[\"chụp\",0,1,0],[\"tạo dáng\",0,1,0],[\"đóng giả\",0,1,0]],[[154,160]],\"\"],[\"a challenge\",21,[[\"một thách thức\",891,1,0],[\"là một thách thức\",0,1,0],[\"một thử thách\",0,1,0],[\"một thách thức đối\",0,1,0],[\"là một thử thách\",0,1,0]],[[161,162],[172,181]],\"\"],[\"profound\",22,[[\"sâu\",1000,1,0],[\"sâu sắc\",0,1,0],[\"sâu xa\",0,1,0],[\"thâm sâu\",0,1,0],[\"sâu đậm\",0,1,0]],[[163,171]],\"\"],[\"\",23,[[\"sắc\",1000,1,0]],,\"\"],[\"the task\",24,[[\"nhiệm vụ\",501,1,0],[\"các nhiệm vụ\",0,1,0],[\"hiện nhiệm vụ\",0,1,0],[\"thành nhiệm vụ\",0,1,0]],[[185,193]],\"\"],[\"of feeding\",25,[[\"của việc nuôi\",1000,1,0],[\"cho ăn\",0,1,0],[\"cung cấp lương thực cho\",0,1,0],[\"của việc nuôi trẻ\",0,1,0],[\"của việc nuôi ăn\",0,1,0]],[[194,204]],\"\"],[\"a\",26,[[\"một\",1000,1,0]],[[205,206]],\"\"],[\"population world\",27,[[\"dân số thế giới\",862,1,0],[\"dân số\",0,1,0],[\"dân số trên thế giới\",0,1,0]],[[207,223]],\"\"],[\"expected to\",28,[[\"dự kiến sẽ\",836,1,0],[\"dự kiến\",0,1,0],[\"sẽ\",0,1,0],[\"mong đợi\",0,1,0]],[[224,235]],\"\"],[\"reach\",29,[[\"đạt\",1000,1,0],[\"đạt được\",0,1,0],[\"tiếp cận\",0,1,0],[\"đạt đến\",0,1,0],[\"đạt tới\",0,1,0]],[[236,241]],\"\"],[\"9\",30,[[\"9\",1000,1,0]],[[242,243]],\"\"],[\"billion people\",31,[[\"tỷ người\",840,1,0],[\"tỉ người\",0,1,0],[\"tỷ dân\",0,1,0],[\"tỉ dân\",0,1,0],[\"tỷ người trên\",0,1,0]],[[244,258]],\"\"],[\"by 2050\",32,[[\"vào năm 2050\",822,1,0],[\"năm 2050\",0,1,0],[\"đến năm 2050\",0,1,0],[\"người vào năm 2050\",0,1,0]],[[259,266]],\"\"],[\", according to a\",33,[[\", theo một\",1000,0,0],[\"Theo một\",0,0,0]],[[266,282]],\"\"],[\"FAO\",35,[[\"FAO\",883,0,0],[\"của FAO\",0,0,0]],[[4,7]],\"new FAO report published today.\"],[\"report\",36,[[\"báo cáo\",836,1,0],[\"báo cáo của\",0,1,0],[\"bản báo cáo\",0,1,0]],[[8,14]],\"\"],[\"new\",37,[[\"mới\",1000,1,0],[\"new\",0,1,0],[\"mới của\",0,1,0],[\"mơi\",0,1,0],[\"mới này\",0,1,0]],[[0,3]],\"\"],[\"published\",38,[[\"được công bố\",674,1,0],[\"xuất bản\",0,1,0],[\"công bố\",0,1,0],[\"được xuất bản\",0,1,0]],[[15,24]],\"\"],[\"today .\",39,[[\"ngày hôm nay.\",747,1,0]],[[25,31]],\"\"]],,,[[\"en\"]],6]";
    processText(text);
  }

}
