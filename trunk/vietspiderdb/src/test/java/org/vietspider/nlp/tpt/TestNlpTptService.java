/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.tpt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2011  
 */
public class TestNlpTptService {
  
  private static void print(NlpTptModel model) {
    if(model == null) return;
    System.out.println("id : " + model.getId());
    System.out.println("title: "+ model.getTitle());
    System.out.println("mobile : " + model.getMobile());
    System.out.println("telephone : " + model.getTelephone());
    System.out.println("address : " + model.getAddress());
    System.out.println("email : " + model.getEmail());
    System.out.println("area : " + model.getArea());
    System.out.println("price : " + model.getPrice());
    System.out.println("action object : " + model.getAction_object());
    System.out.println("=================================");
  }
  
  private static void testTptById(String id) {
    NlpTptStorage storage = NlpTptService.getInstance().getStorage();
    NlpTptModel model = storage.getById(id);
    if(model == null) {
      System.out.println("not found data ");
      return;
    }

    List<NlpTptModel> models  = new ArrayList<NlpTptModel>();
    
    String phone = model.getTelephone();
    if(phone != null) {
      List<NlpTptModel> list = storage.getByTelephone(phone);
      if(list != null) models.addAll(list);
    }

    phone = model.getMobile();
    if(phone != null) {
      List<NlpTptModel> list = storage.getByMobile(phone);
      if(list != null) models.addAll(list);
    }

    String email = model.getEmail();
    if(email != null) {
      List<NlpTptModel> list = storage.getByEmail(email);
      if(list != null) models.addAll(list);
    }

    print(model);
    
    System.out.println(" found " + models.size());
    
    for(int i = 0; i < models.size(); i++) {
      if(model.getId().equals(models.get(i).getId())) continue;
//      if(NlpTptService.getInstance().equals(model, models.get(i))) {
        print(models.get(i));
//      }
    }
  }
  
  private static void testTpt(String phone) {
    NlpTptStorage storage = NlpTptService.getInstance().getStorage();
    storage.test();
    List<NlpTptModel> list = storage.getByMobile(phone);
    if(list == null) {
      System.out.println(" null data ");
      return;
    }
    for(int i = 0; i < list.size(); i++) {
      NlpTptModel model = list.get(i);
      System.out.println(model.getAddress());
    }
  }

  public static void main(String[] args) {
    try {
      File file = new File(NlpTptService.class.getResource("").toURI());
      String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+".."
      +File.separator+"..";
      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
      file  = new File(path);
      
      System.out.println(file.getCanonicalPath());

      //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      Application.PRINT = false;
      
//      testTpt("0918456074");
      
//      testTptById("201102230241120058");
      NlpTptService.getInstance().deleteExpire(30);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(1);
    }
  }
}
