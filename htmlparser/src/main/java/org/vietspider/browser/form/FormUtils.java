/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 14, 2009  
 */
public class FormUtils {
  
  public final static String TYPE_ATTR = "type";
  public final static String PASSWORD = "password";
  
  public final static String ID_ATTR = "id";
  public final static String NAME_ATTR = "name";
  public final static String VALUE = "value";
  public final static String ACTION = "action";
  
  public final static int ERROR = -1;
  public final static int LOGIN = 0;
  public final static int SUCCESSFULL = 1;
  public final static int TIMEOUT = 2;
  
  public List<Form> searchForm(List<NodeImpl>  tokens) {
    List<Form> forms = new ArrayList<Form>();
    
    Form form = null;
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      
      if(node.isNode(Name.FORM)) {
//        System.out.println("== > thay "+ node + " : "+ node.isOpen()  );
        if(node.getType() == TypeToken.TAG) {
          if(form != null) forms.add(form);
          form = new Form();
          Attributes attributes = node.getAttributes();
          Attribute attribute = attributes.get(NAME_ATTR);
          if(attribute != null) form.setName(attribute.getValue());
          attribute = attributes.get("method");
          if(attribute != null) form.setMethod(attribute.getValue());
          attribute = attributes.get(ACTION);
          if(attribute != null) form.setAction(attribute.getValue());
        } else {
          if(form != null) forms.add(form);
          form = null;
        }
      } else if(node.isNode(Name.INPUT)
          || node.isNode(Name.TEXTAREA) 
          || node.isNode(Name.SELECT)) {        
        if(form == null) continue;
        
        Attributes attributes = node.getAttributes();
        Attribute attribute = attributes.get(NAME_ATTR);
        if(attribute == null) attribute = attributes.get(ID_ATTR);;
        if(attribute == null) continue;
        String name  = attribute.getValue();
        if(name == null || name.trim().isEmpty()) continue;
        
        attribute = attributes.get(TYPE_ATTR);
        String type = null;
        if(attribute != null) type = attribute.getValue();
        if(type == null) type = "text";
        type = type.trim();
        
        if(type.equalsIgnoreCase("submit")) {
          if(name.toLowerCase().indexOf("cancel") > -1
              || name.toLowerCase().indexOf("reset") > -1
              || name.toLowerCase().indexOf("clear") > -1) continue;
        }
        
        if("radio".equalsIgnoreCase(type) 
            || "checkbox".equalsIgnoreCase(type)) {
          attribute = attributes.get("checked");
          if(attribute == null) continue;
        }
        
        attribute = attributes.get(VALUE);
        String value = null;
        if(attribute != null) value = attribute.getValue();
        if(value == null) value = "";
        value  = value.trim();
        
        boolean newInput = true;
        for(int k = 0; k < form.getParams().size(); k++) {
          Param param = form.getParams().get(k);
          if(param.getName().equalsIgnoreCase(name) 
              && param.getType().equalsIgnoreCase(type)) {
            param.addValue(Param.FORM, value);
            newInput = false;
          }
        }
        if(!newInput) continue;
        Param param = new Param();
        param.setName(name);
        param.setType(type);
        param.addValue(Param.FORM, value);
        form.getParams().add(param);
      } 
    }
    if(form != null && !forms.contains(form)) forms.add(form);
    return forms;
  }
  
  public List<List<Param>> toMatrixParams(List<Param> list) {
    List<List<Param>> listParams = new ArrayList<List<Param>>();
    listParams.add(new ArrayList<Param>());
    for(int i = 0; i < list.size(); i++) {
      deplicate(listParams, list.get(i)); 
    }
    return listParams;
  }
  
  public void deplicate(List<List<Param>> list, Param param) {
    Set<String> values = param.getValues();
    if(values.size() < 0) values.add("");
    List<List<Param>> listCloneParams = new ArrayList<List<Param>>();
    String [] items = values.toArray(new String[values.size()]);
    for(int l = 0; l < list.size(); l++) {
      List<Param> _params = list.get(l);
      Param newParam = new Param(param.getName(), param.getFrom(), items[0]);
      newParam.setType(param.getType());
      _params.add(newParam);
      
      for(int i = 1; i < items.length; i++) {
        List<Param> cloneParams = cloneList(_params);
        for(int z = 0; z < cloneParams.size(); z++) {
          if(cloneParams.get(z).getName().equalsIgnoreCase(param.getName())) {
            cloneParams.get(z).getValues().add(items[i]);
            break;
          }
        }
        listCloneParams.add(cloneParams);
      }
    }
    list.addAll(listCloneParams);
  }
  
  private List<Param> cloneList(List<Param> list) {
    List<Param> newList = new ArrayList<Param>();
    for(int i = 0; i < list.size(); i++) {
      newList.add(list.get(i));
    }
    return newList;
  }
  
 /* public static void main(String[] args) {
    FormUtils utils = new FormUtils();
    
    List<Param> params = new ArrayList<Param>();
    Param param = new Param("a", "value_a_1");
    param.addValue("value_a_2");
    param.addValue("value_a_3");
    params.add(param);
    
    param = new Param("b", "value_b_1");
    param.addValue("value_b_2");
    params.add(param);
    
    param = new Param("c", "value_c_1");
    params.add(param);
    
    List<List<Param>> matrix = utils.toMatrixParams(params);
    System.out.println(matrix.size());
    for(int i = 0; i < matrix.size(); i++) {
      FormLinkExtrator.printTextParam(matrix.get(i));
    }
  }
  
  */
  

}
