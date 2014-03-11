/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.io.File;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 12, 2007  
 */
public class TestRemoveNode {
  
  public static void main(String[] args) throws Exception {
    File file = new File("F:\\Temp\\html\\a.htm");
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    
    String [] paths = new String [1];
    paths[0] = "BODY[0].TABLE.TBODY.TR[i:2=1]";
    NodePath [] nodePaths = pathParser.toNodePath(paths);

    extractor.remove(document.getRoot(), nodePaths);
    
    
    file = new File(file.getParentFile(), "remove_a.html");
    RWData.getInstance().save(file, document.getTextValue().getBytes("utf-8"));
  }
  
}
