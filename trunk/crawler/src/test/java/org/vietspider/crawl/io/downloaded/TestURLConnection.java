/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2011  
 */
public class TestURLConnection {
  public static void main(String[] args) throws Exception {
    URL yahoo = new URL("http://www.saninfo.vn/default.aspx?__DNNCAPISCI=ViewAnnouncement dnn_ctr1004_ViewAnnouncement&__DNNCAPISCP=%2BW9h6XHudMt3iketPH7Hvx%2BPt8%2FkCWh54L%2B4SYSdBYzfNsGaVQiIobZddPAB7Exw&dnn$ctr1004$ViewAnnouncement$_viewState=%2BW9h6XHudMt3iketPH7Hvx%2BPt8%2FkCWh54L%2B4SYSdBYzfNsGaVQiIobZddPAB7Exw&");
    URLConnection yc = yahoo.openConnection();
    yc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
    BufferedReader in = new BufferedReader(
        new InputStreamReader(yc.getInputStream()));
    String inputLine;

    while ((inputLine = in.readLine()) != null) 
      System.out.println(inputLine);
    in.close();

  }
}
