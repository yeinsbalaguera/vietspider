/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import static org.vietspider.link.generator.Generator.CREATE_LINK_GENERATOR;
import static org.vietspider.link.generator.Generator.DOCUMENT_GENERATOR;
import static org.vietspider.link.generator.Generator.EXTRACT_LINK_GENERATOR;
import static org.vietspider.link.generator.Generator.FUNCTION_FORM_GENERATOR;
import static org.vietspider.link.generator.Generator.FUNCTION_GENERATOR;
import static org.vietspider.link.generator.Generator.SCRAN_LINK_GENERATOR;
import static org.vietspider.link.generator.LinkGeneratorInvoker.constainGenerator;
import static org.vietspider.link.generator.LinkGeneratorInvoker.invoke;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.crawl.CrawlingSession;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.js.JsHandler;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.model.Source;
import org.vietspider.net.client.WebClient;
import org.vietspider.offices.DocumentConverter;
import org.vietspider.offices.DocumentConverter.FullSiteLinkVerifier;
import org.vietspider.pool.Worker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 29, 2007  
 */
public class HTMLLinkExtractor {
  
  private final static HTMLLinkExtractor instance = new HTMLLinkExtractor();
  
  public final static HTMLLinkExtractor getInstance() { return instance; }
  
  private HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
  
  public List<Link> extractLink(Worker<?, Link> worker) {
    Link link = worker.getValue();
//    System.out.println("link : "+ worker.getValue().hashCode() + " : "+ link.hashCode());
    CrawlingSession executor = (CrawlingSession)worker.getExecutor();
    Source source = CrawlingSources.getInstance().getSource(executor.getValue());
    if(source == null) return new ArrayList<Link>();
    
    SessionStore store = SessionStores.getStore(source.getCodeName());
    if(store == null) return new ArrayList<Link>();
    
    String host = executor.getResource(WebClient.class).getHost();
//    Js__doPostBack jsDoPostBack = srResource.getJsDoPostBack();
//    if(jsDoPostBack != null) {
//      executor.addElement(jsDoPostBack.getLinks(this, host, link), source);
//    }
    
    List<Object> generators = source.getLinkGenerators(); 
    if(constainGenerator(generators, FUNCTION_FORM_GENERATOR)) {
      new FormLinkExtractor(link.getBaseURL(), host, link, worker).extract();
    }
    
    return getLinks(link, /*source, */source.getUpdatePaths());
//    invoke(srResource.getLinkGenerators(), values, CREATE_LINK_GENERATOR);
  }
  
  public List<Link> getLinks(Link link, /*Source source,*/ NodePath [] nodePaths) {
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
//    System.out.println(" ====  > "+ link.getSource().hashCode() + " : "+ source.hashCode());
    URL baseUrl = link.getBaseURL();
    List<Link> links = new ArrayList<Link>();
    if(baseUrl == null) return links;
    
//    JSOnclickPatterns jsOnclickPatterns = srResource.getJSOnclickPatterns();
//    NodePath [] nodePaths = srResource.getUpdatePaths();
    HTMLDocument linkDocument = link.<HTMLDocument>getDocument();
    if(nodePaths != null && nodePaths.length > 0) {
      //lookup base node
      linkDocument = getDocument(link, nodePaths, source.getJsDocWriters());
    }
    if(linkDocument == null) {
//      CrawlExecutor executor = (CrawlExecutor)worker.getExecutor();
      LinkLogIO.saveLinkLog(link, "{visit.path.invalid}", LinkLog.PHASE_EXTRACT);
//      LogSource.getInstance().setMessage(source, null,  link.getAddress() + ": {visit.path.invalid}."); 
      return links;
    }
    
    //handle cdata
    HTMLParser2 parser = null;
    NodeIterator iterator = linkDocument.getRoot().iterator();
    List<HTMLNode> cdata = new ArrayList<HTMLNode>();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(!n.isNode(Name.CONTENT)) continue;
      String text = new String(n.getValue());
      text = text.trim();
      if(!text.startsWith("<![CDATA[")) continue;
      cdata.add(n);
    }
    
    for(HTMLNode n : cdata) {
      String text = new String(n.getValue());
      text = text.trim();
      text = text.substring("<![CDATA[".length(), text.length() - 3);
      if(parser == null) parser = new HTMLParser2();
      try {
//        System.out.println("=================================================");
//        System.out.println(text);
        HTMLDocument doc2 = parser.createDocument(text.toCharArray());
        List<HTMLNode> list = getChildren(doc2);
//        System.out.println(list.size());
//        System.out.println("truoc " + n.getParent().getChildren().size());
        if(list.size() > 0) n.getParent().replaceChild(n, list);
//        System.out.println("sau " + n.getParent().getChildren().sidze());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    List<String> values = new ArrayList<String>();
    List<Object> generators = source.getLinkGenerators(); 
    
    if(constainGenerator(generators, DOCUMENT_GENERATOR)) {
      invoke(generators, DOCUMENT_GENERATOR, linkDocument, values);
    }
    
    if(DocumentConverter.getInstance() != null) {
      hyperLinkUtil.setSiteLinkVerifier(new FullSiteLinkVerifier());
    }
    
    try {
      hyperLinkUtil.scanSiteLink(values, linkDocument.getRoot());
    } catch (Throwable e) {
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_EXTRACT);
//      LogWebsite.getInstance().setThrowable(link.getSource(), e, link.getAddress());
    }
    
//    System.out.println("site verifier " + hyperLinkUtil.getSiteLinkVerifier() );
    
    
    if(constainGenerator(source.getLinkGenerators(), FUNCTION_GENERATOR)) {
      List<String> onclicks = new ArrayList<String>();
      hyperLinkUtil.scanScriptLink(onclicks, linkDocument.getRoot());
//      System.out.println(" luc truoc "+values.size());
      //for test
//      for(int i = 0; i < onclicks.size(); i++) {
//        System.out.println(onclicks.get(i));
//      }
      
      if(invoke(generators, FUNCTION_GENERATOR, onclicks)) values.addAll(onclicks);
//      System.out.println(" sau do "+values.size());
//      for(int i = 0; i < values.size(); i++) {
//        System.out.println(values.get(i));
//      }
//      for(int i = 0; i < onclicks.size(); i++) {
//        System.out.println(onclicks.get(i));
//      }
    }
    
    invoke(generators, SCRAN_LINK_GENERATOR, values);
    
//    LinkExchangePatterns exchangesPatterns = srResource.getExchangePatterns();
    URLUtils urlUtils = new URLUtils();

//    System.out.println("\n\n");
//    System.out.println(" value size "+ values.size());
    for(int i = values.size() -1; i > -1 ; i--) {
      String address = values.get(i);
      
      if(address == null) continue;
//      System.out.println("base url " + baseUrl);
//      System.out.println("address " + address);
      address = urlUtils.createURL(baseUrl, address).trim();
      address = urlUtils.getCanonical(address);
//      System.out.println(" ====  link ++++> "+ address);
      
      if(address.length() > 3000) {
        values.set(i, null);
        continue;
      }
      if(address.charAt(0) != '/' && !SWProtocol.isHttp(address)) continue;
      links.add(new Link(address, null));
      
      int idx = address.toLowerCase().indexOf("http:", 10);
      if(idx > -1) {
        idx += 5;
        while(true){
          if(idx >= address.length()) break;
          char c = address.charAt(idx);
          if(c != '/' && c != ':') break; 
          idx++;
        }
        if(idx < address.length()) {
          String subaddress = "http://" + address.substring(idx);
          try {
            subaddress = URLDecoder.decode(subaddress, "utf-8");
            links.add(new Link(subaddress, null));
          } catch (Exception e) {
          }
        }
      }
      
//      System.out.println("trong khi "+linkData.getAddress());
//      System.out.println("co ket qua "+ address);
      
     /* if(exchangesPatterns == null) continue;
      List<String> list = exchangesPatterns.create(address);
      for(int j = 0; j < list.size() ; j++) {
        values.add(new Link(list.get(j)));
      }*/
    }
    
    invoke(generators, CREATE_LINK_GENERATOR, values);
    invoke(generators, EXTRACT_LINK_GENERATOR, values);
    
//    System.out.println(" cuoi cung ta co "+ links.size());
    
    return links;
  }
  
 
  private HTMLDocument getDocument(Link link, NodePath [] nodePaths, List<String> jsDocWriters) {
    HTMLDocument document = link.<HTMLDocument>getDocument();
    if(jsDocWriters.size() > 0) {
      try {
        JsHandler.updateDocument(document, jsDocWriters);
      } catch(Exception exp) {
        Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
        LinkLogStorages.getInstance().save(source, exp);
//        LogWebsite.getInstance().setThrowable(link.getSource(), exp);
      }
    }
    
    HTMLExtractor extractor = new HTMLExtractor();
    try {          
      HTMLDocument linkDocument = extractor.extract(document, nodePaths);
      if(linkDocument != null && linkDocument.getRoot().totalOfChildren() < 1) linkDocument = null;
      return linkDocument;
    } catch(Exception exp) {
//      CrawlExecutor executor = (CrawlExecutor)worker.getExecutor();
      LinkLogIO.saveLinkLog(link, exp, LinkLog.PHASE_DOWNLOAD);
//      LogSource.getInstance().setThrowable(link.getSource(), exp);
    }
    return null;
  }
  
  private List<HTMLNode> getChildren(HTMLDocument doc) {
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();
    HTMLNode root = doc.getRoot();
    if(!root.isNode(Name.HTML)) {
      nodes.add(root);
      return nodes;
    }
    
    List<HTMLNode> children = root.getChildren();
    for(int i = 0; i < children.size(); i ++) {
      HTMLNode child = children.get(i);
//      System.out.println(child.getName());
      if(child.isNode(Name.BODY) && child.getChildren() != null) {
        nodes.addAll(child.getChildren());
        break;
      }
    }
    
//    for(int i = 0; i < nodes.size(); i ++) {
//      System.out.println(nodes.get(i).getTextValue());
//    }
    
    return nodes;
  }

//  
}
