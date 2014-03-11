/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.index.analytics.PhraseData;
import org.vietspider.index.analytics.ViIndexAnalyzer2;
import org.vietspider.locale.Html2Text;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public class HighlightBuilder {

  private String sequence;
  private String n_sequence;
  private List<String> words;
  private TextSearchUtils textUtils = new TextSearchUtils();

  private String startTag = "<b>";
  private String endTag = "</b>";
  private int lengthTag = startTag.length() + endTag.length();

  private boolean highlighted = false;

  public HighlightBuilder(String pattern) {
    ViIndexAnalyzer2 analyzer = new ViIndexAnalyzer2(/*new ConjuntionFilter()*/);
    List<PhraseData> phrases = analyzer.analyze(pattern);
    
    words = new ArrayList<String>();
    for(int i = 0; i < phrases.size(); i++) {
      String value = phrases.get(i).getValue();
      if(!words.contains(value)) words.add(value);
      value = VietnameseConverter.toTextNotMarked(value);
      if(!words.contains(value)) words.add(value);
    }

    sequence = textUtils.normalize(pattern.trim());
    n_sequence = VietnameseConverter.toTextNotMarked(sequence);
  }

  public void build(Article article) {
    Meta meta = article.getMeta();
    String title  = cutTitle(meta.getTitle());
    String desc = meta.getDesc();
    String content = article.getContent().getContent();

    StringBuilder builder = new StringBuilder(desc);
//    builder.append('.').append(' ').append(desc);
    try {
      List<NodeImpl> tokens = new HTMLParser2().createTokens(content.toCharArray());
      content  = Html2Text.toText(tokens);
      builder.append('.').append(' ').append(content);
    } catch (Exception e) {
    }

    meta.setTitle(buildTitle(title));
    HighlightData data =  buildDesc(builder.toString(), 200, 0);
    if(data != null) meta.setDesc(data.getValue());
  }
  
  private String cutTitle(String title) {
    if(title.length() > 60) {
      int index = 60;
      while(index < title.length()) {
        char c = title.charAt(index);
        if(!Character.isLetterOrDigit(c)) break;
        index++;
      }
      return new StringBuilder(title.substring(0, index)).append("...").toString();
    }
    return title; 
  }

  public String buildTitle(String title) {
    int [] positions = textUtils.searchSequence(title, sequence, 0);
    if(positions == null) textUtils.searchSequence(title, n_sequence, 0);
    if(positions != null) return build(title, positions[0], positions[1]);
    
    for(int i = 0; i < words.size(); i++) {
      title  = buildWord(title, words.get(i));
    }

    return title;
  }

  public HighlightData buildDesc(String content, int size, int from) {
    int [] positions = textUtils.searchSequence(content, sequence, from);
    if(positions == null) textUtils.searchSequence(content, n_sequence, from);
//    positions = textUtils.searchSequence(content, sequence, from);
//    System.out.println("step 1 " + positions);
    if(positions != null) {
      content = build(content, positions[0], positions[1]);
      size -= sequence.length();
      int start = textUtils.back(content, positions[0], size/2);
      int end = textUtils.next(content, positions[1]+10, size/2);
      String value = content.substring(start, end); 
      return new HighlightData(value, end);
    }

    HighlightData data = buildContent(content, size, from);
//        System.out.println("===============================================");
//        System.out.println(data.getValue());
    if(highlighted && data.getValue().length() > 10) return data;

    words.clear();
    String [] elements = sequence.split(" ");
    Collections.addAll(words, elements);
    data = buildContent(content, size, from);

    return data.getValue().length() < 10 ? null : data;
  }

  private HighlightData buildContent(String content, int size, int from) {
    StringBuilder builder = new StringBuilder();
    size -= sequence.length();
    int elementSize  = size/words.size();
    elementSize = elementSize/4;
    size += 40*words.size();
    //    System.out.println(content);
    for(int i = 0; i < words.size(); i++) {
      String word = words.get(i);
      int index = indexOf(content, word, from);
      //      System.out.println("==== > "+ word + " / "+ index);
      if(index < 0) continue;

      int start = textUtils.back(content, index, elementSize);
      int end = textUtils.next(content, index + word.length(), elementSize);
      String value = content.substring(start, end);

      from = end;

      builder.append(value).append("...");
      if(builder.length() >= size) break;
    }

    String desc =  builder.toString();
    for(int i = 0; i < words.size(); i++) {
      desc = buildWord(desc, words.get(i));
    }

    return new HighlightData(desc, from);
  }

  private String buildWord(String text, String word) {
    int from = 0;
    //    System.out.println(word);
    while(from < text.length()) {
      int index = indexOf(text, word, from);
      if(index < 0) return text;
      int end  = index + word.length();
      if(index > 0 && Character.isLetterOrDigit(text.charAt(index - 1))) {
        from += end ;
        //        System.out.println(" bi tiep tuc "+ word + " / "+text.charAt(index - 1));
        continue;
      }
      if(end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
        from += end;
        //        System.out.println(" bi tiep tuc 2 "+ " / "+text.charAt(end));
        continue;
      }
      text = build(text, index, end);
      from += end + lengthTag;
    }
    return text;
  }

  private String build(String text, int start, int end) {
    int index = text.indexOf("<span", start);
    if(index > start && index < end) return text;
    index = text.indexOf("</span", start);
    if(index > start && index < end) return text;

    StringBuilder builder = new StringBuilder();
    builder.append(text.substring(0, start));
    builder.append(startTag);
    builder.append(text.substring(start, end)).append(endTag);
    builder.append(text.substring(end, text.length()));

    highlighted = true;

    return builder.toString();
  }

  //  <b style="color: black; background-color: rgb(255, 255, 102);">
  public void setHighlightTag(String start, String end) {
    this.startTag = start;
    this.endTag = end; 
    this.lengthTag = startTag.length() + endTag.length();
  }


  private int indexOf(String value, String pattern, int start){
    boolean is = false;
    if(pattern.length() < 1) return -1;
//    System.out.println(value);
//    System.out.println(pattern);
//    System.out.println(start);
    for(int i = start; i < value.length(); i++){
      is = true;
      for(int j = 0; j< pattern.length(); j++){        
        if(i+j < value.length() 
            &&  Character.toLowerCase(pattern.charAt(j)) == Character.toLowerCase(value.charAt(i+j))) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }

  public static class HighlightData {

    private String value;
    private int end;

    public HighlightData(String text, int end) {
      this.value = text;
      this.end = end;
    }

    public int getEnd() { return end;    }
    public String getValue() { return value; }

  }

}
