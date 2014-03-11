/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2007  
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 2, 2006  
 */
public class TestHttpMethodHandler2 {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address) throws Exception {
//    URL url = new URL(address);
    HttpMethodHandler methodHandler2 = new HttpMethodHandler(webClient);
    
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      HttpResponse httpResponse = methodHandler2.execute(address, null);
      if(httpResponse == null) return new byte[0];
      
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      System.out.println(" status code is "+ statusCode);
      
//      Header [] headers = httpGet.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : " + header.getValue());
//      }
//      
      System.out.println(" \n\n chuan bi read "+ address);
      
      
      long start = System.currentTimeMillis();
      InputStream inputStream  = httpResponse.getEntity().getContent();
      
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      
      byte [] bytes = new byte[8*1024];
      int read = -1;
      while ((read = inputStream.read(bytes)) > -1) {
        output.write(bytes, 0, read);
        System.out.println(" ====  > huhuhu " + read);
      }
      
      long end = System.currentTimeMillis();
      System.out.println(" doc het "+ (end - start)+ " s");
      
      System.out.println(" bytes size "+ bytes.length);
      
      return bytes;
      
    } catch(Exception exp){
      exp.printStackTrace();
//      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
//    String home = "http://vietnamnet.vn/thegioi/";
//    String home = "http://newsdaily.vn/Vietnam/./Details.aspx?View=1022";
//    String home = "http://www.vinhphuc.gov.vn/render.userLayoutRootNode.uP";
//    String home  = "http://vietsonic.com/Product_detail.asp?pID='UltraFlat'";
//    String home = "http://www.binhdinh.gov.vn/BinhDinh/render.userLayoutRootNode.uP";
//    String home = "http://thuannd:9247/data/get/date";
//    String home = "http://www.quangtript.com.vn";
//    String home = "http://www.cand.com.vn/";
//    String home = "http://diendanlequydon.com/viewtopic.php?t=14110";
//    String home = "http://360.yahoo.com/profile-EO8TUQoofakBOZyrBrL.";
//    String home = "http://localhost:8080/tuoitre2/";
//    String home =  "http://blog.360.yahoo.com/blog-o_2VJewgbqfL2HJEtXaTzW5_k8508jdY?tag=vietdart";
//    String home = "http://360.yahoo.com/profile-CcIyptwlc6esXyzRsondCUxgTA9J";
//    String home = "https://forum.defcon.org/showthread.php?t=3741";
//    String home = "http://www.javavietnam.org/javavn/mvnforum/listrecentthreads?offset=10";
//    String home = "http://www.chf.com.vn/?cmd=act:main|cat:25";
//    String home = "http://www.trananh.vn/?modul=cart&pag=add&id=1335";
//    String home = "http://www9.ttvnol.com/forum/f_438/933099.ttvn";
//    String home = "http://thuannd:9245/";
//    String home = "http://abcnews.go.com/\"/International/story?id=5803011&page=1%252F%2522";
//    String home = "http://my.opera.com/trangiahuy/blog/index.dml/tag/chuc'%20cac'%20ban..%20mot..%20ngay`%20zui%20ze~";
//    String home = "http://www.talawas.org/talaDB/showFile.php?res=9681&rb=0401";
//    String home = "http://www.hastc.org.vn/weblink.asp?";
//    String home = "http://www.xe24h.net/?NEWS/VN/Tintucthitruong/Thitruongtrongnuoc/2901.3S";
    String home  = "http://www.theserverside.com/";
//    String home = "http://xRapidshare.net";
    
//    String home  = "https://login.yahoo.com/config/login?";
    
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    webClient.setURL(null, new URL(home));
    
//    webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
//    webClient.registryProxy("216.194.70.3", 8118, null, null);
    
    byte[] obj = loadContent(home);
    System.out.println(" da load xong ");
    if(obj == null) return;
    
    writer.save(new File("D:\\Temp\\a.html"), obj);
    System.exit(1);
  }

}
