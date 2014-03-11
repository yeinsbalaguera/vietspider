package org.vietspider.io;
/*
 * ImageInfo.java
 *
 * Version 1.9
 *
 * A Java class to determine image width, height and color depth for
 * a number of image file formats.
 *
 * Written by Marco Schmidt 
 *
 * Contributed to the Public Domain.
 */

//package imageinfo;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageInfo {

  public static final int FORMAT_JPEG = 0;

  public static final int FORMAT_GIF = 1;

  public static final int FORMAT_PNG = 2;

  public static final int FORMAT_BMP = 3;

  public static final int FORMAT_PCX = 4;

  public static final int FORMAT_IFF = 5;

  public static final int FORMAT_RAS = 6;

  public static final int FORMAT_PBM = 7;

  public static final int FORMAT_PGM = 8;

  public static final int FORMAT_PPM = 9;

  public static final int FORMAT_PSD = 10;

  private static final String[] FORMAT_NAMES = {"JPEG", "GIF", "PNG", "BMP", "PCX", 
                                                "IFF", "RAS", "PBM", "PGM", "PPM", "PSD"};

  private static final String[] MIME_TYPE_STRINGS = {"image/jpeg", 
    "image/gif", "image/png", "image/bmp", "image/pcx", "image/iff", "image/ras",
    "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap", "image/psd"};

  private int width;
  private int height;
  private int bitsPerPixel;
  
  private boolean progressive;
  private int format;
  private InputStream in;
  private DataInput din;
  private boolean collectComments = true;
  
  private List<String> comments;
  private boolean determineNumberOfImages;
  private int numberOfImages;
  private int physicalHeightDpi;
  private int physicalWidthDpi;

//  private void addComment(String s) {
//    if (comments == null)  comments = new ArrayList<String>();
//    comments.add(s);
//  }

  public boolean check() {
    format = -1;
    width = -1;
    height = -1;
    bitsPerPixel = -1;
    numberOfImages = 1;
    physicalHeightDpi = -1;
    physicalWidthDpi = -1;
    comments = null;
    
    try {
      int b1 = read() & 0xff;
      int b2 = read() & 0xff;
      if (b1 == 0x47 && b2 == 0x49) return checkGif();
      else if (b1 == 0x89 && b2 == 0x50) return checkPng();
      else if (b1 == 0xff && b2 == 0xd8) return checkJpeg();
      else if (b1 == 0x42 && b2 == 0x4d) return checkBmp();
      else if (b1 == 0x0a && b2 < 0x06) return checkPcx();
      else if (b1 == 0x46 && b2 == 0x4f) return checkIff();
      else if (b1 == 0x59 && b2 == 0xa6) return checkRas();
      else if (b1 == 0x50 && b2 >= 0x31 && b2 <= 0x36) return checkPnm(b2 - '0');
      else if (b1 == 0x38 && b2 == 0x42) return checkPsd(); else return false;
    } catch (IOException ioe) {
      return false;
    }
  }

  private boolean checkBmp() throws IOException {
    byte[] a = new byte[44];
    if (read(a) != a.length) {
      return false;
    }
    width = getIntLittleEndian(a, 16);
    height = getIntLittleEndian(a, 20);
    if (width < 1 || height < 1) {
      return false;
    }
    bitsPerPixel = getShortLittleEndian(a, 26);
    if (bitsPerPixel != 1 && bitsPerPixel != 4 &&
        bitsPerPixel != 8 && bitsPerPixel != 16 &&
        bitsPerPixel != 24 && bitsPerPixel != 32) {
      return false;
    }
    
    int x = (int)(getIntLittleEndian(a, 36) * 0.0254);
    if (x > 0) setPhysicalWidthDpi(x);
    int y = (int)(getIntLittleEndian(a, 40) * 0.0254);
    if (y > 0) setPhysicalHeightDpi(y);
    format = FORMAT_BMP;
    return true;
  }

  private boolean checkGif() throws IOException {
    final byte[] GIF_MAGIC_87A = {0x46, 0x38, 0x37, 0x61};
    final byte[] GIF_MAGIC_89A = {0x46, 0x38, 0x39, 0x61};
    byte[] a = new byte[11]; // 4 from the GIF signature + 7 from the global header
    if (read(a) != 11) return false;

    if ((!equals(a, 0, GIF_MAGIC_89A, 0, 4)) && (!equals(a, 0, GIF_MAGIC_87A, 0, 4))) return false;
    format = FORMAT_GIF;
    width = getShortLittleEndian(a, 4);
    height = getShortLittleEndian(a, 6);
    int flags = a[8] & 0xff;
    bitsPerPixel = ((flags >> 4) & 0x07) + 1;
    //progressive = (flags & 0x02) != 0;
    if (!determineNumberOfImages) return true;
    // skip global color palette
    if ((flags & 0x80) != 0) {
      int tableSize = (1 << ((flags & 7) + 1)) * 3;
      skip(tableSize);
    }
    numberOfImages = 0;
    int blockType;
    do {
      blockType = read();
      switch(blockType) {
      case(0x2c): {// image separator
        if (read(a, 0, 9) != 9) return false;
        flags = a[8] & 0xff;
        progressive = (flags & 0x40) != 0;
        /*int locWidth = getShortLittleEndian(a, 4);
          int locHeight = getShortLittleEndian(a, 6);
          System.out.println("LOCAL: " + locWidth + " x " + locHeight);*/
        int localBitsPerPixel = (flags & 0x07) + 1;
        if (localBitsPerPixel > bitsPerPixel) bitsPerPixel = localBitsPerPixel;
        if ((flags & 0x80) != 0) skip((1 << localBitsPerPixel) * 3);
        skip(1); // initial code length
        int n;
        do {
          n = read();
          if (n > 0) skip(n); else if (n == -1) return false;
        } while (n > 0);
        numberOfImages++;
        break;
      }
      case(0x21): { // extension
        int extensionType = read();
        if (collectComments && extensionType == 0xfe) {
          StringBuffer sb = new StringBuffer();
          int n;
          do {
            n = read();
            if (n == -1) return false;
            if (n > 0) {
              for (int i = 0; i < n; i++) {
                int ch = read();
                if (ch == -1) return false;
                sb.append((char)ch);
              }
            }
          } while (n > 0);
        } else {
          int n;
          do {
            n = read();
            if (n > 0) skip(n); else if (n == -1) return false;
          } while (n > 0);
        }
        break;
      }
      case(0x3b): // end of file
        break;
      default:
        return false;
      }
    } while (blockType != 0x3b);
    return true;
  }

  private boolean checkIff() throws IOException {
    byte[] a = new byte[10];
    // read remaining 2 bytes of file id, 4 bytes file size 
    // and 4 bytes IFF subformat
    if (read(a, 0, 10) != 10) return false;
    final byte[] IFF_RM = {0x52, 0x4d};
    if (!equals(a, 0, IFF_RM, 0, 2)) return false;
    int type = getIntBigEndian(a, 6);
    if (type != 0x494c424d && type != 0x50424d20)  return false; // type must be ILBM... and ...or PBM
    // loop chunks to find BMHD chunk
    do {
      if (read(a, 0, 8) != 8) return false;
      int chunkId = getIntBigEndian(a, 0);
      int size = getIntBigEndian(a, 4);
      if ((size & 1) == 1) size++;
      if (chunkId == 0x424d4844) { // BMHD chunk
        if (read(a, 0, 9) != 9) return false;
        format = FORMAT_IFF;
        width = getShortBigEndian(a, 0);
        height = getShortBigEndian(a, 2);
        bitsPerPixel = a[8] & 0xff;
        return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel < 33);
      } 
      skip(size);
    } while (true);
  }

  private boolean checkJpeg() throws IOException {
    byte[] data = new byte[12];
    while (true) {
      if (read(data, 0, 4) != 4) return false;
      int marker = getShortBigEndian(data, 0);
      int size = getShortBigEndian(data, 2);
      if ((marker & 0xff00) != 0xff00) return false; // not a valid marker 
      if (marker == 0xffe0) { // APPx 
        if (size < 14) {
          // not an APPx header as we know it, skip
          skip(size - 2);
          continue;
        }
        if (read(data, 0, 12) != 12) return false;

        final byte[] APP0_ID = {0x4a, 0x46, 0x49, 0x46, 0x00};
        if (equals(APP0_ID, 0, data, 0, 5)) {
          //System.out.println("data 7=" + data[7]);
          if (data[7] == 1) {
            setPhysicalWidthDpi(getShortBigEndian(data, 8));
            setPhysicalHeightDpi(getShortBigEndian(data, 10));
          } else if (data[7] == 2) {
            int x = getShortBigEndian(data, 8);
            int y = getShortBigEndian(data, 10);
            setPhysicalWidthDpi((int)(x * 2.54f));
            setPhysicalHeightDpi((int)(y * 2.54f));
          }
        }
        skip(size - 14);
      } else if (collectComments && size > 2 && marker == 0xfffe) { // comment
        size -= 2;
        byte[] chars = new byte[size];
        if (read(chars, 0, size) != size) return false;
//        String comment = new String(chars, "iso-8859-1");
//        comment = comment.trim();
//        addComment(comment);
      } else
        if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8) {
          if (read(data, 0, 6) != 6) return false;
          format = FORMAT_JPEG;
          bitsPerPixel = (data[0] & 0xff) * (data[5] & 0xff);
          progressive = marker == 0xffc2 || marker == 0xffc6 ||
          marker == 0xffca || marker == 0xffce;
          width = getShortBigEndian(data, 3);
          height = getShortBigEndian(data, 1);
          return true;
        } else {
          skip(size - 2);
        }
    }
  }

  private boolean checkPcx() throws IOException {
    byte[] a = new byte[64];
    if (read(a) != a.length) return false;
    if (a[0] != 1) return false; // encoding, 1=RLE is only valid value
    // width / height
    int x1 = getShortLittleEndian(a, 2);
    int y1 = getShortLittleEndian(a, 4);
    int x2 = getShortLittleEndian(a, 6);
    int y2 = getShortLittleEndian(a, 8);
    if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) return false;
    width = x2 - x1 + 1;
    height = y2 - y1 + 1;
    // color depth
    int bits = a[1];
    int planes = a[63];
    // paletted
    if (planes == 1 && (bits == 1 || bits == 2 || bits == 4 || bits == 8)) bitsPerPixel = bits; 
    else if (planes == 3 && bits == 8) bitsPerPixel = 24; else return false;
    setPhysicalWidthDpi(getShortLittleEndian(a, 10));
    setPhysicalHeightDpi(getShortLittleEndian(a, 10));
    format = FORMAT_PCX;
    return true;
  }

  private boolean checkPng() throws IOException {
    final byte[] PNG_MAGIC = {0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
    byte[] a = new byte[27];
    if (read(a) != 27) return false;
    if (!equals(a, 0, PNG_MAGIC, 0, 6)) return false;
    format = FORMAT_PNG;
    width = getIntBigEndian(a, 14);
    height = getIntBigEndian(a, 18);
    bitsPerPixel = a[22] & 0xff;
    int colorType = a[23] & 0xff;
    if (colorType == 2 || colorType == 6) bitsPerPixel *= 3;
    progressive = (a[26] & 0xff) != 0;
    return true;
  }

  private boolean checkPnm(int id) throws IOException {
    if (id < 1 || id > 6) return false;
    final int[] PNM_FORMATS = {FORMAT_PBM, FORMAT_PGM, FORMAT_PPM};
    format = PNM_FORMATS[(id - 1) % 3];
    boolean hasPixelResolution = false;
    String s;
    while (true) {
      s = readLine();
      if (s != null) s = s.trim();
      if (s == null || s.length() < 1) continue;
      if (s.charAt(0) == '#') { // comment
//        if (collectComments && s.length() > 1) addComment(s.substring(1));
        continue;
      }
      
      if (!hasPixelResolution) { // split "343 966" into width=343, height=966
        int spaceIndex = s.indexOf(' ');
        if (spaceIndex == -1) return false;
        String widthString = s.substring(0, spaceIndex);
        spaceIndex = s.lastIndexOf(' ');
        if (spaceIndex == -1) return false;
        String heightString = s.substring(spaceIndex + 1);
        try {
          width = Integer.parseInt(widthString);
          height = Integer.parseInt(heightString);
        } catch (NumberFormatException nfe) {
          return false;
        }
        if (width < 1 || height < 1) return false;
        if (format == FORMAT_PBM) {
          bitsPerPixel = 1;
          return true;
        }
        hasPixelResolution = true;
      } else {
        int maxSample;
        try {
          maxSample = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
          return false;
        }
        if (maxSample < 0) return false;
        for (int i = 0; i < 25; i++) {
          if (maxSample < (1 << (i + 1))) {
            bitsPerPixel = i + 1;
            if (format == FORMAT_PPM) bitsPerPixel *= 3;
            return true;
          }
        }
        return false;
      }
    }
  }

  private boolean checkPsd() throws IOException {
    byte[] a = new byte[24];
    if (read(a) != a.length) return false;
    final byte[] PSD_MAGIC = {0x50, 0x53};
    if (!equals(a, 0, PSD_MAGIC, 0, 2)) return false;
    format = FORMAT_PSD;
    width = getIntBigEndian(a, 16);
    height = getIntBigEndian(a, 12);
    int channels = getShortBigEndian(a, 10);
    int depth = getShortBigEndian(a, 20);
    bitsPerPixel = channels * depth;
    return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 64);
  }

  private boolean checkRas() throws IOException {
    byte[] a = new byte[14];
    if (read(a) != a.length) return false;
    final byte[] RAS_MAGIC = {0x6a, (byte)0x95};
    if (!equals(a, 0, RAS_MAGIC, 0, 2)) return false;
    format = FORMAT_RAS;
    width = getIntBigEndian(a, 2);
    height = getIntBigEndian(a, 6);
    bitsPerPixel = getIntBigEndian(a, 10);
    return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 24);
  }

  private static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {
    while (num-- > 0) {
      if (a1[offs1++] != a2[offs2++]) return false;
    }
    return true;
  }

  public int getBitsPerPixel() { return bitsPerPixel; }

  public String getComment(int index) {
    if (comments == null || index < 0 || index >= comments.size()) {
      throw new IllegalArgumentException("Not a valid comment index: " + index);
    }
    return comments.get(index);
  }

  public int getFormat() { return format; }

  public String getFormatName() {
   return (format >= 0 && format < FORMAT_NAMES.length) ? FORMAT_NAMES[format] : "?";
  }

  public int getHeight() { return height; }

  private static int getIntBigEndian(byte[] a, int offs) {
    return (a[offs] & 0xff) << 24 | 
    (a[offs + 1] & 0xff) << 16 | (a[offs + 2] & 0xff) << 8 | a[offs + 3] & 0xff;
  }

  private static int getIntLittleEndian(byte[] a, int offs) {
    return (a[offs + 3] & 0xff) << 24 | 
    (a[offs + 2] & 0xff) << 16 |  (a[offs + 1] & 0xff) << 8 | a[offs] & 0xff;
  }

  public String getMimeType() {
    if (format >= 0 && format < MIME_TYPE_STRINGS.length) {
      return (format == FORMAT_JPEG && progressive) ? "image/pjpeg" : MIME_TYPE_STRINGS[format];
    }
    return null;
  }

  public int getNumberOfComments() { return comments == null ?  0 : comments.size(); }

  public int getNumberOfImages()  { return numberOfImages; }

  public int getPhysicalHeightDpi() { return physicalHeightDpi; }

  public float getPhysicalHeightInch() {
    int h = getHeight();
    int ph = getPhysicalHeightDpi();
    return  (h > 0 && ph > 0)  ? ((float)h) / ((float)ph) : -1.0f;
  }

  public int getPhysicalWidthDpi() { return physicalWidthDpi; }

  public float getPhysicalWidthInch() {
    int w = getWidth();
    int pw = getPhysicalWidthDpi();
    return (w > 0 && pw > 0) ?  ((float)w) / ((float)pw) : -1.0f;
  }

  private static int getShortBigEndian(byte[] a, int offs) {
    return (a[offs] & 0xff) << 8 | (a[offs + 1] & 0xff);
  }

  private static int getShortLittleEndian(byte[] a, int offs) {
    return (a[offs] & 0xff) | (a[offs + 1] & 0xff) << 8;
  }

  public int getWidth() { return width; }

  public boolean isProgressive(){ return progressive; }

  private int read() throws IOException {
    return (in != null) ? in.read() : din.readByte();
  }

  private int read(byte[] a) throws IOException {
    if (in != null) return in.read(a);
    din.readFully(a);
    return a.length;
  }

  private int read(byte[] a, int offset, int num) throws IOException {
    if (in != null) return in.read(a, offset, num);
    din.readFully(a, offset, num);
    return num;
  }

  private String readLine() throws IOException {
    return readLine(new StringBuffer());
  }

  private String readLine(StringBuffer sb) throws IOException {
    boolean finished;
    do {
      int value = read();
      finished = (value == -1 || value == 10);
      if (!finished) sb.append((char)value);      
    } while (!finished);
    return sb.toString();
  }

  public void setCollectComments(boolean newValue) { collectComments = newValue; }

  public void setDetermineImageNumber(boolean newValue) {
    determineNumberOfImages = newValue;
  }

  public void setInput(DataInput dataInput) {
    din = dataInput;
    in = null;
  }

  public void setInput(InputStream inputStream) { 
    in = inputStream;
    din = null;
  }

  private void setPhysicalHeightDpi(int newValue) { physicalWidthDpi = newValue; }

  private void setPhysicalWidthDpi(int newValue) { physicalHeightDpi = newValue; }

  private void skip(int num) throws IOException {
    while (num > 0) {
      long result;
      if (in != null) result = in.skip(num); else result = din.skipBytes(num);
      if (result > 0) {
        num -= result;
        continue;
      } 
      if (in != null) result = in.read(); else result = din.readByte();                
      if (result == -1) throw new IOException("Premature end of input.");                
      num--;
    }
  }
}
