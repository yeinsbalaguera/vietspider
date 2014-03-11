/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2007  
 */
import java.io.File;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 2, 2006  
 */
public class TestWebClient {

  private static WebClient webClient = new WebClient();

  public static byte[] loadContent(String address) throws Exception {
    //    URL url = new URL(address);

    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, null);

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);

      Header [] headers = httpGet.getAllHeaders();
      for(int i = 0; i < headers.length; i++) {
        System.out.println(headers[i].getName()+" : "+ headers[i].getValue());
      }

      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      System.out.println(httpResponse);

      int statusCode = httpResponse.getStatusLine().getStatusCode();
      System.out.println(" status code is "+ statusCode);

      //      Header [] headers = httpResponse.getAllHeaders();
      //      for(Header header : headers) {
      //        System.out.println(header.getName() + " : " + header.getValue());
      //      }

      Header header = httpResponse.getFirstHeader("Content-Type");
      System.out.println(header.getValue());

      //      
      //      System.out.println(" \n\n chuan bi read "+ address);

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      byte [] bytes = null; 

      long start = System.currentTimeMillis();

      //      HttpEntity httpEntity = httpResponse.getEntity();
      //      bytes = webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());

      bytes = httpResponseReader.readBody(httpResponse);

      long end = System.currentTimeMillis();
      //      System.out.println(" doc het "+ (end - start)+ " s");

      System.out.println(" bytes size "+ bytes.length);

      return bytes;

    } catch(Exception exp){
      exp.printStackTrace();
      //      LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    //    String home  = "http://vietnamnet.vn/thegioi/";
    //    String home = "http://newsdaily.vn/Vietnam/./Details.aspx?View=1022";
    //    String home = "http://www.vinhphuc.gov.vn/render.userLayoutRootNode.uP";
    //    String home  = "http://vietsonic.com/Product_detail.asp?pID='UltraFlat'";
    //    String home = "http://www.binhdinh.gov.vn/BinhDinh/render.userLayoutRootNode.uP";
    //    String home  = "http://thuannd:9247/data/get/date";
    //    String home  = "http://www.quangtript.com.vn";
    //    String home  = "http://www.cand.com.vn/";
    //    String home  = "http://diendanlequydon.com/viewtopic.php?t=14110";
    //    String home  = "http://360.yahoo.com/profile-EO8TUQoofakBOZyrBrL.";
    //    String home = "http://localhost:8080/tuoitre2/";
    //    String home =  "http://blog.360.yahoo.com/blog-o_2VJewgbqfL2HJEtXaTzW5_k8508jdY?tag=vietdart";
    //    String home = "http://360.yahoo.com/profile-CcIyptwlc6esXyzRsondCUxgTA9J";
    //    String home = "https://forum.defcon.org/showthread.php?t=3741";
    //    String home = "http://www.javavietnam.org/javavn/mvnforum/listrecentthreads?offset=10";
    //    String home = "http://www.chf.com.vn/?cmd=act:main|cat:25";
    //    String home  = "http://www.trananh.vn/?modul=cart&pag=add&id=1335";
    //    String home  = "http://www9.ttvnol.com/forum/f_438/933099.ttvn";
    //    String home = "http://thuannd:9245/";
    //    String home  = "http://abcnews.go.com/\"/International/story?id=5803011&page=1%252F%2522";
    //    String home  = "http://my.opera.com/trangiahuy/blog/index.dml/tag/chuc'%20cac'%20ban..%20mot..%20ngay`%20zui%20ze~";
    //    String home = "http://www.talawas.org/talaDB/showFile.php?res=9681&rb=0401";
    //    String home = "http://www.hastc.org.vn/weblink.asp?";
    //    String home  = "http://www.xe24h.net/?NEWS/VN/Tintucthitruong/Thitruongtrongnuoc/2901.3S";
    //    String home = "http://myworld.vn/blog/maihung/blog/view/43405";
    //    String home = "http://huydiemmobile.com/index.asp";
    //    String home  = "http://www.dpi.hochiminhcity.gov.vn/vie/webappdn/sqlresult.asp?whichpage=4&pagesize=20&fpage=1&ht=DT";
    //    String home = "http://ttvnol.com/forum/tacphamvanhoc/1168656.ttvn";
    //    String home = "http://nhuthuan.blogspot.com/2009/06/ban-test-truoc-cua-vietspider-3-build.html";
    //    String home  = "http://www.dienbien.gov.vn/c/portal/render_portlet?p_l_id=48807&p_p_id=vcmsviewcontent_INSTANCE_yD3H&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-20&p_p_col_pos=0&p_p_col_count=1&currentURL=%2Fweb%2Fguest%2Fct_chinhquyen%3Fp_p_id%3Dvcmsviewcontent_INSTANCE_yD3H%26p_p_lifecycle%3D0%26p_p_col_id%3Dcolumn-7%26p_p_col_pos%3D2%26p_p_col_count%3D4%26_vcmsviewcontent_INSTANCE_yD3H_struts_action%3D%252Fvcmsviewcontent%252Fview%26_vcmsviewcontent_INSTANCE_yD3H_articleId%3D8203%26_vcmsviewcontent_INSTANCE_yD3H_categoryId%3D107&_vcmsviewcontent_INSTANCE_yD3H_categoryId=107&_vcmsviewcontent_INSTANCE_yD3H_struts_action=%2Fvcmsviewcontent%2Fview&_vcmsviewcontent_INSTANCE_yD3H_articleId=8203";
    //    String home = "http://www.duythinh.com.vn/hinhanh.php?id=303&rnd=373322&type=1";
    //    String home  = "http://chotructuyen.com/ajax/product_detail.php?divid=detail11_d18730&product_id=18730";
    //    String home  = "http://search.yahoo.com/mrss/category_schema";
    //    String home = "http://www.azchiroboard.us/DaEngine.asp";
    //    String home = "http://tinnhanhmuaban.com/news/modules.php?name=Raovat&func=displayad&id=918";
    //    String home  = "http://enbac.com/Do-cho-nam/p464608/style/images/spacer.gif";
    //    String home  = "http://cgi.ebay.com/New-Microsoft-Zune-HD-3-3-Black-16GB-MP3-MP4-Player_W0QQitemZ300382410151QQcmdZViewItemQQptZOther_MP3_Players?hash=item45f02fd5a7#ht_6175wt_1165";
    String home = "http://www.foody.vn/";

    //    String home  = "https://login.yahoo.com/config/login?";

    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);

    System.out.println(home);

    org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    webClient.setURL(null, new URL(home));
    webClient.registryISAProxy("fsoft-proxy", 8080, "thuannd2", "kontung131");

    DefaultHttpClient httpClient = (DefaultHttpClient)webClient.getHttpClient();
    

//    BasicClientCookie cookie = new BasicClientCookie("ASP.NET_SessionId", 
//    "rcwa5lhf2qvwukpfxtrw050k");
//
//    cookie.setDomain(".foody.vn");
//    cookie.setPath("/");
//    Calendar calendar = Calendar.getInstance();
//    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
//    cookie.setExpiryDate(calendar.getTime());
//    httpClient.getCookieStore().addCookie(cookie);

    //    webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
    //    webClient.registryProxy("216.194.70.3", 8118, null, null);

    /* for(int i = 0; i < 10; i++) {
      byte[] obj = loadContent(home);
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      } 
    }*/
    home = "http://www.foody.vn/ho-chi-minh/dia-diem?vt=row&st=1&dt=undefined&c=1&page=1&provinceId=217&categoryId=1&append=true";
    byte[] obj = loadContent(home);
    if(obj == null) return;
    //    
    writer.save(new File("D:\\Temp\\a.html"), obj);
    System.exit(1);
  }

}
