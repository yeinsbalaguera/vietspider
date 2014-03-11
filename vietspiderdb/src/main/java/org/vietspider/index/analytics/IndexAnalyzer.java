/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index.analytics;

import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 18, 2010  
 */
public interface IndexAnalyzer {

  public List<PhraseData> analyze(String text) ;
  
}
