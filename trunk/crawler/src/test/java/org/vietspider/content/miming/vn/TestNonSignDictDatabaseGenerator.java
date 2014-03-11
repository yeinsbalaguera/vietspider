/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.miming.vn;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 16, 2009  
 */
public class TestNonSignDictDatabaseGenerator {
  
  /*private static HTMLParser2 parser = new HTMLParser2();
  private static DictSequenceSplitter splitter = new DictSequenceSplitter();
  
  public static void execute2(String date, int page) throws Exception  {
    DatabaseReader getter = DatabaseService.getLoader();
    
    MenuInfo menuInfo = SourceLogHandler.getInstance().loadData(date); 
    Domain domain = new Domain();
    domain.setDate(date);
    
    int totalPage = menuInfo.getTotalData() / DatabaseService.PAGE_SIZE ;
    if (menuInfo.getTotalData() % DatabaseService.PAGE_SIZE > 0) totalPage++ ;
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    File folder = UtilFile.getFolder("system/dictionary/vn/non/");
    File file = new File(folder, "them2.txt");
    
    TreeSet<String> set = new TreeSet<String>();
    
    for (; page < totalPage; page++) {
      MetaList metas = new MetaList();
      metas.setCurrentPage(page);
      getter.loadMetaFromDomain(domain, metas);
      
      List<Article> list = metas.getData();
      System.out.println(" load page "+ page);
      for(int i = 0; i < list.size(); i++) {
        String id = list.get(i).getMeta().getId();
        Article article = null;
        try {
          article = getter.loadArticle(id);
        } catch (Exception e) {
          e.printStackTrace();
        }
        if(article == null) continue;
        
        Meta meta = article.getMeta();
        Content content = article.getContent();
        System.out.println(meta.getTitle());
        
        StringBuilder builder = new StringBuilder();
        builder.append(meta.getTitle()).append('.').append('\n');
        builder.append(meta.getDesc()).append('.').append('\n');
        List<NodeImpl> tokens = parser.createTokens(content.getContent().toCharArray()); 

        for(int k = 0; k < tokens.size(); k++) {
          if(tokens.get(k).isNode(Name.CONTENT)) {
            builder.append(' ').append(tokens.get(k).getValue());
          } else if(tokens.get(k).isNode(Name.STYLE) 
              || tokens.get(k).isNode(Name.SCRIPT)) {
            k++;
          }
        }
        
        List<String> sequences = splitter.split(builder.toString());
        builder.setLength(0);
        
        for (int z = 0; z < sequences.size(); z++) {
          set.add(sequences.get(z));
        }
      }
      if(set.size() > 1000) break;
    }
    
    StringBuilder builder = new StringBuilder();

    Iterator<String> iterator = set.iterator();
    while(iterator.hasNext()) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(iterator.next());
    }
    writer.append(file, builder.toString().getBytes("utf-8"));
    System.exit(0);
   
  }
  
  public static void main(String[] args) throws Throwable {
    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
     execute2("14/09/2009", 1100);
  }*/
}
