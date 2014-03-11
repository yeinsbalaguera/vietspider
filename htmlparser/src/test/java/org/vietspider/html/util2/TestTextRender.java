/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util2;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.DocumentExtractor;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.TextRenderer;
import org.vietspider.html.renderer.TextRenderer2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class TestTextRender {
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\Temp2\\rongbay.html");
    HTMLDocument document = (new HTMLParser2()).createDocument(file,"utf-8");
    
    DocumentExtractor extractor  = new DocumentExtractor();
    NodePathParser pathParser = new NodePathParser();
    
    String [] paths = new String [1];
//    paths[0] = "BODY[0].UL[0].LI[2]";
//    paths[1] = "BODY[0].A[0].H2[0]";
//    NodePath [] nodePaths = pathParser.toNodePath(paths);
//    HTMLDocument newDocument = extractor.extract(document, nodePaths);
    
//    NodeIterator iterator = newDocument.getRoot().iterator();
//    while(iterator.hasNext()) {
//      System.out.println(iterator.next().getName());
//    }
    
//    System.out.println(newDocument.getRoot().getTextValue());
    TextRenderer2 renderer = new TextRenderer2(document.getRoot(), null, TextRenderer.RENDERER, true);
//    System.out.println(renderer.getTextValue());
    
    file  = new File("D:\\Temp2\\rongbay.txt");
    byte [] bytes = renderer.getTextValue().toString().getBytes(Application.CHARSET);
    org.vietspider.common.io.RWData.getInstance().save(file, bytes);
  }
}
