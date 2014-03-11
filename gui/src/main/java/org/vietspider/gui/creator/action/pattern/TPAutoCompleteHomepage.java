/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action.pattern;

import java.net.URL;

import org.vietspider.common.text.SWProtocol;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.gui.creator.URLTemplate;
import org.vietspider.gui.creator.URLWidget;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 13, 2009  
 */
public class TPAutoCompleteHomepage implements TextPatternModify {

  @Override
  public void execute(ISourceInfo iSourceInfo, String address) {
    URLWidget txtHome = iSourceInfo.<URLWidget>getField("txtHome");
    if(txtHome.getText().trim().length() < 1 
        && txtHome.getItems() != null && txtHome.getItems().length < 1)  {
      try {
        URL url = new URL(address);
        txtHome.setItem(SWProtocol.HTTP+url.getHost());
      } catch(Exception exp) {
      }
    }
    URLTemplate templateDataLink = iSourceInfo.<URLTemplate>getField("templateDataLink");
    if(templateDataLink.getItemCount() < 1) {
      templateDataLink.putText(address);
    }
    
  }

}
