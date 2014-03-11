/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.html.refs;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
public class TestCharsRef {
  
  public static void main(String arg[]){
    RefsDecoder ref = new RefsDecoder();
//    String text =  "n&#7919; chuy&#234;n gia h&#432;&#7899;ng d&#7851;n di&#7877;n xu&#7845;t t&#7915;ng truy&#7873;n ngh&#7873; cho c&#244;ng ch&#250;a nh&#7841;c pop kh&#7859;ng &#273;&#7883;nh, Britney kh&#244;ng bao gi&#7901; tr&#7903; th&#224;nh m&#7897;t di&#7877;n vi&#234;n c&#243; h&#7841;ng. Nh&#7919;ng nh&#7853;n x&#233;t c&#7911;a c&#244; gi&#225;o gi&#7845;u t&#234;n n&#224;y c&#243; th&#7875; s&#7869; khi&#7871;n ca s&#297; 24 tu&#7893;i x&#226;y x&#7849;m m&#7863;t m&#224;y.";
//    String text = "&nbsp;&nbsp;&nbsp;Ngày 11/11, trong các bu?i ti?p ?y viên Qu?c v? vi?n Trung Qu?c ???ng Gia Tri?n t?i Hà N?i,";
    //n&#7919; chuy&#234;n gia h&#432;&#7899;ng d&#7851;n di&#7877;n xu&#7845;t t&#7915;ng truy&#7873;n ngh&#7873; cho c&#244;ng ch&#250;a nh&#7841;c pop kh&#7859;ng &#273;&#7883;nh, Britney kh&#244;ng bao gi&#7901; tr&#7903; th&#224;nh m&#7897;t di&#7877;n vi&#234;n c&#243; h&#7841;ng. Nh&#7919;ng nh&#7853;n x&#233;t c&#7911;a c&#244; gi&#225;o gi&#7845;u t&#234;n n&#224;y c&#243; th&#7875; s&#7869; khi&#7871;n ca s&#297; 24 tu&#7893;i x&#226;y x&#7849;m m&#7863;t m&#224;y.";    
//    System.out.println(ref.decode(text.toCharArray()));
    
    RefsEncoder encoder = new RefsEncoder(false);
    char [] chars = "Thử encoder một đoạn text có @ và >".toCharArray();
    chars = encoder.encode(chars);
    System.out.println(new String(chars));
    chars = ref.decode(chars);
    System.out.println(new String(chars));
    //g&#7847;n 80 tu&#7893;i, kh&#244;ng
    
    /*Calendar calendar = Calendar.getInstance();
    String time  = String.valueOf(calendar.get(HOUR))+":"+  String.valueOf(calendar.get(MINUTE))+":"
         + String.valueOf(calendar.get(SECOND))+":" +  String.valueOf(calendar.get(MILLISECOND));   ;
    String imgFolder =  time.replaceAll(":", "_");
    System.out.println(time +" va "+imgFolder);*/
  }
}
