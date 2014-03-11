/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.handler;

import java.io.File;
import java.text.Normalizer;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.plugin.handler.WebAutoExtractor;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 20, 2009  
 */
public class TestWebAutoHandler {
  
  public static void main(String[] args) throws Exception  {
    File file;
    
//    file  = new File("F:\\Temp2\\web\\vietnamnet.htm");
//    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
//    String address  = "http://vnmedia.vn/newsdetail.asp?NewsId=154558&CatId=58";
    
//    String address = "http://vietnamnet.vn/xahoi/2009/02/828968/";
//    String address =  "http://www.tuoitre.com.vn/Tianyon/Index.aspx?ArticleID=301367&ChannelID=6";
//    String address = "http://vietnamnet.vn/xahoi/2009/02/828932/";
//    String address = "http://tuanvietnam.net/vn/vnn/1160672/index.aspx";
    
//    String address = "http://vnexpress.net/GL/Kinh-doanh/2009/02/3BA0B4F8/";
    
//    String address = "http://yeumaytinh.wordpress.com/2009/01/29/giao-trinh-autocad-2004/";
//    String address = "http://vneconomy.vn/p0c16/cong-nghe.htm";
//    String address = "http://vneconomy.vn/20090213122145173P0C6/gian-han-nop-thue-nhap-khau-cho-nhieu-loai-mat-hang.htm";
    String address = "http://www.ngoisaoblog.com/m.php?u=thoidaiso&p=32581";
//    String address = "http://vinhnguyen.vnweblogs.com/post/11355/131517";
    
    java.net.URL url = new java.net.URL(address);
    HTMLDocument document = new HTMLParser2().createDocument(url.openStream(),"utf-8");
    
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      node.setValue(chars);              
    }  
    
    WebAutoExtractor autoExtractor = new WebAutoExtractor(new ViDateTimeExtractor());
    HTMLNode  body = autoExtractor.extractData(document, address);
    
    StringBuilder builder = new StringBuilder(autoExtractor.getTitle()).append('\n');
    builder.append("=====================================================").append('\n');
    builder.append(autoExtractor.getDesc()).append('\n');
    builder.append("=====================================================").append('\n');
    System.out.println(builder);
//    StringBuilder builder = new StringBuilder();
    builder.append(body.getTextValue());
    
    file  = new File("F:\\Temp2\\web\\output\\extract.htm");
    byte [] bytes = builder.toString().getBytes(Application.CHARSET);
    org.vietspider.common.io.RWData.getInstance().save(file, bytes);
  }
}
