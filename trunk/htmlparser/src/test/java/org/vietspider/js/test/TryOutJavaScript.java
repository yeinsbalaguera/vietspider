/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.js.test;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2007  
 */
import static java.lang.System.out;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TryOutJavaScript {

   public static void main(String[] args) {
//      new TryOutJavaScript().runInlineJSCode();
      new TryOutJavaScript().runExternalJSCode();
   }

   void runInlineJSCode() {
      ScriptEngineManager scriptMgr = new ScriptEngineManager();
      ScriptEngine jsEngine = scriptMgr.getEngineByName("JavaScript");
      try {
        jsEngine.eval("print('Hello from inline exec!')");
        out.println();
      } 
      catch (ScriptException ex) {
        ex.printStackTrace();
      }
   }

   void runExternalJSCode() {
      ScriptEngineManager scriptMgr = new ScriptEngineManager();
      ScriptEngine jsEngine = scriptMgr.getEngineByName("JavaScript");
      InputStream is = this.getClass().getResourceAsStream("env.js");
      try {
        Reader reader = new InputStreamReader(is);
        out.println(jsEngine.eval(reader));
//        jsEngine.put("name", "Nhu Dinh Thuan");
//        out.println(jsEngine.get("name"));
      } 
      catch (ScriptException ex) {
        ex.printStackTrace();
      }
   }
}