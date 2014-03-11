
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */

public class VietnamnetCrawler {

  public static void main(String[] args) throws Exception {
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://vietnamnet.vn/cntt/", 
        null,
        new String[]{"BODY[0].TABLE[3].TBODY[0].TR[0].TD[2].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0]"},
        null);

  }

}
