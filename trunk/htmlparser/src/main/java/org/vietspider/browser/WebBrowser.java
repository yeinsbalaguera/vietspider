/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Main;
import org.vietspider.common.io.LogService;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 30, 2007  
 */
public class WebBrowser {
  
  public static WebClient WEB_CLIENT = new WebClient(); 
  
  private Context context ;
  private Scriptable scope;
  
  public WebBrowser() {
    Runtime.getRuntime().addShutdownHook(new Thread () {
      public void run() {
        Context.exit();
      }
    });
    
    context = Context.enter();
    URL url = WebBrowser.class.getResource("env.js");
    
    try {
      scope = context.initStandardObjects();
      Main.processFile(context, scope, url.getFile());
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
