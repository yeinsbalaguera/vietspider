/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html;

import java.net.URL;
import java.util.List;

import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 4, 2006
 */
public class StockInfo {

  public static void main(String[] args) throws Exception {
    URL url = new URL("http://www3.tuoitre.com.vn/transweb/tygia.htm");
    HTMLDocument document = new HTMLParser2().createDocument(url.openStream(), "utf-8");
    
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    NodePath nodePath = pathParser.toPath("BODY[0].TABLE[1].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0]");
    HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);
    
    List<HTMLNode> tableRows  = node.getChildren();
    
    for(int i = 0; i < tableRows.size(); i++) {
      List<HTMLNode> tableColumns = tableRows.get(i).getChildren();
      for(HTMLNode tableColumn : tableColumns) {
        if(tableColumn.getChildren().size() < 1) continue;
        System.out.print(tableColumn.getChildren().get(0).getTextValue() +" | ");
      }
      System.out.println();
    }
    
  }

}
