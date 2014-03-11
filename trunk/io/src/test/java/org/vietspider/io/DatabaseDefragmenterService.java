/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 2, 2008  
 */
public class DatabaseDefragmenterService {
  
  
  public static void main(String[] args) throws Exception  {
   /* String dir = "D:\\Temp\\BLOG.Blog Vi-u1EC7t Nam 2.Yobanbe";
    File dbdir = new File(dir) ;
    if(!dbdir.exists()) dbdir.mkdirs() ;
    
    DatabasePlugin<Homepage, MD5Hash> dbplugin = new DatabasePlugin<Homepage, MD5Hash>() {
      public Homepage createStorable() { return  new Homepage();}
      public Homepage[] createStorable(int size) {
        Homepage[] homepages = new Homepage[size];
        for(int i = 0; i < homepages.length; i++) {
          homepages[i] = new Homepage();
        }
        return homepages;
      }
      public MD5Hash createKey() { return new MD5Hash(); }
      public Comparator<MD5Hash> createKeyComparator() { return MD5Hash.COMPARATOR ; }
      public int getKeySize() { return MD5Hash.DATA_LENGTH; }
    } ;
    
    DatabaseConfig dbconfig = new DatabaseConfig(dir) ;
    dbconfig.setCompress(false) ;
    dbconfig.setAllocatedExtra(50) ;
    Database<Homepage, MD5Hash> database = new Database<Homepage, MD5Hash>(dbconfig, dbplugin) ;
    
    DatabaseDefragmenter<Homepage, MD5Hash> optimizer = 
      new DatabaseDefragmenter<Homepage, MD5Hash>(database) ;
    optimizer.setIgnoreCorruptedRecord(true);
    optimizer.run() ;*/
  }
}
