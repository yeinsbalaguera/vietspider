/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class SourceLogUtils {
  
  /*void loadFile(File file, TrackId menuInfo) throws Exception  {
    RandomAccessFile random = null;
    try {
      random = new RandomAccessFile(file, "r");

      while(random.getFilePointer() < random.length()) {
        String line = random.readUTF();
        int visit = random.readInt();
        int data = random.readInt();
        long link = random.readLong();
        menuInfo.add(line, visit, data, link, 0);
      }
    } finally {
      try {
        if(random != null) random.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SOURCE LOG", e);
      }
    }
  }*/
  
 /* public String [] loadDate() {
    Set<String> list = new HashSet<String>(); 
    DateFormat folderDateFormat = CalendarUtils.getFolderFormat();
    DateFormat dateFormat = CalendarUtils.getDateFormat();
    try {
      File folder = UtilFile.getFolder("content/summary/");
      File [] files = UtilFile.listFiles(folder);
      for(File ele : files) {
        String name = ele.getName();
        if(name.equalsIgnoreCase("eid")) continue;
        if(name.endsWith("_mdb")) {
          name = name.substring(0, name.length() - 4);
        } else if(name.indexOf('_') < 0) {
          UtilFile.deleteFolder(ele);
          continue;
        }
        list.add(name);
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SOURCE LOG", e);
    }
    
    String [] dateValues = list.toArray(new String[list.size()]);
    java.util.Arrays.sort(dateValues, new DateFolderComparator());
    List<String> values = new ArrayList<String>();
    for(int i = 0; i < dateValues.length; i++) {
      try {
        Date date = folderDateFormat.parse(dateValues[i]);
        values.add(dateFormat.format(date));
      } catch (Exception e) {
        LogService.getInstance().setMessage("SOURCE LOG", e, e.toString());
      }
    }
    return values.toArray(new String[values.size()]);
  }*/
}
