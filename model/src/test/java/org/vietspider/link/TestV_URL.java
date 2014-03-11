/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 9, 2009  
 */
public class TestV_URL {
  public static void main(String[] args) {
//    String address  = "http://ddth.com/index.php";
    String address = "http://moom.vn/Setup.do?searchState=terms%3D\\%3Blocale%3Dvn\\%3Blang%3Dall\\%3Btype%3DCLASSIFIED&dbtype=CONTENTS";
    
    address = "http://us.mc01g.mail.yahoo.com/mc/welcome?.gx=1&.rand=76jkptcrp5fbo#_pg=showFolder;_ylc=X3oDMTBudWdoYjF0BF9TAzM5ODMwMTAyMARhYwNkZWxNc2dz&&filterBy=&fid=Inbox&.rand=1941887761&nsc&hash=550b172053e0e3a90b3fce53d96441af&.jsrand=6084501";
    
    address = "http://1088.net.vn/index.php/index.php/aaa/component/user/?param=b&&&vanhoa=a&vanhoa=a&vanhoa=a&vanhoa=a&vanhoa=a&" ;
    
    address = "http://1088.net.vn/index.php/index.php//aaa/component/user/component/user/component/user/#ja-mainnav" ;
    
    System.out.println(new V_URL(address, null).toNormalize());
    
    address = "http://www.vtc.vn/xahoi/video-phot-lo-lenh-cam-xe-qua-tai-van-qua-cau-duong/214963/index.htm";
    String a1 = new V_URL(address, null).toNormalize();
    
    
    address = "http://vtc.vn/xahoi/video-phot-lo-lenh-cam-xe-qua-tai-van-qua-cau-duong/214963///index.htm";
    String a2 = new V_URL(address, null).toNormalize();
    System.out.println(a1.equals(a2));
    
    V_URL v_url = new V_URL("http:///sdfksjdfjdsi");
   
    
    address = "http://1088.net.vn/index.php/component/content/article/74-tin-tc-khoa-hc/518-ao-to-ch-ca-hang-sa-in-thoi-di-ng/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href/component/content/this.href";
    v_url = new V_URL(address);
    System.out.println(v_url.toNormalize());
    
    address = "http://1asdas88.net.vn/raovats.com/tin-tuc/mua-ban-tu-dien-rao-vat/tim-kiem/0/on/10/0/all";
    v_url = new V_URL(address);
    System.out.println(v_url.toNormalize());
    
  }
} 
