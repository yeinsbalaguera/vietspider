/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser.form;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2009  
 */
@NodeMap("form")
public class Form {

  @NodeMap("name")
  private String name;
  @NodeMap("action")
  private String action;
  @NodeMap("method")
  private String method;

  @NodesMap(value = "params", item = "param")
  private List<Param> params  = new ArrayList<Param>();

  public Form() {
  }

  public Form(String name) {
    this.name = name;
  }



  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getAction() { return action; }
  public void setAction(String action) { this.action = action;  }

  public String getMethod() { return method; }
  public void setMethod(String method) { this.method = method;  }

  public List<Param> getParams() { return params;  }
  public void setParams(List<Param> params) { this.params = params; }

  public boolean hasParam(Param...ps) {
    for(Param param : ps) {
      if(param == null) continue;
      boolean exists = false;
      String paramName  = param.getName().trim();
      for(int i = 0; i < params.size(); i++) {
        if(params.get(i).getName().trim().equalsIgnoreCase(paramName)) {
          exists = true;
          break;
        }
      }
      if(!exists) return false;
    }
    return true;
  }

  public void putParamValue(List<Param> listParams) {
    for(Param param : listParams) {
      if(param == null) continue;
      String paramName  = param.getName().trim();
      
      if(param.getValue().equalsIgnoreCase("delete")) {
        for(int i = 0; i < params.size(); i++) {
          if(params.get(i).getName().trim().equalsIgnoreCase(paramName)) {
            params.remove(i);
            break;
          }
        }
        continue;
      }
//      System.out.println(param.getName()+ " : "+ param.getValue());
      boolean add = true;
      for(int i = 0; i < params.size(); i++) {
        if(params.get(i).getName().trim().equalsIgnoreCase(paramName)) {
          params.get(i).setValues(param.getFrom(), param.getValues());
          params.get(i).setLinkType(param.getLinkType());
          add = false;
        }
      }
      if(add) params.add(param);
    }
  }

  public Form clone() {
    Form form = new Form();
    form.setName(name);
    form.setMethod(method);
    form.setAction(action);

    List<Param> newParams  = new ArrayList<Param>();
    for(int i = 0; i < params.size(); i++) {
      newParams.add(params.get(i).clone());
    }
    form.setParams(newParams);
    return form;
  }

  public String toText() {
    StringBuilder builder = new StringBuilder();
    builder.append("name: ").append(name).append('\n');
    builder.append("method: ").append(method).append('\n');
    builder.append("action: ").append(action).append('\n').append('\n');
    for(Param param : params) {
      builder.append(param.getName()).append('=').append(param.getValue()).append('\n');
    }
    return builder.toString().trim();
  }
  
}
