/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
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
public class ImageHandler extends CMSHandler<Image> {

  public ImageHandler(String type) {
    super(type); 
    name = "IMAGE"; 
  }

  public  String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
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
    
//    if(image.getImage() == null) {
//    System.out.println(" =================== > "+ image.getId() + " : "+ image.getImage());
//    }

//    if(image == null) throw new NullPointerException("Image not found");
    return write(request, response, context, image, params);
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, Image image, String cookies[], String... params) throws Exception {
    output.write(image.getImage());
    return image.getType();
  }

}
