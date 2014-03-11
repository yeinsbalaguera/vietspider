/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.highlight;

import static org.vietspider.bean.NLPData.ADDRESS;
import static org.vietspider.bean.NLPData.CITY;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.chars.CharsUtil;
import org.vietspider.common.Application;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.NLPRenderer;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.text.Splitter;
import org.vietspider.solr2.SolrIndexStorage;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 14, 2011  
 */
public class TextHighlighter {

  private String hlStartTag;
  private String hlEndTag;

  private Splitter<TextElement> splitter;
  private HTMLParser2 parser2 = new HTMLParser2();
  private  List<Word> words;
  
  public TextHighlighter(CommonSearchQuery searchQuery) {
    this(searchQuery.getPattern(), searchQuery.getHighlightStart(), searchQuery.getHighlightEnd());
  }

  public TextHighlighter(String pattern, String start, String end) {
    this.words = Word.split(pattern);
    this.hlStartTag = start;
    this.hlEndTag = end;

    this.splitter = new Splitter<TextElement>() {

      public TextElement create(String seq, TextElement current) {
        TextElement element = new TextElement(seq);
        if(current == null) return element;
        current.next = element;
        element.previous = current;
        return element;
      }
    };

    Map<Short, Collection<?>> records = QueryAnalyzer.getProcessor().process(pattern);
    addWord(records, CITY);
    addWord(records, ADDRESS);
  }

  TextFragment highlight(String text) {
    if(text.length() > 75) text = CharsUtil.cutLabel(text, 75);
//    System.out.println(text);
    String lower = TextElement.toTextNotMarked(text);
    //    System.out.println(lower);

    List<Point> points = new ArrayList<Point>();
    
    for(int i = 0; i < words.size(); i++) {
      Word word = words.get(i);

      int[] indexes = indexOf(text, lower, word, 0);
//      System.out.println(word.value + " : "+ indexes[0] + " : "+ indexes[1]);
      while(indexes[1] > -1) {
        //        System.out.println(indexes[2]);
        if(indexes[0] > -1) {
          points.add(new Point(indexes[2], indexes[0], indexes[1]));
        }
        indexes = indexOf(text, lower, word, indexes[1]);
      }
    }

    if(points.size() < 1) return new TextFragment(text, 0);

    int score = text.length() > 20 ? (text.length()/20)*2 : -5;;
    for(int i = 0; i < points.size(); i++) {
      score += points.get(i).score;
    }

    Point.mergePoints(points);

    StringBuilder builder = new StringBuilder();
    int start = 0;
    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      build(builder, text, start, point.start, point.end);
      start = point.end;
    }

    if(start < text.length()) builder.append(text.substring(start));
    return new TextFragment(builder.toString(), score);
  }

  public List<TextFragment> highlight2(String text) {
    List<TextFragment> fragments = new ArrayList<TextFragment>();
    TextElement root = splitter.split(text, 80);
    TextElement element = root;
    while(element != null) {
      String value = element.value;
      fragments.add(highlight(value));
      element = element.getNext();
    }

    Collections.sort(fragments, new Comparator<TextFragment>() {
      @Override
      public int compare(TextFragment f1, TextFragment f2) {
        return f2.getScore() - f1.getScore();
      }

    });

    return fragments;
  }

  private void build(StringBuilder builder, 
      String text, int start, int sw, int ew) {
    builder.append(text.substring(start, sw));
    builder.append(hlStartTag);
    builder.append(text.substring(sw, ew));
    builder.append(hlEndTag);
  }

  boolean existCharOrDigit(String lower, int start, int end) {
    int index = start;
    while(index < end) {
      char c = lower.charAt(index);
      if(Character.isLetterOrDigit(c)) return true;
      index++;
    }
    return false;
  }

  private int[] indexOf(String text, String lower, Word word, int start) {
//    System.out.println("============>"+text + " : "+ word.value + " : "+ start);
    int [] indexes = indexOf(text, word.value, start, 4);
    if(indexes[0] > -1) return indexes;
    indexes = indexOf(text.toLowerCase(), word.value, start, 4);
    if(indexes[0] > -1) return indexes;
    return indexOf(lower, word.nomark, start, word.equals ? 2 : 1);
  }

  private int[] indexOf(String lower, String word, int start, int score) {
    int index = lower.indexOf(word, start);
//        System.out.println(lower);
//        System.out.println(word + " : "+ index);
    if(index < 0) return new int[]{-1, -1, score};
    int end = index + word.length();
    if(index > 0 
        && Character.isLetterOrDigit(lower.charAt(index-1))) {
      //      System.out.println("error 1: " + word + " : "+ index);
      return new int[]{-1, end, score};
    }
    if(end < lower.length()  
        && Character.isLetterOrDigit(lower.charAt(end))) {
      //      System.out.println("error 2: " + word + " : "+ index);
      return new int[]{-1, end, score};
    }
    while(end < lower.length()) {
      char c = lower.charAt(end);
      if(Character.isLetterOrDigit(c)) break;
      end++;
    }
    //    System.out.println(" ===  >"+ index + " : "+ end);
    return new int[]{index, end, score};
  }


  public void highlight(Article article) throws Exception {
    Meta meta = article.getMeta();
    Content content = article.getContent();
    
    if(meta.getTitle() == null) return;

    TextFragment fragment = highlight(meta.getTitle());
    meta.putProperty("hl.title", fragment.getValue());

    StringBuilder builder = new StringBuilder(meta.getDesc());
    builder.append('\n');
    
    if(content == null || content.getContent() == null) return;

    HTMLDocument doc = parser2.createDocument(content.getContent());
    new NLPRenderer(builder, doc.getRoot());
    List<TextFragment> fragments = highlight2(builder.toString());

    builder.setLength(0);
    for(int i = 0; i < Math.min(3, fragments.size()); i++) {
      builder.append(fragments.get(i).getValue()).append("...");
      if(builder.length() > 150) break;
    }

    meta.putProperty("hl.desc", builder.toString());
  }

  private void addWord(Map<Short, Collection<?>> records, short type) {
    Collection<?> collections = records.get(type);
    if(collections == null || collections.isEmpty()) return;
    Iterator<?> iterator = collections.iterator();

    while(iterator.hasNext()) {
      String value = iterator.next().toString();
      List<Word> list = Word.split(value);
      this.words.addAll(list);
    }
  }

  public static void main(String[] args) {
    try {
      File file = new File(SolrIndexStorage.class.getResource("").toURI());
      String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
      path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
      path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
      file  = new File(path);

      //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");

      Application.PRINT = false;
    } catch (Exception e) {
      e.printStackTrace();
    }

    String pattern = " thue  cho nha p8 quang   ";
    String title =  "Cho thuê nHà  nguyên căn P8, Quang Trung, Gv";

    pattern = "nhà long biên";
    title = "Mình có 2 phòng cho thuê thuộc phường Long Biên - Quận Long Biên cần cho thuê ( nhà cách cầu Chương Dương";

    pattern = "bán nhà trung hòa";
    title  = "Bán hoặc cho thuê nhà tại Hoàng Ngân, Trung Hòa Nhân Chính: ";

    pattern = "nhà hà nội";
    title = "bán nhà hà nội số 3 ngõ 238 Ngọc hồi 52m2, 2 mặt tiền.";

    pattern = "ban nha hn";
    title = "Bán nhà chính chủ, DT 60m2, (5x12m), mặt ngõ 2,5m, ở Dốc BV Phụ Sản HN, giá 1,7 tỷ.Nếu mua một nửa nhà";
    
    pattern = "ban nha \"giá rẻ\"";
    title = "Cần bán nhà giá rẻ";

    TextHighlighter highlighter = new TextHighlighter(pattern, "<b>", "</b>");
    System.out.println(highlighter.highlight(title).getValue());
  }

}
