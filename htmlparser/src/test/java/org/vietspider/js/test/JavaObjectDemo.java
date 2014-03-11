/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.js.test;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2007  
 */
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public class JavaObjectDemo {  
  public static void main(String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine jsEngine;  
    jsEngine = manager.getEngineByExtension("js");
    jsEngine.eval("importPackage(javax.swing.JOptionPane);"
        +"var optionPane = JOptionPane.showMessageDialog(null, 'Hello!);");
  }
}
