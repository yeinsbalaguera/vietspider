/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.common.io.RWData;
import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLParserDetector;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 29, 2009  
 */
public class TestFormLinkExtractor {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());


    byte [] bytes = RWData.getInstance().load(new File("D:\\Temp\\2859291.html"));
    String charset = new HTMLParserDetector().detectCharset(bytes);
    char [] chars = CharsDecoder.decode(charset, bytes, 0, bytes.length);
    LinkExplorer link = new LinkExplorer("https://www.bpn.gov/CCRSearch/Search.aspx", 0);
    link.setTokens(new HTMLParser2().createTokens(chars));
    
    String [] linkGenerators = new String[] {
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults', 'Page$*')",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}",
        "ctl00$ContentPlaceHolder1$ddlState=[state]",
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults','detail$*') #data",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}"
    };
    
    FunctionFormGenerator generator = new FunctionFormGenerator(null, linkGenerators);
    List<FunctionFormGenerator> generators = new ArrayList<FunctionFormGenerator>();
    generators.add(generator);
    
    FormLinkExtractor linkExtractor = new FormLinkExtractor(new URL(link.getAddress()), link);
    linkExtractor.setGenerators(generators);
    List<LinkExplorer> explorers = linkExtractor.extract();
    
    System.out.println("explorers size "+explorers.size());
    
    for(LinkExplorer explorer : explorers) {
      if(explorer.isData()) continue;
      java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/"+ 
          String.valueOf(link.hashCode()) +"/");
      
      org.vietspider.serialize.Object2XML bean2XML = org.vietspider.serialize.Object2XML.getInstance();
      org.vietspider.parser.xml.XMLDocument document = bean2XML.toXMLDocument(explorer);
      file = new java.io.File(folder, "_"+String.valueOf(explorer.hashCode())+ ".txt");
      RWData.getInstance().save(file, document.getTextValue().getBytes("utf-8"));
    }
    
    System.exit(0);
  }
}
