/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.test;

import java.io.File;
import java.util.Properties;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.handler.XMLHandler;
import org.vietspider.handler.XMLResource;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
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
public class XmlTestPlugin extends XMLHandler implements TestPlugin {

  private  RefsEncoder encoder = new RefsEncoder();

  public XmlTestPlugin() {
  }

  public Object process(Source source, HTMLDocument document)  {
    Region [] regions = source.getProcessRegion();
    
    Properties properties = source.getProperties();
    String template = properties.getProperty("DocumentTemplate");
    if(template == null || template.trim().isEmpty()) {
      template = null;
    } else {
      RefsDecoder decoder = new RefsDecoder();
      template = new String(decoder.decode(template.toCharArray()));
    }

    XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
    XMLDocument  xmlDocument = new XMLDocument(xmlRoot);

    HTMLNode root = document.getRoot();

    //    RefsDecoder decoder = new RefsDecoder();
    for(int i = 0; i < regions.length; i++) {
      String key = regions[i].getName();
      try {
        Object value = htmlUtil.lookupTextValue2(root, regions[i], source.getPattern());
        if(value instanceof String[]) {
          createTextNode(xmlRoot, key, (String[])value);  
        } else if (value instanceof XMLResource[]) {
          createResources(key, xmlRoot, (XMLResource[])value, source.getPattern());
        }
        
        //      content  = new String(decoder.decode(content.toCharArray()));

      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }

    //    try {
    //      xmlRoot.addChild(createResources(source.getPattern()));
    //    } catch (Exception e) {
    //      ClientLog.getInstance().setException(null, e);
    //    }
    createSourceNode(xmlRoot, source.getPattern());

    xmlRoot.setIsOpen(false);
    
    split(xmlRoot);

    File folder = ClientConnector2.getCacheFolder("temp");
    String osName = System.getProperty("os.name").toLowerCase();
    if(osName.indexOf("mac os") > -1) {
      char [] chars = buildTemplate(template, xmlDocument).toCharArray();
      chars = encoder.encode(chars);
      return new String(chars);
    }
    //  String model = System.getProperty("sun.arch.data.model");
    //linux
    File file = null;
    if(osName.equals("linux")) {
      file = new File(folder, "test.txt");
    } else {
      file = new File(folder, "test.xml");
    }
//    File file = new File(folder, "test.xml");
    try {
      String xml = buildTemplate(template, xmlDocument);
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
      return file.toURI().toURL();
    } catch (Exception e) {
      char [] chars = buildTemplate(template, xmlDocument).toCharArray();
      chars = encoder.encode(chars);
      return new String(chars);
    }
    //  return new String[]{title, desc, xmlDocument.getTextValue()};
  }

}
