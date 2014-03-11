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
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.checker.NodeChecker;
import org.vietspider.html.renderer.extractor.WebPageExtractor;
import org.vietspider.html.renderer.extractor.WebPageFilter;
import org.vietspider.html.renderer.extractor.WebPageSearcher;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public class TestWebExtractors {
  
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
    UtilFile.deleteFolder(new File("F:\\Temp2\\web\\output\\"), false);
    File file = new File("F:\\Temp2\\web\\output\\");
    file.mkdirs();
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    File folder  = new File("F:\\Temp2\\web\\");
    File [] files = folder.listFiles();
    
    WebPageFilter webPageFilter = new WebPageFilter();
    WebPageExtractor webPageExtractor = new WebPageExtractor();
    WebPageSearcher webPageSearcher = new WebPageSearcher();
    
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".htm") 
          || files[i].getName().endsWith(".html")) {
        HTMLDocument document = HTMLParser.createDocument(files[i],"utf-8");
        
        NodePath nodePath  = pathParser.toPath("BODY");
        HTMLNode body = extractor.lookNode(document.getRoot(), nodePath);
        if(body == null) continue;
        decode(body);
        
        webPageFilter.filter(body, NodeChecker.createDefaultCheckers());
        body = webPageExtractor.extract(body);
//        body = webPageSearcher.searchContentNode(body);
        
//        ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, null);
//        ContentRegionSearcher2  regionSearcher2 = new ContentRegionSearcher2();
//        NodeRenderer nodeRenderer = regionSearcher2.extractContent(renderer);
//        body = nodeRenderer.getParent();
        
        file  = new File("F:\\Temp2\\web\\output\\"+ files[i].getName());
        writer.save(file, body.getTextValue().getBytes(Application.CHARSET));
      }
    }
  }
}
