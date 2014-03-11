/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 31, 2009  
 */
public interface Generator {

  public final static short HOMEPAGE_GENERATOR = 0;
  
  public final static short SCRAN_LINK_GENERATOR = 1;
  
  public final static short CREATE_LINK_GENERATOR = 2;
  
  public final static short EXTRACT_LINK_GENERATOR = 3;
  
  public final static short FUNCTION_GENERATOR = 4;
  
  public final static short DOCUMENT_GENERATOR = 5;
  
  public final static short UPDATE_DOCUMENT_GENERATOR = 6;
  
  public final static short FUNCTION_FORM_GENERATOR = 7;

  public void generate(List<String> list);
  
}
