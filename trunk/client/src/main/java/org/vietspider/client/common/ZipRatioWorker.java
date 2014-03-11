/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import org.vietspider.common.io.GZipIO.ZipProgress;
import org.vietspider.common.util.RatioWorker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public abstract class ZipRatioWorker extends RatioWorker implements ZipProgress {

  public void setValue(int value) {
    super.increaseRatio(value);
  }
}
