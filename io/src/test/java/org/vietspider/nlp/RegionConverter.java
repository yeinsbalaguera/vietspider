/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2009  
 */
public class RegionConverter {
  
//  public void extract() {
//    try {
//      File file  = UtilFile.getFile("system", "nlpconverter2.txt");
//      byte [] bytes = RWData.getInstance().load(file);
//      String text = new String(bytes, "utf-8");
//      String [] dates = text.split("\n");
//      for(int i = 0; i < dates.length; i++) {
//        dates[i] = dates[i].trim();
//        if(dates[i].isEmpty()) continue;
//        extract(dates[i]);
//      }
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//    }
//  }
//  
//  public void extract(String date) {
//    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
//    Date dateInst = null;
//    try {
//      dateInst = dateFormat.parse(date);
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//      return;
//    }
//    SimpleDateFormat folderFormat = CalendarUtils.getFolderFormat();
//    String folderName = folderFormat.format(dateInst);
//    File file  = UtilFile.getFile("content/summary/eid/"+folderName+"/", folderName+".eid");
//    
//    ContentMapper mapper = new ContentMapper();
//    CityCodeDetector cityDetector = new CityCodeDetector();
//    HTMLParser2 parser = new HTMLParser2();
//    
//    RandomAccessFile random = null;
//    try {
//      random = new RandomAccessFile(file, "r");
//      long length  = random.length();
//
//      LogService.getInstance().setMessage(null, "Start nlp extractor " +  date+"...");
//      while(random.getFilePointer() < length) {
//        long id = random.readLong();
//        String metaId = String.valueOf(id);
//        int status  = random.readInt();
//        if(status == -1) continue;
//        try {
//          Article article = IDatabases.getInstance().loadArticle(metaId, IArticleDatabase.NORMAL);
//          if(article == null) continue;
//          Meta meta = article.getMeta();
//          Content content = article.getContent();
//          
//          StringBuilder builder = new StringBuilder();
//          builder.append(meta.getTitle()).append('\n');
//          builder.append(meta.getDesc()).append('\n');
//          
//          HTMLDocument document = parser.createDocument(content.getContent().toCharArray());
//          TextRenderer textRenderer = new TextRenderer(document.getRoot(), TextRenderer.RENDERER);
//          builder.append(textRenderer.getTextValue());
//          
//          String city = cityDetector.detect(builder.toString());
//          if(city == null) {
//            System.out.println(builder);
//            System.out.println("=============================================================");
//            continue;
//          }
//          
////          System.out.println(meta.getId() + " / "+ city);
//          
//          meta.putProperty("region", city);
//          IDatabases.getInstance().save(article);
//        } catch (Exception e) {
//          LogService.getInstance().setMessage(e, e.toString());
//        }
//      }
//      LogService.getInstance().setMessage(null, "Finish nlp extractor " +  date+"...");
//    } catch (Throwable e) {
//      LogService.getInstance().setThrowable(e);
//      LogService.getInstance().setMessage(null, "nlp extractor " +  date + " is failed!");
//    } finally {
//      try {
//        if(random != null) random.close();
//      } catch (Exception e) {
//        LogService.getInstance().setThrowable(e);
//      }
//    }
//  }
//  
//  public static void main(String[] args) throws Exception {
//    File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
//    System.setProperty("vietspider.data.path", file.getCanonicalPath());
//    
//    RegionConverter  converter = new RegionConverter();
//    converter.extract();
//    System.exit(1);
//    
//    
//  }
}
