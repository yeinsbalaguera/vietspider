/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;

import org.vietspider.common.io.CommonFileFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2008  
 */
@SuppressWarnings("serial")
public class SourceFileFilter extends CommonFileFilter {
  
  private boolean isTemplate = false;
  
  private final char separator = '.';
  
  private final String verion = ".v.";
  
  public static final String TEMPLATE_SUFFIX = "_template";
  
  public SourceFileFilter (boolean isTemplate) {
    this.isTemplate = isTemplate;
  }
  
  public boolean accept(File f) {
    String name = f.getName();
    if(f.isDirectory() || f.length() < 1 
        || name.indexOf(separator) < 1 
        || name.indexOf(verion) > 0) return false;
    
    return  (isTemplate & name.indexOf(TEMPLATE_SUFFIX) > 0) 
           || (!isTemplate & name.indexOf(TEMPLATE_SUFFIX) < 0);
  }
}
