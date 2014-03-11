/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.parser;

import org.vietspider.chars.jchardet.nsDetector;
import org.vietspider.chars.jchardet.nsICharsetDetectionObserver;
import org.vietspider.chars.jchardet.nsPSMDetector;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 27, 2009  
 */
public class EncodingDetector {
  
  protected String charset_;
  
  public String detect(byte [] buf){
    nsDetector det = new nsDetector(nsPSMDetector.ALL) ;
    charset_ = null;
    det.Init(new nsICharsetDetectionObserver() {
      @Override
      public void Notify(String charset) {
        charset_ = charset;
        
      }
    });

    boolean isAscii = true ;
    int len = buf.length;
    
    isAscii = det.isAscii(buf, len);   
    if (!isAscii) det.DoIt(buf, len, false);
    det.DataEnd();
    
    if (isAscii) charset_ = "ASCII";
    return charset_;
  }
  
  public String getCharset(){ return charset_; }
}
