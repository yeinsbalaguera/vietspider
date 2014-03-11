/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.channels.ClosedByInterruptException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.html.Group;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.CharacterUtil;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 12, 2007
 */
public class ContentWriter {
  
  private ConcurrentLinkedQueue<TextContent> queue ;
  private volatile int total = 0;
  private final static int SIZE_DATA = 50;
  
  private Name [] styles = {Name.FONT, Name.SUB, Name.SUP};
  private char [] endSentences  = {'.', ':', '?'};
  
  private Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{Digit}]");
  
  ContentWriter() {
    queue = new ConcurrentLinkedQueue<TextContent>();
    
    Application.addShutdown(new Application.IShutdown() {
      public void execute() {
        saveTextContent();
      }
    });
    
    loadContent();
  }
  
  public void write(PluginData pluginData) {
    List<HTMLNode> textNodes = pluginData.getCloneTextNodes();
    if(textNodes == null) return;
    
    String id  = pluginData.getMeta().getId();
    String group = pluginData.getGroup().getType();
    int minRelation = pluginData.getGroup().getMinPercentRelation();
    if(pluginData.isWebPage()) minRelation = 75;
    int rangeRelation = pluginData.getGroup().getDateRangeRelation();
    
    TextContent content = new TextContent(id, group, minRelation, rangeRelation);
    char [] lastElement = null;
    
    CharacterUtil charUtil = new CharacterUtil();
    
    for(HTMLNode node : textNodes)  {
      char [] chars = node.getValue();
      if(chars.length < 1) continue;
      
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
      content.getContent().append(chars).append(' ');
      lastElement = chars;
    }
    
    loadContent();
    
    if(total >= SIZE_DATA) {
      saveTextContent(content);
      return;
    }    
    
//    System.out.println(" write data for mining index "+ content.getContent().toString().hashCode());
    queue.add(content);
    total++;
  }
  
  public TextContent read() {
    if(total > 0) total--;
    if(!queue.isEmpty()) return queue.poll();
    return null;
  }
  
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
  
  private void saveTextContent() {
    while(!queue.isEmpty()) {
      TextContent content = queue.poll();
      saveTextContent(content);
    }
  }
  
  private void saveTextContent(TextContent content) {
    try {
      String xml = Object2XML.getInstance().toXMLDocument(content).getTextValue();
      File file  = UtilFile.getFile("content/text/", content.getId());
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  private void loadContent() {
    if(total >= SIZE_DATA) return ;
    File file = UtilFile.getFolder("content/text/");
    File [] files = file.listFiles();
    java.util.Arrays.sort(files, new Comparator<File>() {
      public int compare(File a, File b) {
        return a.getName().compareTo(b.getName());
      }
    });
    XML2Object xml2Bean = XML2Object.getInstance();
    for(File ele : files) {
      if(!ele.exists()) continue;
      try {
        byte [] bytes = RWData.getInstance().load(ele);
        TextContent textContent = xml2Bean.toObject(TextContent.class, bytes);
        if(textContent == null) continue;
        queue.add(textContent);          
        total++;
      } catch (ClosedByInterruptException e) {
        break;
      } catch (FileNotFoundException e) {
        continue;
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        continue;
      }
      ele.delete();
      if(total >= SIZE_DATA) break;
    }
  }


  @NodeMap("text-content")
  public static class TextContent {
    
    @NodeMap("id")
    private String id;
    
    @NodeMap("group-type")
    private String group = org.vietspider.model.Group.ARTICLE;
    
    @NodeMap("min-percent-relation")
    private int minPercentRelation = 0;
    
    @NodeMap("date-range-relation")
    private int dateRangeRelation = 3;
    
    @NodeMap("content")
    private StringBuilder content;
    
    public TextContent() {
    }
    
    public TextContent(String id, String group, int minPercentRelation, int dateRangeRelation) {
      this.id = id;
      this.group = group;
      this.minPercentRelation = minPercentRelation;
      this.dateRangeRelation = dateRangeRelation;
      content = new StringBuilder();
    }

    public StringBuilder getContent() { return content; }

    public String getId() { return id; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public int getMinPercentRelation() { return minPercentRelation; }

    public void setMinPercentRelation(int minPercentRelation) {
      this.minPercentRelation = minPercentRelation;
    }

    public int getDateRangeRelation() { return dateRangeRelation; }
    public void setDateRangeRelation(int dateRangeRelation) { 
      this.dateRangeRelation = dateRangeRelation;
    }
    
  }
}
