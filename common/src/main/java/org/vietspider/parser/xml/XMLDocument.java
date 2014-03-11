/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.parser.xml;

import org.vietspider.chars.XMLDataEncoder;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLDocument  {
  
  private XMLNode root;
  
  private XMLNode xmlType ;
  
  public XMLDocument(XMLNode root){  this.root = root; }  
  
  public XMLNode getRoot(){ return root; }

  public XMLNode getXmlType() { return xmlType; }

  public void setXmlType(XMLNode xmlType) { this.xmlType = xmlType; }
  
  public String getTextValue() { return getTextValue(null); }
  
  public String getTextValue(XMLDataEncoder encoder){
    StringBuilder builder = new StringBuilder();
    if(xmlType != null){
      builder.append('<').append(xmlType.getNodeValue()).append(">\n");
    }else{
      builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    }    
    root.buildValue(builder, encoder);    
    return builder.toString();
  }

}
