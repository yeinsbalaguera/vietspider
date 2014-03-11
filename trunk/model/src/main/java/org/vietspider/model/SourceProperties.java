/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.text.SWProtocol;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 5, 2008  
 */
public class SourceProperties {

  public final static String SESSION_PARAMETER = "SessionParameter";

  public final static String DATA_PATTERN_PROPERTY  = "URLPattern";
  public final static String LINK_PATTERN_PROPERTY  = "LinkPattern";

  public final static String PROXY = "Proxy";
  public final static String PROXY_HOST = "proxyHost";

  public final static String LOGIN = "Login";

  public final static String USER_AGENT = "UserAgent";
  public final static String REFERER_NAME = "Referer";

//public final static String TEMPLATE_URL = "TemplateURL";
//  public final static String ONCLICK_PATTERN_PROPERTIES = "OnclickPattern";

  public final static String JS_ONCLICK_PATTERN  = "JSOnclickPattern";
  public final static String JS_COMPLETE_DOC  = "JSCompleteDoc";
  public final static String LINK_GENERATOR = "LinkGenerator";
  public final static String HOMEPAGE_TEMPLATE = "HomepageTemplate";

  //for decode content use #decode# value to ContentFilter properties 
  public final static String CONTENT_FILTER = "ContentFilter";
  public final static String PAGE_CHECKER = "PageChecker";
  public final static String MIN_SIZE_OF_PAGE = "MinSizeOfPage";
  public final static String MAX_EXECUTOR = "MaxExecutor";

  public final static String HOMEPAGE_FILE_DOWNLOADING = "HomepageFileDownloading";

  public final static String  SOURCE_CONFIG_ERROR = "source.config.error";

  public final static String getHomepageTemplate(Source source) {
    Properties properties = source.getProperties();
    String sourceName  = source.getFullName();
    String homeTemplate = properties.getProperty(HOMEPAGE_TEMPLATE);
    String linkGenerator =  properties.getProperty(LINK_GENERATOR);
    return getHomepageTemplate(sourceName, homeTemplate, linkGenerator);
  }
  
  public final static String getHomepageTemplate(
      String sourceName, String homepageTemplate, String linkGenerator) {
    StringBuilder builder = new StringBuilder();

    if(homepageTemplate != null && !homepageTemplate.trim().isEmpty()) {
      builder.append(homepageTemplate);
    }
    if(builder.length() > 0) builder.append('\n');


    if(linkGenerator == null 
        || (linkGenerator = linkGenerator.trim()).length() < 1) {
      return builder.toString().trim();
    }
    
    String [] elements = splitGenerators(linkGenerator);
    for(int i = 0; i < elements.length; i++) {
      if(isTemplate(sourceName, elements[i])) {
        builder.append(elements[i].trim());
      }
    }

    return builder.toString().trim();
  }
  
  private final static boolean isTemplate(String sourceName, String value) {
    String [] elements = value.split("\n");
    for(int i = 0; i < elements.length; i++) {
      if(elements[i].startsWith("type ")) continue;
      if(SWProtocol.isHttp(elements[i])) continue;
      if(!sourceName.equals(elements[i])) return false;
    }
    return true;
  }
  
  public final static String[] splitGenerators(String value) {
    int index = 0;
    int start = index;
    int len = value.length();
    List<String> list = new ArrayList<String>();
    while(index < len) {
      char c = value.charAt(index);
      if(c == '#') {
        if(index > 0 && index < len - 1
            && value.charAt(index - 1) == '\n' 
              && Character.isWhitespace(value.charAt(index+1))) {
          list.add(value.substring(start, index-1));
          start = index+1;
        }
      }
      index++;
    }
    if(start < index) {
      list.add(value.substring(start, index));
    }
    return list.toArray(new String[list.size()]);
  }
  
  public static String getHomepageTemplate(Properties properties) {
    String hpTemplate = properties.getProperty(SourceProperties.HOMEPAGE_TEMPLATE);
    String linkGenerator = properties.getProperty(SourceProperties.LINK_GENERATOR);
    if(linkGenerator != null) {
      String [] elements = linkGenerator.split("#\n");
      linkGenerator = null; 
      for(int i = 0; i < elements.length; i++) {
        if(elements[i].indexOf("HomepageExtractor") > -1)  {
          linkGenerator = elements[i];
          break;
        }
      }
    }

    StringBuilder builder = new StringBuilder();
   
    if(hpTemplate != null) builder.append(hpTemplate);
    if(builder.length() > 0) builder.append('\n');
    if(linkGenerator != null) builder.append(linkGenerator);
   
    String [] elements = builder.toString().trim().split("\n");
    builder.setLength(0);
    
    for(int i = 0; i < elements.length; i++) {
      if((elements[i] = elements[i].trim()).isEmpty()) continue;
      if(SWProtocol.isHttp(elements[i])) {
        if(builder.length() > 0) builder.append('\n');
        builder.append(elements[i]);
      }
    }
   return builder.toString().trim();
  }
}
