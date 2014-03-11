
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 15, 2006
 */
public class ThanhnienCrawler {

  public static void main(String[] args) throws Exception {
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://www.thanhniennews.com/",
        "BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[0]",
       new String[]{"BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[1].TD[0].DIV[0].TABLE[0].TBODY[0].TR[1]",
           "BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[1].TD[0].DIV[0].TABLE[0].TBODY[0].TR[2]",
           "BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[1].TD[0].DIV[0].TABLE[0].TBODY[0].TR[3]"},
       new String[] {});
  }

}
