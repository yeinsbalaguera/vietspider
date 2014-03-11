/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

/** 
 *          nhudinhthuan@yahoo.com
 * Apr 22, 2008  
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.vietspider.common.io.LogService;

/**
 * Wrapping entity that compresses content when {@link #writeTo writing}.
 *
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 *
 * <!-- empty lines above to avoid 'svn diff' context problems -->
 * @version $Revision$
 * 
 * @since 4.0
 */
public class GzipCompressingEntity extends HttpEntityWrapper {

  private static final String GZIP_CODEC = "gzip";

  private ByteArrayOutputStream  byteArrayStream;

  public GzipCompressingEntity(final HttpEntity entity) {
    super(entity);

    byteArrayStream = new ByteArrayOutputStream();

    try {
      GZIPOutputStream gzip = new GZIPOutputStream(byteArrayStream);
      InputStream in = entity.getContent();
      byte[] tmp = new byte[2048];
      int pos;
      while ((pos = in.read(tmp)) != -1) {
        gzip.write(tmp, 0, pos);
      }
      gzip.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
  }

  public Header getContentEncoding() {
    return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
  }

  public long getContentLength() { return byteArrayStream.size(); }

  public boolean isChunked() { return false; }

  public void writeTo(final OutputStream outstream) throws IOException {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    outstream.write(byteArrayStream.toByteArray());
  }

} // class GzipCompressingEntity
