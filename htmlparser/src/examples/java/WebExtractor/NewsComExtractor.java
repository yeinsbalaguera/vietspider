import java.io.File;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 5, 2006
 */
public class NewsComExtractor {
  
  private static byte[] download(WebClient webClient, String address) throws Exception {
    HttpGet httpGet = null;
    try {
      httpGet = webClient.createGetMethod(address, null);      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp) {
      throw exp;
    }
  }
  
  public static void main(String[] args) throws Exception  {
    URL url = new URL("http://news.google.com.vn/");
    
    WebClient webClient = new WebClient();
    webClient.setURL(null, url);
    
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor htmlExtractor = new HTMLExtractor();
    
    byte  [] bytes = download(webClient, "http://news.google.com.vn/");
    
    HTMLDocument document = new HTMLParser2().createDocument(bytes, null);
    
    String [] paths = {
        "BODY[0].TABLE[2].TBODY[0].TR[0].TD[3].TABLE[1].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[0].DIV[0].TABLE[0].TBODY[0].TR[0].TD[0].DIV[*]"
    };
    
    NodePath [] nodePaths = new NodePath[paths.length];
    for(int i=0; i<paths.length; i++){
      nodePaths[i] = pathParser.toPath(paths[i]);
    }
    
    HTMLDocument doc = htmlExtractor.extract(document, nodePaths);
    System.out.println(doc.getTextValue());
    
    paths = new String[]{
        "DIV[*].BR[*]",
    };
    
    nodePaths = new NodePath[paths.length];
    for(int i=0; i<paths.length; i++){
      nodePaths[i] = pathParser.toPath(paths[i]);
    }
    
    htmlExtractor.remove(doc.getRoot(), nodePaths);
    
    System.out.println(doc.getRoot().getTextValue());
    
    File file = new File("a.html");
    byte[] data = doc.getTextValue().getBytes();
    org.vietspider.common.io.RWData.getInstance().save(file, data);
  }
  
}
