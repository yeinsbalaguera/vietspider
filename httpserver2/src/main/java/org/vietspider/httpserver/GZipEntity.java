/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 2, 2008  
 */
public class GZipEntity extends ByteArrayEntity {
  
  private static final String GZIP_CODEC = "gzip";

  public GZipEntity(byte [] bytes) {
    super(bytes);
  }
  
  public Header getContentEncoding() {
    return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
  }

}
