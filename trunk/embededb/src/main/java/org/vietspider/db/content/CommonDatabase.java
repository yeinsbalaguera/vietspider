/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.db.content;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.vietspider.common.io.LogService;
import org.vietspider.db.BytesDatabase;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class CommonDatabase extends BytesDatabase {
  
  protected long maxSize = 1000000l;
  
  public CommonDatabase(File folder, String name, long cachedSize, boolean readOnly) throws Exception {
    super(folder, name, cachedSize, readOnly);
  }

  public void save(long id, byte[] bytes) throws Throwable {
    if(isClose) return;
    if(bytes.length >= 500) {
      bytes = compress(bytes);
    }
    cleanOutOfSize();
//    System.out.println(" conent "+ new String(bytes, "utf-8"));
    map.put(id, bytes);
  }
  
  public boolean contains(long id) {
    if(isClose) return false;
    try {
      return map.containsKey(id);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return false;
    }
  }
  
  public byte[] load(long id) throws Throwable {
    if(isClose) return null;
    byte [] bytes = map.get(id);
    if(bytes == null) return null;
    byte [] newBytes = uncompress(bytes);
    if(newBytes == null) {
      map.remove(id);
      return bytes;
    }
    return newBytes;
  }
  
  private byte [] compress(byte [] bytes) {
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

  private byte [] uncompress(byte [] bytes) {
    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    try {
      GZIPInputStream gzip = new GZIPInputStream(input);
      byte[] tmp = new byte[2048];
      int read;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      while ((read = gzip.read(tmp)) != -1) {
        output.write(tmp, 0, read);
      }
      gzip.close();
      return output.toByteArray();
    } catch (OutOfMemoryError e) {
      LogService.getInstance().setThrowable("SERVER", e);
      return null;
    } catch (IOException e) {
      return bytes;
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
      return bytes;
    }
  }
  
  public List<String> loadKey(int from, int to) {
    List<String> list = new ArrayList<String>();
    Iterator<Long> iterator = map.keySet().iterator();
    int index = 0;
    while(iterator.hasNext()) {
      long key = iterator.next();
      if(index >= from && index < to) {
        list.add(String.valueOf(key));
      }
      index++;
    }
    
    return  list;
  }
  
  public void sync() throws Throwable {
    db.sync();
  }
  
  /*private final CRC32 crc = new CRC32();
  private final static int GZIP_MAGIC = 0x8b1f;
  
  private boolean isGZipStream(InputStream _in) {
    CheckedInputStream in = new CheckedInputStream(_in, crc);
    crc.reset();
    // Check header magic
    try {
      if (readUShort(in) != GZIP_MAGIC) return false;
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  
  
  private int readUShort(InputStream in) throws IOException {
    int b = readUByte(in);
    return ((int)readUByte(in) << 8) | b;
  }

  private int readUByte(InputStream in) throws IOException {
    int b = in.read();
    if (b == -1) {
      throw new EOFException();
    }
    if (b < -1 || b > 255) {
      // Report on this.in, not argument in; see read{Header, Trailer}.
      throw new IOException(in.getClass().getName()
          + ".read() returned value out of range -1..255: " + b);
    }
    return b;
  }*/
  
  public void cleanOutOfSize() {
    int total = (int)(map.size() - maxSize);
    if(total < 1) return;
    cleanOutOfSize(total);
  }
  
  public void cleanOutOfSize(int total) {
    Iterator<Long> iterator = map.keySet().iterator();
    List<Long> keys = new ArrayList<Long>();
    while(iterator.hasNext()) {
      keys.add(iterator.next());
      if(keys.size() > total) break;
    }
    
    for(int i = 0; i < keys.size(); i++) {
      map.remove(keys.get(i));
    }
  }
  
  public long getMaxSize() { return maxSize; }

  public void setMaxSize(long maxSize) { this.maxSize = maxSize; }

  public SortedMap<Long, byte[]> getMap() { return map; }

}