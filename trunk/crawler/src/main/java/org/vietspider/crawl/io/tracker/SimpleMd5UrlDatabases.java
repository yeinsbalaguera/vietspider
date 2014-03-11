/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.tracker;



/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 25, 2007  
 */
public final class SimpleMd5UrlDatabases /*extends Md5UrlDatabases*/ {

 /* private FileDownloadedTracker tracker;

  SimpleMd5UrlDatabases(String group) throws Exception {
    super("track/url/" + group, "url", group, 1*24*60*60*1000, 5, 5*1024*1024);
    createTrackDownloaded();
  }

//  public boolean isEmptyTemp() { return codes.isEmpty(); }

  public void write(Link link) throws Throwable {
    super.write(link.getUrlId(), 1);
  }

  public boolean search(Link link) throws Throwable {
    if(isClose()) return true;
    int value = super.read(link.getUrlId());
    if(value != -1) {
//    System.out.println(" da thay link bi download "+ link.getAddress());
      return true;
    }
    
    if(tracker == null) return false;

    boolean exists = tracker.search(link.getCode(), false);
    if(exists) super.write(link.getUrlId(), 1);
    return exists;
  }

  private void createTrackDownloaded() {
    File folder = UtilFile.getFolder("track/");
    if(databases.size() > max) {
      UtilFile.deleteFolder(folder);
      return;
    }

    folder = new File(folder, "downloaded/");
    if(!folder.exists()) return;
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) {
      folder.delete();
      return;
    }

    folder = new File(folder, group);
    if(!folder.exists()) return;
    files = folder.listFiles();
    if(files == null || files.length < 1) {
      folder.delete();
      return;
    }

    tracker = new FileDownloadedTracker("track/downloaded/" + group+ "/");
  }*/

}
