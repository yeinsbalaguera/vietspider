/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.util.List;

import org.vietspider.crawl.CrawlingSources;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 25, 2008  
 */
public abstract class URLGenerator {
  
//  protected volatile Link homepage;
//  protected volatile CrawlExecutor executor;
//  protected volatile SessionStore sessionStore;
  
  protected volatile String [] templates;
  
  protected String key;
  protected String sourceFullName;
  
  public URLGenerator(String sourceFullName, String[] templates_) {
    this.templates = templates_;
    this.sourceFullName = sourceFullName;
    
    StringBuilder builder = new StringBuilder(getClass().getName());
    for(int i = 0; i < templates.length; i++) {
      templates[i] = templates[i].trim();
      if(templates[i].isEmpty()) continue;
      builder.append(templates[i]).append('\n');
    }
    
    int hashCode = builder.toString().trim().hashCode();
    key  = String.valueOf(hashCode);
//    System.out.println("+===== "+ key);
    
  }
  
  public abstract void generate(List<String> list) ;
  
  protected void saveSource(String index) {
//  if(setPriority) saveSource.setPriority(-1);
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    SourceIO.getInstance().saveProperty(source, key, index);
//    source.getProperties().put(key, String.valueOf(index));
  }
  
}
