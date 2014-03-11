/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.token.attribute;

import org.vietspider.token.Node;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 2, 2006  
 */
public class TestAttributeParser {
  
//   a class='title_tk' href ='Index5.aspx?func=ndungtin&matin=3&ngay=6/18/2007'
//   a class='title_tk' href ='Index5.aspx?func=ndungtin&matin=2&ngay=6/18/2007'

  
  public static void main(String[] args) {
    MockNode node = new MockNode();
    StringBuilder builder = new StringBuilder("a ");
    
//    builder.append("class='newsTitle' target='_self' href='/read.asp?id=287'");
//    builder.append("class='newsTitle' target=_self href=\"/read.asp?id=287\"");
    
//    builder.append("src=\"href=?/Uploaded/thanduong/Nam 2007/Thang 9/Ngay 19/5.jpg\" border=\"0\" align=\"Left\" width=\"160\"");
    
//    builder.append("class=\"mainlevel\" href=\"http://www.hoahoctro.vn/nhipsong"+
//                            "               /6456.hht\"");
    
//    builder.append(" title=\"Xem chi tiet\" href=\"default.aspx?cat_id=638&amp;news_id=1016#content\"");
    
//    builder.append("href = 'showtinvan.aspx?lang=4&ma_tinvan=11774' title=' '");
    
//    builder.append(" href='../tintuc/default.aspx?cat_id=571'\n" +
//                          "target=''");
    
//    builder.append("name= size=\"'15'\" maxlength=30\" length=2 ");
    
//    builder.append(" onmouseover=\"this.style.visibility='visible';\" ");
    
//    builder.append("style=\"width: 80px;\" type='text' ");
    
//    builder.append(" src='http://www.ddth.com/banners/trananh.gif=' txt=\" ");
    
//    builder.append(" src=\"/Uploaded/thienduy/nam 2007/nam 2007/thang 9/ngay 6/chuyen thoat.jpg\" border=\"0\" align=\"Left\" width=\"160\" height=\"130\" /");
    
//      builder.append(" href=index.asp?ID=7&dataID=12100 class= style=\"font-family:Tahoma; font-size:10px; color:#805218; font-weight:bold;\"");
//    builder.append("href=\"/vietnamese/news_detail.php?id=715\" ");
    
//    builder.append(" href=search.asp?KW=admin&SM=1&SI=AR&FM=0&OB=1' class=\"smLink\" src='forum_images");
//    builder.append("  src='images/tranh/A-0007.jpg' width='300' height=135'");
    
//    builder.append("title='cssbody=[dvbdy1] cssheader=[dvhdr1] header=[>"+
//                    "<span class=\"toolTipTitle\">"+
//                    "Microsoft và Adobe đưa ứng dụng văn phòng lên web"+
//                    "</SPAN>"+
//                    "] body=["+
//                    "<table>"+
//                    "<TBODY>"+
//                    "<tr>"+
//                    "<td valign=top>"+
//                    "</TD>"+
//                    "<td style=padding-left:5px valign=top>"+
//                    "<span class=\"toolTipBody\">"+
//                    "Không cần cài đặt phần mềm, người sử dụng sẽ có thể soạn thảo văn bản, bảng tính, trình chiếu, e-mail... theo định dạng của Microsoft Office và Adobe ngay trên trình duyệt web."+
//                    "</SPAN>"+
//                    "</TD>"+
//                    "</TR>"+
//                    "</TBODY>"+
//                    "</TABLE>"+
//                    "]' style=\"VERTICAL-ALIGN: middle;overflow:hidden;background-color:White\" href=\"http://www.vntrades.com/tintuc/modules.php?name=News&file=article&sid=12840\"");
    
    
//    builder.append(" scrolling=\"no\" frameborder=\"0\" width=\"580\" height=\"172\" name=\"I20\" nosize src=\"adsnhanhoa.htm\" topmargin=\"0\" leftmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" marginheight=\"1\" marginwidth=\"1\" style=\"behavior: url(#htmlfilter)\"");
    builder.append("name=\"log\" id=\"user_login\" class=\"input\" value=\"\" size=\"20\" tabindex=\"10\" /");
    node.setValue(builder.toString().toCharArray());
    
    Attributes attrs = AttributeParser.getInstance().get(node); 
    
//    System.out.println(attrs.get("src"));
    
    System.out.println("===================================" + attrs.indexOf("_base_href"));
    for(Attribute attr : attrs){
      System.out.println("------->"+attr.getName()+" : "+attr.getValue());
    }
    
    /*try {
      System.out.println(URLDecoder.decode("http://pagead2.googlesyndication.com/pagead/ads?client=ca-pub-0232405586141330&amp;dt=1190433944906&amp;lmt=1190433943&amp;format=728x90_as&amp;output=html&amp;correlator=1190433944906&amp;url=http%3A%2F%2Fcomic.vuilen.com%2Fview_book_detail.php%3Fbookid%3D196%26chapterid%3D2899&amp;color_bg=FFFFFF&amp;color_text=000000&amp;color_link=0000FF&amp;color_url=008000&amp;color_border=FFFFFF&amp;ad_type=text&amp;cc=100&amp;ga_vid=994267548.1190433945&amp;ga_sid=1190433945&amp;ga_hid=391065258&amp;flash=9&amp;u_h=800&amp;u_w=1280&amp;u_ah=770&amp;u_aw=1280&amp;u_cd=32&amp;u_tz=-420&amp;u_his=1&amp;u_java=true&amp;u_nplug=29&amp;u_nmime=142", "utf-8"));
    }catch (Exception e) {
      // TODO: handle exception
    }*/
  }
  
  static private class MockNode implements  Node<Object> {
    
    public Object getName() { return "a"; }
    
    public String name() { return "a"; }

    public char[] getValue() {
      return value.toCharArray();
    }

    public void setValue(char[] chars) {
       value = new String(chars);
    }
    
    public boolean isTag() {
      return true; 
    }

    private String value = "";
  }
  //name="log" id="user_login" class="input" value="" size="20" tabindex="10" /
}
