/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.search.ads;

import java.io.File;

import org.vietspider.common.io.CommonFileFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 8, 2010  
 */
@SuppressWarnings("all")
public class AdvFileFilter extends CommonFileFilter {
  public boolean accept(File f) {
    return f.getName().endsWith(".adv");
  }
}
