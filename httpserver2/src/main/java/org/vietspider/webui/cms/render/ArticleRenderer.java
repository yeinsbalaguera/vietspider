/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms.render;

import java.io.OutputStream;

import org.vietspider.bean.Article;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public interface ArticleRenderer {
  
  public void setParams(String [] params);
  
  public String write(OutputStream output, String viewer, String [] cookies, Article article, String referer) throws Exception;
  
}
