/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.List;

import org.vietspider.browser.FastWebClient;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.net.channel.ISourceConfig;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 20, 2008  
 */
public interface ISourceInfo {

  public <T> T getField(String fieldName);

  public String getReferer();

  public FastWebClient getWebClient();

  public void setCharset(String charset);

  public Source createSource();

  public void setSource(Source source, int version);

  public void setIsNewSource(boolean isNewSource);

//  public String getGroup();

  public void setVisitRegions(String [] paths) ;
  public String[] getVisitRegions() ;

  public void setExtractRegions(String [] paths) ;
  public String[] getExtractRegions() ;
  
  public void setRemoveRegions(String [] paths) ;
  public String[] getRemoveRegions() ;
  
  public void setDataRegions(List<Region>  regions);
  public List<Region> getDataRegions();
  
  public boolean check();
  
  public ISourceConfig getSourceConfig() ;

}
