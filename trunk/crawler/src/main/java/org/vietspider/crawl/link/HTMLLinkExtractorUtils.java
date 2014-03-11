/***************************************************************************
 * Copyright 2003-2012 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.crawl.link;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.vietspider.chars.URLEncoder;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.db.link.track.LinkLogStorages;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.model.Source;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *  Email:nhudinhthuan@yahoo.com
 *  Website: vietspider.org       
 * Jan 29, 2012
 */
public class HTMLLinkExtractorUtils {

  public static URL getBaseURL(Link link) {
    String baseHref = null;
    if(link  == null) return null;

    List<NodeImpl> tokens = link.getTokens();
    if(tokens  == null) return null;
    
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    //    HTMLDocument document = link.getDocument();
    //    NodeIterator iterator = document.getRoot().iterator();
    //    while(iterator.hasNext()) {
    //      HTMLNode node = iterator.next();
    for(int i = 0; i < tokens.size(); i++) {
      if(!tokens.get(i).isNode(Name.BASE)) continue;
      //      if(!node.isNode(Name.BASE)) continue;
      Attributes attrs = tokens.get(i).getAttributes(); 
      //      Attributes attrs = node.getAttributes(); 
      int idx = attrs.indexOf("href");
      if(idx > -1) {
        baseHref = attrs.get(idx).getValue();
        link.setBaseHref(baseHref);
      }
      break;
    }

    if(baseHref == null || baseHref.trim().length() < 1) baseHref = link.getAddress();

    if("/".equals(baseHref)) {
      baseHref = source.getHome()[0];
      int index = baseHref.indexOf('/', 8);
      if(index > 0) baseHref = baseHref.substring(0, index);
    }

    return createBaseURL(source, baseHref);

    /*URL url = null;

    try {
      baseHref = new URLEncoder().encode(baseHref);
      url = new URL(baseHref).toURI().normalize().toURL();
//      url = new URI(baseHref).normalize().toURL();
    } catch (IllegalArgumentException e) {
      LogWebsite.getInstance().setMessage(e, baseHref);
    } catch (URISyntaxException e) {
      LogWebsite.getInstance().setMessage(e, baseHref);
    } catch (Exception e) {
      e.printStackTrace();
      LogWebsite.getInstance().setThrowable(e);
      return null;
    }
    return url;*/
  }

  private static URL createBaseURL(Source source , String value) {
    URL url = null;

    try {
      value = new URLEncoder().encode(value);
      url = new URL(value).toURI().normalize().toURL();
      //      url = new URI(baseHref).normalize().toURL();
    } catch (IllegalArgumentException e) {
      LinkLogStorages.getInstance().save(source, e, value);
      //      LogWebsite.getInstance().setMessage(e, value);
    } catch (URISyntaxException e) {
      LinkLogStorages.getInstance().save(source, e, value);
      //      LogWebsite.getInstance().setMessage(e, value);
    } catch (Exception e) {
      LinkLogStorages.getInstance().save(source, e, value);
      //      LogWebsite.getInstance().setThrowable(e);
      return null;
    }
    return url;
  }

  public static void main(String[] args) {
    //  String onclick = "http://admicro.vn/ilayer/click.php?c=98&ad=5252285&z=44&num=7&nexturl=http://rongbay.com/c98/raovat-5252285/THONG-TAC-Cong-Toilet-Thoat-San-Chau-Rua-gt-Gia-re-KO-uc-Pha-_XE-HUT-Be-Phot-Ha-Noi-P-v_Nhanh24-24.html";
    //  int idx = onclick.indexOf("http://", 10);
    //  if(idx > 0) System.out.println(onclick.substring(idx));

    //  String link = "http://www.bestbuy.com/site/olstemplatemapper.jsp?id=pcat17080&type=page&qp=crootcategoryid%23%23-1%23%23-1~~q70726f63657373696e6774696d653a3e313930302d30312d3031~~cabcat0100000%23%230%23%23se~~cabcat0101000%23%230%23%2362~~cpcmcat158900050008%23%230%23%233~~nf519%7C%7C24323030202d20243234392e3939&list=y&nrp=15&sc=TVVideoSP&sp=%2Bbrand+skuid&usc=abcat0100000";
    String link  = "http://www.baodatviet.vn/Home/congdongviet/url%28%23default%23homepage";
    System.out.println(link);
    System.out.println(createBaseURL(null, link));
  }
}
