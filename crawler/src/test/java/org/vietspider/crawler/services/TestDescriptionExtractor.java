/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.crawler.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.unicode.Normalizer;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.crawl.plugin.desc.DescAutoExtractor2;
import org.vietspider.crawl.plugin.desc.TitleAutoExtractor;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.ArticleCleaner;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 25, 2007
 */
public class TestDescriptionExtractor {
  
  private static ArticleCleaner loadCleaner() {
    File file = UtilFile.getFile("system", "article-cleaner.xml");
    if(file.exists())  {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        XMLDocument document = XMLParser.createDocument(xml, null);
        return XML2Object.getInstance().toObject(ArticleCleaner.class, document);
      }catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return new ArticleCleaner();
    } 

    ArticleCleaner articleCleaner = new ArticleCleaner();
    try {
      String xml = Object2XML.getInstance().toXMLDocument(articleCleaner).getTextValue();
      org.vietspider.common.io.RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    return articleCleaner;
  }
  
  private static void decode(HTMLNode node){
    NodeIterator iterator = node.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.CONTENT)) {
        char [] chars = n.getValue();
        chars = new RefsDecoder().decode(chars);
        chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
        chars = new Normalizer().normalize(chars);
        n.setValue(chars);
      }
    }
    
   /* List<HTMLNode> children = node.getChildren();   
    if(children == null) return ;
    for(HTMLNode child : children) {
      if(child.isNode(Name.COMMENT)) continue;
      if(child.isNode(Name.CONTENT)) {
        char [] chars = child.getValue();
        chars = ServicesContainer.get(RefsDecoder.class).decode(chars);
        chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
        chars = ServicesContainer.get(Normalizer.class).normalize(chars);
        child.setValue(chars);              
      } else if(!child.isNode(Name.SCRIPT) && !child.isNode(Name.STYLE)) {
        decode(child) ;
      }
    }  */
  }  
  
  public static void main(String[] args) throws Exception {
    String dataPath  = "D:\\java\\headvances\\core\\trunk\\vietspider\\startup\\src\\test\\data";
    
//    UtilFile.FOLDER_DATA = file.getCanonicalPath();
    System.setProperty("vietspider.data.path", dataPath);
    
    NodeHandler nodeHandler  = new NodeHandler();
    DocumentFormatCleaner cleanData = new DocumentFormatCleaner();
    
    ViDateTimeExtractor timeExtractor = new ViDateTimeExtractor();
//    RemoveDescImage removeDescImage = new RemoveDescImage(nodeHandler);
    
    HTMLDocument doc = new HTMLParser2().createDocument(new File("D:\\Temp\\title_des\\a.html"), "utf-8");
    decode(doc.getRoot());
    
    List<HTMLNode> list = new ArrayList<HTMLNode>();
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(list, doc.getRoot(), verify);
//    nodeHandler.searchTextNode(doc.getRoot(), list);
    timeExtractor.removeDateTimeNode(list, new Meta());
//    removeDescImage.removeDescImageNode(doc.getRoot(), list);
    StringBuilder builder = new StringBuilder();
    
//==================================================================================================
    
    HTMLExtractor extractor = new HTMLExtractor();
    TitleAutoExtractor titleHandler = new TitleAutoExtractor(extractor, nodeHandler);
    builder.append(titleHandler.extract(doc.getRoot(), list));
    
//==================================================================================================
    
    builder.append("\n\n\n <br> <br>");
    
//==================================================================================================
//    TextRenderer renderer = new TextRenderer();
//    StringBuilder builder2 = new StringBuilder();
//    renderer.build(builder2, doc.getRoot(), list);
//    File cFile  = new File("F:\\Temp\\title_des\\a.txt");
//    buffer.save(cFile, builder2.toString().getBytes("utf-8"));
    
    DescAutoExtractor2 extractor2 = new DescAutoExtractor2(extractor, nodeHandler);
    builder.append(extractor2.extract(doc.getRoot(), list));
    
//    cleanData.handle(doc.getRoot());
    
//==================================================================================================
    
    File file = new File("D:\\Temp\\title_des\\b.htm");    
    RWData.getInstance().save(file, doc.getRoot().getTextValue().getBytes("utf-8"));
    
    file = new File("D:\\Temp\\title_des\\c.htm");    
    RWData.getInstance().save(file, builder.toString().getBytes("utf-8"));
    
    System.exit(0);
  }
  
}
