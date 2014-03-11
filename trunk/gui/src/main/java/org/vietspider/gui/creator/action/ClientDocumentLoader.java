/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.util.List;
import java.util.Properties;

import org.vietspider.browser.FastWebClient;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.html.HTMLDocument;
import org.vietspider.link.explorer.SiteExplorer;
import org.vietspider.link.generator.FormDataExtractor;
import org.vietspider.link.generator.UpdateDocument;
import org.vietspider.link.generator.UpdateDocumentGenerator;
import org.vietspider.model.Source;
import org.vietspider.model.SourceProperties;
import org.vietspider.net.channel.DefaultSourceConfig;
import org.vietspider.net.channel.DocumentLoader;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 13, 2009  
 */
public class ClientDocumentLoader {
  
  protected ISourceInfo iSourceInfo;
  
  protected DefaultSourceConfig config;
  
  public void setISourceInfo(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
    this.config = (DefaultSourceConfig)iSourceInfo.getSourceConfig();
    
    loadDocumentGenerator();
  }
  
  public HTMLDocument getDocument(String address, boolean cache) throws Exception {
    config.setAddress(address);
    config.setCache(cache);
    
    if(config.isServer()) {
      config = DataClientHandler.getInstance().loadDoc(config);
    } else {
      new DocumentLoader(config).load();
    }
    return config.getDoc();
  }
  
  public void abort(String url) { config.abort(url); }
  
  private void loadDocumentGenerator() {
    List<UpdateDocument> updateDocs = config.getUpdateDocs();
    updateDocs.clear();
    Properties properties = config.getProperties(); 
    String generatorValue = properties.getProperty(SourceProperties.LINK_GENERATOR);
    if(generatorValue == null) return ;
    String [] elements = SourceProperties.splitGenerators(generatorValue);
    Source source = new Source();
    source.setEncoding(config.getCharset());

    for(String element : elements) {
      if(element.indexOf("org.vietspider.link.generator.UpdateDocumentGenerator") > -1) {
        String type  = "org.vietspider.link.generator.UpdateDocumentGenerator";
        int index = element.indexOf(type);
        if(index < 0) continue;
        String value = element.substring(index+type.length()+1).trim();
        try {
          UpdateDocumentGenerator generator = new UpdateDocumentGenerator(source.getFullName(), value.split("\n"));
          generator.setCharset(config.getCharset());
          updateDocs.add(generator);
        } catch (Exception e) {
          config.log(e);
        }
      } else if(element.indexOf("org.vietspider.link.generator.FormDataExtractor") > -1) {
        try {
          FormDataExtractor generator = new FormDataExtractor(source.getFullName());
          updateDocs.add(generator);
        } catch (Exception e) {
          config.log(e);
        }
      } else if(element.indexOf("org.vietspider.crawl.link.generator.FunctionFormGenerator") > -1) {
        if(element.indexOf("#data") < 0) return;
        String type  = "org.vietspider.crawl.link.generator.FunctionFormGenerator";
        int index = element.indexOf(type);
        if(index < 0) continue;
        String value = element.substring(index+type.length()+1).trim();
        String [] templates = value.split("\n");

        SiteExplorer siteExplorer = new SiteExplorer(config.getHomepage(), 4);
        try {
          FastWebClient webClient = config.getWebClient();
          siteExplorer.setWebClient(webClient);
          webClient.registryProxy(config.getSourceProxy());
        } catch (Exception e) {
          config.log(e);
        }

        FunctionFormGenerator generator = new FunctionFormGenerator(null, templates);
        siteExplorer.addFunctionFormGenerator(generator);
        config.setSiteExplorer(siteExplorer);
      }
    }

  }
  
}
