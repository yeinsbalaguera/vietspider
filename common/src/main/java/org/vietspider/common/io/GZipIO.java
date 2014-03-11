/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2008  
 */
public class GZipIO {

  public byte[] zip(byte [] bytes) {
    if(bytes == null) return bytes;
    try {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      new GZipIO().zip(input, output);
      byte[] newBytes =  output.toByteArray();
      if(newBytes.length >= bytes.length) return bytes;
      return newBytes;
    } catch (Exception e) {
      return bytes;
    }
  }
  
  public byte[] load(InputStream contentInput) throws Exception {
    GZIPInputStream gzip = null;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      gzip = new GZIPInputStream(contentInput);
      byte[] tmp = new byte[8*1024];
      int read;
      while ((read = gzip.read(tmp)) != -1) {
//        System.out.println(" vao day "+ read);
        output.write(tmp, 0, read);
        output.flush();
      }
    } catch (Exception e) {
      byte [] bytes = new byte[1024];
      int read = 0;
      while((read = contentInput.read(bytes)) > -1) {
        output.write(bytes, 0, read);
        output.flush();
      }
      output.flush();
    } finally {
      contentInput.close();
    }
    return output.toByteArray();
  }

  public void load(InputStream contentInput,
      OutputStream output, ZipProgress worker) throws Exception {
    GZIPInputStream gzip = null;
    try {
      gzip = new GZIPInputStream(contentInput);
      byte[] tmp = new byte[8*1024];
      int read;
      while ((read = gzip.read(tmp)) != -1) {
        output.write(tmp, 0, read);
        output.flush();
        if(worker != null) worker.setValue(read);
      }
    } catch (Exception e) {
      byte [] bytes = new byte[1024];
      int read = 0;
      while((read = contentInput.read(bytes)) > -1) {
        output.write(bytes, 0, read);
        output.flush();
        if(worker != null) worker.setValue(read);
      }
      output.flush();
    } finally {
      contentInput.close();
    }
  }

  public byte[] unzip(byte [] bytes) throws Exception {
    if(bytes == null || bytes.length < 1) return bytes;
    GZIPInputStream gzip = null;
    try {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      gzip = new GZIPInputStream(input);
      byte[] tmp = new byte[2048];
      int read;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      while ((read = gzip.read(tmp)) != -1) {
        output.write(tmp, 0, read);
      }
      return output.toByteArray();
    } catch (Exception e) {
      return bytes;
    } finally {
      try {
        if(gzip != null) gzip.close();        
      } catch (Exception e) {
      }
    }
  }

  public void zip(InputStream input, OutputStream output) throws Exception {
    GZIPOutputStream gzip = new GZIPOutputStream(output);
    byte[] tmp = new byte[2048];
    int read;
    while ((read = input.read(tmp)) != -1) {
      gzip.write(tmp, 0, read);
    }
    gzip.close();
  }

  public void unzip(OutputStream output, InputStream input, ZipProgress progress) throws Exception {
    GZIPInputStream gzip = new GZIPInputStream(input);
    byte[] tmp = new byte[2048];
    int read;
    while ((read = gzip.read(tmp)) != -1) {
      output.write(tmp, 0, read);
      if(progress != null) progress.setValue(read);
    }
    gzip.close();
  }

  public static interface ZipProgress {

    public void setValue(int value);

  }

}
