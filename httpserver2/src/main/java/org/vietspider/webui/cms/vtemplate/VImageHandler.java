/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vietspider.bean.Image;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.handler.ImageIO;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class VImageHandler extends VTemplateHandler {

  public VImageHandler(String type) {
    super(type); 
    name = "IMAGE"; 
  }

  
  public void render(HttpRequestData hrd) throws Exception {
    String params [] = hrd.getParams();
    Image image = new Image();
    image.setId(params[0]);

    if(CrawlerConfig.SAVE_IMAGE_TO_FILE){
      Date date = new SimpleDateFormat("yyyyMMdd").parse(params[0].substring(0, 8));
      String dateValue = CalendarUtils.getFolderFormat().format(date);
      File folder  = UtilFile.getFolder("content/images/"+ dateValue);
      new ImageIO("content/images/").loadImage(image, folder);
    } 

    if(image.getImage() == null || image.getImage().length < 1) {
      image = DatabaseService.getLoader().loadImage(params[0]);
    }

    if(image.getImage() == null || image.getImage().length < 1) {
      Date date = new SimpleDateFormat("yyyyMMdd").parse(params[0].substring(0, 8));
      String dateValue = CalendarUtils.getFolderFormat().format(date);
      File folder  = UtilFile.getFolder("content/images/"+ dateValue);
      new ImageIO("content/images/").loadImage(image, folder);
    }
    
//    if(image == null) throw new Exception("Image not found");
    
    hrd.getOutput().write(image.getImage());
    hrd.setContentType(image.getType());
  }

}
