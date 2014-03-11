/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.xlightweb;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2008  
 */
public class Test2HttpClient {
  
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
  
  public static byte[] loadContent2(String address) throws Exception {
    HttpClient httpClient = new HttpClient();
    IHttpRequest request = new GetRequest(address);
    
    IHttpResponse response = httpClient.call(request);
    String charset =  response.getHeader("Content-Encoding");
    
    BlockingBodyDataSource bodyDataSource = response.getBlockingBody();
    if(bodyDataSource == null) return null;
//    bodyDataSource.readString()
    byte [] bytes = bodyDataSource.readBytes();
    if(charset == null) return bytes;
    return decode(bytes, charset);
  }
  
  public static byte[] loadContent(String address) throws Exception {
    DefaultHttpClient httpClient = new DefaultHttpClient();

    URL url  = new URL(address);
    HttpHost httpHost = new HttpHost(url.getHost(), url.getDefaultPort());

    HttpGet httpGet = new HttpGet(address);
    HttpResponse httpResponse = httpClient.execute(httpHost, httpGet);

    HttpResponseReader httpResponseReader = new HttpResponseReader();
    return httpResponseReader.readBody(httpResponse);
  }
  
  private static void read(String address) throws Exception {
    URLEncoder urlEncoder = new URLEncoder();
    address = urlEncoder.encodeURL(address);
    
    System.out.println("\n\n===================="+ address+"=====================================\n");
    long start = System.currentTimeMillis();
    byte[] bytes = loadContent(address);
    long end = System.currentTimeMillis();
    System.out.println(" apache doc het "+ (end - start)+ " s");

    System.out.println(" apache bytes size "+ (bytes != null ? bytes.length : "null"));
    
    System.out.println("\n");
    
    start = System.currentTimeMillis();
    bytes = loadContent2(address);
    end = System.currentTimeMillis();
    System.out.println(" xlightweight doc het "+ (end - start)+ " s");
    System.out.println(" xlightweight bytes size "+ (bytes != null ? bytes.length : "null"));
  }
  
  public static void main(String[] args) throws Exception {
    String [] urls =  {"http://vietnamnet.vn/"
        ,"http://www.vinhphuc.gov.vn/render.userLayoutRootNode.uP"
        ,"http://vietsonic.com/Product_detail.asp?pID='UltraFlat'"
        ,"http://www.binhdinh.gov.vn/BinhDinh/render.userLayoutRootNode.uP"
        ,"http://thuannd:9247/data/get/date"
        ,"http://www.quangtript.com.vn"
        , "http://www.cand.com.vn/"
        ,"http://diendanlequydon.com/viewtopic.php?t=14110"
        ,"http://360.yahoo.com/profile-EO8TUQoofakBOZyrBrL."
//        ,"http://localhost:8080/tuoitre2/"
        , "http://blog.360.yahoo.com/blog-o_2VJewgbqfL2HJEtXaTzW5_k8508jdY?tag=vietdart"
        ,"http://360.yahoo.com/profile-CcIyptwlc6esXyzRsondCUxgTA9J"
        ,"https://forum.defcon.org/showthread.php?t=3741"
        ,"http://www.javavietnam.org/javavn/mvnforum/listrecentthreads?offset=10"
        ,"http://www.chf.com.vn/?cmd=act:main|cat:25"
        ,"http://www.trananh.vn/?modul=cart&pag=add&id=1335"
        ,"http://www9.ttvnol.com/forum/f_438/933099.ttvn"
//        ,"http://thuannd:9245/"
//        ,"http://www.talawas.org/talaDB/showFile.php?res=9681&rb=0401"
        };
    
    for(int i = 0; i < urls.length; i++) {
      try {
        read(urls[i]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }*/
}
