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
 * Aug 6, 2008  
 */
public class ExtractForum {
  
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
    String homepage = "http://forums.java.net/";
    webClient.setURL(homepage, new URL(homepage));
    
    String address = "http://forums.java.net/jive/thread.jspa?threadID=40523&tstart=0";
    byte  [] bytes = download(homepage, address);
    
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(bytes, "utf-8");
    String titlePathValue  = "BODY[0].DIV[1].TABLE[0].TBODY[0].TR[0].TD[0].P[1]";
    
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor htmlExtractor = new HTMLExtractor();
    
    NodePath titlePath = pathParser.toPath(titlePathValue);
    HTMLNode titleNode = htmlExtractor.lookNode(document.getRoot(), titlePath);
    String titleThread =  buildText(titleNode);
    
    String [] postPathValues = {
        "BODY[0].DIV[1].TABLE[1].TBODY[0].TR[0].TD[0].DIV[1].DIV[0].DIV[0].TABLE[0]",
        "BODY[0].DIV[1].TABLE[1].TBODY[0].TR[0].TD[0].TABLE[*].TBODY[0].TR[0].TD[1].DIV[0].DIV[0].DIV[0].TABLE[0]"
    };
    
    NodePath [] postPaths = pathParser.toNodePath(postPathValues);
    HTMLDocument document2 = htmlExtractor.extract(document, postPaths);
    
    String userPathValue = "TABLE[*].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[0].NOBR[0].A[0]";
    NodePath userPath = pathParser.toPath(userPathValue);
    List<HTMLNode> userNodes = htmlExtractor.matchNodes(document2.getRoot(), userPath);
    
    List<String> users = new ArrayList<String>();
    for(HTMLNode userNode : userNodes) {
      users.add(buildText(userNode));
    }
    
    String textPostPathValue = "TABLE[*].TBODY[0].TR[0].TD[1].TABLE[0].TBODY[0].TR[1]";
    NodePath textPostPath = pathParser.toPath(textPostPathValue);
    List<HTMLNode> textPostNodes = htmlExtractor.matchNodes(document2.getRoot(), textPostPath);

    List<String> posts = new ArrayList<String>();
    for(HTMLNode textPostNode : textPostNodes) {
      posts.add(buildText(textPostNode));
    }
    
    //pint
    System.out.println(titleThread+"\n\n");
    for(int i = 0;  i < Math.min(users.size(), posts.size()); i++) {
      System.out.print("-------------------------------------------");
      System.out.print(users.get(i));
      System.out.println("-------------------------------------------");
      System.out.println(posts.get(i));
    }
    
  }
  
  
}
