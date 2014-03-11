/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

import java.io.File;
import java.util.List;

import org.vietspider.bean.website.Website;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.locale.vn.VietnameseDataChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2009  
 */
public class TestWebsiteDetector {
  
  /*public static void main(String[] args) {
  VietnameseDataChecker langChecker = new VietnameseDataChecker();
  WebsiteDetector  thread  = new WebsiteDetector(null, langChecker);
  Website website = new Website();
  //http://www.5comm.vn
  website.setAddress("http://www.rubbergroup.vn");
  thread.start(website);

  try {
    Thread.sleep(30*1000);
  } catch (Exception e) {
  }

  System.out.println("thay co "+ website.getLanguage());

  if("en".equalsIgnoreCase(website.getLanguage())) {
    System.out.println("\n\n\n\n\n");
    System.out.println(thread.getHtml());
  }
  System.exit(0);
}
 */
  
  
  public static void main(String[] args) {
    WebsiteDetector detector = new WebsiteDetector(null, new VietnameseDataChecker()) {
      public void run() {
        start = System.currentTimeMillis();

        String html = null;
        List<NodeImpl> tokens = detect(website.getAddress());
        if(tokens != null) html = new TokenHtmlRenderer().buildHtml(tokens);
        
        website.setTimeDownload(website.getTimeDownload()+1);
        if(html == null) {
          if(website.getTimeDownload() >= 3) {
            website.setHtml("no content");
            website.setLanguage("en");
            website.setStatus(Website.UNAVAILABLE);
            WebsiteStorage.getInstance().save(website);
          }
          return;
        }

        String text = html.toLowerCase();
        if(text.indexOf("sponsored listings") > -1 
            || text.indexOf("buy this domain") > -1) {
          website.setStatus(Website.UNAVAILABLE);
        } else if(text.indexOf("under construction") > -1) {
          website.setStatus(Website.UNAVAILABLE);
        } else if(text.indexOf("invalid hostname") > -1 
            || text.indexOf("directory listing denied") > -1
            || text.indexOf("forbidden") > -1 
        ) {
          website.setStatus(Website.UNAVAILABLE);
        }
        
        if(website.getStatus() == Website.NEW_ADDRESS) {
          for(int i = 0; i < vietnamLabels.length; i++) {
            if(text.indexOf(vietnamLabels[i]) > -1) {
              website.setDesc("vietnam");
              break;
            }
          }
        }
        
        if(website.getLanguage().equalsIgnoreCase("en")) {
          text = html.toLowerCase();
          if(text.indexOf("sponsored listings") > -1 
              && text.indexOf("buy this domain") > -1) {
          }
        }
        
        File file  = new File("D:\\Temp\\a.html");
        try {
          org.vietspider.common.io.RWData.getInstance().save(file, html.getBytes("utf-8"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        System.out.println(website.getLanguage());
        
        website.setHtml(html);
        System.exit(0);
      }  
    };
    
    String address = "http://www.rubbergroup.vn";
//    address = "http://www.rubbergroup.vn/index.php";
    address = "http://linkhay.com/tin-moi/c12/cong-nghe/page2";
    address = "http://www.cic8.com";
    address = "http://www.vinasuncorp.com";
    address = "http://www.binhminh.com.vn";
    address = "http://www.case.vn/";
    address = "http://www.truongtienco.com.vn";
    address = "http://sonoivu.namdinh.gov.vn";
    address = "http://www.hungvuongtech.edu.vn";
    address = "http://www.tuvanviva.com";
    address = "http://www.caobinh.com";//xem lai check sai tieng viet
//    address = "http://www.metroid.com";
    address = "http://www.vn.fi/ktm";
//    address = "http://www.mayvanphong24h.net/";
//    address = "http://vietnamnet.vn/"; // check tieng viet
    
    //bug web
    //http://www.hiephoatrade.spaces.live.com
    //http://www.ruoumercy.com.vn
    
    Website _website = new Website();
    _website.setAddress(address);
    detector.start(_website);
    
  }
}
