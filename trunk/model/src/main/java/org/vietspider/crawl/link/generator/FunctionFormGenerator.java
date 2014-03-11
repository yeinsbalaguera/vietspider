/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import static org.vietspider.link.pattern.LinkPatternFactory.createMultiPatterns;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.browser.form.Param;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.link.generator.DictTemplateUtil;
import org.vietspider.link.generator.Generator;
import org.vietspider.link.pattern.JSOnclickPatterns;
import org.vietspider.link.pattern.model.JSOnclickPattern;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2009  
 */
//https://www.bpn.gov/CCRSearch/Search.aspx
//http://www.bbb.org/us/Find-Business-Reviews/
public class FunctionFormGenerator {
  
  private String form = "*";
  protected String action = null; 
  
  protected volatile JSOnclickPatterns jsOnclickPatterns;
  
  protected volatile DictTemplateUtil dict;
  
  protected String sourceFullName; 
  
  protected List<Param> defaultParams  = new ArrayList<Param>();
  
  @SuppressWarnings("unused")
  public FunctionFormGenerator(String sourceFullName, String...values) {
    this.sourceFullName = sourceFullName;
    
    List<String> functions  = new ArrayList<String>();
    List<String> templates  = new ArrayList<String>();
    
    RefsDecoder decoder = new RefsDecoder();
    for(int i = 0; i < values.length; i++) {
      if(values[i].startsWith("javascript") 
          || (values[i].indexOf('(') > -1 && values[i].indexOf(')') > -1)) {
        if(i < values.length - 1) {
          functions.add(values[i]);
          functions.add(values[i+1]);
          i++;
        }
      } else if(values[i].startsWith("form=") 
          || values[i].startsWith("form =")) {
        String [] elements = values[i].split("=");
        if(elements.length > 1) form = elements[1];
      } else if(values[i].startsWith("action=") 
          || values[i].startsWith("action =")) {
        String [] elements = values[i].split("=");
        if(elements.length > 1) action = elements[1];
      } else if(values[i].indexOf('[') > -1 && values[i].indexOf(']') > -1) { 
          templates.add(values[i]);
      } else {
        String [] elements = values[i].split("=");
        if(elements.length > 1) defaultParams.add(new Param(elements[0], Param.FORM, elements[1]));
      }
    }
    
    if(functions.size() > 1) { 
      jsOnclickPatterns = createMultiPatterns(
          JSOnclickPatterns.class, functions.toArray(new String[functions.size()]));
    } else {
      jsOnclickPatterns = null;
    }
    
    if(templates.size() > 0) {
      Source source = CrawlingSources.getInstance().getSource(sourceFullName);
      dict = new DictTemplateUtil(source, templates.toArray(new String[templates.size()]));
    } else {
      dict = null;
    }
  }
  
  public boolean isForm(String name) {
    if("*".equalsIgnoreCase(form) || form.equalsIgnoreCase(name)) return true;
    String [] elements = form.split(",");
    for(int i = 0 ; i < elements.length; i++) {
      if(elements[i].equalsIgnoreCase(name)) return true;
    }
    return false;
  }
  
  public void generate(List<List<Param>> params, List<String> onclicks) {
    if(params.size() < 1) params.add(new ArrayList<Param>());
    
    for(int k = 0; k < defaultParams.size(); k++) {
      addParam(params, defaultParams.get(k));
    }
    
    generateDict(params);
    if(onclicks != null && onclicks.size() > 0) generateFunction(params, onclicks);
    
  }
  
  private void generateFunction(List<List<Param>> params, List<String> onclicks) {
//  System.out.println(" da vao day roi "+ onclicks.size());
    if(jsOnclickPatterns == null) return;
    JSOnclickPattern [] patterns = jsOnclickPatterns.getPatterns();
    
    if(patterns == null) return;

    for(int i = 0; i < onclicks.size(); i++) {
      String onclick = onclicks.get(i);
      if(onclick == null) continue;
     
      RefsDecoder decoder = new RefsDecoder();
      onclick = new String(decoder.decode(onclick.toCharArray()));
//      if(onclick.startsWith("javascript:__doPostBack")) {
//        System.out.println(" vao "+ onclick);
//      }
      String value =  null;
      short linkType = JSPattern.LINK;
      for(JSOnclickPattern pattern : patterns) {
        if(pattern == null || !pattern.match(onclick)) continue;
        String url = pattern.create(onclick);
        if(url == null) continue; 
        value = url;
        linkType = pattern.getType();
      }
      
//      if(onclick.startsWith("javascript:__doPostBack")) {
//        System.out.println(" ra "+ value);
//      }
      
      if(value == null) continue;
//      System.out.println(" ====  >"+ onclick);
      Param [] arrs = parseParam(value);
      for(int k = 0; k < arrs.length; k++) {
        if(arrs[k] == null) continue;
        arrs[k].setLinkType(linkType);
        addParam(params, arrs[k]);
      }
    }
  }
 
  private void generateDict(List<List<Param>> params) {
    if(dict == null) return;
    List<String> list = new ArrayList<String>();
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    dict.generate(source, list, 1);
    for(int i = 0; i < list.size(); i++) {
      String [] elements = list.get(i).split("=");
      if(elements.length < 2) continue; 
      addParam(params, new Param(elements[0], Param.FORM, elements[1]));
    }
  }
  
  private void addParam(List<List<Param>> params, Param param) {
    int length = params.size();
    for(int i = 0; i < length; i++) {
      List<Param> newParams = addParams(params.get(i), param);
      if(newParams == null) return;
      if(!params.contains(newParams)) params.add(newParams);
    }
  }
  
  private List<Param> addParams(List<Param> params, Param param) {
    for(int i = 0; i < params.size(); i++) {
      Param _param = params.get(i);
      if(_param.getName().equalsIgnoreCase(param.getName())) {
//        System.out.println(_param.getValue()+ " : "+ param.getValue()+ " : " + _param.getValue().equalsIgnoreCase(param.getValue()));
        if(_param.getValue().equalsIgnoreCase(param.getValue())) {
          return null;
        }
        List<Param> newParams = cloneList(params, param.getName());
        newParams.add(param);
        return newParams;
      }
    }
    params.add(param);
    return params;
  }
  
  private List<Param> cloneList(List<Param> list, String ignore) {
    List<Param> newList = new ArrayList<Param>();
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getName().equalsIgnoreCase(ignore)) continue;
      newList.add(list.get(i));
    }
    return newList;
  }
  
  public short getType() { return Generator.FUNCTION_FORM_GENERATOR; }

  public JSOnclickPatterns getJsOnclickPatterns() { return jsOnclickPatterns; }
  
  private Param[] parseParam(String onclick) {
    String [] elements = onclick.split("&");
    Param [] params = new Param[elements.length];
    for(int i = 0; i < elements.length; i++) {
      String [] data = elements[i].split("=");
      if(data.length < 2) continue;
      params[i] = new Param(data[0], Param.SCRIPT, data[1]);
    }
    return params;
  }
  
  public String getAction() { return action; }
  
  /*
   * js_dopostback thay bang 
   * type org.vietspider.crawl.link.generator.FunctionFormGenerator
   * javascript:__doPostBack('*','') 
   * __EVENTTARGET={1}
   */
  
  public static void main(String[] args) throws Exception {
    java.io.File file = new java.io.File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");

//  System.out.println(file.getCanonicalPath());
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Source source = new Source();
    String [] elements = new String[] {
        "javascript:__doPostBack('*','')", 
        " __EVENTTARGET={1}",
        "name=abc",
        "state=[state]"
    };
    FunctionFormGenerator generator = new FunctionFormGenerator(source.getFullName(), elements);
    
    List<String> onclicks = new ArrayList<String>();
    onclicks.add("javascript:__doPostBack('abc','')");
    
    List<List<Param>> params = new ArrayList<List<Param>>();
    params.add(new ArrayList<Param>());
    
    generator.addParam(params, new Param("a", Param.FORM, "value_a1"));
    generator.addParam(params, new Param("b", Param.FORM, "value_b1"));
    generator.addParam(params, new Param("a", Param.FORM, "value_a2"));
    generator.addParam(params, new Param("c", Param.FORM, "value_c1"));
    generator.addParam(params, new Param("a", Param.FORM, "value_a1"));
    System.out.println(params.size());
    
//    for(int k = 0; k < 10; k++) {
//      List<Param> params = new ArrayList<Param>();
//      generator.generate(params, onclicks);
//      for(int i = 0; i < params.size(); i++) {
//        System.out.println(params.get(i).getName()+ " : "+ params.get(i).getValue());
//      }
//      System.out.println("==============================================================");
//    }
    
  }

  
}
