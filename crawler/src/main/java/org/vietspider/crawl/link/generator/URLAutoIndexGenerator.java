/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.security.InvalidParameterException;
import java.util.Hashtable;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.io.tracker.PageDownloadedTracker;
import org.vietspider.crawl.link.Link;
import org.vietspider.link.generator.Generator;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 11, 2008  
 */
public final class URLAutoIndexGenerator {

  protected final static Hashtable<String, Single> REGISTER = new Hashtable<String, Single>();
  
  private String sourceFullName;

  public URLAutoIndexGenerator(String sourceFullName, String... templates) throws Exception {
    this.sourceFullName = sourceFullName;
    synchronized (REGISTER) {
      Single single = REGISTER.get(sourceFullName);
      if(single  != null) return;
      single  = new Single(templates);
      REGISTER.put(sourceFullName, single);
    }
  }

  public void generate(List<String> list) {
    Single single = REGISTER.get(sourceFullName);
    if(single != null) single.generateLinks(list);
  }
  
  public void endSession() {
    Single single = REGISTER.get(sourceFullName);
    if(single != null) single.save();
  }


  public short getType() { return Generator.HOMEPAGE_GENERATOR; }

  private final class Single {

    private long min = 0;
    private long max = 0;
    private long step = 1;
    //  private int time = 0;

    protected volatile long index;

    protected String linkTemplate;

    private Single(String...templates) {
      for(int i = 0;  i < templates.length; i++) {
        if(templates[i].indexOf("[index]") > -1) {
          linkTemplate = templates[i];
        } else if(templates[i].indexOf('-') > -1 
            || templates[i].indexOf('>') > -1 ) {
          String [] range = templates[i].split("->");
          if(range.length < 2) range = templates[i].split("-");
          if(range.length < 2) range = templates[i].split(">");
          if(range.length < 2) {
            throw new InvalidParameterException("Bad parameter: " + templates[i]);
          }

          try {
            min = Long.parseLong(range[0]);
          } catch (Exception e) {
            throw new InvalidParameterException("Min isn't a number: " + range[0]);
          }

          try {
            index = Long.parseLong(range[1]);
          } catch (Exception e) {
            throw new InvalidParameterException("Min isn't a number: " + range[0]);
          }

          try {
            step = Long.parseLong(range[2]);
          } catch (Exception e) {
            throw new InvalidParameterException("Max isn't a number: " + range[1]);
          }
        }
      }

      max = index + step;

      new Thread() {
        public void run() {
          index = inited(index);
//          System.out.println(" duoc tai " + index);
          save();
        }
      }.start();
    }
    
    private synchronized void generateLinks(List<String> list) {
//      System.out.println(" vao day generaate "+ list.size() + " : "+ index);
      int size = 50;

      while(index < max) {
        String valueIndex = String.valueOf(index);
        list.add(linkTemplate.replaceAll("\\[index\\]", valueIndex));  
        index++;  
        if(list.size() >= size) break;
      }

//      System.out.println(" sau do sinh ra generaate "+ list.size() + " : "+ index);
//      System.out.println(" va luc ra generaate "+ list.size());
    }
    
    private synchronized void save() {
//      System.out.println(" invoke end session roi "+ max+ " min "+ index);
      Source source = CrawlingSources.getInstance().getSource(sourceFullName);
      String propertyValue = source.getProperties().getProperty("LinkGenerator");
      String type = "org.vietspider.crawl.link.generator.URLAutoIndexGenerator";
      int start = propertyValue.indexOf(type);
      int end = propertyValue.indexOf('#', start);
      if(end  < 0) end = propertyValue.length();
      StringBuilder builder = new StringBuilder();
      builder.append(propertyValue.subSequence(0, start));
      builder.append("org.vietspider.crawl.link.generator.URLAutoIndexGenerator\n");
      builder.append(linkTemplate).append('\n');
      builder.append(min).append("->").append(index).append("->").append(step).append('\n');
      builder.append(propertyValue.substring(end));

      source.getProperties().remove("LinkGenerator");
      SourceIO.getInstance().saveProperty(source, "LinkGenerator", builder.toString());
    }
    
    private synchronized  long inited(long start) {
      while(start > min) {
        String valueIndex = String.valueOf(start);
        String address = linkTemplate.replaceAll("\\[index\\]", valueIndex);  
        Link link  = new Link(address, null);
        link.setSourceFullName(sourceFullName);
        try {
          if(PageDownloadedTracker.searchUrl(link, false)) {
            max = start + step;
            return start - 25;
          }
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
        if(start % 10000 == 0) System.out.println(start);
        start--;
      }
      max = start + step;
      return start;
    }
  }

}
