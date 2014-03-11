/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.plugin.wordpress;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.document.util.ImageDescRemover;
import org.vietspider.document.util.OtherLinkRemover2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.server.plugin.content.ImageHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 21, 2010  
 */
public class TestWordPressContentFormatter {
  
  private static void format(String articleId) throws Exception {
    DatabaseReader getter = DatabaseService.getLoader();
    Article article = getter.loadArticle(articleId);

    ImageDescRemover imageDescRemover = new ImageDescRemover(); 
    OtherLinkRemover2 linkRemover = new OtherLinkRemover2();

    Content content = article.getContent();
    Meta meta = article.getMeta();
    
    File folder = UtilFile.getFolder("track/temp");
    File file  = new File(folder,  articleId + "_a_0.html");
    new DataWriter().save(file, content.getContent().getBytes("utf8"));

    HTMLDocument document = null;
    try {
      document = new HTMLParser2().createDocument(content.getContent());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    HTMLNode root = document.getRoot();
    List<HTMLNode> removes = imageDescRemover.removeDesc(root);
    removes.addAll(linkRemover.removeLinks(root, null));

    StringBuilder bodyBuilder = new StringBuilder();
    //  if(metaImage != null) bodyBuilder.append(metaImage);

    bodyBuilder.append("<p>").append(meta.getDesc()).append("</p>");

    ImageHandler imageHandler = new ImageHandler();
    WordPressContentFormatter2 contentFormatter = new WordPressContentFormatter2(imageHandler);
    bodyBuilder.append(contentFormatter.build(new StringBuilder(), document));


    String html = bodyBuilder.toString();

   
    file  = new File(folder, articleId + "_a_1.html");
    new DataWriter().save(file, html.getBytes("utf8"));
  }

  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\vietspider3\\workspace\\test\\vsnews\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");

//    format("201106051246370000");
//    format("201106051246330015");
//    format("201106051246110007");
//    format("201106051246150010");
    format("201112092319250014");

    System.exit(0);
  }

}
