/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptTest {
    public static void main(String[] args) throws ScriptException {
        // create a script engine manager
        ScriptEngineManager manager = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine javaScriptEngine = manager.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        javaScriptEngine.eval(
            "println('Hello World from JavaScript');");
    }
}

