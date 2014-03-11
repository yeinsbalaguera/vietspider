/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.js.test;

import java.io.File;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Main;
import org.vietspider.html.HTMLDocument;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2007  
 */
public class TestJavascriptDOM {
  
  public static HTMLDocument doc;
  

  public static void main(String[] args) throws Exception {
    Context cx = Context.enter();
    
    URL url = TestJavascriptDOM.class.getResource("env.js");
    File file  = new File("F:\\Temp2\\a.html");
//    
    try {
      Scriptable scope = cx.initStandardObjects();
      Main.processFile(cx, scope, url.getFile());
//      Class<?> cl = Class.forName("org.mozilla.javascript.tools.shell.JavaPolicySecurity");
//      SecurityProxy securityImpl = (SecurityProxy)cl.newInstance();
//      SecurityController.initGlobal(securityImpl);
//      
//      String source = (String)readFileOrUrl(url.toString(), true);
//      Script script = cx.compileString(source, "<stdin>",  0, null);
//      
//      System.out.println(file.getAbsolutePath());
      String text = "window.location = 'http://www.vietnamnet.vn';";
//
      cx.evaluateString(scope, text, "<cmd>", 1, null);
      Thread.sleep(5000);
      scope.get("window", scope);
//      System.out.println("chay thu "+doc.getTextValue());
    } finally {
      Context.exit();
    }
  }
  

}
