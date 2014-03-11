/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.model.LinkBuilder;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2008  
 */
public class LinkCreator extends LinkBuilder<Link> {
  
  public LinkCreator(Source source) {
    super(source);
  }
  
 /* public void generate(List<Link> list) {
    URL homepage = null;
    try {
      homepage = new URL(source.getHome()[0]);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(source, e);
      return;
    }
    if(homepage == null) return;
    
    LinkQueue stores = new LinkQueue(source);
    
    for(int i = 0; i < list.size(); i++) {
      String address = list.get(i).getAddress();
      Link link = createLink(homepage, address);
      if(link == null) continue;
      int hashCode = link.getAddressCode();
      if(codes.contains(hashCode)) continue;
      stores.push(link);
      codes.add(hashCode);
    }
    LinkCacher cacher = LinkCacherService.getCacher(source);
    if(cacher != null) cacher.save(stores);
  }
  
  private Link createLink(URL homepage, String address) {
    if(address == null 
        || (address = address.trim()).isEmpty()
        || !address.toLowerCase().startsWith("http")) return null;

    URL url = null;
    try {
      url = new URL(address);
    } catch (Exception e) {
      LogWebsite.getInstance().setMessage(e, "1. Get link:");
      return null;
    }

    if(visitPatterns == null && dataPatterns == null) {
      if(!codeGenerator.compareHost(url.getHost(), homepage.getHost())) return null;
    } 
    boolean isData = dataPatterns == null || dataPatterns.match(address);
    boolean isLink = visitPatterns == null || visitPatterns.match(address);
    
    int addressCode = codeGenerator.hashCode(url, sessionParameter);
    Link link  = null;
    
    int level = source.getDepth();
    if(level > 1 && isLink) level--;

    String ref = url.getRef();
    if(ref != null && !(ref = ref.trim()).isEmpty()) {
      String href = address.substring(0, address.indexOf('#'));
      link = new Link(address, href, ref, level, source, addressCode);
    }

    link = new Link(address, level, source, addressCode);
    link.setIsData(isData);
    link.setIsLink(isLink);
    
//    System.out.println(link.getAddress() + " : "+ link.isData() + " : "+ link.isLink() + " : "+ link.getLevel());
    
    return link;
  }*/
  
  public Link create(String host, String address, int level/*, int homeCode*/) {
    if(address == null 
        || (address = address.trim()).isEmpty()) return null;

    URL url = null;
    try {
      url = new URL(address);
    } catch (Exception e) {
//      LogService.getInstance().setMessage(e, "1. Get link: ");
      return null;
    }

    if(host != null && visitPatterns == null && dataPatterns == null ) {
      if(!codeGenerator.compareHost(url.getHost(), host)) return null;
    }

//    int addressCode = codeGenerator.hashCode(url, sessionParameter);

    String ref = url.getRef();
    if(ref != null && !(ref = ref.trim()).isEmpty()) {
      String link = address.substring(0, address.indexOf('#'));
      return new Link(address, link, ref, level, sessionParameter, sourceFullName/*, homeCode*/);
    }
    
//    System.out.println(" create link in LinkCreator "+ address);

    return new Link(address, level, sessionParameter, sourceFullName/*, homeCode*/);
  }
  
  public List<Link> createRedirect(String host, Link referer, String redirect) {
    List<Link> values = new ArrayList<Link>(1);
    
    int level = referer.getLevel();
    
    Link link = create(host, redirect, level/*, referer.getRootCode()*/);
    if(link == null) return values;

    link.setIsData(referer.isData());
    link.setIsLink(referer.isLink());
    link.setReferer(referer.getAddress());

    values.add(link);
    return values;
  }

}
