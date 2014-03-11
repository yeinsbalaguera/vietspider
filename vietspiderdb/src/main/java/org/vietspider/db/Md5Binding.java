/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.MD5Hash;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class Md5Binding extends TupleBinding<byte[]>{

  // javadoc is inherited
  public byte[] entryToObject(TupleInput input) {
    byte [] bytes = new byte[MD5Hash.DATA_LENGTH];
    try {
      int read = input.read(bytes);
      if(read < MD5Hash.DATA_LENGTH) return null;
      return bytes;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  // javadoc is inherited
  public void objectToEntry(byte[] bytes, TupleOutput output) {
    try {
      output.write(bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

 /* // javadoc is inherited
  @SuppressWarnings("unused")
  protected TupleOutput getTupleOutput(byte[] bytes) {
    return sizedOutput();
  }

  *//**
   * Converts an entry buffer into a simple <code>bytes</code> value.
   *
   * @param entry is the source entry buffer.
   *
   * @return the resulting value.
   *//*
  
  public static byte[] entryToMd5(DatabaseEntry entry) {
    byte [] bytes = new byte[MD5Hash.DATA_LENGTH];
    try {
      int read = entryToInput(entry).read(bytes);
      if(read < MD5Hash.DATA_LENGTH) return null;
      return bytes;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  *//**
   * Converts a simple <code>bytes</code> value into an entry buffer.
   * @param val is the source value.
   * @param entry is the destination entry buffer.
   *//*
  
  public static void md5ToEntry(byte [] bytes, DatabaseEntry entry) {
    TupleOutput output = sizedOutput();
    try {
      output.write(bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    outputToEntry(output, entry);
  }

  *//**
   * Returns a tuple output object of the exact size needed, to avoid
   * wasting space when a single primitive is output.
   *//*
  
  private static TupleOutput sizedOutput() {
    return new TupleOutput(new byte[MD5Hash.DATA_LENGTH]);
  }*/
}
