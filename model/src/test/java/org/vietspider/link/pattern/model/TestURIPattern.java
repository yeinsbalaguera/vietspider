/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class TestURIPattern extends TestCase {

  @Test
  public void testTreeMap() throws Exception {
    String pattern;
    String value;

    pattern = "*";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://www.hinhdongphatgiao.com/@/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.hinhdongphatgiao.com/*/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.hinhdongphatgiao.com/$/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://www.hinhdongphatgiao.com/@";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/abc";
    value = "http://www.hinhdongphatgiao.com/forum/abc";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/abc/";
    value = "http://www.hinhdongphatgiao.com/forum/abc/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/*/";
    value = "http://www.hinhdongphatgiao.com/forum/fsdfdsfds/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://hoiquantinhoc.com/forum/showthread.php?t=*";
    value = "http://hoiquantinhoc.com/forum/showthread.php?t=3176";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://hoiquantinhoc.com/forum/*.php?t=*&sss=*";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=*&sss=@";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=$&sss=$";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=$&sss=$";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=31a76&sss=4554";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://hoiquantinhoc.com/forum/*?t=*&sss=*";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    //debug www o dau 
    pattern = "http://*.thanhnien.com.vn/*/*/*/*/*.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://*.thanhnien.com.vn/@/$/$/*/$.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://*.thanhnien.com.vn/@/@/$/*/$.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://www7.thanhnien.com.vn/*/*/*/*/*.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://localhost:8080/tuoitre2/Index.aspx-ArticleID=*&ChannelID=*.htm";
    value  = "http://localhost:8080/tuoitre2/Index.aspx-ArticleID=167356&ChannelID=10.htm";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://www.hiendaihoa.com/*.php?Conpany=*&CopyRight=hiendaihoa.com&lang=*&id=*&Article=*";
    value  = "http://www.hiendaihoa.com/automoto_detail.php?Conpany=HiendaihoaJSC&CopyRight=hiendaihoa.com&lang=vn&id=1619&Article=D%C3%A2n%20%C4%91%E1%BA%A1o%20ch%C3%ADch%20%E1%BB%9F%20M%E1%BB%B9";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern  = "http://www.hiendaihoa.com/*.php?Conpany=*&CopyRight=*&lang=vn&id=*&Article=*&Designer=*&MyEmail=*";
    value = "http://www.hiendaihoa.com/automation_detail.php?Conpany=HiendaihoaJSC&CopyRight=hiendaihoa%2Ecom&lang=vn&id=5372&Article=UNO%2D1150%3A%20m%C3%A1y%20t%C3%ADnh%20t%E1%BB%B1%20%C4%91%E1%BB%99ng%20h%C3%B3a%20nh%C3%BAng%20c%E1%BB%A7a%20Advantage&Designer=NguyenVanNgan&MyEmail=mail@hiendaihoa.com";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "toggle_info('../ajax/product_detail.php?divid=*&product_id=*','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=dfs&product_id=35sdfsdf','23432')";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "toggle_info(\'*\','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf','23432')";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://*.9kute.com/blogs.html,*=";
    value = "http://capuchino.9kute.com/blogs.html,aJ9xBoOla2McaTHfL29gBzIhqUWcMKZ=";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://*.9kute.com/blogs.html,$=";
    value = "http://capuchino.9kute.com/blogs.html,aJ9xBoOla2McaTHfL29gBzIhqUWcMKZ=";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://muabanperfume.com/*/*";
    value = " http://muabanperfume.com/hieu-nuoc-hoa/117/BVLgari.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://www.gsmvn.vn/forum/f*/";
    value = "http://www.gsmvn.vn/forum/f17/";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://ttvnol.com/forum/f_*/*.ttvn";
    value = "http://ttvnol.com/forum/f_69/1178888.ttvn";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);

    pattern = "http://vatgia.com/raovat/*/*.html";
    value = "http://vatgia.com/raovat/3562/tang-101-cuon-sach-tieng-anh%3A-gia-bia-110-000d.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://huydiemmobile.com/ProductList.asp?id=*";
    value =  "http://wwww.huydiemmobile.com/ProductList.asp?ID=4";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.muaban.net/*/Default.aspx?CT=AD&AID=*&NID=*&SID=*#0";
    value = "http://muaban.net/miennam/default.aspx?CT=AD&AID=6201&NID=398929&SID=199502#0";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.muaban.net/*/Default.aspx?CT=AD&AID=*&NID=*&SID=*";
    value = "http://muaban.net/miennam/default.aspx?CT=AD&AID=6201&NID=398929&SID=199502#0";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://www.megabuy.vn/?a=PRO&pro=LIST&hdn_category_id=*&hdn_order_by=PRICEASC&hdn_offset=*";
    value = "http://www.megabuy.vn/?a=PRO&pro=LIST&hdn_category_id=326&hdn_order_by=PRICEASC&hdn_offset=60";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://vatgia.com/austech&module=product&view=listudv&record_id=317&page=*";
    value = "http://vatgia.com/austech&module=product&view=listudv&record_id=317&page=2";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.nhipcaunhadat.com/*.asp";
    value = "http://www.nhipcaunhadat.com/efault.asp?DisplayPage=104&ban-nha-4-tang-mat-pho-trung-tam-gan-ho-s=65m2-224.asp";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);

    pattern = "http://chotructuyen.com/product/../ajax/product_detail.php?divid=*&product_id=*";
    value  = "http://chotructuyen.com/ajax/product_detail.php?divid=detail11_d18730&product_id=18730";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://www.chokimbien.com/for@um/*/*.html";
    value = "http://www.chokimbien.com/for@um/thoi-trang-nam/931-thoi-trang-hoa-hau-nguoi-mau-nong-bong-ca-sy-yeu-thich.html#post1250";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*/$.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam/8.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*/$.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam%28gia=2-3%";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://muare.vn/$/";
    value = "http://muare.vn/ThoiTrangNu/2498906";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://muare.vn/$/";
    value = "http://muare.vn/ThoiTrangNu";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://www.vietnamnetwork.org/@/$-*.html";
    value = "http://www.vietnamnetwork.org/dich-vu-gia-dinh/33556-gia-su-day-ka-m-anh-ngu-giao-tiep-nang-cao-phu-hop-voi-moi-doi-tuong.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://www.vietnamnetwork.org/*/$-@.html";
    value = "http://www.vietnamnetwork.org/members/dangtin310.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://cuahangdocu.com/index.php?mod=classified&cat=*&classified_id=$";
    value = "http://cuahangdocu.com/index.php?mod=classified&cat=Domain,Web,Hosting&classified_id=2373";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://forum.gocmuaban.com/*.html";
    value  = "http://forum.gocmuaban.com/j600-may-dep-gia-re-vo-cung-75886.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://*.jaovat.com/*-cat-*";
    value = "http://www.jaovat.com/phu-nu-tim-dan-ong-cat-292";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    pattern = "http://thanhphohochiminhcity.jaovat.com/*-iid-$";
    value = "http://thanhphohochiminhcity.jaovat.com/tim-ban-gai-de-tam-su-iid-60273622";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
    
    value = "http://thanhphohochiminhcity.jaovat.com/dan-ong-tim-phu-nu-cat-293";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    value = "http://hanoicity.jaovat.com/xe-mo-to-xe-may-cat-379-p-6";
    Assert.assertEquals(new URIPattern(pattern).match(value), false);
    
    pattern = "http://www.bizrate.com/*/*/*.html";
    value = "http://www.bizrate.com/tablet-computers/2593835597/index__ctr_brand--43160__ctr_pos--BR%3BUS%3B10J%3Bhot%2FHotListViewFixed%3B1%3B20%3BProductListPod%3Bproductpod%2FProductPodListViewFixed%3Bvisible%3B4%3Bmain%3B1__ctr_rel--3712451870720.000000.html";
    Assert.assertEquals(new URIPattern(pattern).match(value), true);
  }
}
