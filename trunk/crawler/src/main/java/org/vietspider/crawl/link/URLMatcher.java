/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.chars.URLEncoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 6, 2007  
 */
public class URLMatcher {

  private final static String DATA_PATTERN_PROPERTY  = "URLPattern";
  private final static String LINK_PATTERN_PROPERTY  = "LinkPattern";
  
  private final static String ONCLICK_PATTERN_PROPERTIES = "OnclickPattern";
  private final static String TEMPLATE_URL = "TemplateURL";
  
  private Pattern [] dataPatterns;
  
  private Pattern [] linkPatterns;
  
  private Pattern [] onclickPatterns;
  
  private String [] templates;
  
  public URLMatcher() {
  }

  public void setPatterns(Properties properties) {
    dataPatterns = createPatterns(properties, DATA_PATTERN_PROPERTY);
    if(dataPatterns != null && dataPatterns.length < 1) dataPatterns = null;
    
    linkPatterns = createPatterns(properties, LINK_PATTERN_PROPERTY);
    if(linkPatterns != null && linkPatterns.length < 1) linkPatterns = null;
    
    onclickPatterns = createPatterns(properties, ONCLICK_PATTERN_PROPERTIES);
    if(onclickPatterns != null && onclickPatterns.length < 1) onclickPatterns = null;
    
    String templateValue = properties.getProperty(TEMPLATE_URL);
    if(templateValue == null 
        || (templateValue = templateValue.trim()).isEmpty()) {
      templates = null;
    } else {
      templates = templateValue.split("\n");
    }
  }
  
  private Pattern[] createPatterns(Properties properties, String key) {
    if(!properties.containsKey(key)) return null;
    URLEncoder encoder = new URLEncoder();
    String value = properties.getProperty(key);
    if(value == null || (value = value.trim()).length() < 1) return null;
    String [] elements = value.split("\n");
    if(elements == null || elements.length < 1) return null;
    
    Pattern [] patterns = new Pattern[elements.length];
    for(int i = 0; i < elements.length; i++) {
      if(elements[i] == null || (elements[i] = elements[i].trim()).isEmpty()) continue;
      patterns[i] = createPattern(encoder.encode(elements[i]));
    }
    return patterns;
  }
  
  private Pattern createPattern(String value) {
    int i = 0;
    StringBuilder builder = new StringBuilder();
    while(i < value.length()) {
      char c = value.charAt(i);
      if(Character.isLetterOrDigit(c)) {
        builder.append(c);
      } else if(c == '*') {
        builder.append("[^/\\?&]*");
      } else {
        builder.append("\\s*[").append(c).append(']');
      }
      i++;
    }
    return Pattern.compile(builder.toString(), Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
  }
  
  public boolean hasDataPattern() { return dataPatterns != null; }
  
  public boolean hasLinkPattern() { return linkPatterns != null; }
  
  public boolean hasOnclickPattern() { return onclickPatterns != null; }
  
  public boolean hasTemplate() { return templates != null; }
  
  public boolean isDataURL(String url) { return match(dataPatterns, url) > -1; }
  
  public boolean isLinkURL(String url) { return match(linkPatterns, url) > -1; }
  
  public int isOnclickURL(String url) { return match(onclickPatterns, url); }
  
  private int match(Pattern [] patterns, String url) {
//    System.out.println("thay rang "+patterns.length);
    for(int i = 0; i< patterns.length; i++) {
      Pattern pattern = patterns[i];
//      System.out.println(pattern+ " : "+ url);
      if(pattern == null) continue;
      if(match(pattern, url)) return i;
//        System.out.println(pattern+ " : "+ url);
//        return true; }
    }
    return -1;
  }
  
  private boolean match(Pattern pattern, String url) {
    Matcher matcher = pattern.matcher(url);
    if(!matcher.find()) return false;
    int end = matcher.end();
    if(end == url.length() - 1) return true;
//    System.out.println(pattern.toString());
//    System.out.println(url);
//    System.out.println("----------------------------------------------------");
    return !(url.indexOf("/", end) > -1  
            || url.indexOf("&", end) > -1 
            || url.indexOf("#", end) > -1);
  }
  
  public String getTemplate(int idx) {
    if(templates.length <= idx) return null;
    return templates[idx]; 
  } 
  
  public String createURL(String template, String [] elements) {
    for(int i = 0; i < elements.length; i++) {
//      System.out.println("sau  => "+ value);
      template = template.replaceAll("\\{"+String.valueOf(i+1)+"\\}", elements[i]);
    }
//    System.out.println(" thanh ----->" + value);
    return template;
  }
  
//  public static void main(String[] args) {
//    URLMatcher  urlMatcher = new URLMatcher();
//    Pattern pattern = urlMatcher.createPattern("http://*.vnweblogs.com/post/*/*/*/*.html");
//    String value  = "http://1269logviet.vnweblogs.com/post/6234/46719/tong-hop/neu-ban-la-dong-song.html#comments";
//    System.out.println(urlMatcher.match(pattern, value));
//  }

}
