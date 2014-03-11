/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vietspider.browser.form.Form;
import org.vietspider.browser.form.FormUtils;
import org.vietspider.browser.form.Param;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.model.Source;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2009  
 */
public class FormLinkExtractor {
  
  private URL baseUrl;
  private String host;
  private Link referer;
  private CrawlingSession executor;
  
  private HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
  
  public FormLinkExtractor(URL baseUrl, String host, Link refer, Worker<?, Link> worker) {
    this.baseUrl = baseUrl;
    this.host = host;
    this.referer = refer;
    this.executor = (CrawlingSession)worker.getExecutor();
    
    Source source = CrawlingSources.getInstance().getSource(executor.getValue());
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return;
  }

  public void extract() {
    FormUtils formUtils = new FormUtils(); 
    List<Form> forms = formUtils.searchForm(referer.getTokens());

    Source source = CrawlingSources.getInstance().getSource(referer.getSourceFullName());
    List<Object> generators = source.getLinkGenerators(); 
    for(int i = 0; i < generators.size(); i++) {
      if(FunctionFormGenerator.class.isInstance(generators.get(i))) {
        extract((FunctionFormGenerator)generators.get(i), forms);
      }
    }
  }
  
  public void extract(FunctionFormGenerator generator, List<Form> forms) {
    
    List<String> onclicks = new ArrayList<String>();
    if(generator.getJsOnclickPatterns() != null) {
      HTMLDocument linkDocument = referer.<HTMLDocument>getDocument();
      hyperLinkUtil.scanScriptLink(onclicks, linkDocument.getRoot());
    }
    
    List<List<Param>> params = new ArrayList<List<Param>>();
    generator.generate(params, onclicks);
    
//    System.out.println("\n==============================================================");
//    for(int i = 0; i < params.size(); i++) {
//      List<Param> list = params.get(i);
//      for(int j = 0; j < list.size(); j++) {
//        System.out.println(list.get(j).getName()+ " : "+ list.get(j).getValue());
//      }
//    }
//    System.out.println("==============================================================");
    
    List<Link> links = createLink(referer, generator, forms, params);
    List<Link> values = completeLink(links);
//    for(Link l :  values ) {
//      System.out.println(" tao ra duoc "+ l.getAddress() + " : "+ l.isLink() + " : "+ l.isData());
//    }
    executor.addElement(values, referer.getSourceFullName());
  }
  
  private List<Link> completeLink(List<Link> links) {
    Source source = CrawlingSources.getInstance().getSource(referer.getSourceFullName());
    LinkCreator linkCreator = (LinkCreator)source.getLinkBuilder();
    List<Link> values = new ArrayList<Link>();
    Iterator<Link> iterator = links.iterator();
    URLUtils urlUtils = new URLUtils();
    while(iterator.hasNext()) {
      Link tempLink = iterator.next();
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
      
      Link link = linkCreator.create(host, address, referer.getLevel()+1/*, referer.getRootCode()*/);
      values.add(link);
      link.setIsLink(tempLink.isLink());
      link.setIsData(tempLink.isData());
      link.setParams(tempLink.getParams());
      
      values.add(link);
    }
    return values;
  }
  
  
  private List<Link> createLink(Link link,  
      FunctionFormGenerator generator, List<Form> forms, List<List<Param>> params ) {
    List<Link> links = new ArrayList<Link>();
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
        
        Link newLink  = new Link();
        newLink.setReferer(link.getRef());
        newLink.setAddress(address);
        newForm.putParamValue(params.get(m));
        newLink.setParams(cleanParam(newForm.getParams()));
        
//        System.out.println("hidshfidsfds form ");
//        for(int j = 0; j < form.getParams().size(); j++) {
//          System.out.println(form.getParams().get(j).getName()+ " : "+ form.getParams().get(j).getValue());
//        }
        
        detectLinkOrData(newLink) ;
//        System.out.println(" tao ra duoc11111 "+ newLink.getAddress() + " : "+ newLink.isLink() + " : "+ newLink.isData());
        links.add(newLink);
      }
    }
//    System.out.println(" tao duoc tong cong "+ links.size());
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
  
  private void detectLinkOrData(Link link) {
    List<Param> params = link.getParams();
    for(int i = 0; i < params.size(); i++) {
      if(params.get(i).getLinkType() == JSPattern.DATA) {
//        System.out.println("data "+ params.get(i).getValue());
        link.setIsData(true);
        link.setIsLink(false);
        return;
      } else if(params.get(i).getLinkType() == JSPattern.ALL) {
//        System.out.println("all "+ params.get(i).getValue());
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
