/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.vietspider.gui.source.SourcesExplorer.SourceFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 26, 2008  
 */
public class CrawlSourceFilterBak extends SourceFilter {
  
  private SourceListCheckerBak  sourceListCheckerBak;
  
  public CrawlSourceFilterBak(DownloadListClient downloadList) {
    sourceListCheckerBak = new SourceListCheckerBak(downloadList);
  }
  
  public boolean accept(String fullName) {
    return !sourceListCheckerBak.contains(fullName);
    /*String [] elements = fullName.split("\\.");
    if(elements.length < 3) return false;
    String patternValue = elements[2] + "[.]" + elements[0] + "[.]" + elements[1];
    return !downloadList.contains(patternValue);*/
    
    
   /* String [] elements = fullName.split("\\.");
    if(elements.length < 3) return false;
    
    String patternValue = elements[2] + "[.]" + elements[0] + "[.]" + elements[1];
    Charset charset = Charset.forName("utf-8");
    Pattern pattern = Pattern.compile(patternValue, Pattern.CASE_INSENSITIVE);
    
    File folder = ClientConnector.getCacheFolder("download");
    File [] files = folder.listFiles();
    
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".temp")) continue;
      FileInputStream stream =  null;
      try {
        stream = new FileInputStream(files[i]);
        FileChannel f = stream.getChannel();
        ByteBuffer bytes = f.map(FileChannel.MapMode.READ_ONLY, 0, f.size());
        CharBuffer chars = charset.decode(bytes);
        Matcher matcher = pattern.matcher(chars);
        if (matcher.find()) return false;
      } catch (Exception e) {
      } finally {
        try {
          if(stream != null) stream.close();
        } catch (Exception e) {
        }
      }
    }
    return true;*/
  }
  
}