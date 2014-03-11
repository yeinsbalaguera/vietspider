/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.codes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.ConcurrentSetInt;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.ConcurrentIntSort;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2008  
 */
public class FileCodesWriter extends CodesWriter<ConcurrentSetInt> {
  
  private final static int BUFFER_SIZE = 1024;
  
  protected final static int SAVE_SIZE = 1000;
  
  public void save() {
    if(codes.size() < 1) return;
    
//    int[] intValues = new int[Math.min(SAVE_SIZE, codes.size())];
//    int size = Math.min(SAVE_SIZE, codes.size());
    List<Integer> intList = new ArrayList<Integer>();
    codes.addToList(intList);
   /* Iterator<Integer> iterator = codes.iterator();
    for(int i = 0; i < size; i++) {
      if(!iterator.hasNext()) break;
      Integer it = iterator.next();
      if(it == null) continue;
      intList.add(it); 
    }*/
    
    int[] intValues = new int[intList.size()];//intList.toArray(new Integer[intList.size()]);
    for(int i = 0; i < intValues.length; i++) {
      intValues[i] = intList.get(i);
    }
    
    ConcurrentIntSort.sort(intValues);
    try {
      if(!file.exists() || file.length() < 1) {
        writeNewFile(intValues);
      } else {
        appendFile(intValues);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    codes.clear();
//    for(int i = 0; i < intValues.length; i++) {
//      codes.remove(intValues[i]);
//    }
  }
  
  public boolean isRunning() { return file != null; }
  
  private void appendFile(int [] intValues) throws Exception {
    long [] seekes = new long[intValues.length];
    for(int i = 0; i < seekes.length; i++) {
      seekes[i] = search(file, intValues[i]);
    }
    
    List<Integer> minCodes = new ArrayList<Integer>(intValues.length);
    List<Integer> maxCodes = new ArrayList<Integer>(intValues.length);
    List<Integer> normalCodes = new ArrayList<Integer>(intValues.length);
    List<Long> normalSeekes = new ArrayList<Long>(intValues.length);

    for(int i = 0; i < seekes.length; i++) {
      if(seekes[i] == -1) {
        continue;
      } else if(seekes[i] == 0) {
        minCodes.add(intValues[i]);
      } else if(seekes[i] == file.length()) {
        maxCodes.add(intValues[i]);
      } else {
        normalCodes.add(intValues[i]);
        normalSeekes.add(seekes[i]);
      }
    }

    File tmpFile = new File(file.getParentFile(), file.getName()+"_new_temp");
    tmpFile.delete();
    tmpFile.createNewFile();

    FileInputStream fileInputStream = null;
    FileChannel inputChannel = null;

    FileOutputStream fileOutputStream = null;
    FileChannel outputChannel = null; 

    try {
      fileInputStream = new FileInputStream(file);
      inputChannel = fileInputStream.getChannel();

      fileOutputStream = new FileOutputStream(tmpFile, true);
      outputChannel = fileOutputStream.getChannel();
      //write min codes
      ByteBuffer intBuffer = ByteBuffer.allocate(minCodes.size()*4);
      for(Integer integer : minCodes) intBuffer.putInt(integer.intValue());
      intBuffer.rewind();
      outputChannel.write(intBuffer);
      intBuffer.clear();

      //tranfer old data to new file and write normal codes
      long start = 0;
      for(int i = 0; i < normalSeekes.size(); i++) {
        long end = normalSeekes.get(i).longValue();
        inputChannel.transferTo(start, end - start, outputChannel);

        //write code
        intBuffer = ByteBuffer.allocate(4);
        intBuffer.putInt(normalCodes.get(i));
        intBuffer.rewind();
        outputChannel.write(intBuffer);
        intBuffer.clear();

        start = normalSeekes.get(i).longValue();
      }

      if(start < file.length()) {
        inputChannel.transferTo(start, file.length() - start, outputChannel);
      }

      //write max codes
      intBuffer = ByteBuffer.allocate(maxCodes.size()*4);
      for(Integer integer : maxCodes) intBuffer.putInt(integer.intValue());
      intBuffer.rewind();
      outputChannel.write(intBuffer);
      intBuffer.clear();

      inputChannel.close();
      fileInputStream.close();

      outputChannel.close();
      fileOutputStream.close();

      if(tmpFile.length() > 0)  RWData.getInstance().copy(tmpFile, file);
      tmpFile.delete();
    } finally {
      try {
        if(inputChannel != null) inputChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(fileInputStream != null) fileInputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(outputChannel != null) outputChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(fileOutputStream != null) fileOutputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      tmpFile.delete();
    }
  }
  
  protected void append(FileChannel outputChannel, InputStream input) throws Exception {
    int read = -1;
    byte bytes [] = new byte[BUFFER_SIZE];
    try {
      while((read = input.read(bytes)) > -1){
        ByteBuffer outputBuff = ByteBuffer.allocateDirect(read);
        outputBuff.put(bytes, 0, read);
        outputBuff.rewind();
        outputChannel.write(outputBuff);
        outputBuff.clear(); 
      } 
    } finally {
      try {
        input.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  protected void writeNewFile(int [] intValues) throws Exception {
    FileOutputStream outputStream = null; 
    FileChannel outputChannel = null;
    
    try {
      outputStream = new FileOutputStream(file, true);
      outputChannel = outputStream.getChannel();

      ByteBuffer intBuffer = ByteBuffer.allocate(intValues.length*4);
      for(Integer integer : intValues) intBuffer.putInt(integer.intValue());
      intBuffer.rewind();
      outputChannel.write(intBuffer);        
      intBuffer.clear();
      
      outputChannel.close();
      outputStream.close();
    } finally {
      try {
        if(outputChannel != null) outputChannel.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      try {
        if(outputStream != null) outputStream.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  protected long search(File f, int c)  {
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(f, "r");
      long value = search(random, f, c, 0, random.length());
      random.close();
      return value;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return -1;
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  
  protected long search(RandomAccessFile random, File f, int c, long start, long end) throws Exception {
    //check start
    random.seek(start);
    int value = random.readInt();
    if(value == c) return -1;
    if(value > c) return start;

    //check end
    random.seek(end - 4);
    value  = random.readInt();
    if(value < c) return end;

    long seek = (end+start)/2;
    if(seek%4 != 0) seek -= 2;
    
//    if(writing && compare) return -2;
    random.seek(seek);
    value  = random.readInt();
//    System.out.println("start "+ start+ " end "+end + " seek "+seek + " : "+value);
    if(value > c) return search(random, f, c, start, seek);
    return search(random, f, c, seek, end);
  }
  
}
