/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.db2.content;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.xtreemfs.babudb.api.database.DatabaseInsertGroup;
import org.xtreemfs.babudb.api.database.DatabaseRequestResult;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 30, 2009  
 */
public class CommonBabuDatabase extends BytesBabuDatabase {
  
  public final static int META = 0;
  public final static int CONTENT = 1;
  public final static int URL = 1;
  
  public CommonBabuDatabase(File folder, String name) throws Exception {
    super(folder, name);
  }
  
  public void save(int index, long id, String data) throws Throwable {
    save(index, id, compress(data.getBytes(Application.CHARSET)));
  }

  private void save(int index, long id, byte[] bytes) throws Throwable {
    if(isClose) return;
    byte [] keys = toKeys(id); 
    save(index, keys, bytes);
  }
  
  /*public void save(int index, String key, String data) throws Throwable {
    if(isClose) return;
    db.singleInsert(index, key.getBytes(), data.getBytes(Application.CHARSET), null);
  }*/
  
  private void save(int index, byte [] keys, byte[] bytes) throws Throwable {
    if(isClose) return;
//    if(bytes.length >= 500) bytes = compress(bytes);
    db.singleInsert(index, keys, bytes, null);
  }
  
  public boolean contains(int index, long id) {
    if(isClose) return false;
    DatabaseRequestResult<byte[]> result = null; 
    try {
      byte [] keys = toKeys(id); 
      result = db.lookup(index,  keys, null);
      return result.get() != null;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return false;
  }
  
  public byte[] load(int index, long id) throws Throwable {
    if(isClose) return null;
    byte [] keys = toKeys(id); 
    byte [] bytes = load(index, keys);
    // will remove when done
    if(bytes == null) {
      bytes = load(index, new byte[]{new Long(id).byteValue()});
    }
    return bytes;
  }
  
  public byte[] load(int index, byte[] keys) throws Throwable {
    DatabaseRequestResult<byte[]> result = null; 
    byte [] bytes = null;
    try {
      result = db.lookup(index,  keys, null);
      bytes = result.get();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(bytes == null) return null;
    byte [] newBytes = uncompress(bytes);
    if(newBytes == null) return bytes;
    return newBytes;
  }
  
  public void delete(int index, String key) throws Throwable {
    delete(index, key.getBytes());
  }
  
  public void delete(int index, long id) throws Throwable {
    delete(index, toKeys(id));
  }
  
  public void delete(int index, byte[] keys) throws Throwable {
    DatabaseInsertGroup group =  db.createInsertGroup();
    group.addDelete(index, keys);
    db.insert(group, null);
  }
  
  public DatabaseInsertGroup createInsertGroup() {
    return db.createInsertGroup();
  }
  
  public void insert(DatabaseInsertGroup group) {
    db.insert(group, null);
  }
  
  public void delete(int[] indexes, long id) throws Throwable {
    DatabaseInsertGroup group =  db.createInsertGroup();
    byte [] keys = toKeys(id); 
    for(int i = 0; i < indexes.length; i++) {
      group.addDelete(indexes[i], keys);
    }
    db.insert(group, null);
  }
  
  public byte [] compress(byte [] bytes) {
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
//      System.out.println(" === "+ new String(bytes, Application.CHARSET));
      int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
//      System.out.println(" chay thu "+ (GZIPInputStream.GZIP_MAGIC != head));
      if(GZIPInputStream.GZIP_MAGIC != head) {
        return bytes;
      }
      
//      String text = new String(bytes, Application.CHARSET);

      /*PushbackInputStream pb = new PushbackInputStream(input, 2); 
      //we need a pushbackstream to look ahead
      byte [] signature = new byte[2];
      pb.read( signature ); //read the signature
      pb.unread( signature ); //push back the signature to the stream
      if( signature[0] != (byte) 0x1f || signature[1] != (byte) 0x8b ) {
        //check if matches standard gzip maguc number
        System.out.println(" no gzip data");
        return bytes;
      }*/
      
      GZIPInputStream gzip = new GZIPInputStream(input);
      byte[] tmp = new byte[2048];
      int read;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      while ((read = gzip.read(tmp)) != -1) {
        output.write(tmp, 0, read);
      }
      gzip.close();
//      System.out.println("========================================");
//      System.out.println(" is gzip data");
//      System.out.println(new String(bytes, Application.CHARSET));
//      System.out.println("================== end ======================");
      return output.toByteArray();
    } catch (OutOfMemoryError e) {
      LogService.getInstance().setThrowable("SERVER", e);
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return bytes;
    } catch (Exception e) {
      e.printStackTrace();
      LogService.getInstance().setThrowable("SERVER", e);
      return bytes;
    }
  }
  
  public byte[] toKeys(long id) throws Exception {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();  
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);  
    dataOutputStream.writeLong(id);  
    dataOutputStream.close();
    return byteOutputStream.toByteArray();
  }
  
  /*public static void main(String[] args) throws Exception  {
    long id = 201205020835310009l;
    byte [] keys1 = toKeys(id); 
    System.out.println(new String(keys1));
    
    id = 201205020850500025l;
    byte[] keys2 = toKeys(id); 
    System.out.println(new String(keys2));
    
    System.out.println("length "+ keys1.length + " : "+ keys2.length);
    
    for(int i = 0 ; i < keys1.length; i++) {
      if(keys1[i] != keys2[i]) {
        System.out.println(keys1[i] + "!= " + keys2[i]);
      } else {
        System.out.println(keys1[i] + "== " + keys2[i]);
      }
    }
  }*/
  
}