/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import org.vietspider.link.pattern.model.IPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2008  
 */
public class AbsPatterns<T extends IPattern> {
  
  protected T [] patterns;
  
  public AbsPatterns(T [] patterns) {
    this.patterns = patterns;
  }
  
  public T[] getPatterns() { return patterns; }
  
//  public static abstract class AbsPattern {
//    
//    protected Pattern pattern;
////    protected boolean endWith = false;
//    
//    public AbsPattern() {
//    }
//    
//    public void setPattern(Pattern pattern) {
//      this.pattern = pattern;
//    }
//
////    public boolean isEndWith() { return endWith; }
//
////  /  public void setEndWith(boolean endWith) { this.endWith = endWith; }
//
//    public Pattern getPattern() { return pattern;    }
//
//  }
  
}
