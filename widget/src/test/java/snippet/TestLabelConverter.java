/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package snippet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 3, 2008  
 */
public class TestLabelConverter {

  public static void main(String[] args) throws  Exception {
    File file  = new File("D:\\Temp\\en\\");
    File [] files = file.listFiles();
    for(int i = 0; i < files.length; i++) {
      StringBuilder builder = new StringBuilder();
      byte [] bytes = RWData.getInstance().load(files[i]);
      String [] elements = new String(bytes, "utf-8").split("\n");
      char[] convtBuf = new char[1024];
      
      for(String ele : elements) {
        if(builder.length() > 0) builder.append('\n'); 
        if(ele.trim().isEmpty()) {
//          builder.append('\n');
          continue;
        }
        String [] data = ele.split("=");
        if(data.length < 2) {
          builder.append(data[0]).append('=');
        } else {
          char [] chars = data[1].toCharArray();
          String value = loadConvert(chars, 0, chars.length, convtBuf);
          builder.append(data[0]).append('=').append(value);
        }
        
      }
//      load0(builder, new LineReader(builder, new FileInputStream(files[i])));
      files[i].delete();
      org.vietspider.common.io.RWData.getInstance().save(files[i], builder.toString().getBytes("utf-8"));
    }
  }


  private static void load0 (StringBuilder builder, LineReader lr) throws IOException {
    char[] convtBuf = new char[1024];
    int limit;
    int keyLen;
    int valueStart;
    char c;
    boolean hasSep;
    boolean precedingBackslash;

    while ((limit = lr.readLine()) >= 0) {
      c = 0;
      keyLen = 0;
      valueStart = limit;
      hasSep = false;

      //System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
      precedingBackslash = false;
      while (keyLen < limit) {
        c = lr.lineBuf[keyLen];
        //need check if escaped.
        if ((c == '=' ||  c == ':') && !precedingBackslash) {
          valueStart = keyLen + 1;
          hasSep = true;
          break;
        } else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
          valueStart = keyLen + 1;
          break;
        } 
        if (c == '\\') {
          precedingBackslash = !precedingBackslash;
        } else {
          precedingBackslash = false;
        }
        keyLen++;
      }
      while (valueStart < limit) {
        c = lr.lineBuf[valueStart];
        if (c != ' ' && c != '\t' &&  c != '\f') {
          if (!hasSep && (c == '=' ||  c == ':')) {
            hasSep = true;
          } else {
            break;
          }
        }
        valueStart++;
      }
      String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
      System.out.println(key);
      String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
      if(value == null) value = "";
      if(builder.length() > 0) builder.append('\n');
      builder.append(key).append('=').append(value);
    }
  }

  /* Read in a "logical line" from an InputStream/Reader, skip all comment
   * and blank lines and filter out those leading whitespace characters 
   * (\u0020, \u0009 and \u000c) from the beginning of a "natural line". 
   * Method returns the char length of the "logical line" and stores 
   * the line in "lineBuf". 
   */
  static class LineReader { 
    
    private StringBuilder builder;
    public LineReader(StringBuilder builder, InputStream inStream) {
      this.inStream = inStream;
      this.builder = builder;
      inByteBuf = new byte[8192]; 
    }

    public LineReader(Reader reader) {
      this.reader = reader;
      inCharBuf = new char[8192]; 
    }

    byte[] inByteBuf;
    char[] inCharBuf;
    char[] lineBuf = new char[1024];
    int inLimit = 0;
    int inOff = 0;
    InputStream inStream;
    Reader reader;

    int readLine() throws IOException {
      int len = 0;
      char c = 0;

      boolean skipWhiteSpace = true;
      boolean isCommentLine = false;
      boolean isNewLine = true;
      boolean appendedLineBegin = false;
      boolean precedingBackslash = false;
      boolean skipLF = false;

      while (true) {
        if (inOff >= inLimit) {
          inLimit = (inStream==null)?reader.read(inCharBuf)
              :inStream.read(inByteBuf);
          inOff = 0;
          if (inLimit <= 0) {
            if (len == 0 || isCommentLine) { 
              return -1; 
            }
            return len;
          }
        }     
        if (inStream != null) {
          //The line below is equivalent to calling a 
          //ISO8859-1 decoder.
          c = (char) (0xff & inByteBuf[inOff++]);
        } else {
          c = inCharBuf[inOff++];
        }
        if (skipLF) {
          skipLF = false;
          if (c == '\n') {
            continue;
          }
        }
        if (skipWhiteSpace) {
          if (c == ' ' || c == '\t' || c == '\f') {
            continue;
          }
          if (!appendedLineBegin && (c == '\r' || c == '\n')) {
            if(builder.charAt(builder.length()-1) == '\r') {
              builder.append('\n');
            }
            continue;
          }
          skipWhiteSpace = false;
          appendedLineBegin = false;
        }
        if (isNewLine) {
          isNewLine = false;
          if (c == '#' || c == '!') {
            isCommentLine = true;
            continue;
          }
        }

        if (c != '\n' && c != '\r') {
          lineBuf[len++] = c;
          if (len == lineBuf.length) {
            int newLength = lineBuf.length * 2;
            if (newLength < 0) {
              newLength = Integer.MAX_VALUE;
            }
            char[] buf = new char[newLength];
            System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
            lineBuf = buf;
          }
          //flip the preceding backslash flag
          if (c == '\\') {
            precedingBackslash = !precedingBackslash;
          } else {
            precedingBackslash = false;
          }
        }
        else {
          // reached EOL
          if (isCommentLine || len == 0) {
            isCommentLine = false;
            isNewLine = true;
            skipWhiteSpace = true;
            len = 0;
            continue;
          }
          if (inOff >= inLimit) {
            inLimit = (inStream==null)
            ?reader.read(inCharBuf)
                :inStream.read(inByteBuf);
            inOff = 0;
            if (inLimit <= 0) {
              return len;
            }
          }
          if (precedingBackslash) {
            len -= 1;
            //skip the leading whitespace characters in following line
            skipWhiteSpace = true;
            appendedLineBegin = true;
            precedingBackslash = false;
            if (c == '\r') {
              skipLF = true;
            }
          } else {
            return len;
          }
        }
      }
    }
  }

  private static String loadConvert (char[] in, int off, int len, char[] convtBuf) {
    if (convtBuf.length < len) {
      int newLen = len * 2;
      if (newLen < 0) {
        newLen = Integer.MAX_VALUE;
      } 
      convtBuf = new char[newLen];
    }
    char aChar;
    char[] out = convtBuf; 
    int outLen = 0;
    int end = off + len;

    while (off < end) {
      aChar = in[off++];
      if (aChar == '\\') {
        aChar = in[off++];   
        if(aChar == 'u') {
          // Read the xxxx
          int value=0;
          for (int i=0; i<4; i++) {
            aChar = in[off++];  
            switch (aChar) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
              value = (value << 4) + aChar - '0';
              break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
              value = (value << 4) + 10 + aChar - 'a';
              break;
            case 'A': case 'B': case 'C':
            case 'D': case 'E': case 'F':
              value = (value << 4) + 10 + aChar - 'A';
              break;
            default:
              throw new IllegalArgumentException(
                  "Malformed \\uxxxx encoding.");
            }
          }
          out[outLen++] = (char)value;
        } else {
          if (aChar == 't') aChar = '\t'; 
          else if (aChar == 'r') aChar = '\r';
          else if (aChar == 'n') aChar = '\n';
          else if (aChar == 'f') aChar = '\f'; 
          out[outLen++] = aChar;
        }
      } else {
        out[outLen++] = (char)aChar;
      }
    }
    return new String (out, 0, outLen);
  }

}
