/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2009  
 */
public class TestNLPExtractor {
//  public static void main(String[] args) throws Exception {
//    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
//    System.setProperty("vietspider.data.path", file.getCanonicalPath());
//    
//    URL url = TestNLPExtractor.class.getResource("article.txt");
//    File contentFile = new File(url.toURI());
//    DataReader reader = RWData.getInstance();
//    String text = new String(reader.load(contentFile), "utf-8");
//    
//    int idx = text.indexOf("//");
//    int start = 0;
//    String title = text.substring(start, idx).trim();
//    start = idx + 2;
//    idx = text.indexOf("//", start);
//    String desc = text.substring(start, idx).trim();
//    
//    start = idx + 2;
//    text = text.substring(start).trim();
//    
//    System.out.println(title);
//    System.out.println(desc);
//    
//    Domain domain = new Domain();
//    domain.setGroup("CLASSIFIED");
//
//    Meta meta = new Meta();
//    meta.setSource("http://chohanoi.com/default.asp?control=news&action=view&id=216316");
//    
//    meta.setTitle(title);
//    meta.setDesc(desc);
//
//    Content content = new Content();
//    content.setContent(text);
//
//    Article article = new Article();
//    article.setDomain(domain);
//    article.setMeta(meta);
//    article.setContent(content);
//
//    NLPExtractor extractor = new NLPExtractor();
//    NLPRecord record = extractor.extract(article);
//    List<NLPRecordItem> items = record.getItems();
////    for(int i = 0; i < extracts.size(); i++) {
////      System.out.println(extracts.get(i));
////    }
//    NLPMapper mapper = new NLPMapper();
//    text = mapper.record2Text(record);
//    System.out.println(text);
//    
//    NLPRecord record2 = mapper.text2Record(text);
//    String text2 = mapper.record2Text(record2);
//    System.out.println(text2);
//    
//  }
}

