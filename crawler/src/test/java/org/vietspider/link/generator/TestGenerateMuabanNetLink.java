/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateMuabanNetLink {

  public static void main(String[] args) throws Exception {
    String [] elements  =  {
        "2/viec-lam",  
        "3/viec-tim-nguoi",
//        "http://muaban.net/ho-chi-minh/raovat/20/nguoi-tim-viec/1"
        "33/bat-dong-san",
        "34/o-to",
        "32/tuyen-sinh-dao-tao",
        "35/xe-may",
        "36/dien-tu-dien-may",
        "37/do-dung-mat-hang-khac",
        "38/tim-doi-tac",
        "39/Dich-vu",
        "634/cong-dong"
        
    };
    
    for(int i = 0; i < elements.length; i++) {
      if(i%6 == 0) System.out.println("\n\n\n");
      for(int j = 1; j <= 150; j++) {
        String value  = "http://rongbay.com/" + elements[i] + String.valueOf(j)+".html";
        System.out.println(value);
      }
    }
  }

}
