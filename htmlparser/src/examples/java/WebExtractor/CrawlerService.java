import java.net.URL;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.net.client.WebClient;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */
public class CrawlerService extends Thread {

  private SingleCrawlThread [] childrenThread;

  private boolean complete = true;

  private String url;
  private NodePath homePath ;
  private NodePath  [] addPaths, removePaths ;

  private List<String> links;

  private int idx = 0;

  private HyperLinkUtil linkUtil = new HyperLinkUtil();
  private NodePathParser pathParser = new NodePathParser();
  private HTMLExtractor htmlExtractor = new HTMLExtractor();
  private WebClient webClient = new WebClient();

  public CrawlerService(){
    childrenThread = new SingleCrawlThread [3];
    for(int i=0; i<childrenThread.length; i++){
      childrenThread[i] = new SingleCrawlThread(webClient, htmlExtractor);
    }
    new Thread(this).start();
  }

  public void run(){
    while(true){
      try {
        processHome();
        processLink();
        Thread.sleep(1000);
      }catch(Exception exp){
        exp.printStackTrace();
      }
    }
  }

  private void processHome() throws Exception {
    if(links != null && links.size() >  0) return;
    if(!childrenThread[0].isComplete()) return ;
    if(childrenThread == null) return;
    if(childrenThread[0] == null) return;
    byte [] data = childrenThread[0].getData();
    if(data == null || data.length < 0) return; 
    HTMLDocument document =  new HTMLParser2().createDocument(data, "utf-8");
    if(homePath != null) {
      document = htmlExtractor.extract(document, new NodePath[]{homePath});
    }
    linkUtil.createFullNormalLink(document.getRoot(), new URL(url));
    links  = linkUtil.scanSiteLink(document.getRoot());
    idx = 0;
  }

  private void processLink() throws Exception{
    if(links == null || links.size() <  1) return;
    if(idx >= links.size()) return;
    for(int i=0; i < childrenThread.length; i++){
      if(!childrenThread[i].isComplete()) continue;
      childrenThread[i].saveData();
      if(idx >= links.size()){
        System.out.println("download completed !");
        return;
      }           
      childrenThread[i].startDownload(url, links.get(idx), idx);
      idx++;
    }
  }
  
  public void startCrawl(String url_, String homePathValue, String [] addPathValues, String [] removePathValues) throws Exception {
    this.url = url_;
    this.webClient.setURL(null, new URL(url));
    if(homePathValue != null) {
      this.homePath = pathParser.toPath(homePathValue);
    }
    
    this.addPaths = new NodePath[addPathValues.length];
    
    for(int i=0 ;i<addPathValues.length; i++){
      this.addPaths[i] = pathParser.toPath(addPathValues[i]);
    }
    
    if(removePathValues != null){
      this.removePaths = new NodePath[removePathValues.length];
      for(int i=0 ;i<removePathValues.length; i++){
        this.removePaths[i] = pathParser.toPath(removePathValues[i]);
      } 
    }
    
    for(int i = 0; i < childrenThread.length; i++){
      childrenThread[i].setPaths(addPaths, removePaths);
    }
    childrenThread[0].startDownload(null, url_, 0);
  }

  public boolean isComplete() { return complete; }

}
