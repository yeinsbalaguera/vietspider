/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;
import java.io.File;

import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.common.Application;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;
import org.vietspider.serialize.XML2Unknown;
import org.vietspider.token.TypeToken;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 14, 2006
 */
@SuppressWarnings("serial")
@NodeMap("database")
public class DBScripts extends XMLProperties {
  
  public DBScripts() {
  }
  
  public DBScripts(File file) throws Exception {
    super(file, "database",  false);   
  }
  
  public DBScripts(File file, String name) throws Exception {
    super(file, name,  false);   
  }
  
  public void toProperties(XMLDocument xmlDocument, String name, boolean readonly) throws Exception {
    XML2Unknown mapper = XML2Unknown.getInstance();
    mapper.toObject(DBScripts.class, this, xmlDocument.getRoot().getChild(0));    
    super.toProperties(xmlDocument, name, readonly);
  }
  
  public byte [] getBytes() throws Exception {
    XMLNode node = getNode(document.getRoot(), "inited");
    node.getChildren().clear();
    String value = String.valueOf(inited);
    node.addChild(new XMLNode(value.toCharArray(), null, TypeToken.CONTENT));
    
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    document.getRoot().getChild(0).buildValue(builder, new RefsEncoder());
    return builder.toString().getBytes(Application.CHARSET);
  }
      
  @NodeMap(value = "inited")
  private boolean inited;
  @GetterMap("inited")
  public boolean getInited() { return inited; }
  @SetterMap("inited")
  public void setInited(boolean value) { this.inited = value; }
  
  @NodeMap("scripts")
  private String[] initDB ;
  @GetterMap("scripts")
  public String[] getInitDB() { return initDB; }
  @SetterMap("scripts")
  public void setInitDB(String[] initDB) { this.initDB = initDB; }
 
}

