/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.handler;

import java.io.File;
import java.io.FileFilter;
import java.nio.channels.ClosedByInterruptException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.vietspider.bean.Image;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 12, 2007
 */
public class ImageIO {
  
  private String catcheDate = "" ; 
  private File folder;
  
  private Pattern punctPattern = Pattern.compile("\\p{Punct}");
  
  private String root;
  
  public ImageIO(String _root) {
    this.root = _root;//"content/images/"
  }
  
  public void saveImage(Image image, byte [] data, String date) {
    if(!date.equals(catcheDate)) {
     catcheDate = date;
     Date calendar = null;
     try {
       calendar = CalendarUtils.getDateFormat().parse(date);
     } catch (Exception e) {
       LogService.getInstance().setThrowable(e);
       return;
     }
     folder  = UtilFile.getFolder(root + CalendarUtils.getFolderFormat().format(calendar));     
    }
    StringBuilder builder  = new StringBuilder().append(image.getId()).append('.');
    if(image.getName() == null || image.getName().trim().length() <1) builder.append(".jpg");    
    builder.append(image.getName());
    File file = new File(folder, builder.toString());
    try {
      RWData.getInstance().save(file, data);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public void saveImage(Calendar calendar, final Image image) {
    String today = CalendarUtils.getFolderFormat().format(calendar.getTime());
    File  imgFolder  = UtilFile.getFolder(root + today);
    StringBuilder builder  = new StringBuilder().append(image.getId()).append('.');
    String name = image.getName();
    if(name != null && punctPattern.matcher(name).find()) name = null;
    builder.append(name == null ? ".jpg" : name);
    final File file = new File(imgFolder, builder.toString());
    final byte [] data = image.getImage(); 
//    new Thread(new Runnable(){
//      public void run() {
    try {
      RWData.getInstance().save(file, data);
    } catch (ClosedByInterruptException e) {
      LogService.getInstance().setMessage(e, "ImageSave: Close channel by exception");
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
//      }
//    }).start();
  }
  
  public void loadImage(Image image, String date) {
    Date calendar = null;
    try {
      calendar = CalendarUtils.getDateFormat().parse(date);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return ;
    }
    File saveFolder  = UtilFile.getFolder(root + CalendarUtils.getFolderFormat().format(calendar));
    loadImage(image, saveFolder);
  }
  
  public void loadImage2(final Image image, String dateFolder) {
    File saveFolder  = UtilFile.getFolder(root + dateFolder);
    loadImage(image, saveFolder);
  }
  
  public void loadImage(final Image image, File saveFolder) {
    File [] files = saveFolder.listFiles( new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.getName().startsWith(image.getId());
      }
    });
    if(files.length < 1) return ;
    try {
      byte [] data = RWData.getInstance().load(files[0]);
      image.setImage(data);
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
}
