/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;
import java.text.Normalizer;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.renderer.checker.NodeChecker;
import org.vietspider.html.renderer.extractor.WebPageExtractor;
import org.vietspider.html.renderer.extractor.WebPageFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class TestWebExtractor {
  
  private static void decode(HTMLNode root) {
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      //      chars = normalizer.normalize(chars);
      node.setValue(chars);              
    }  
  }
  
  public static void main(String[] args) throws Exception {
    String name = 
//      "autocad.htm"
//      "vnweblogs.htm"
//      "vnweblogs1.htm"
//      "wordpress6.htm"
//      "ngoisaoblog8.htm"
//      "blogger4.htm"
//      "blogger5.htm"
//      "blogger3.htm"
//      "blogger1.html"
//      "blogger1.htm"
//      "laodong1.htm"
//      "vnexpress2.htm"
//      "blogger_sai1.htm"
//      "wordpress14.htm"
//      "thanhnien.htm"
//      "thuytop2.htm"
//      "halida.html"
//      "ngoisaoblog3.htm"
//      "ngoisaoblog5.htm"
//      "wordpress4.htm"
//      "vietnamnet3.htm"
//      "tuoitre1.htm"
//      "sky3.htm"
//      "thanhnien3.htm"
//      "vnmedia.htm"
//      "vneconomy.htm"
//      "wordpress6.htm"
//      "ngoisaoblog9.htm"
//      "wordpress15.htm"
      "ngoisaoblog10.htm"
      ;
    File file  = new File("F:\\Temp2\\web\\" + name);
    HTMLDocument document = HTMLParser.createDocument(file, "utf-8");
    HTMLNode node = document.getRoot().getChild(1);
    decode(node);
    
    WebPageFilter webPageFilter = new WebPageFilter();  
    WebPageExtractor webPageExtractor = new WebPageExtractor();
    
//    WebPageSearcher webPageSearcher = new WebPageSearcher();
    
    webPageFilter.filter(node, NodeChecker.createDefaultCheckers());
    node = webPageExtractor.extract(node);
    
//    System.out.println(node.getTextValue());
//    node = webPageSearcher.searchContentNode(node);
    
//  ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, null);
//  ContentRegionSearcher2  regionSearcher2 = new ContentRegionSearcher2();
//  NodeRenderer nodeRenderer = regionSearcher2.extractContent(renderer);
//  body = nodeRenderer.getParent();
  
    
    file  = new File("F:\\Temp2\\web\\output\\extract.htm");
    
    org.vietspider.common.io.RWData.getInstance().save(file, node.getTextValue().getBytes(Application.CHARSET));
  }
}
