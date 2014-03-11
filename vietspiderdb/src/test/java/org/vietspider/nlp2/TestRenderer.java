/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.NLPRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 10, 2011  
 */
public class TestRenderer {
  public static void main(String[] args) throws Exception  {
    File file = new File("D:\\Temp\\renderer\\c.html");
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLNode root = parser2.createDocument(file, "utf-8").getRoot();
    
    NLPRenderer textRenderer = new NLPRenderer(root);
    String text = textRenderer.getTextValue().toString();
    
    file = new File(file.getParentFile(), file.getName() + ".txt");
    org.vietspider.common.io.RWData.getInstance().save(file, text.getBytes(Application.CHARSET));
    
  }
}
