/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateRongBayLink2 {

  public static void main(String[] args) throws Exception {
    String [] elements  =  {
        "c98/Dich-vu-tan-nha/page-",  
        "c276/Mua-sam-Gia-dinh/page-",
        "c278/Cho-Sim/page-",
        "c15/Mua-Ban-nha-dat/page-",
        "c280/Dien-lanh-Dien-may/page-",
        "c2/Dien-thoai/page-",

        "c272/Thue-va-cho-thue-nha/page-",
        "c279/Dien-tu-Ky-thuat-so/page-",
        "c19/O-to/page-",
        "c264/Lao-dong-tri-oc/page-",
        "c100/Co-hoi-giao-thuong/page-",
        "c20/Xe-may-Xe-dap/page-",


        "c69/Lao-dong-pho-thong/page-",
        "c277/Dich-vu-Do-van-phong/page-",
        "c1/May-tinh-P-Mem-Mang/page-",
        "c266/Thoi-trang-My-pham/page-",
        "c83/Du-lich-Dia-diem-vui-choi/page-",
        "c275/Tong-Hop/page-"
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
