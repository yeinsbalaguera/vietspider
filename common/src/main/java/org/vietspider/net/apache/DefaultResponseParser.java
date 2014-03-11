/*
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
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.conn.params.ConnConnectionPNames;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

public class DefaultResponseParser implements HttpMessageParser {

  private final SessionInputBuffer _sessionBuffer;
  private final int maxHeaderCount;
  private final int maxLineLen;
//  private final boolean timeoutSocket;
  protected final LineParser lineParser;

  private final HttpResponseFactory responseFactory;
  private final CharArrayBuffer lineBuf;
  private final int maxGarbageLines;

  public DefaultResponseParser(final SessionInputBuffer buffer,
      final LineParser parser, final HttpResponseFactory responseFactory, final HttpParams params) {
    if (buffer == null) {
      throw new IllegalArgumentException("Session input buffer may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    this._sessionBuffer = buffer;
//    this.timeoutSocket = params.getBooleanParameter("vietspider.socket.timeout", false);
    this.maxHeaderCount = params.getIntParameter(CoreConnectionPNames.MAX_HEADER_COUNT, -1);
    this.maxLineLen = params.getIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, -1);
    this.lineParser = (parser != null) ? parser : BasicLineParser.DEFAULT;

    if (responseFactory == null) {
      throw new IllegalArgumentException("Response factory may not be null");
    }

    this.responseFactory = responseFactory;
    this.lineBuf = new CharArrayBuffer(128);
    this.maxGarbageLines = params.getIntParameter(
        ConnConnectionPNames.MAX_STATUS_LINE_GARBAGE, Integer.MAX_VALUE);
  }


  protected HttpMessage parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException {
    // clear the buffer
    this.lineBuf.clear();
    //read out the HTTP status string
    int count = 0;
    ParserCursor cursor = null;

    //    long start = System.currentTimeMillis();
    //    HeaderReaderTimer timer =  null;
    //    if(timeoutSocket) timer = new HeaderReaderTimer(sessionBuffer);
    //    try {
    //      if(timer != null) new Thread(timer).start();
    do {
      int i = sessionBuffer.readLine(this.lineBuf);
      if (i == -1 && count == 0) {
        // The server just dropped connection on us
        throw new NoHttpResponseException("The target server failed to respond");
      }
      cursor = new ParserCursor(0, this.lineBuf.length());
      if (lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
        // Got one
        break;
      } else if (i == -1 || count >= this.maxGarbageLines) {
        // Giving up
        throw new ProtocolException("The server failed to respond with a valid HTTP response");
      } 
      //        else if(timer != null && timer.isDead()) {
      //          throw new NoHttpResponseException("The target server failed to respond");
      //        }
      count++;
    } while(true);
    //    } finally {
    //      if(timer != null)  timer.closeTimer();
    ////      long end = System.currentTimeMillis();
    ////      System.out.println(" ihihihi "+ sessionBuffer.hashCode()+ " / "+(end - start));
    //    }


    //create the status line from the status string
    StatusLine statusline = lineParser.parseStatusLine(this.lineBuf, cursor);
    return this.responseFactory.newHttpResponse(statusline, null);
  }

  public HttpMessage parse() throws IOException, HttpException {
    HttpMessage message = null;
    try {
      message = parseHead(this._sessionBuffer);
    } catch (ParseException px) {
      throw new ProtocolException(px.getMessage(), px);
    }
    Header[] headers = parseHeaders(
        this._sessionBuffer,  this.maxHeaderCount, this.maxLineLen, this.lineParser);
    message.setHeaders(headers);
    return message;
  }

  public static Header[] parseHeaders(final SessionInputBuffer inbuffer,
      int maxHeaderCount, int maxLineLen, LineParser parser) throws HttpException, IOException {

    if (inbuffer == null) {
      throw new IllegalArgumentException("Session input buffer may not be null");
    }

    if (parser == null) parser = BasicLineParser.DEFAULT;

    ArrayList<CharArrayBuffer> headerLines = new ArrayList<CharArrayBuffer>();

    CharArrayBuffer current = null;
    CharArrayBuffer previous = null;
    for (;;) {
      if (current == null) {
        current = new CharArrayBuffer(64);
      } else {
        current.clear();
      }
      int l = inbuffer.readLine(current);
      if (l == -1 || current.length() < 1) {
        break;
      }
      // Parse the header name and value
      // Check for folded headers first
      // Detect LWS-char see HTTP/1.0 or HTTP/1.1 Section 2.2
      // discussion on folded headers
      if ((current.charAt(0) == ' ' || current.charAt(0) == '\t') && previous != null) {
        // we have continuation folded header
        // so append value
        int i = 0;
        while (i < current.length()) {
          char ch = current.charAt(i);
          if (ch != ' ' && ch != '\t') {
            break;
          }
          i++;
        }
        if (maxLineLen > 0 
            && previous.length() + 1 + current.length() - i > maxLineLen) {
          throw new IOException("Maximum line length limit exceeded");
        }
        previous.append(' ');
        previous.append(current, i, current.length() - i);
      } else {
        headerLines.add(current);
        previous = current;
        current = null;
      }

      if (maxHeaderCount > 0 && headerLines.size() >= maxHeaderCount) {
        throw new IOException("Maximum header count exceeded");
      }
    }

    Header[] headers = new Header[headerLines.size()];
    for (int i = 0; i < headerLines.size(); i++) {
      CharArrayBuffer buffer = headerLines.get(i);
      try {
        headers[i] = parser.parseHeader(buffer);
      } catch (ParseException ex) {
        throw new ProtocolException(ex.getMessage());
      }
    }
    return headers;
  }

}