/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.BasicManagedEntity;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.pool.timer.TimerMonitor;
import org.vietspider.pool.timer.TimerWorker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 15, 2008  
 */
public class HttpResponseReader implements TimerWorker {

  public static final long BIG_SIZE = 5*1024*1024l; 
  
  public final static String CONTENT_LENGTH = "Content-Length";
  
  public final static long TIMEOUT = 5*60*1000l;
  
  protected HttpEntity httpEntity;
  protected Header contentLengthHeader;
  protected long readData = 0;
  protected boolean isClose = false;
  
  public HttpResponseReader() {
  }
  
  public byte[] readBody(HttpResponse httpResponse) throws Exception  {
    if(httpResponse == null) return null;
    httpEntity = httpResponse.getEntity();
    if(httpEntity == null) return null;
    
    
    Header zipHeader = httpResponse.getFirstHeader("zip");
    boolean zip = zipHeader != null && "true".equals(zipHeader.getValue());
    
    TimerMonitor timerMonitor = new TimerMonitor(this);
    timerMonitor.startSession();
    
    contentLengthHeader = httpResponse.getFirstHeader(CONTENT_LENGTH);
    
    try {
      byte [] bytes = readBody(contentLengthHeader);
//      System.out.println(" total bytes "+ bytes);
      if(zip) bytes = new GZipIO().unzip(bytes);
      if(bytes == null || bytes.length < 1) return bytes;
//      System.out.println(" thay co "+ bytes);
      readData = bytes.length;    
      return decodeResponse(bytes, httpEntity.getContentEncoding());
    } finally {
      if(httpEntity instanceof BasicManagedEntity) {
        BasicManagedEntity entity = (BasicManagedEntity) httpEntity;
//        System.out.println(" hihi  da thay roi 2 " + entity);
        entity.releaseConnection();
      } else {
        if(httpEntity.getContent() != null) httpEntity.getContent().close();
      }
    }
  }
  
  protected byte[] readBody(Header header) throws Exception  {
    InputStream inputStream = httpEntity.getContent();
//    System.out.println(" ============  > "+ header);
    
    if(header == null) return readBody(inputStream);
    String value = header.getValue();
    if(value == null) return readBody(inputStream);

    long length = -1l;
    try {
      length = Long.parseLong(value.trim());
      if(length < 10) return readBody(inputStream);
    } catch (Exception e) {
      length = -1;
    }
    if(length >= BIG_SIZE /*|| length == 0*/) return null;
    
    if(length > 0) return readBody(inputStream, length);

    return readBody(inputStream); 
  }

  protected byte[] readBody(InputStream inputStream, long bodySize) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream((int)bodySize);
    try {
      int read = 0;
      byte [] bytes = new byte[8*1024];
//      managers.put(input, 0);
      long start = System.currentTimeMillis();
      while ((read = inputStream.read(bytes)) > -1) {
        output.write(bytes, 0, read);
//        System.out.println("dang doc data 1 "+ read + " : "+ bodySize + " : "+ output.size());
        if(isClose || output.size() >= bodySize ) break ;
        if(System.currentTimeMillis() - start >= TIMEOUT) break;
      }
//      System.out.println(" mat het 1 "+ (System.currentTimeMillis() - start));
      if(output.size() >= BIG_SIZE) output.reset();
//      System.out.println(" keke ");
    } catch (IllegalStateException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw e;
    } finally {
      isClose = true;
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }

      try {
        output.close();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
    }

    byte [] bytes  = output.toByteArray();
    output.flush();
    output.close();
//    System.out.println(" release  ");
    if(bytes.length > BIG_SIZE) return null;
    return bytes;
  }
  
  protected byte[] readBody(InputStream inputStream) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      int read = 0;
      byte [] bytes = new byte[8*1024];
//      managers.put(input, 0);
      long start = System.currentTimeMillis();
      
      while ((read = inputStream.read(bytes)) > -1) {
        output.write(bytes, 0, read);
        if(isClose || System.currentTimeMillis() - start >= TIMEOUT) break;
      }
//      System.out.println(" mat het 2 "+ (System.currentTimeMillis() - start));
      
      if(output.size() >= BIG_SIZE)  output.reset();

      inputStream.close();
//    } catch (SocketException e) {
//      System.out.println(new String(output.toByteArray(), "utf-8"));
//      System.exit(0);
    } catch (IllegalStateException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw e;
    } finally {
      isClose = true;
      try {
        if(inputStream != null) inputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        output.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
    }

    byte [] bytes  = output.toByteArray();
    output.flush();
    output.close();
    if(bytes.length > BIG_SIZE) return null;
    return bytes;
  }
  
  public byte[] decodeResponse(final byte[] bytes, Header header) throws IOException {
//    System.out.println(" vao day "+ header);
    if(header == null) return bytes;
    String encoding = header.getValue();
    if(encoding == null || (encoding = encoding.trim()).isEmpty()) return bytes;

    InputStream encodedStream = null;

    // GZIP
    if (encoding.equals ("gzip")) {
      encodedStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
    }

    // DEFLATE
    else if (encoding.equals ("deflate")) {
      encodedStream = new InflaterInputStream(new ByteArrayInputStream (bytes), new Inflater(true));
//    encodedStream = new ZipInputStream(new ByteArrayInputStream (responseBytes));
    }

    final ByteArrayOutputStream decodedStream = new ByteArrayOutputStream ();
    final byte buffer[] = new byte[1024];

    try {
      int length; 
//      System.out.println(" start ");
      while(true) {
        try {
          length = encodedStream.read(buffer, 0, 1024);
        } catch (Exception e) {
          break;
        }
//        System.out.println(" start "+ length);
        if(length < 0) break;
        decodedStream.write(buffer, 0, length);
        if(encodedStream.available() == 0) break;
      }
     /* for (int length; (length = encodedStream.read(buffer, 0, 1024)) != -1;) {
        decodedStream.write(buffer, 0, length);
        if(encodedStream.available() == 0) break;
      }*/
    } finally { 

      /*catch (EOFException e) {
      LogService.getInstance().setMessage(e, "WebClient 130: ");
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }*/

      // closing
      if(encodedStream != null) encodedStream.close ();
      decodedStream.close ();
    }

    if(decodedStream.size() < 1) return bytes;
    return decodedStream.toByteArray ();
  }

  public long getReadData() { return readData; }
  
  public void destroy() { abort(); }
  
  public void abort() {
    if(isClose || httpEntity == null) return;
//    System.out.println(" buoc phai abort " + httpEntity + " / " + " / "+ hashCode());
    isClose = true;
    try {
      if(httpEntity instanceof BasicManagedEntity) {
        BasicManagedEntity entity = (BasicManagedEntity) httpEntity;
        entity.abortConnection();
      } else {
        LogService.getInstance().setMessage(null, "http entity "+ httpEntity.getClass().getName());
        if(httpEntity.getContent() != null) httpEntity.getContent().close();
      }
//      if(httpEntity != null) httpEntity.consumeContent();
    } catch (IllegalStateException e) {
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
  }
  
  public long getTimeout() { return 5*60*1000; }

  public boolean isFree() { return false; }

  public boolean isLive() {
    return !isClose && httpEntity != null;
  }

  public boolean isPause() { return false; }

  public void newSession() {
  }
  
}
