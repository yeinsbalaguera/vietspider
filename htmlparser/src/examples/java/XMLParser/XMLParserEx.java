import java.net.URL;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 19, 2008  
 */
public class XMLParserEx {
  
  private static WebClient webClient = new WebClient();
  
  private static byte[] loadContent(String address) throws Exception {
    webClient.setURL(address, new URL(address));
    HttpGet httpGet = null;
    URLEncoder urlEncoder = new URLEncoder();
    address = urlEncoder.encode(address);
    httpGet = webClient.createGetMethod(address, "");

    if(httpGet == null) return null;
    HttpHost httpHost = webClient.createHttpHost(address);
    HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    System.out.println(" status code is "+ statusCode);

    HttpResponseReader httpResponseReader = new HttpResponseReader();
    return httpResponseReader.readBody(httpResponse);
  }
  
  private static void printTreeNode(XMLNode node, int level) {
    if(node == null) return;
    for(int i = 0; i < level; i++) {
      System.out.print("   ");
    }
    if(node.getName() != null) {
      System.out.println(" " + node.getName()+ " : "+ new String(node.getValue()));
    } else {
      System.out.println(" CONTENT : "+ new String(node.getValue()));
    }
    List<XMLNode> nodes = node.getChildren();
    if(nodes == null) return;
    for(int i = 0; i < nodes.size(); i++) {
      printTreeNode(nodes.get(i), level+1);
      
    }
  }
  
  public static void main(String[] args) throws Exception {
    byte  [] bytes = loadContent("http://www.theserverside.com/rss/theserverside-atom.xml");
    if(bytes == null) {
      System.out.println(" data is null");
      return;
    }
    XMLDocument document = XMLParser.createDocument(bytes, "utf-8", null);
    printTreeNode(document.getRoot(), 0);
  }
  

}
