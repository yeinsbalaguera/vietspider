/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.test;

import java.io.File;

import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector;
import org.vietspider.common.io.RWData;
import org.vietspider.handler.XMLHandler3;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.TypeToken;
import org.vietspider.ui.services.ClientLog;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 22, 2009  
 */
public class DocumentTestPlugin extends XMLHandler3 implements TestPlugin {
  
  private RefsEncoder encoder = new RefsEncoder();

  public Object process(Source source, HTMLDocument document) {
    Region[] regions = source.getProcessRegion();

    XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
    XMLDocument xmlDocument = new XMLDocument(xmlRoot);

    XMLNode xmlContentNode = new XMLNode("content", TypeToken.TAG);
    xmlRoot.addChild(xmlContentNode);

    HTMLNode root = document.getRoot();

    for (int i = 0; i < regions.length; i++) {
      String key = regions[i].getName();
      try {
        NodePath[] nodePaths = regions[i].getNodePaths();
        String content = lookupTextValue2(root, nodePaths, regions[i].getType(), null);

        xmlRoot.addChild(createPropertyNode(key, content));
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
    
    try {
      xmlRoot.addChild(createResources(source.getPattern()));
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e); 
    }
    
    createSourceNode(xmlRoot, source.getPattern());

    xmlRoot.setIsOpen(false);

    File folder = ClientConnector.getCacheFolder("temp");
    File file = new File(folder, "test.xml");
    char[] chars;
    try { 
      RWData.getInstance().save(file, xmlDocument.getTextValue().getBytes("UTF-8"));
      return file.toURI().toURL();
    } catch (Exception e) {
      chars = xmlDocument.getTextValue().toCharArray();
      chars = this.encoder.encode(chars);
    }
    return new String(chars);
  }
}
