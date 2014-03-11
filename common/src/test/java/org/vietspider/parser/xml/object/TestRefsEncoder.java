/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.xml.object;

import org.vietspider.chars.refs.RefsDecoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 26, 2009  
 */
public class TestRefsEncoder {
  public static void main(String[] args) {
    RefsDecoder encoder = new RefsDecoder();
    String text = "javascript:WebForm_DoPostBackWithOptions(new%20WebForm_PostBackOptions(\"ctl10$gc1$s$gridResults$ctl23$pagerLinkButton5\",%20\"\",%20true,%20\"\",%20\"\",%20false,%20true))";
    char [] chars = text.toCharArray();
    chars = encoder.decode(chars);
    System.out.println(new String(chars));
  }
}
