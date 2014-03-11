/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vietspider.browser.form.Form;
import org.vietspider.browser.form.FormUtils;
import org.vietspider.browser.form.Param;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.link.pattern.model.JSPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2009  
 */
class FormLinkExtractor {
  
  private URL baseUrl;
//  private String host;
  private LinkExplorer refer;
  private List<FunctionFormGenerator> generators = new ArrayList<FunctionFormGenerator>();
  
  FormLinkExtractor(URL baseUrl, LinkExplorer refer) {
    this.baseUrl = baseUrl;
//    this.host = host;
    this.refer = refer;
  }

  List<LinkExplorer> extract() throws Exception {
    FormUtils formUtils = new FormUtils(); 
    List<Form> forms = formUtils.searchForm(refer.getTokens());
    
    List<LinkExplorer> linkExplorers = new ArrayList<LinkExplorer>();

    for(int i = 0; i < generators.size(); i++) {
      extract(linkExplorers, generators.get(i), forms);
    }
    
    return linkExplorers;
  }
  
  private void extract(List<LinkExplorer> linkExplorers, FunctionFormGenerator generator, List<Form> forms) throws Exception {
    List<List<Param>> params = new ArrayList<List<Param>>();
    
    HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
    
    List<String> onclicks = new ArrayList<String>();
    if(generator.getJsOnclickPatterns() != null) {
      HTMLDocument linkDocument = refer.<HTMLDocument>getDocument();
      hyperLinkUtil.scanScriptLink(onclicks, linkDocument.getRoot());
    }

    generator.generate(params, onclicks);
    
//    System.out.println("\n==============================================================");
//    for(int i = 0; i < params.size(); i++) {
//      System.out.println(params.get(i).getName()+ " : "+ params.get(i).getValue());
//    }
//    System.out.println("==============================================================");
    
    List<LinkExplorer> links = createLink(refer, generator, forms, params);
    
    linkExplorers.addAll(completeLink(links));
  }
  
  private List<LinkExplorer> completeLink(List<LinkExplorer> links) {
    List<LinkExplorer> values = new ArrayList<LinkExplorer>();
    Iterator<LinkExplorer> iterator = links.iterator();
    URLUtils urlUtils = new URLUtils();
    while(iterator.hasNext()) {
      LinkExplorer tempLink = iterator.next();
      String address = tempLink.getAddress();
      if(address == null) {
        iterator.remove();
        continue;
      }

      address = urlUtils.createURL(baseUrl, address).trim();
//    System.out.println(" co na "+ address+ "    "+ address.length());
      address = urlUtils.getCanonical(address);

      if(address.length() > 3000) {
        iterator.remove();
        continue;
      }
      
      if(address.charAt(0) != '/' && !SWProtocol.isHttp(address)) {
        iterator.remove();
        continue;
      }
      
      LinkExplorer link = create(address, refer.getLevel()+1);
      values.add(link);
      link.setIsLink(link.isData());
      link.setIsData(link.isLink());
      link.setParams(tempLink.getParams());
      values.add(link);
    }
    return values;
  }
  
  
  private List<LinkExplorer> createLink(LinkExplorer link,  
      FunctionFormGenerator generator, List<Form> forms, List<List<Param>> params) {
    List<LinkExplorer> links = new ArrayList<LinkExplorer>();
//  System.out.println(" truoc tien tokesn " + link.getTokens().size() );
//  System.out.println(" tim thay "+ forms.size());
    if(forms.size() < 1) return links;
    
    for(int k = 0; k < forms.size(); k++) {
      Form form = forms.get(k);
      if(!generator.isForm(form.getName())) continue;
      
      if(generator.getAction() != null 
          && !generator.getAction().trim().isEmpty()) {
        form.setAction(generator.getAction());
      }
      
      for(int m = 0; m < params.size(); m++) {
        Form newForm = form.clone();
        
        String address = newForm.getAction();
        if(address == null || address.trim().isEmpty()) address = link.getAddress();
        
        LinkExplorer newLink  = new LinkExplorer();
        newLink.setReferer(link.getRef());
        newLink.setAddress(address);
        newForm.putParamValue(params.get(m));
        newLink.setParams(cleanParam(newForm.getParams()));
        detectLinkOrData(newLink) ;
        links.add(newLink);
      }
    }
    return links;
  }
  
  private List<Param> cleanParam(List<Param> params) {
    Iterator<Param> iterator = params.iterator();
    boolean hasScript = false;
    while(iterator.hasNext()) {
      Param param = iterator.next();
      if(Param.SCRIPT == param.getFrom()) {
        hasScript = true;
        break;
      }
    }
    if(!hasScript) return params;
    
    iterator = params.iterator();
    while(iterator.hasNext()) {
      Param param = iterator.next();
      if("submit".equals(param.getType())
          || "image".equals(param.getType())) iterator.remove();
    }
    return params;
  }
  
  public LinkExplorer create(String address, int level) {
    if(address == null 
        || (address = address.trim()).isEmpty()) return null;

    URL url = null;
    try {
      url = new URL(address);
    } catch (Exception e) {
//      LogService.getInstance().setMessage(e, "1. Get link: ");
      return null;
    }


//    int addressCode = codeGenerator.hashCode(url, sessionParameter);

    String ref = url.getRef();
    if(ref != null && !(ref = ref.trim()).isEmpty()) {
      String link = address.substring(0, address.indexOf('#'));
      return new LinkExplorer(address, link, ref, level);
    }
    
//    System.out.println(" create link in LinkCreator "+ address);

    return new LinkExplorer(address, level);
  }

  public void setGenerators(List<FunctionFormGenerator> generators) {
    this.generators = generators;
  }
  
  private void detectLinkOrData(LinkExplorer link) {
    List<Param> params = link.getParams();
    for(int i = 0; i < params.size(); i++) {
      if(params.get(i).getLinkType() == JSPattern.DATA) {
        link.setIsData(true);
        link.setIsLink(false);
        return;
      } else if(params.get(i).getLinkType() == JSPattern.ALL) {
        link.setIsData(true);
        link.setIsLink(true);
        return;
      }
    }
    link.setIsData(false);
    link.setIsLink(true);
  }
  
 /* public static void printTextParam(List<Param> params) {
    FormLinkExtractor extractor = new FormLinkExtractor();
    
    StringBuilder builder = new StringBuilder();
    for(Param param : params) {
      builder.append(param.getName()).append('=').append(param.getValue()).append('\n');
    }
    System.out.println("\n===================================================================");
    System.out.println(builder.toString().trim());
    System.out.println("===================================================================\n");
  }*/

  /*
   * js_dopostback thay bang 
   * type org.vietspider.crawl.link.generator.FunctionFormGenerator
   * javascript:__doPostBack('*','') 
   * __EVENTTARGET={1}
   */
}
