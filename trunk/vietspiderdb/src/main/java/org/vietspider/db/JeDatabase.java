/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import org.vietspider.common.io.LogService;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2009  
 */
public class JeDatabase {
  
  protected Environment env;
  protected Database db;
  
  protected  volatile boolean isClose = false;
  
  public void close() {
    isClose = true;
    internalClose();
  }

  public boolean isClose() { return isClose; }
  
  private void internalClose()  {
    if (db != null) {
      try {
        db.close();
      } catch (Throwable e) {
        //        e.printStackTrace();
        LogService.getInstance().setMessage("URLDATABASE" , null, e.toString());
      }
      db = null;
    }

    if (env != null) {
      try {
        env.close();
      } catch (Throwable e) {
        LogService.getInstance().setMessage("URLDATABASE" , null, e.toString());
      }
      env = null;
    }
  }

}
