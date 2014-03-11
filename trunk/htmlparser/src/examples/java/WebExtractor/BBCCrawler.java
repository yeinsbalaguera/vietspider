
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */
public class BBCCrawler {

  public static void main(String[] args) throws Exception {
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://news.bbc.co.uk/",
        "BODY[0].TABLE[1].TBODY[0].TR[0].TD[2].TABLE[1].TBODY[0].TR[0]",
        new String[]{"BODY[0].TABLE[1].TBODY[0].TR[0].TD[2].TABLE[1].TBODY[0].TR[1].TD[0].FONT[0]"},
        null);
  }

}
