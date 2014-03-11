import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLUtils;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */
public class SingleCrawlThread extends Thread {
  
  private String url;
  private String referer;
  
  private NodePath [] addPaths;
  private NodePath [] removePaths;

  private int index = 0;

  private byte[] data;
  
  private HTMLExtractor htmlExtractor;
  
  private  URLUtils urlUtils = new URLUtils();
  private WebClient webClient;

  public SingleCrawlThread(WebClient webClient, HTMLExtractor extractor){
    this.webClient = webClient;
    this.htmlExtractor = extractor;
     
    new Thread(this).start();
  }
  
  public void setPaths(NodePath[] addPaths, NodePath[] removePaths) {
    this.addPaths = addPaths;
    this.removePaths = removePaths;
  }

  public void run(){
    while(true){
      try{
        if(url != null){
          System.out.println("start download "+url);
          data = download();
          url = null;
        }
        Thread.sleep(1000);
      }catch(Exception exp){
        exp.printStackTrace();
      }
    }
  }

  public boolean isComplete(){
    return url == null;
  }

  public void startDownload(String referer_, String url_, int idx){
    this.url = url_;
    this.referer = referer_;
    this.index = idx;
  }
  
  public byte[] getData() { return data; } 

  public void saveData() throws Exception {
    String name = String.valueOf(index)+".htm";
    saveData(name);
  }

  public void saveData(String name) throws Exception {
    if(data == null || data.length < 1) return;
    HTMLDocument doc = new HTMLParser2().createDocument(data, "utf-8");
    if(this.addPaths == null) return;
    doc = htmlExtractor.extract(doc, this.addPaths);
    if(removePaths != null && removePaths.length > 0){
      htmlExtractor.remove(doc.getRoot(), removePaths);
    }
    data = doc.getTextValue().getBytes("utf-8");
    if(data.length < 1) return;
    File file = new File(name);
    FileOutputStream output = new FileOutputStream(file);
    output.write(data);
    output.flush();
    output.close();
    data = null;
  }

  private byte[] download() throws Exception {
    HttpGet httpGet = null;
    try {
      String address = urlUtils.getCanonical(url);
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


}
