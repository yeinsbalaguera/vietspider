/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.html.Group;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.CharacterUtil;
import org.vietspider.locale.vn.ArticleWordSplitter;
import org.vietspider.locale.vn.VietWordSplitter;
import org.vietspider.locale.vn.Word;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 18, 2009  
 */
public class PluginData2TpDocument {

  private Name [] styles = {Name.FONT, Name.SUB, Name.SUP};
  private char [] endSentences  = {'.', ':', '?'};

  private Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");

  //  private TopicTrackingAnalyzer analyzer;

  private VietWordSplitter wordSplitter;
  
  private WordStore wordStore;

  public PluginData2TpDocument() {
    File folder = UtilFile.getFolder("system/dictionary/vn/");
    
    File file  = new File(folder, "full.vn.dict.cfg");
//    System.out.println(file.exists() +  " : "+ file.getAbsolutePath());
    if(file.exists() && file.length() > 0) {
      wordSplitter = new ArticleWordSplitter(file) ;
    } else {
      wordSplitter = new ArticleWordSplitter();
    }
    
    wordStore = new WordStore();
    
    //    analyzer = new ViTopicTrackingAnalyzer();
  }

  public VietWordSplitter getWordSplitter() { return wordSplitter; }

  public WordStore getWordStore() { return wordStore; }

  public TpWorkingData convert(PluginData pluginData) throws Exception {
    List<HTMLNode> textNodes = pluginData.getCloneTextNodes();
    if(textNodes == null) return null;

    CharacterUtil charUtil = new CharacterUtil();
    StringBuilder builder = new StringBuilder();
    char [] lastElement = null;

    for(HTMLNode node : textNodes)  {
      char [] chars = node.getValue();
      if(chars.length < 1) continue;
      
      /*if(chars[0] == '(') {
        int i = 1;
        for(; i < chars.length; i++) {
          if(chars[i] == ')') break;
        }
        if(i < 10) {
          chars = Arrays.copyOfRange(chars, i+1, chars.length);
          System.out.println(new String(chars));
        }
      }*/
      
      if(isLinkNode(node, 0)) continue;

      int counter = charUtil.count(chars);    
      if(lastElement != null 
          && isSentence(lastElement) 
          && counter < 5) continue;

      /** only for webpage, safe data */
      if(pluginData.isWebPage()) {
        counter = countWords(new String(chars));
        if(counter < 20) continue;
      }

      if(!isStyle(node) && !isSentence(chars)){
        char [] nText = new char[chars.length+1];
        System.arraycopy(chars, 0, nText, 0, nText.length-1);
        nText[chars.length] = '.';
        chars = nText;
      }
      builder.append(chars).append(' ');
      lastElement = chars;
    }

    try { 
      TpWorkingData workingData = convert(pluginData,
          pluginData.getMeta().getId(), builder.toString());

//      String id  = pluginData.getMeta().getId();
//      String group = pluginData.getGroup().getType();
//      int minRate = pluginData.getGroup().getMinPercentRelation();
//      if(pluginData.isWebPage()) minRate = 75;
//      minRate = Math.max(15, minRate);
//      int range = pluginData.getGroup().getDateRangeRelation();

//      workingData.getTpDocument().setId(id);
//      workingData.setMinRate(minRate);
//      workingData.setRange(range);
//      workingData.setGroup(group);
      
      workingData.setTitle(pluginData.getMeta().getTitle());
      workingData.setSource(pluginData.getArticle().getDomain().getName());
      workingData.setCategory(pluginData.getArticle().getDomain().getCategory());
      
      return workingData;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      //    e.printStackTrace();
      LogService.getInstance().setMessage(new Exception(e),
          e.toString()+ " / "+ pluginData.getMeta().getId());
      return null;
    }


  }

  public TpWorkingData convert(PluginData pluginData, String id, String text) throws Exception {
    List<Word> words = wordSplitter.split(text); 
    if(pluginData != null) pluginData.setWords(words);
//    TpDocument tpDocument = workingData.getTpDocument();

    TpWorkingData workingData = new TpWorkingData(id);
    for(int i = 0; i < words.size(); i++) {
      Word word = words.get(i);
      if(word.getValue().length() == 1) continue;
      String newValue = wordStore.getAbbreviateValue(word.getValue());
      if(newValue != null) word.setValue(newValue);
      
      if(wordStore.isConfig(word)) {
//        System.out.println("config noun = "+word.getValue());
        word.setNoun(1);
      }
     
      /*check ignore word*/
      
      /*set data*/
      if(word.getNoun() > 0) {
//        System.out.println(word.getValue());
        if(wordStore.isIgnoreNoun(word)) continue;
        
        if(word.getNoun() == 2) {
//          System.out.println(word.getValue() + " : "+isIgnore(ignoreWords, word));
          if(wordStore.isIgnoreWord(word))continue;
        } else if(word.getElements().length > 1) {
          if(isUpperWhole(word) && wordSplitter.contains(word.getElements())) {
            word.setNoun(-1);
//            System.out.println(" === > "+ word.getValue());
          }
        }
      }
      
      if(word.getNoun() > 0) {
//        System.out.println("=== >" + word.getValue());
        workingData.addKey(word.getValue());
        continue;
      } 
      
        if(wordStore.isIgnoreWord(word)) continue;
//        System.out.println("=== >"+ word.getValue());
//        test 1
//        if(i%10 == 0) System.out.println();
//        System.out.print(word.getValue()+ "; ");
        workingData.addWord(word.getValue());
    }
//  test 1
//    System.out.println("\n");
    
    
    //      TpWorkingData workingData = analyzer.analyzeDocument(builder.toString());
    //factory.create(topicTracking.getMetaId(), builder.toString());
    //    topicTracking.setSummarize(summarize);

    return workingData;
  }
  
  private boolean isUpperWhole(Word word) {
    String [] elements = word.getElements();
    for(String element : elements) {
      for(int i = 0; i < element.length(); i++) {
        if(Character.isLowerCase(element.charAt(i))) return false;
      }
    }
    return true;
  }

 /* public static ContentIndex toIndexContent(PluginData pluginData) {
    if(!CrawlerConfig.INDEX_CONTENT)  return null;

    List<HTMLNode> nodes = pluginData.getCloneTextNodes();
    if(nodes == null) return null;

    ContentIndex entry = new ContentIndex();

    Meta meta = pluginData.getMeta();
    entry.setId(meta.getId());
    entry.setTitle(meta.getTitle());
    entry.setDescription(meta.getDesc());
    entry.setContent(meta.getPropertyValue("temp.text"));

    //    StringBuilder builder = new StringBuilder();
    //    for(int i = 0; i < nodes.size(); i++) {
    //      builder.append(' ').append(nodes.get(i).getValue());
    //    }

    Calendar calendar = meta.getCalendar();
    entry.setDate(CalendarUtils.getDateFormat().format(calendar.getTime()));

    return entry;
  }*/


  private boolean isSentence(char[] text){
    char c = text[text.length - 1]; 
    for(char ele : endSentences){
      if(c == ele) return true;
    }
    return false;
  }

  public int countWords(CharSequence charSeq){
    int start = 0;
    int counter = 0;
    Matcher matcher = wordPattern.matcher(charSeq);
    while(matcher.find(start)) {
      start = matcher.start() + 1;
      counter++;
    }
    return counter;
  }

  private boolean isStyle(HTMLNode node){
    if(node.getConfig().type() == Group.Fontstyle.class) return true;
    if(node.getConfig().type() == Group.Phrase.class) return true;
    for(Name name : styles) {
      if(node.getName() == name) return false;
    }
    return false;
  }

 
  private boolean isLinkNode(HTMLNode node, int counter) {
    if(counter >= 2) return false;
    HTMLNode parent = node.getParent();
    if(parent == null) return false;
    if(parent.isNode(Name.A)) {
      String text = new String(node.getValue());
      int index = 0;
      while(index < Math.min(10, text.length())) {
        char c = text.charAt(index);
        if(Character.isLetter(c) && Character.isUpperCase(c)) return true;
        if(Character.isDigit(c)) return true;
        index++;
      }
    }
    return isLinkNode(node.getParent(), counter+1);
  }
  

}
