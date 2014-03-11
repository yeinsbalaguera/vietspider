/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 23, 2008  
 */
class CrawlReporterHandler /*extends SourceMonitorHandler*/ {
  
//  public CrawlReporterHandler(String[] dates) {
//    super("monitor", dates);
//  }
//  
//  public void download(Console console) throws Throwable {
//    int totalLink  = 0; 
//    int totalData = 0;
//    
//    File file = ClientConnector2.getCacheFolder("monitor");//UtilFile.getFolder(folder);
//    UtilFile.deleteFolder(file, false);
//
//    if(dates == null || dates.length < 1) {
//      printGlobal(console, new int[]{0,0});
//      return ;
//    }
//
//    File tmpFile  = readFromServer(true);
//    if(tmpFile == null) {
//      printGlobal(console, new int[]{0,0});
//      return ;
//    }
//    
//    
//    BufferedReader reader = null;
//    FileOutputStream outputStream = null;
//    HashMap<String, Integer> totalGroups = new HashMap<String,Integer>();
//    try { 
//      FileInputStream fileInputStream = new FileInputStream(file);
//      InputStreamReader streamReader = new InputStreamReader(fileInputStream, "utf-8");
//      reader = new BufferedReader(streamReader);
//      
//      String line = null;
//      
//      AccessChecker accessChecker = new OrganizationClientHandler().loadAccessChecker();
//
//      StringBuilder builder = new StringBuilder();
//      while((line = reader.readLine()) != null) {
//        builder.setLength(0);
//        
//        if(line.trim().isEmpty()) continue;
//        
//        String [] elements = line.split("/");
//        if(elements.length < 5) continue;
//        
//        String sourceName  = elements[0];
//        int crawlTime  = Integer.parseInt(elements[1]);
//        int link  = Integer.parseInt(elements[2]);
//        int data = Integer.parseInt(elements[3]);
////        long totalDownload  = Long.parseLong(elements[4]);
//        long lastAccess = Long.parseLong(elements[4]);
//        
//        SourceMonitor sourceMonitor = new SourceMonitor();
//        sourceMonitor.setSource(sourceName);
//        sourceMonitor.setCrawlTime(crawlTime);
//        sourceMonitor.setLinkCounter(link);
//        sourceMonitor.setDataCounter(data);
////        sourceMonitor.setDownloaded(totalDownload);
//        sourceMonitor.setLastAccess(lastAccess);
//        
//        if(validate(sourceMonitor, null) && accessChecker.isPermitAccess(line, false) ) {
//          builder.append(line).append('\n');
//          
//          totalLink +=  sourceMonitor.getLinkCounter();
//          totalData += sourceMonitor.getDataCounter();
//          
//          elements = sourceName .split("\\.");
//          if(elements.length > 2) {
//            String key  = elements[0];
//            if(totalGroups.containsKey(key)) {
//              totalGroups.put(key, totalGroups.get(key) + sourceMonitor.getDataCounter());
//            } else {
//              totalGroups.put(key, sourceMonitor.getDataCounter());
//            }
//          }
//        }
//
//      }
//      if(outputStream != null) outputStream.close();
//    } catch (EOFException e) {
////    e.printStackTrace();
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//      try {
//        if(outputStream != null) outputStream.close(); 
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      
//      try {
//        if(reader != null) reader.close(); 
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      tmpFile.delete();
//    }
//    printGlobal(console, new int[]{totalLink, totalData});
//    if(totalGroups.size() > 0) printGroup(console, totalGroups);
//  }
//  
//  private void printGroup(Console console, HashMap<String, Integer> totalGroups) {
//    Iterator<String> iterator = totalGroups.keySet().iterator();
//    console.printf("\n");
//    
//    int size = 5;
//    
//    while(iterator.hasNext()) {
//      String key = iterator.next();
//      if(key.length() > size) size = key.length();
//    }
//    
//    iterator = totalGroups.keySet().iterator();
//    while(iterator.hasNext()) {
//      String key = iterator.next();
//      int value = totalGroups.get(key);
//      console.printf("\n" + key);
//      int index = key.length();
//      while(index < size) {
//        console.printf(" ");
//        index++;
//      }
//      console.printf(" : " + String.valueOf(value));
//    }
//    console.printf("\n");
//  }
//  
//  private void printGlobal(Console console, int [] values) {
//    int ratio = values[0] > 0 ? ((values[1]*100)/values[0]) : 0;
//    console.printf("\n\nTotal visited link:" + String.valueOf(values[0])+"\n");
//    System.out.println("Total data:" + String.valueOf(values[1])+ " ("+String.valueOf(ratio)+"%)");
//  }
}
