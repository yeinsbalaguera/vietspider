/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.util.List;

import org.vietspider.crawl.CrawlingSources;
import org.vietspider.link.generator.DictTemplateUtil;
import org.vietspider.link.generator.Generator;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 11, 2008  
 */
public final class URLDictGenerator {

  private DictTemplateUtil dict;
  private String sourceFullName;
  
  public URLDictGenerator(String sourceFullName, String...values) throws Exception {
    this.sourceFullName = sourceFullName;
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    dict = new DictTemplateUtil(source, values);
  }
  
  public void generate(List<String> list) {
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    dict.generate(source, list, 25);
  }
  
  public short getType() { return Generator.HOMEPAGE_GENERATOR; }
  
 /* public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
//  file = new File("F:\\Projects\\VietStock\\VietSpider3Build13Server\\data\\");

//  System.out.println(file.getCanonicalPath());
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
  
    String [] templates = {
          "http://www.addresses.com/results.php?ReportType=34&qfilter[pro]=on&qi=0&qk=10&qn=[last_name]&qs=[state]"
    };
    Source source = new Source();
    URLDictGenerator dictGenerator = new URLDictGenerator(source, templates);
    for(int i = 0; i < 10000; i++) {
      List<String> values = new ArrayList<String>();
      dictGenerator.generate(values);
    }
  }*/
}
