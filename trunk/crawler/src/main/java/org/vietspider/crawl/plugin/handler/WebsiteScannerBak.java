package org.vietspider.crawl.plugin.handler;


public final class WebsiteScannerBak {

  /*private Pattern [] patterns;
  private final static String prefix = "http://";
  private CrawlExecutor executor;

  public WebsiteScannerBak(CrawlExecutor executor) {
    String [] txtPatterns = {
        "http[:][/][/][[\\p{L}\\p{Digit}]*[.]]*",
        "www[.] [[\\p{L}\\p{Digit}]*[.]]*",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]vn",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]com.vn",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]com",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]net",
        "[[\\p{L}\\p{Digit}]*[.]]*[.]org",
    };

    patterns = new Pattern[txtPatterns.length];
    for(int i = 0; i < patterns.length; i++) {
      try {
        int type = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
        patterns[i] = Pattern.compile(txtPatterns[i], type);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    this.executor = executor;
  }

  public void detectWebsite(Link refer, String value) {
    List<Link> list = new ArrayList<Link>();
    for(int i = 0; i < patterns.length; i++) {
      StringBuilder builder = new StringBuilder();
      Matcher matcher = patterns[i].matcher(value);
      int lastStart = 0;
      while(matcher.find()) {
        int start = matcher.start();
        int end = matcher.end();
        if(start > -1 && end > start) {
          builder.append(value.substring(lastStart, start));
          String address = value.substring(start, end).toLowerCase();
          if(!address.startsWith(prefix)) {
            address = prefix + address;
          }    
          list.add(new Link(address, null));
          //putAddress(refer, address); put to link store
          lastStart = end+1; 
        }
      }
      
      if(lastStart > 0 && lastStart < value.length()) {
        builder.append(value.substring(lastStart, value.length()));
      }
      
      if(builder.length() > 0) value = builder.toString();
    }
//    executor.addWebsiteElement(list, refer);
  }*/
}
