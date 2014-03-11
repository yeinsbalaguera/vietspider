/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 25, 2009  
 */
public class DictTemplateUtil {
  
  private List<Template> templates = new ArrayList<Template>();
  
  private int index = 0;
  
  public DictTemplateUtil(Source source, String...templateValues) {
    for(int i = 0 ; i < templateValues.length; i++) {
      loadDict(source, templateValues[i]);
    }
  }
    
  private void loadDict(Source source, String templateValue) {
    int start = 0;
    Template template = new Template(templateValue);
    while(start > -1) {
      start = templateValue.indexOf('[', start+1);
      if(start < 0) break;
      int end = templateValue.indexOf(']', start);
      if(end < 0) break;
      String name  = templateValue.substring(start+1, end);
      File file  = new File(UtilFile.getFolder("sources/type/"), name);
      if(!file.exists()) {
        LogService.getInstance().setMessage(source, null, file.getAbsolutePath() + " not found.");
        continue;
      }
      try {
        byte [] bytes = RWData.getInstance().load(file);
        String value = new String(bytes, Application.CHARSET);
        template.addData(new Data(source, name, value.split(",")));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(source, e);
      }
    }
    
    if(template.datas.size() > 0) {
      templates.add(template);
    } else {
      LogService.getInstance().setMessage(source, null, templateValue + " no dict");
    }
  }
  
  public void generate(Source source, List<String> list, int size) {
    if(index >= templates.size()) index = 0;
    try {
      for(;index < templates.size(); index++) {
        Template template = templates.get(index); 
        while(true) {
          String value = template.next();
          if(value == null) break;
          list.add(value);
          template.save(source);
          if(list.size() >= size) {
            break;
          }
        }
      }

      for(;index < templates.size(); index++) {
        templates.get(index).save(source); 
      }
    } catch (Throwable e) { 
      LogService.getInstance().setThrowable(e);
    }
  }
  
  
  public static class Template {
    
    private String template;
    
    private List<Data> datas = new ArrayList<Data>();
    
    public Template(String value) {
      template = value;
    }
    
    public void addData(Data data) {
      datas.add(data);
    }
    
    public String next() {
      int index = datas.size()-1;
      Data data = datas.get(index);
      while(true) {
        if(data.index < data.words.length-1){
          data.index++;
          break;
        }
       
        data.index = 0;
        if(index < 1) break;
        index--;
        data = datas.get(index);
      }
      
      String value = new String(template);
      for(int i = 0; i < datas.size(); i++) {
        data = datas.get(i);
        String name = data.getName();
        value = replace(value, "["+name+"]", data.current());
      }
      
      return value;
    }

    public List<Data> getDatas() { return datas; }
    
    public void save(Source source) {
      for(int i = 0; i < datas.size(); i++) {
        datas.get(i).save(source);
      }
    }
  }
  
  private static String replace(String text, String pattern, String value) {
    int idx = text.indexOf(pattern);
    if(idx < 0) return text;
    text = text.substring(0, idx) + value + text.substring(idx+pattern.length());
    return replace(text, pattern, value);
  }
  
  public static class Data {
    
    private int index = 0;
    
    private String name;
    
    private String [] words;
    
    public Data(Source source, String n, String [] w) {
      this.words = w;
      this.name = n;
      
      try {
        String value = source.getProperties().getProperty(name+".index");
        index = Integer.parseInt(value.trim());
//        System.out.println(" load ra duoc " + name + " : " + index);
      } catch (Exception e) {
        
      }
    }
    
    public String current() { return words[index]; }
    
    public void save(Source source) {
      SourceIO.getInstance().saveProperty(source, name+".index", String.valueOf(index));
    }
    
    public String getName() { return name; }
  }

}
