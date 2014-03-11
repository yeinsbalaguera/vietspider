/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.ui.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.vietspider.common.io.UtilFile;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 4, 2007
 */
public class ImageLoader {
  
  public Image load(Display dis, String name){
    try {
      File file = new File(UtilFile.getFolder("client/resources/"), "images/" + name);
      if(file.exists() && file.length() > 0) {
        ImageData imgData = new ImageData(new FileInputStream(file));      
        return new Image(dis, imgData);
      }
      InputStream input = ImageLoader.class.getResource("/resources/images/" + name).openStream();     
      ImageData imgData = new ImageData(input);      
      return new Image(dis, imgData);
    } catch( Exception exp){
    	System.out.println(name);
    	exp.printStackTrace();
//      ClientLog.getInstance().setMessage(name +" : "+exp.toString());
      return null;
    }
  }
  
//  public InputStream load(String name){
//    try{
//      ClassLoader cl = ImgLoader.class.getClassLoader() ;
//      return cl.getResourceAsStream("resources/" + name) ;     
//    }catch( Exception exp){
//      ClientLog.getInstance().setMessage(name +" : "+exp.toString());
//      return null;
//    }
//  }
  
}
