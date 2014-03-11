/*
 * $HeadURL: https://svn.apache.org/repos/asf/httpcomponents/httpcore/tags/4.0.1/httpcore/src/main/java/org/apache/http/impl/io/AbstractSessionInputBuffer.java $
 * $Revision: 744526 $
 * $Date: 2009-02-14 18:04:18 +0100 (Sat, 14 Feb 2009) $
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.vietspider.net.apache;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

/**
 * Abstract base class for session input buffers that stream data from 
 * an arbitrary {@link InputStream}. This class buffers input data in 
 * an internal byte array for optimal input performance.
 * <p>
 * {@link #readLine(CharArrayBuffer)} and {@link #readLine()} methods of this 
 * class treat a lone LF as valid line delimiters in addition to CR-LF required
 * by the HTTP specification. 
 *
 * @since 4.0
 */
public abstract class AbstractSessionInputBuffer implements SessionInputBuffer {

  private InputStream instream;
  private byte[] buffer;
  private int bufferpos;
  private int bufferlen;

  private ByteArrayBuffer linebuffer = null;

  private String charset = HTTP.US_ASCII;
  private boolean ascii = true;
  private int maxLineLen = -1;

//  private boolean timeoutSocket;

  private HttpTransportMetricsImpl metrics;

  protected void init(final InputStream _instream, int buffersize, final HttpParams params) {
    if (_instream == null) {
      throw new IllegalArgumentException("Input stream may not be null");
    }
    if (buffersize <= 0) {
      throw new IllegalArgumentException("Buffer size may not be negative or zero");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    this.instream = _instream;
    this.buffer = new byte[buffersize];
    this.bufferpos = 0;
    this.bufferlen = 0;
    this.linebuffer = new ByteArrayBuffer(buffersize);
    this.charset = HttpProtocolParams.getHttpElementCharset(params);
//    this.timeoutSocket = params.getBooleanParameter("vietspider.socket.timeout", false);
    this.ascii = this.charset.equalsIgnoreCase(HTTP.US_ASCII) || this.charset.equalsIgnoreCase(HTTP.ASCII);
    this.maxLineLen = params.getIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, -1);
    this.metrics = new HttpTransportMetricsImpl();
  }

  protected int fillBuffer() throws IOException {
    if (this.bufferpos > 0) {
      int len = this.bufferlen - this.bufferpos;
      if (len > 0) {
        System.arraycopy(this.buffer, this.bufferpos, this.buffer, 0, len);
      }
      this.bufferpos = 0;
      this.bufferlen = len;
    }
    int l;
    int off = this.bufferlen;
    int len = this.buffer.length - off;
    l = this.instream.read(this.buffer, off, len);
    if (l == -1) return -1;
    this.bufferlen = off + l;
    this.metrics.incrementBytesTransferred(l);
    return l;
  }

  protected boolean hasBufferedData() { return this.bufferpos < this.bufferlen; }

  public int read() throws IOException {
    int noRead = 0;
    while (!hasBufferedData()) {
      noRead = fillBuffer();
      if (noRead == -1) return -1;
    }
    return this.buffer[this.bufferpos++] & 0xff;
  }

  public int read(final byte[] b, int off, int len) throws IOException {
    if (b == null) return 0;
    int noRead = 0;
    while (!hasBufferedData()) {
      noRead = fillBuffer();
      if (noRead == -1) return -1;
    }
    int chunk = this.bufferlen - this.bufferpos;
    if (chunk > len) chunk = len;
    System.arraycopy(this.buffer, this.bufferpos, b, off, chunk);
    this.bufferpos += chunk;
    return chunk;
  }

  public int read(final byte[] b) throws IOException {
    return (b == null) ? 0 : read(b, 0, b.length);
  }

  private int locateLF() {
    for (int i = this.bufferpos; i < this.bufferlen; i++) {
      if (this.buffer[i] == HTTP.LF) return i;
    }
    return -1;
  }

  public int readLine(final CharArrayBuffer charbuffer) throws IOException {
    if (charbuffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    this.linebuffer.clear();
    int noRead = 0;
    //    HeaderReaderTimer2 timer = new HeaderReaderTimer2(instream);
    boolean retry = true;
    //    try {
    //      if(timeoutSocket) new Thread(timer).start();
    //      while (timer.isLive()) {
    while(retry) {
      // attempt to find end of line (LF)
      int i = locateLF();
      if (i != -1) {
        // end of line found. 
        if (this.linebuffer.isEmpty()) {
          // the entire line is preset in the read buffer
          return lineFromReadBuffer(charbuffer, i);
        }
        retry = false;
        //          timer.closeTimer();
        int len = i + 1 - this.bufferpos;
        this.linebuffer.append(this.buffer, this.bufferpos, len);
        this.bufferpos = i + 1;
      } else {
        // end of line not found
        if (hasBufferedData()) {
          int len = this.bufferlen - this.bufferpos;
          this.linebuffer.append(this.buffer, this.bufferpos, len);
          this.bufferpos = this.bufferlen;
        }
        noRead = fillBuffer();
        if (noRead == -1) retry = false;/*timer.closeTimer(); */
      }

      if (this.maxLineLen > 0 && this.linebuffer.length() >= this.maxLineLen) {
        throw new IOException("Maximum line length limit exceeded");
      }
    }
    //    } finally {
    //      timer.closeTimer();
    //    }
    if (noRead == -1 && this.linebuffer.isEmpty()) return -1;
    return lineFromLineBuffer(charbuffer);
  }

  private int lineFromLineBuffer(final CharArrayBuffer charbuffer)  throws IOException {
    // discard LF if found
    int l = this.linebuffer.length(); 
    if (l > 0) {
      if (this.linebuffer.byteAt(l - 1) == HTTP.LF) {
        l--;
        this.linebuffer.setLength(l);
      }
      // discard CR if found
      if (l > 0) {
        if (this.linebuffer.byteAt(l - 1) == HTTP.CR) {
          l--;
          this.linebuffer.setLength(l);
        }
      }
    }
    l = this.linebuffer.length(); 
    if (this.ascii) {
      charbuffer.append(this.linebuffer, 0, l);
    } else {
      String s = new String(this.linebuffer.buffer(), 0, l, this.charset);
      charbuffer.append(s);
    }
    return l;
  }

  private int lineFromReadBuffer(final CharArrayBuffer charbuffer, int pos) throws IOException {
    int off = this.bufferpos;
    int len;
    this.bufferpos = pos + 1;
    if (pos > 0 && this.buffer[pos - 1] == HTTP.CR) {
      // skip CR if found
      pos--;
    }
    len = pos - off;
    if (this.ascii) {
      charbuffer.append(this.buffer, off, len);
    } else {
      String s = new String(this.buffer, off, len, this.charset);
      charbuffer.append(s);
    }
    return len;
  }

  public String readLine() throws IOException {
    CharArrayBuffer charbuffer = new CharArrayBuffer(64);
    int l = readLine(charbuffer);
    return (l != -1) ? charbuffer.toString() : null;
  }

  public HttpTransportMetrics getMetrics() { return this.metrics; }
}
