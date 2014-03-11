import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2008  
 */
public class HASTCIndexEx {
  
  private static WebClient webClient = new WebClient();
  
  private static byte[] download(String referer, String address) throws Exception {
    HttpGet httpGet = null;
    try {
      httpGet = webClient.createGetMethod(address, referer);      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp) {
      throw exp;
    }
  }
  
  private static String buildText(HTMLNode node) {
    StringBuilder builder = new StringBuilder();
    buildText(builder, node);
    return builder.toString();
  }
  
  private static void buildText(StringBuilder builder, HTMLNode node) {
    if(node == null) return;
    if(node.isNode(Name.CONTENT)) builder.append(' ').append(node.getValue());
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      buildText(builder, children.get(i));
    }
  }
  
  public static void main(String[] args) throws Exception {
    String homepage = "http://www.hastc.org.vn/";
    webClient.setURL(homepage, new URL(homepage));
    
    String address = "http://www.hastc.org.vn/Ketqua_giaodich.asp?stocktype=2&menuid=103120";
    byte  [] bytes = download(homepage, address);
    
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(bytes, "utf-8");
    
    String path = "BODY[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[0]";
    path += ".TABLE[0].TBODY[0].TR[1].TD[1].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[1].TBODY[0].TR[1]";
    path += ".TD[0].TABLE[1].TBODY[0].TR[i>0]";
    
    NodePathParser pathParser = new NodePathParser();
    
    NodePath nodePath = pathParser.toPath(path);
    
    HTMLExtractor htmlExtractor = new HTMLExtractor();
    document = htmlExtractor.extract(document, new NodePath[]{nodePath});
    
    List<HTMLNode> children = document.getRoot().getChildren();
    
    //print header
    List<String> headers = new ArrayList<String>();
    headers.add("STT");
    headers.add("MaCK");
    headers.add("GiaTC");
    headers.add("GiaT");
    headers.add("GiaS");
    headers.add("GiaMC");
    headers.add("GiaDC");
    headers.add("KH");
    headers.add("TD");
    headers.add("%TD");
    headers.add("GiaCN");
    headers.add("GiaTN");
    headers.add("GiaBQ");
    headers.add("Khoi luong");
    headers.add("Gia tri");
    print(headers);
    System.out.print("  -----------------------------------------------------------------");
    System.out.print("------------------------------------------------------------------------\n");
    //print value
    for(int i = 0; i < children.size(); i++) {
      List<HTMLNode> tdChildren = children.get(i).getChildren();
      List<String> values = new ArrayList<String>();
      for(int j = 0; j < tdChildren.size(); j++) {
        values.add(buildText(tdChildren.get(j)));
      }
      print(values);
    }
  }
  
  private static void print(List<String> values) {
    int max = 6;
    for(int j = 0; j < values.size(); j++) {
      if(j == 12) max = 10; else if(j == 13) max = 20;
      int k = 0;
      while(k < (max - values.get(j).length())) {
        System.out.print(' ');
        k++;
      }
      System.out.print(values.get(j)+"|");
    }
    System.out.println();
  }
  
}
