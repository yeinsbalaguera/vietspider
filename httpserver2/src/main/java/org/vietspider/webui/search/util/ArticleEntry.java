/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.util;

import org.vietspider.bean.Article;
import org.vietspider.index.result.DocEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 14, 2009  
 */
public class ArticleEntry {
  
  private DocEntry entry;
  private Article article;
  
  public ArticleEntry() {
  }
  
  public ArticleEntry(DocEntry entry, Article article) {
    this.entry = entry;
    this.article = article;
  }

  public DocEntry getEntry() { return entry; }
  public void setEntry(DocEntry entry) { this.entry = entry; }


  public Article getArticle() { return article; }
  public void setArticle(Article article) { this.article = article; }
  
//  public static void main(String[] args) {
////    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
//    System.out.println("www.thietkewebnhanh.com - Thiết kế web nhanh - VINA Design - Thiet ke web. Thiết kế web nhanh, VINA Design, Thiet ke web, Thiet ke website. VINA Design - Thiết kế web đẹp và thiết kế web chuyên nghiệp. Luôn là đối tác tin cậy thiết kế web cho các".length());
//    java.text.DecimalFormat format = new java.text.DecimalFormat("###,###.###"); 
//    System.out.println(format.format((134f/1000f)));
//  }
}
