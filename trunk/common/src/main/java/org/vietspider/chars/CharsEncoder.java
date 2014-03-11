/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.chars;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import sun.nio.cs.HistoricallyNamedCharset;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 21, 2008  
 */
public class CharsEncoder {

  private Charset cs;
  private CharsetEncoder ce;
  private final String requestedCharsetName;

  public CharsEncoder(Charset cs, String rcn) {
    this.requestedCharsetName = rcn;
    this.cs = cs;
    this.ce = cs.newEncoder().onMalformedInput(CodingErrorAction.REPLACE)
                              .onUnmappableCharacter(CodingErrorAction.REPLACE);
  }

  String charsetName() {
    if (cs instanceof HistoricallyNamedCharset) {
      return ((HistoricallyNamedCharset)cs).historicalName();
    }
    return cs.name();
  }

  final String requestedCharsetName() { return requestedCharsetName; }

  public byte[] encode(char[] ca, int off, int len) {
    int en = scale(len, ce.maxBytesPerChar());
    byte[] ba = new byte[en];
    if (len == 0) return ba;

    ce.reset();
    ByteBuffer bb = ByteBuffer.wrap(ba);
    CharBuffer cb = CharBuffer.wrap(ca, off, len);
    try {
      CoderResult cr = ce.encode(cb, bb, true);
      if (!cr.isUnderflow()) cr.throwException();
      cr = ce.flush(bb);
      if (!cr.isUnderflow()) cr.throwException();
    } catch (CharacterCodingException x) {
      throw new Error(x);
    }
    return safeTrim(ba, bb.position());
  }

  private static int scale(int len, float expansionFactor) {
    return (int)(len * (double)expansionFactor);
  }

  private static byte[] safeTrim(byte[] ba, int len) {
    if (len == ba.length) return ba;
    return Arrays.copyOf(ba, len);
  }
}

