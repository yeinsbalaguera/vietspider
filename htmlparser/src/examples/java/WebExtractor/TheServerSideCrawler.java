
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */

public class TheServerSideCrawler {

  public static void main(String[] args) throws Exception {
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://www.theserverside.com/",
        "BODY[0].TABLE[1].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[0].H1[*]",
        new String[]{
        "BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[0].TBODY[0].TR[0].TD[0]",
        "BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[i>0]"
        },
        null);

  }

}
