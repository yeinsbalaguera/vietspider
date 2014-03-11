/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.io.GCData;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.client.HttpClientFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 28, 2008  
 */
public class VietSpiderInitService extends Thread {
  
  public VietSpiderInitService() {
    start();
  }
  
  public void run() {
    SystemProperties system = SystemProperties.getInstance();
    Application.SERVER_PROPERTIES = system.getProperties();
    
    try {
      //start mining service 
//      String startMiningValue = system.getValue(Application.START_MINING_INDEX_SERVICE);
//      startMiningValue = startMiningValue != null ? startMiningValue.trim() : null;
//      if("true".equals(startMiningValue)) TopicTrackingServices.createInstance();
      //start clean data thread
      GCData.createService();
    } catch(Exception exp){      
      LogService.getInstance().setThrowable(exp);
    }
    
    /*try {
      //start office document 
      String path = system.getValue("office.path");
      path = path != null ? path.trim() : null;
      if(path != null && path.length() > 0) DocumentConverter.createInstance(path);
    } catch(Exception exp){      
      LogService.getInstance().setThrowable(exp);
    }*/

    /*try {
      //start index service 
      String startIndexValue = system.getValue(Application.START_INDEX_DATA_SERVICE);
      startIndexValue = startIndexValue != null ? startIndexValue.trim() : null;
      if("true".equals(startIndexValue) && Application.LICENSE != Install.PERSONAL) {
        DbIndexerService.createInstance();
      }
      //start clean data thread
//      new Thread(new GCData()).start();
    } catch(Exception exp){      
      LogService.getInstance().setThrowable(exp);
    }*/

    try {
      String value = system.getValue(Application.AUTO_START_CRAWLER);
      Boolean auto = new Boolean(value.trim());
      if(auto.booleanValue()) CrawlService.getInstance().initServices();
    }catch (Exception exp) {
      LogService.getInstance().setThrowable(exp);
    }

    try {
      String value = system.getValue(Application.HTTP_CLIENT_TIMEOUT);
      HttpClientFactory.TIMEOUT = Integer.parseInt(value);
      if(HttpClientFactory.TIMEOUT > 3*60*1000) {
        HttpClientFactory.TIMEOUT = 3*60*1000;
        system.putValue(Application.HTTP_CLIENT_TIMEOUT, 
            String.valueOf(HttpClientFactory.TIMEOUT), false);
      }
    } catch (Exception exp) {
      LogService.getInstance().setThrowable(exp);
    }
    
//    TptService.getInstance();
    
//    try {
//      //start set priority service
//      new Thread(new UpdatePriorityService()).start();
//    } catch(Exception exp){      
//      LogService.getInstance().setThrowable(exp);
//    }
    
  }
}
