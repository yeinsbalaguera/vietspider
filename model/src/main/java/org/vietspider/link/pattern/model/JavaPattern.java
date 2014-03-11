/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class JavaPattern implements IPattern  {
  
  protected Pattern pattern;
  
  public JavaPattern() {
  }
  
  public void setValue(String...values) {
    if(values.length < 1) return;
    this.pattern = BeanCreator.toPattern(values[0]);
  }
  
  public boolean match(String value) {
    if(pattern == null) return false;
    Matcher matcher = pattern.matcher(value);
    
    if(!matcher.find()) return false;
//  http://
    if(matcher.start() != 7) return false;
    
    int end = matcher.end();
    return end >= value.length() - 1 || !Character.isLetterOrDigit(value.charAt(end+1));
  }

}
