/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.xlightweb;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2007  
 */

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 2, 2006  
 */
public class TestWebClient {
  
 /* private static byte[] decode(byte [] bytes, String encoding) throws Exception {
    InputStream encodedStream = null;

    // GZIP
    if (encoding.equals ("gzip")) {
      encodedStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
    }

    // DEFLATE
    else if (encoding.equals ("deflate")) {
      encodedStream = new InflaterInputStream(new ByteArrayInputStream (bytes), new Inflater(true));
//    encodedStream = new ZipInputStream(new ByteArrayInputStream (responseBytes));
    }

    
    final BytesOutputStream decodedStream = new BytesOutputStream ();
    final byte buffer[] = new byte[1024];

    try {
      for (int length; (length = encodedStream.read(buffer, 0, 1024)) != -1;) {
        decodedStream.write(buffer, 0, length);
      }
    } catch (EOFException e) {
      LogService.getInstance().setMessage(e, "WebClient 130: ");
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    // closing
    if(encodedStream != null) encodedStream.close ();
    decodedStream.close ();

    if(decodedStream.size() < 1) return bytes;
    return decodedStream.toByteArray ();
  }
  
  
  public static void main(String[] args) throws Exception {
////    String home  = "http://vietnamnet.vn/";
////    String home = "http://www.vinhphuc.gov.vn/render.userLayoutRootNode.uP";
////    String home  = "http://vietsonic.com/Product_detail.asp?pID='UltraFlat'";
////    String home = "http://www.binhdinh.gov.vn/BinhDinh/render.userLayoutRootNode.uP";
////    String home  = "http://thuannd:9247/data/get/date";
////    String home  = "http://www.quangtript.com.vn";
    String home  = "http://www.cand.com.vn/";
////    String home  = "http://diendanlequydon.com/viewtopic.php?t=14110";
////      String home  = "http://360.yahoo.com/profile-EO8TUQoofakBOZyrBrL.";
////    String home = "http://localhost:8080/tuoitre2/";
////    String home =  "http://blog.360.yahoo.com/blog-o_2VJewgbqfL2HJEtXaTzW5_k8508jdY?tag=vietdart";
////    String home = "http://360.yahoo.com/profile-CcIyptwlc6esXyzRsondCUxgTA9J";
////    String home = "https://forum.defcon.org/showthread.php?t=3741";
////    String home = "http://www.javavietnam.org/javavn/mvnforum/listrecentthreads?offset=10";
////    String home = "http://www.chf.com.vn/?cmd=act:main|cat:25";
////    String home  = "http://www.trananh.vn/?modul=cart&pag=add&id=1335";
////    String home  = "http://www9.ttvnol.com/forum/f_438/933099.ttvn";
////    String home = "http://thuannd:9245/";
////    String home = "http://www.talawas.org/talaDB/showFile.php?res=9681&rb=0401";
    
//    String home = "http://www.hastc.org.vn/weblink.asp?";
//    home  = utils.replaceSpace(home);
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encodeURL(home);
    
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    HttpClient httpClient = new HttpClient();
    
    IHttpRequest request = new GetRequest(home);
    
    request.setHeader("Accept-Encoding", "gzip,deflate");

    
    IHttpResponse response = httpClient.call(request);
    
    System.out.println(response.getCharacterEncoding());
    System.out.println(response.getContentLength());
    System.out.println(response.getContentType());
//    System.out.println(response.getNonBlockingBody().getDataHandler());
    
    BlockingBodyDataSource bodyDataSource = response.getBlockingBody();
//    bodyDataSource.readString()
    byte [] bytes = bodyDataSource.readBytes();
    bytes = decode(bytes, "deflate");


    // get response header data 
    String contentType = response.getContentType();
    //...

    httpClient.close();
    
//    byte[] obj = loadContent(home);
    writer.save(new File("D:\\Temp\\webclient\\a.html"), bytes);
    
//    Cookie [] cookies = webClient.getHttpClient().getCookieStore().getCookies();
//    System.out.println(cookies);
//    System.out.println(cookies.length);
//    System.out.println(cookies[0].getName() +" : "+cookies[0].getValue());
    
//    obj = loadContent(home);
//    writer.save(new File("F:\\Temp2\\webclient\\b.html"), obj);
    
//    cookies = webClient.getHttpClient().getCookieStore().getCookies();
//    System.out.println(cookies);
//    System.out.println(cookies.length);
//    System.out.println(cookies[0].getName() +" : "+cookies[0].getValue());
    
//    home = "http://vietnamnet.vn/xahoi/2007/10/750823/";
//    home = "http://www.vinhphuc.gov.vn/tag.b7a4741664ea428d.render.userLayoutRootNode.target.107.uP?view=Home&newsID=10225&topicID=2&fromView=Home#107";
//    home  = "http://muabanraovat.com/main.php?cat_id=1";
//    obj = webClient.loadContent(home);
//    writer.save(new File("F:\\Temp2\\webclient\\b.html"), obj);
    
//    obj = client.loadContent(address);
//    writer.save(new File("C:\\Temp\\b.html"), obj);
//    
//    obj = client.loadContent(address);
//    writer.save(new File("C:\\Temp\\c.html"), obj);
//    
//    System.out.println(get.getFollowRedirects());
  }*/

}
