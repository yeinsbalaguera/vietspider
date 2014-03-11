/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;
import java.text.Normalizer;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.document.util.OtherLinkRemover2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2009  
 */
public class TestContentRendererData {

  private static void proceess(File file) throws Exception  {
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
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

    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    NodePath nodePath  = pathParser.toPath("BODY");
    HTMLNode  body = extractor.lookNode(document.getRoot(), nodePath);

    List<HTMLNode> contents = TestContentRender.searchNodes(body, Name.A);
    OtherLinkRemover2 linkRemover = new OtherLinkRemover2();
    List<HTMLNode> nodes = linkRemover.removeLinks(body, null);
    for(int i = 0; i < nodes.size(); i++) {
      contents.remove(nodes.get(i));
    }
    ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, null);

    file  = new File("F:\\Temp2\\web\\output\\"+file.getName()+".txt");
    byte [] bytes = renderer.getTextValue().getBytes(Application.CHARSET);
    org.vietspider.common.io.RWData.getInstance().save(file, bytes);
  }

  public static void main(String[] args) throws Exception {
    //  UtilFile.deleteFolder(new File("F:\\Temp2\\web\\output\\"), false);

    File file  = new File("F:\\Temp2\\web\\ngoisaoblog8.htm");
    proceess(file);
  }
}
