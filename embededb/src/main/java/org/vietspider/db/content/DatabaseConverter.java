/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 16, 2009  
 */
public class DatabaseConverter {

  /*public static void execute2() throws Throwable  {
    File folder = UtilFile.getFolder("content/cindexed/");
    File[] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isDirectory()) return false;
        return UtilFile.validate(f.getName());
      }
    });
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
    LogService.getInstance().setMessage(null, "start convert data");
    for(int i = 0; i < files.length; i++) {
      LogService.getInstance().setMessage(null, "convert data from " + files[i].getName() );
      execute2(dateFormat.format(folderFormat.parse(files[i].getName())));
      try {
        Thread.sleep(10*60*1000);
      } catch (Exception e) {
      }
    }
    LogService.getInstance().setMessage(null, "convert data successfull");
  }

  public static void execute2(String date) throws Throwable  {
    if(date.equalsIgnoreCase("19/09/2009")) return;
    if(date.equalsIgnoreCase("18/09/2009")) return;
    if(date.equalsIgnoreCase("17/09/2009")) return;
    if(date.equalsIgnoreCase("16/09/2009")) return;
    if(date.equalsIgnoreCase("15/09/2009")) return;
    if(date.equalsIgnoreCase("14/09/2009")) return;
    if(date.equalsIgnoreCase("13/09/2009")) return;
    if(date.equalsIgnoreCase("12/09/2009")) return;
    if(date.equalsIgnoreCase("11/09/2009")) return;
    org.vietspider.data.jdbc.DataGetter getter = new org.vietspider.data.jdbc.DataGetter();

    Date dateInstance = CalendarUtils.getDateFormat().parse(date);
    ArticleDatabase database = IDatabases.getInstance().getDatabase(dateInstance); 

    MenuInfo menuInfo = SourceLogHandler.getInstance().loadData(date); 
    Domain domain = new Domain();
    domain.setDate(date);

    int totalPage = menuInfo.getTotalData() / DatabaseService.PAGE_SIZE ;
    if (menuInfo.getTotalData() % DatabaseService.PAGE_SIZE > 0) totalPage++ ;

    int page = 1;
    int max = totalPage;
    if(date.equalsIgnoreCase("18/09/2009")) {
      page = 3460;
      max = 3510;
    } else

    if(date.equalsIgnoreCase("08/09/2009")) {
      page = 6615;
    }

    EntryReader entryReader = new EntryReader();
    for (; page <= max; page++) {
      LogService.getInstance().setMessage(null, "convert date " + date + " : "+ page);

      MetaList metas = new MetaList();
      metas.setCurrentPage(page);

      if(page%100 == 0) {
        try {
          Thread.sleep(10*1000);
        } catch (Exception e) {
        }
      }

      List<MetaIdEntry> entries = entryReader.readData(domain, metas, -1);

      //      getter.loadMetaFromDomain(domain, metas);
      //      List<Article> list = metas.getData();
      //      System.out.println(" ===  >"+ list.size());
      for(int i = 0; i < entries.size(); i++) {
        //        String id = list.get(i).getMeta().getId();
        String id = String.valueOf(entries.get(i).getMetaId());
        //        System.out.println("++== > "+ id);
        //        Article article = new Article();
        Meta meta = getter.loadMeta(id);
        if(meta == null) continue;
        //        article.setMeta(meta);

        Content content  = getter.loadContent(id);
        if(content == null) continue;
        //        article.setContent(content);

        Domain domain2 = getter.loadDomainById(meta.getDomain());
        if(domain2 == null) continue;
        //        article.setDomain(domain2);

        database.save(meta, domain2, content, null);

        //        article.setRelations(getter.loadRelation(id));
        //        IDatabases.getInstance().save(article);

        database.save(getter.loadRelation(id));
      }

      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }

  }*/

}
