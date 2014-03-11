/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.regex;

import junit.framework.Assert;

import org.junit.Test;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 5, 2011  
 */
public class TestNewURIPattern {

  @Test
  public void testTreeMap() throws Exception {
    String pattern;
    String value;
    
    pattern  = "http://www.dzone.com/links/r/benchmarking_html_5_and_javascript.html";
    value = "http://www.dzone.com/links/r/benchmarking_html_5_and_javascript.html";
    Assert.assertEquals(true, URIParser.parse(pattern).match(value));

    pattern = "http://www.dzone.com/links/r/*_html_5_and_javascript.html";
    value  = "http://www.dzone.com/links/r/benchmarking_html_5_and_javascript.html";
    Assert.assertEquals(true, URIParser.parse(pattern).match(value));
    
    pattern = "http://www.dzone.com/links/r/*_html_$_and_javascript.html";
    value  = "http://www.dzone.com/links/r/benchmarking_html_5_and_javascript.html";
    Assert.assertEquals(true, URIParser.parse(pattern).match(value));
    
    pattern = "http://www.dzone.com/links/r/*_html_$_and_javascript.html";
    value  = "http://www.dzone.com/links/r/benchmarking_html_a_and_javascript.html";
    Assert.assertEquals(false, URIParser.parse(pattern).match(value));
    
    pattern = "*";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(false, URIParser.parse(pattern).match(value));
    
    pattern = "http://www.hinhdongphatgiao.com/@/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.hinhdongphatgiao.com/*/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.hinhdongphatgiao.com/$/";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://www.hinhdongphatgiao.com/@";
    value = "http://www.hinhdongphatgiao.com/forum/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/abc";
    value = "http://www.hinhdongphatgiao.com/forum/abc";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/abc/";
    value = "http://www.hinhdongphatgiao.com/forum/abc/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://www.hinhdongphatgiao.com/*/*/";
    value = "http://www.hinhdongphatgiao.com/forum/fsdfdsfds/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://hoiquantinhoc.com/forum/showthread.php?t=*";
    value = "http://hoiquantinhoc.com/forum/showthread.php?t=3176";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://hoiquantinhoc.com/forum/*.php?t=*&sss=*";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=*&sss=@";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=$&sss=$";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://hoiquantinhoc.com/forum/*.php?t=$&sss=$";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=31a76&sss=4554";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://hoiquantinhoc.com/forum/*?t=*&sss=*";
    value = "http://hoiquantinhoc.com/forum/showsthread.php?t=3176&sss=4554";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    //debug www o dau 
    pattern = "http://*.thanhnien.com.vn/*/*/*/*/*.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://*.thanhnien.com.vn/@/$/$/*/$.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://*.thanhnien.com.vn/@/@/$/*/$.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://www7.thanhnien.com.vn/*/*/*/*/*.tno";
    value = "http://www.thanhnien.com.vn/Chaobuoisang/2008/8/30/259128.tno";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://localhost:8080/tuoitre2/Index.aspx-ArticleID=*&ChannelID=*.htm";
    value  = "http://localhost:8080/tuoitre2/Index.aspx-ArticleID=167356&ChannelID=10.htm";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://www.hiendaihoa.com/*.php?Conpany=*&CopyRight=hiendaihoa.com&lang=*&id=*&Article=*";
    value  = "http://www.hiendaihoa.com/automoto_detail.php?Conpany=HiendaihoaJSC&CopyRight=hiendaihoa.com&lang=vn&id=1619&Article=D%C3%A2n%20%C4%91%E1%BA%A1o%20ch%C3%ADch%20%E1%BB%9F%20M%E1%BB%B9";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern  = "http://www.hiendaihoa.com/*.php?Conpany=*&CopyRight=*&lang=vn&id=*&Article=*&Designer=*&MyEmail=*";
    value = "http://www.hiendaihoa.com/automation_detail.php?Conpany=HiendaihoaJSC&CopyRight=hiendaihoa%2Ecom&lang=vn&id=5372&Article=UNO%2D1150%3A%20m%C3%A1y%20t%C3%ADnh%20t%E1%BB%B1%20%C4%91%E1%BB%99ng%20h%C3%B3a%20nh%C3%BAng%20c%E1%BB%A7a%20Advantage&Designer=NguyenVanNgan&MyEmail=mail@hiendaihoa.com";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "toggle_info('../ajax/product_detail.php?divid=*&product_id=*','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=dfs&product_id=35sdfsdf','23432')";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "toggle_info(\'*\','*')";
    value = "toggle_info('../ajax/product_detail.php?divid=df&product_id=35sdfsdf','23432')";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://*.9kute.com/blogs.html,*=";
    value = "http://capuchino.9kute.com/blogs.html,aJ9xBoOla2McaTHfL29gBzIhqUWcMKZ=";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://*.9kute.com/blogs.html,$=";
    value = "http://capuchino.9kute.com/blogs.html,aJ9xBoOla2McaTHfL29gBzIhqUWcMKZ=";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://muabanperfume.com/*/*";
    value = " http://muabanperfume.com/hieu-nuoc-hoa/117/BVLgari.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://www.gsmvn.vn/forum/f*/";
    value = "http://www.gsmvn.vn/forum/f17/";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://ttvnol.com/forum/f_*/*.ttvn";
    value = "http://ttvnol.com/forum/f_69/1178888.ttvn";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);

    pattern = "http://vatgia.com/raovat/*/*.html";
    value = "http://vatgia.com/raovat/3562/tang-101-cuon-sach-tieng-anh%3A-gia-bia-110-000d.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://huydiemmobile.com/ProductList.asp?id=*";
    value =  "http://wwww.huydiemmobile.com/ProductList.asp?ID=4";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.muaban.net/*/Default.aspx?CT=AD&AID=*&NID=*&SID=*#0";
    value = "http://muaban.net/miennam/default.aspx?CT=AD&AID=6201&NID=398929&SID=199502#0";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.muaban.net/*/Default.aspx?CT=AD&AID=*&NID=*&SID=*";
    value = "http://muaban.net/miennam/default.aspx?CT=AD&AID=6201&NID=398929&SID=199502#0";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://www.megabuy.vn/?a=PRO&pro=LIST&hdn_category_id=*&hdn_order_by=PRICEASC&hdn_offset=*";
    value = "http://www.megabuy.vn/?a=PRO&pro=LIST&hdn_category_id=326&hdn_order_by=PRICEASC&hdn_offset=60";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://vatgia.com/austech&module=product&view=listudv&record_id=317&page=*";
    value = "http://vatgia.com/austech&module=product&view=listudv&record_id=317&page=2";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.nhipcaunhadat.com/*.asp";
    value = "http://www.nhipcaunhadat.com/efault.asp?DisplayPage=104&ban-nha-4-tang-mat-pho-trung-tam-gan-ho-s=65m2-224.asp";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);

    pattern = "http://chotructuyen.com/product/../ajax/product_detail.php?divid=*&product_id=*";
    value  = "http://chotructuyen.com/ajax/product_detail.php?divid=detail11_d18730&product_id=18730";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.chokimbien.com/for@um/*/*.html";
    value = "http://www.chokimbien.com/for@um/thoi-trang-nam/931-thoi-trang-hoa-hau-nguoi-mau-nong-bong-ca-sy-yeu-thich.html#post1250";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*/$.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam/8.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://muaban.net/ha-noi/raovat/$/*/$.html";
    value = "http://muaban.net/ha-noi/raovat/2/viec-lam%28gia=2-3%";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://muare.vn/$/";
    value = "http://muare.vn/ThoiTrangNu/2498906";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://muare.vn/$/";
    value = "http://muare.vn/ThoiTrangNu";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://www.vietnamnetwork.org/*/$-*.html";
    value = "http://www.vietnamnetwork.org/dich-vu-gia-dinh/33556-gia-su-day-ka-m-anh-ngu-giao-tiep-nang-cao-phu-hop-voi-moi-doi-tuong.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://www.vietnamnetwork.org/*/$-@.html";
    value = "http://www.vietnamnetwork.org/members/dangtin310.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://cuahangdocu.com/index.php?mod=classified&cat=*&classified_id=$";
    value = "http://cuahangdocu.com/index.php?mod=classified&cat=Domain,Web,Hosting&classified_id=2373";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://forum.gocmuaban.com/*.html";
    value  = "http://forum.gocmuaban.com/j600-may-dep-gia-re-vo-cung-75886.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://*.jaovat.com/*-cat-*";
    value = "http://www.jaovat.com/phu-nu-tim-dan-ong-cat-292";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://thanhphohochiminhcity.jaovat.com/*-iid-$";
    value = "http://thanhphohochiminhcity.jaovat.com/tim-ban-gai-de-tam-su-iid-60273622";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    value = "http://thanhphohochiminhcity.jaovat.com/dan-ong-tim-phu-nu-cat-293";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    value = "http://hanoicity.jaovat.com/xe-mo-to-xe-may-cat-379-p-6";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://batdongsanaz.vn/detail-sellproperty-view-$-$-$_*.html";
    value = "http://batdongsanaz.vn/detail-sellproperty-view-1-25-499_ban-nha-tho-cu-khu-918-phuong-phuc-donglong-bien.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    value = "http://batdongsanaz.vn/menu-link-1-11-1_an-cu-company.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://batdongsantq.com/diendan/*/$-*.html";
    value = "http://batdongsantq.com/diendan/bat-dong-san-ban/32922-ucity-song-da-ucity-van-khe-chung-cu-ucity-can-ho-ucity-hinh-thuc-uy-quyen.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    value = "http://batdongsantq.com/diendan/bat-dong-san-ban/index8.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), false);
    
    pattern = "http://timkiemnhadat.com.vn/*_$.html";
    value = "http://timkiemnhadat.com.vn/he-thong-website-quang-cao-bat-dong-san-mien-phi-lon-nhat-viet-nam_n_315_322_55917.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://timkiemnhadat.com.vn/*_$.html";
    value = "http://timkiemnhadat.com.vn/he-thong-website-quang-cao-_5bat-dong-san-mien-phi-lon-nhat-viet-nam_n_315_322_55917.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://timkiemnhadat.com.vn/*_@.html";
    value = "http://timkiemnhadat.com.vn/he-thong-website-quang-cao-bat-dong-san-mien-phi-lon-nhat-viet-nam_n_ASe_322_CVsA.html";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    pattern = "http://quangcaoso.vn/diendan/showthread.php?$-*";
    value = "http://quangcaoso.vn/diendan/showthread.php?462-Can-ho-Hiep-Tan-Quan-Tan-Phu-Gia-chi-610-trieu-m-";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    value  = "http://quangcaoso.vn/diendan/showthread.php?917-Can-ho-Thao-Dien-Pear-goi-ngay-de-duoc-gia-tot-nhat-view-dep-tang-cao&s=8db94c1e980aa86cbf5c7c3055a0db40";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
    
    value  = "http://maps.google.com/maps/place?cid=532685203575765619&q=Cafe+near+Ba+%C4%90%C3%ACnh,+%C4%90%E1%BB%91ng+%C4%90a,+H%C3%A0+N%E1%BB%99i,+Vi%E1%BB%87t+Nam&hl=en&ved=0CKMBEPoLMAk&sa=X&ei=owMLT8GAOqHHmQWS69mvBg&sig2=uyI17E5cQZSFpcZuBQ1iLQ";
    pattern = "http://maps.google.com/maps/place?cid=*&q=*&hl=*&ved=*&sa=*&ei=*&sig2=*";
    Assert.assertEquals(URIParser.parse(pattern).match(value), true);
  }
}
