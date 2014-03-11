/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.homepage;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.vietspider.db.url.HomepageDatabase;
import org.vietspider.link.pattern.LinkPatternExtractor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2009  
 */
public class HomepageTemplate {

  private String sourceName;

//  private String sourceAddress;

  private LinkPatternExtractor extractor;

  private Set<String> values = new CopyOnWriteArraySet<String>();
  
  public HomepageTemplate(String sourceName, List<String> templates) throws Exception {
    this(sourceName, templates.toArray(new String[templates.size()]));
  }
  
  public HomepageTemplate(String sourceName, String[] templates) throws Exception {
    this.sourceName = sourceName;
    extractor = createPatterns(LinkPatternExtractor.class, templates);
    if(extractor == null) throw new Exception("Cann't create extractor!");
  }

  public HomepageTemplate(String sourceName, LinkPatternExtractor extractor) {
    this.sourceName = sourceName;
    this.extractor = extractor;
  }

  public boolean extract(String address) {
    String homepage = extractor.extract(address);
    if(homepage == null) return false;
    values.add(homepage);
    return true;
  }
  
  public String buildURL() {
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = values.iterator();
    while(iterator.hasNext()) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(iterator.next());
    }
    return builder.toString();
  }

  public void commit() throws Exception {
    if(values.size() < 1) return;
    String [] arr = values.toArray(new String[values.size()]);
    values.clear();                      

//    if(sourceAddress == null) {
    HomepageDatabase db = new HomepageDatabase(sourceName, true);
    for(int i = 0; i < arr.length; i++) {
      db.saveUrl(arr[i]);
    }
//      return;
//    } 
//
//    VietSpiderDataClient client = new VietSpiderDataClient(sourceAddress);
//    StringBuilder builder = new StringBuilder(sourceName);
//    Iterator<String> iterator = values.iterator();
//    while(iterator.hasNext()) {
//      builder.append('\n').append(iterator.next());
//    }
//    Header [] headers = new Header[] {
//        new BasicHeader("action", "save.homepage.list")
//    };
//    byte [] bytes = builder.toString().getBytes(Application.CHARSET);
//    bytes = new GZipIO().zip(bytes);  
//    client.post(URLPath.DATA_HANDLER, bytes, headers);
  }

  public String getSourceName() { return sourceName; }

//  public void setSourceAddress(String sourceAddress) {
//    this.sourceAddress = sourceAddress;
//  }
}
