/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.net.URLEncoder;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.link.generator.Generator;
import org.vietspider.locale.vn.VietnameseSingleWords;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 11, 2008  
 */
public final class URLWordGenerator extends URLGenerator {
  
  private String [] words = {"a", "à", "ả", "á", "ạ",
      "ác", "ách", "ạch", " Ag", "ai", "ải", "ái",
      " am", " ám", "an", "án", "ang", "áng", "anh", "ảnh", "ánh"};
  
  protected volatile int index;
  
  public URLWordGenerator(String sourceFullName, String...values) throws Exception {
    super(sourceFullName, values);
    
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    try {
      index = Integer.parseInt(source.getProperties().getProperty(key));
    } catch (Exception e) {
    }
    
    VietnameseSingleWords dict = new VietnameseSingleWords();
    words = dict.getDict().toArray(new String[0]);
    
    if(index >= words.length) index = 0;
  }
  
  
  public void generate(List<String> list) {
    if(index >= words.length) return;
    int size = 25;
    saveSource(String.valueOf(index));
    while(index < words.length) {
      String word = null; 
      try {
        word = URLEncoder.encode(words[index], Application.CHARSET);
      } catch (Exception e) {
      }
      index++;
      if(word == null) continue;

      for(String template : templates) {
        if(template.indexOf("[index]") > -1) {
          String address = template.replaceAll("\\[index\\]", word);
          list.add(address);
        } else if(template.indexOf("[vietspider.word]") > -1) {
          String address = template.replaceAll("\\[vietspider\\.word\\]", word);
          list.add(address);
        } else  if(template.indexOf("[word]") > -1) {
          String address = template.replaceAll("\\[word\\]", word);
          list.add(address);
        }
      }
      size--;
      if(size < 1) break;
    }
  }
  
 /* private class SaveExecutor extends Thread {
    
    private int lastIndex = 0;
    
    public void run () {
      while(true) {
        
        try {
          Thread.sleep(2*60*1000);
        } catch (Exception e) {
        }
        
        Source source = homepage.getSource();
        SourceResource sourceResource = executor.getResource(SourceResource.class);
        
        if(index >= words.length) return;
        
        if(executor.getValue() != source || sourceResource.isTimeout()) return;
        
        if(lastIndex == index) continue;
        lastIndex = index;
        saveSource(false);
      }
    }
  }*/
  
  public short getType() { return Generator.HOMEPAGE_GENERATOR; }
  
}
