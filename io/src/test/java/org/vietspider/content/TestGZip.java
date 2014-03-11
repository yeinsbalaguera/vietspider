/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class TestGZip {
  private static  byte [] compress(byte [] bytes) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    try {
      GZIPOutputStream gzip = new GZIPOutputStream(byteArrayStream);
      gzip.write(bytes, 0, bytes.length);
      gzip.close();
      return byteArrayStream.toByteArray();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
      return bytes;
    }
  }

  private static byte [] uncompress(byte [] bytes) {
    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      GZIPInputStream gzip = new GZIPInputStream(input);
      byte[] tmp = new byte[2048];
      int read;
      while ((read = gzip.read(tmp)) != -1) {
        output.write(tmp, 0, read);
      }
      gzip.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
      return bytes;
    }
    return output.toByteArray();
  }
  public static void main(String[] args) throws Exception {
    String value  = "Số tiến sỹ này mà dựng bia giống ở Văn Miếu chắc phải ra bãi Sông Hồng mới đủ chỗ";
    byte [] bytes = value.getBytes("utf-8");
    bytes = compress(bytes);
    bytes = uncompress(bytes);
    System.out.println(new String(bytes, "utf-8"));
  }
}
