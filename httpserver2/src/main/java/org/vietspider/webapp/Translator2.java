/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webapp;

import java.io.File;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2011  
 */
public class Translator2 {
  
  protected HTMLParser2 parser;
  private TranslateMode config;
  
  private TranslatorMachine [] machines;
  
  public Translator2() {
    try {
      File file = new File(UtilFile.getFolder("system/plugin/"), "bing.translation.config");
      if(file.exists() && file.length() > 0) {
        byte [] bytes = RWData.getInstance().load(file);
        config = XML2Object.getInstance().toObject(TranslateMode.class, bytes);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }
    if(config == null) return;
    parser = new HTMLParser2();
    machines = new TranslatorMachine[] {
      new GoogleWebTranslator(config),
      new BingWebTranslator(config),
      new BingAPITranslator(config)
    };
  }

  public String[] translate(String title, String desc, String content,
      String from, String to, StringBuilder indexBuilder)  {
    if(config == null) return null;

    //    System.out.println(" ==================  > to : "+ to);
    //    System.out.println(" ==================  > from : "+ from);

    if(from == null) from = config.getFrom();
    if(to == null) to = config.getTo();

    try {
      List<NodeImpl> nodes = parser.createTokens(content.toCharArray());

      String newTitle = translate(title, from, to);
      indexBuilder.append(newTitle);
      
      String newDesc = translate(desc, from, to);
      indexBuilder.append(newDesc);

      for(int i = 0; i < nodes.size(); i++) {
        NodeImpl node = nodes.get(i);
        if(node.getName() != Name.CONTENT) continue;
        String text = new String(node.getValue());
        String newText = translate(text, from, to);
        
        indexBuilder.append('\n').append(newText);
        
        if(config.getMode() == TranslateMode.PARAGRAPH) {
          newText = "<i>" + text + "</i> <br/><br/> " + newText;
        } 
        node.setValue(newText.toCharArray());
        
      }

      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < nodes.size(); i++) {
        NodeImpl node = nodes.get(i);
        int type = node.getType();
        Name name = node.getName();
        boolean isTag = name != Name.CONTENT  && name != Name.COMMENT && name != Name.CODE_CONTENT;
        if(isTag) builder.append('<');
        if(type == TypeToken.CLOSE) builder.append('/');
        builder.append(node.getValue());
        if(isTag) builder.append('>');
        if(type == TypeToken.CLOSE || node.getConfig().hidden()) continue;
      }

      String [] values = null; 
      if(config.getMode() == TranslateMode.PARAGRAPH) {
        values = new String[] {
            "<i>" + title + "</i><br/><br/>" + newTitle, 
            "<i>" + desc + "</i><br/><br/>" + newDesc,
            builder.toString() 
        };

      } else  if(config.getMode() == TranslateMode.COUPLE) {
        values = new String[] {
            newTitle + "<br/><br/><i>" + title + "</i>", 
            newDesc + "<br/><br/><i>" + desc + "</i>", 
            builder.toString() + "<br/><br/><br/><i>" + content + "</i>" 
        };
      } else {
        values = new String[]{newTitle, newDesc, builder.toString()};
      }
      return values;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  private String translate(String text, String from, String to) {
    if(text == null) return text;
    text = text.trim();
    if(text.length() < 1) return text;
    String error = "Error!";
    for(int i = 0; i < machines.length; i++) {
      try {
        String value = machines[i].translate(text, from, to);
        if(value == null) continue;
        value = value.trim();
        if(text.length() > 0 && value.length() < 1) continue;
        return value;
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
        error = e.toString();
      }
    }
    
    return error;
  }
  

  
}
