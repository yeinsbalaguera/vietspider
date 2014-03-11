/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.nlp.AddressDetector;
import org.vietspider.nlp.impl.Addresses;
import org.vietspider.nlp.impl.Country;
import org.vietspider.nlp.impl.PlaceLoader;
import org.vietspider.nlp.place.PlaceDetector;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextSplitter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class AddressTestFolder {
  
  private static File noDataFolder = new File("D:\\Temp\\no_mail\\");
  private static Country country = null;

  public static void main(String[] args) throws Exception {
    UtilFile.deleteFolder(noDataFolder, false);
    
    try {
//      country = PlaceLoader.load();
      country = new PlaceLoader().load(new File("D:\\Temp\\place\\"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    
    PlaceDetector placeDetector = new PlaceDetector(country.getHN());
    
    for(int i = 6; i < 10; i++) {
      File folder = new File("D:\\Temp\\test" + String.valueOf(i) + "\\"); 
      process(placeDetector, folder);
    }
    
    System.out.println(placeDetector.getPlaceWords().toString());
  }
  
  private static void process(PlaceDetector placeDetector, File folder) throws Exception {
    File [] files = folder.listFiles();
    for(int i = 0; i< files.length; i++) {
      if(files[i].isDirectory()) continue;
      String text = new String(RWData.getInstance().load(files[i]), "utf-8");
      
//      if(text.indexOf("Khu Đô Thị Mỹ Phước 4 nằm cách Q1 TP Hồ Chí Minh") > -1
//          || text.indexOf("Đường Nguyễn Thị Tần, Quận 8") > -1) {
//        System.out.println(files[i].getName());
//      }
      
//      LineSplitter splitter = new LineSplitter();
      TextSplitter splitter = new TextSplitter();
      TextElement root = splitter.split(text);
      Addresses addresses = AddressDetector.getInstance().detectAddresses(country, root);
      if(addresses.list().size() < 1) {
        RWData.getInstance().copy(files[i], new File(noDataFolder, files[i].getName()));
        continue;
      }
      
//      if(!addresses.list().get(0).getPlace().getName().equals("khánh hòa")) continue;
      
//      System.out.println("\n==============================================");
//      System.out.println(files[i].getName());
//      
//      String [] values = addresses.toAddress(false, true);
//      for(String value : values) {
//        System.out.println(value);
//      }
//      
//      System.out.println(addresses.getAddress());
//      if(addresses.isChinhchu()) System.out.println("Chính chủ");
      
      placeDetector.detect(addresses);
    }
  }

}
