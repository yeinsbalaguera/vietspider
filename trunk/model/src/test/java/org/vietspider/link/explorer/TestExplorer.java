/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.io.File;
import java.util.List;

import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 27, 2009  
 */
public class TestExplorer {
  
  /*192.84.171.99:3128
41.210.252.16:80
207.61.241.100:9090
24.125.182.66:9090*/

  public static void main(String[] args) throws Exception  {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");

    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    
    System.out.println(file.getCanonicalPath());
    
    String homepage = "https://www.bpn.gov/CCRSearch/Search.aspx";
    SiteExplorer siteExplorer = new SiteExplorer(homepage, 4);
    
    WebClient webClient = new WebClient();
    webClient.setLog(true);
    siteExplorer.setWebClient(webClient);
    webClient.registryProxy("192.84.171.99:3128");
    
    String [] linkGenerators = new String[] {
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults', 'Page$*')",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}",
        "ctl00$ContentPlaceHolder1$ddlState=[state]",
        "javascript:__doPostBack('ctl00$ContentPlaceHolder1$gvSearchResults','detail$*') #data",
        "__EVENTTARGET={1}&__EVENTARGUMENT={2}"
    };
    
    FunctionFormGenerator generator = new FunctionFormGenerator(null, linkGenerators);
    siteExplorer.addFunctionFormGenerator(generator);
    
    siteExplorer.explore();
    
    List<Exception> exceptions = siteExplorer.getExceptions();
    for(Exception exception : exceptions) {
      System.err.println("\n\n");
      exception.printStackTrace();
    }
    
    System.exit(0);
    
  }
}
