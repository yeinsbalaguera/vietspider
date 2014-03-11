/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.text.VietComparator;
import org.vietspider.gui.source.SourcesExplorer.SourceFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 26, 2008  
 */
public class CrawlSourceFilter extends SourceFilter {
  
  private DownloadListClient downloadList;
  private File file;
  private List<Long> positions;
  private int time = 0; 
  
  public CrawlSourceFilter(DownloadListClient downloadList) {
    this.downloadList = downloadList;
    file = downloadList.getFile();
    positions = downloadList.countPage(file);
    time = downloadList.getCurrentTime();
  }
  
  public boolean accept(String fullName) {
    if(time != downloadList.getCurrentTime()) {
      file = downloadList.getFile();
      positions = downloadList.countPage(file);
      time = downloadList.getCurrentTime();
    }
    
    String [] elements = fullName.split("\\.");
    if(elements.length < 3) return false;
    String patternValue = elements[2] + "[.]" + elements[0] + "[.]" + elements[1];
    Pattern pattern = Pattern.compile(patternValue, Pattern.CASE_INSENSITIVE);
    
    RandomAccessFile stream = null;
    FileChannel channel = null;
    
    VietComparator comparator = new VietComparator();
//    System.out.println(" =================================================");
    try {
      stream = new RandomAccessFile(file, "r");
      channel = stream.getChannel();
      if(positions.size() < 1) {
        return searchExtend(channel, pattern, 0);
      }

      int startIndex = 0;
      int endIndex = positions.size();
      String source = elements[2];
      long endPos  = positions.get(positions.size() - 1);
      
      while(startIndex < endIndex) {
        int index = (startIndex+endIndex)/2;
        long pos = positions.get(index);
        stream.seek(pos);
        int idx = -1;
        String line  = null;
        
        while(idx < 0) {
          line  = downloadList.readLine(stream);
          if(line == null) break;
          idx = line.indexOf('.');
        }
        
        if(line == null || idx < 0) break;
        
        line  = line.substring(0, idx);
        int compare = comparator.compare(source, line);
//        System.out.println(" so sanh "+ source + " line "+ line + " : "+ compare);
        if(compare == 0) break;
        
        if(startIndex >= endIndex -1) break;
        
        if(compare < 0) {
          endIndex = index;
        } else {
          startIndex = index;
        } 
//        System.out.println(" thay co "+ startIndex + " :  "+ endIndex);
      }
      
//      if(!search) {
//        return searchExtend(channel, pattern, endPos);
//      }
      
//      System.out.println(" start "+ startIndex + " end "+ endIndex);
      long pos  = positions.get(startIndex);
      long size = 0;
      if(endIndex >= positions.size() - 1) {
        size = file.length() - pos;
      } else {
        size = positions.get(endIndex) - pos;
      }
      
      ByteBuffer bytes = channel.map(FileChannel.MapMode.READ_ONLY, pos, size);
      
      Charset charset = Charset.forName("utf-8");
      CharBuffer chars = charset.decode(bytes);
      Matcher matcher = pattern.matcher(chars);
      if (matcher.find()) return false;
      
      return searchExtend(channel, pattern, endPos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if(channel != null) channel.close();
      } catch (Exception e) {
      }
      try {
        if(stream != null) stream.close();
      } catch (Exception e) {
      }
    }
    return true;
  }
  
  private boolean searchExtend(FileChannel channel, Pattern pattern, long end) throws Exception {
    if(end >= file.length()) return true;
    long start = end;
    long size = file.length() - start;
    ByteBuffer bytes = channel.map(FileChannel.MapMode.READ_ONLY, start, size);
    Charset charset = Charset.forName("utf-8");
    CharBuffer chars = charset.decode(bytes);
    Matcher matcher = pattern.matcher(chars);
    if (matcher.find()) return false;
    return true;
  }
  
}