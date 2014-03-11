/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.index;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Content;
import org.vietspider.bean.xml.XArticle;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.locale.Html2Text;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class ArticleExtractor {
  
  private static HTMLParser2 parser2 = new HTMLParser2();
  
  
  public static void converts(File file)  throws Exception {
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".article.xml")) {
        convert(files[i]);
      } else {
        files[i].delete();
      }
    }
  }
  
  public static void convert( File file) throws Exception {
    byte [] bytes = RWData.getInstance().load(file);
    XArticle xArticle = XML2Object.getInstance().toObject(XArticle.class, bytes);
    Content content = new Content(xArticle.getId(), xArticle.getTime(), xArticle.getContent());
    List<NodeImpl> tokens = parser2.createTokens(content.getContent().toCharArray());
    String text = Html2Text.toText(tokens);
    String name = file.getName();
    int idx = name.indexOf('.');
    if(idx > 0) name = name.substring(0, idx);
    
    file = new File("D:\\Temp\\articles\\text\\"+name+".txt");
    RWData.getInstance().save(file, text.getBytes("utf-8"));
  }
  
  
  
  public static void main(String[] args)  throws Exception {
    File file = new File("D:\\Temp\\articles\\input");
    converts(file);
  }
}
