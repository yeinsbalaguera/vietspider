/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;
import static org.vietspider.model.SourceProperties.DATA_PATTERN_PROPERTY;
import static org.vietspider.model.SourceProperties.LINK_PATTERN_PROPERTY;
import static org.vietspider.model.SourceProperties.SESSION_PARAMETER;

import java.util.List;
import java.util.Properties;

import org.vietspider.html.util.URLCodeGenerator;
import org.vietspider.link.pattern.LinkPatterns;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 16, 2009  
 */
public abstract class LinkBuilder<T> {
  
  protected volatile LinkPatterns visitPatterns;
  protected volatile LinkPatterns dataPatterns;
  protected volatile String sessionParameter = null; 
  
  protected String refererURL = "";
  
  protected volatile URLCodeGenerator codeGenerator;
  
  protected String sourceFullName;
  
  public LinkBuilder(Source source) {
    this.sourceFullName = source.getFullName();
    Properties properties = source.getProperties();
    //set url matcher
    visitPatterns = createPatterns(LinkPatterns.class, properties, LINK_PATTERN_PROPERTY);
    dataPatterns = createPatterns(LinkPatterns.class, properties, DATA_PATTERN_PROPERTY);
    
    codeGenerator = new URLCodeGenerator();
    
    if(properties.containsKey(SourceProperties.REFERER_NAME)) {
      refererURL = properties.getProperty(SourceProperties.REFERER_NAME).trim();
    }
    
    sessionParameter = properties.getProperty(SESSION_PARAMETER);
    if(sessionParameter == null) return; 
    if((sessionParameter = sessionParameter.trim()).isEmpty()) sessionParameter = null;
  }
  
  public abstract T create(String host, String address, int level/*, int homeCode*/) ;
  
  public abstract List<T> createRedirect(String host, T referer, String redirect) ;
  
  public LinkPatterns getVisitPatterns() { return visitPatterns; }
  public void setVisitPatterns(LinkPatterns visitPatterns) { this.visitPatterns = visitPatterns; }

  public LinkPatterns getDataPatterns() { return dataPatterns; }
  public void setDataPatterns(LinkPatterns dataPatterns) { this.dataPatterns = dataPatterns;  }
  
  public String getRefererURL() { return refererURL; }

  public String getSessionParameter() { return sessionParameter; }

}
