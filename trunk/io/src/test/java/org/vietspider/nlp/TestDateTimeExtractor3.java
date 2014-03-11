/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.content.nlp.classified.PostDateDetector;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.renderer.TextRenderer2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 6, 2009  
 */
public class TestDateTimeExtractor3 extends TestCase {

  private PostDateDetector detector = new PostDateDetector(); 
  private HTMLParser2 parser2 = new HTMLParser2();

  @Override
  protected void setUp() throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
  }

  private String detect(String name) throws Exception {
    URL url = TestNLPExtractor.class.getResource(name);
    File contentFile = new File(url.toURI());
    String text = new String(RWData.getInstance().load(contentFile), "utf-8");

    String date = detector.detectDate(text);
    if(date == null) return null;
    int index = date.indexOf(' ');
    return index > 0 ? date.substring(0, index) : date;
  }
  
  private String detectHTML(String name) throws Exception {
    URL url = TestNLPExtractor.class.getResource(name);
    File contentFile = new File(url.toURI());
    String text = new String(RWData.getInstance().load(contentFile), "utf-8");
    
    HTMLDocument document = parser2.createDocument(text);
    TextRenderer2 textRenderer = new TextRenderer2(document.getRoot(), TextRenderer.RENDERER);
    text = textRenderer.getTextValue().toString();
    
//    System.out.println(text);

    String date = detector.detectDate(text);
    if(date == null) return null;
    int index = date.indexOf(' ');
    return index > 0 ? date.substring(0, index) : date;
  }


  @Test
  public void testData() throws Exception {
    Assert.assertEquals(detect("test/article.txt"), "24/09/2009");
    Assert.assertEquals(detect("test/article2.txt"), "30/09/2009");
    Assert.assertEquals(detect("test/article3.txt"), "28/09/2009");
    Assert.assertEquals(detect("test/article4.txt"), "24/08/2009");
    Assert.assertEquals(detect("test/article5.txt"), "06/10/2009");
    Assert.assertEquals(detectHTML("test/article6.txt"), "06/10/2009");
    Assert.assertEquals(detectHTML("test/article7.txt"), "26/05/2009");
    Assert.assertEquals(detectHTML("test/article8.txt"), "05/08/2009");
    Assert.assertEquals(detectHTML("test/article9.txt"), "06/10/2009");
  }

}
