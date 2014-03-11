/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2009  
 */
public class ParameterParser {
  
  public static String[][] getParameters(String text) throws Exception {
    List<String> list = new ArrayList<String>();
    int idx  = 0;
    int start = 0;
    int length = text.length();
    while(idx < length) {
      char c = text.charAt(idx);
      if(c == '&') {
        list.add(text.substring(start, idx));
        start = idx+1;
      }
      idx++;
    }
    if(start < length) list.add(text.substring(start, length));
    String [][] parameters = new String[list.size()][2];
    length = list.size();
    for(int i = 0; i < length; i++) {
      String param = list.get(i);
      idx = param.indexOf('=');
      if(idx < 0) {
        parameters[i][0] = param; 
        parameters[i][1] = null;
        continue;
      }
      
      parameters[i][0] = param.substring(0, idx); 
      parameters[i][1] = param.substring(idx+1, param.length());
      parameters[i][1] = URLDecoder.decode(parameters[i][1], Application.CHARSET);
    }
    return parameters;
  }
  
}
