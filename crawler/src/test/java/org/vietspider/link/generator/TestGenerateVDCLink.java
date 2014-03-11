/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateVDCLink {
  
  public static void main(String[] args) throws Exception {
//    String  [] elements = {
//        "BCH","BCVT","BQL","BTL","CAND","CHLB","CLB","CN","CP","Cty","DV",
//        "ĐH","ĐSQ","GT","GTVT","HTX","KD","KH","KHKT","KHTN","KHXH","KHXH và NVQG",
//        "KT","KTQD","KTQS","KTTC","KTX","LD","LĐ","NN","NN và PTNT","NR","NXB","PCCC",
//        "PKKQ","PT","PTTH","QG","QL","QT","QĐND","SX","TBXH","TCTY","TDTT","THCS","TNCS",
//        "TNHH","TP","TT","TTX","TTDV", "TTâm","TW","TĐ","UB","UBND","VN","VP","VPĐD","XD","XH","XK","XN","XNK"
//    }; 
//    
//    
//    
//    for(int i = 0; i < 20; i++) {
//      for(int j = 0; j < 10; j++) {
//        StringBuilder builder = new StringBuilder("http://www.rugstomydoor.com/scripts/prodList.asp?pMaxItemsPerPage=24&curPage=");
//        builder.append(i);
//        builder.append("&strshape=&strprice=&strsecondcolor=&strtype=&strorigins=&strSearch=&strstyle=");
//        builder.append(j);
//        builder.append("&strmanufacturer=&strfiber=&strmaincolor=&srtcollection=&curPage2=1");
//        System.out.println(builder);
//      }
//    }
    
    for(int i = 0; i < 750; i+=30) {
        System.out.println("http://www.knx.org/nl/knx-partners/knxeib-partners/knx-partners-result/?order=company&limit=" + i + "&search=Show&countrycodes[]=31");
    }
    
  }
  
}
