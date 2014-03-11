/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.word;

import org.vietspider.index.analytics.PhraseData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 28, 2009  
 */
public interface PhraseFilter {
  
  public boolean isValid(PhraseData phrase) ;
  
}