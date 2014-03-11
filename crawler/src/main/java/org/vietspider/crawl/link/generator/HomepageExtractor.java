/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.db.url.HomepageDatabase;
import org.vietspider.io.homepage.HomepageTemplate;
import org.vietspider.io.homepage.StorageHomepageLoader;
import org.vietspider.link.generator.Generator;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.model.SourceProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2008  
 */
public class HomepageExtractor {
  
  private List<HomepageTemplate> hpTemplates = new ArrayList<HomepageTemplate>(); 
  
  private String sourceFullName;
//  private List<String> sources = new ArrayList<String>();
//  private List<LinkPatternExtractor> extractors = new ArrayList<LinkPatternExtractor>();

  /* format:
   * type   org.vietspider.crawl.link.generator.HomepageGenerator
   * BLOG.BLOG VIET NAM.360 YAHOO
   * use for itseft
   * http://*.blog.360.yahoo.com/blog-*?cq=1
   * http://*.blog.360.yahoo.com/blog-*
   * http://blog.360.yahoo.com/blog-*?cq=1
   *  
   */
  public HomepageExtractor(String sourceFullName, String...values) throws Exception {
    this.sourceFullName = sourceFullName;
    if(values == null || values.length < 1) {
      throw new NullPointerException("no template");
    }
    
    List<String> list  = new ArrayList<String>();
    for(int i = 0; i < values.length; i++) {
      if(values[i].startsWith("type ")) continue;
      if(SWProtocol.isHttp(values[i])) {
        list.add(values[i]); 
      } else  if (sourceFullName.equalsIgnoreCase(values[i])){
        continue;
      } else {
        List<String> templates = loadTemplate(values[i]);
        generateExtractor(values[i], templates);
      }
    }
    generateExtractor(sourceFullName, list);
    
    if(hpTemplates.size() < 1) {
      throw new NullPointerException("Cann't create extractors.");
    }
  }
  
  public void generate(List<String> list) {
    for(int i = 0; i < list.size(); i++) {
      for(int k = 0; k < hpTemplates.size(); k++) {
        if(list.get(i) == null) continue;
        hpTemplates.get(k).extract(list.get(i));
      }
     /* for(int k = 0; k < sources.size(); k++) {
        String homepage = extractors.get(k).extract(address);
        if(homepage == null) continue;
//        System.out.println(" ket qua " + sources.get(k)+ " : "+ homepage);
        try {
          homepageService.insert(sources.get(k), homepage);
//        service.save(key , homepage);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(sources.get(k), e);
        }
      }*/
      //end 
    }
    
    for(int k = 0; k < hpTemplates.size(); k++) {
      try {
        hpTemplates.get(k).commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(hpTemplates.get(k).getSourceName(), e);
      }
    }
  }

  public short getType() { return Generator.EXTRACT_LINK_GENERATOR; }
  
  private void generateExtractor(final String name, List<String> list) {
    if(list.size() < 1) return;
    String [] arr = list.toArray(new String[list.size()]);
    try {
      hpTemplates.add(new HomepageTemplate(name, arr));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    final StorageHomepageLoader loader = new StorageHomepageLoader(name, arr);
    
    SystemProperties systemProperties = SystemProperties.getInstance();
    
    if(!"true".equals(systemProperties.getValue("detect.website"))) return;
    
    new Thread() {
      public void run() {
        detect("*");
        detect("vn");
        detect("en");
      }
      
      private void detect(String lang) {
//        System.out.println(" == > chuan bi detect");
        String [] homepages = loader.download(lang);
//        System.out.println(" ket qua ta co duoc " + lang + " : "+ (homepages == null ? "null" : homepages.length));
        if(homepages == null || homepages.length < 1) return;
        HomepageDatabase db = new HomepageDatabase(name, true);
        for(int i = 0; i < homepages.length; i++) {
          db.saveUrl(homepages[i]);
        }
      }
    }.start();
  /*  String [] arrTempl = list.toArray(new String[list.size()]);
    LinkPatternExtractor extractor = createPatterns(LinkPatternExtractor.class, arrTempl);
    if(extractor == null) return;
    sources.add(name);
    extractors.add(extractor);*/
  }
  
  private List<String> loadTemplate(String sourceName) {
    String [] elements = sourceName.split("\\.");
    if(elements.length < 3) return new ArrayList<String>();
    String group = NameConverter.encode(CharsUtil.cutAndTrim(elements[0]));
    String category = NameConverter.encode(CharsUtil.cutAndTrim(elements[1]));
    String name  = category+"."+NameConverter.encode(CharsUtil.cutAndTrim(elements[2]));
    Source source = SourceIO.getInstance().loadSource(group, category, name);
    
    if(source == null) {
      LinkLogStorages.getInstance().sourceNull(group+"."+category+"."+name);
      return new ArrayList<String>();
    }
    return loadTemplate(source);
  }
  
  private List<String> loadTemplate(Source source) {
    String value = SourceProperties.getHomepageTemplate(source);
    List<String> list = new ArrayList<String>();
    if(value.trim().isEmpty()) return list;
    
    String [] elements = value.split("\n");
    for(String element : elements)
    if(SWProtocol.isHttp(element)) {
      list.add(element);
    }
    return list;
  }
  
}
