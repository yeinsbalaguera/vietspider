/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tags;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;
import org.vietspider.html.HTMLNode;
import org.vietspider.locale.vn.VietnameseConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2011  
 */
public class Tags {

  private volatile static Tags INSTANCE = null; 

  public synchronized final static Tags getInstance(String defaultKey)  {
    if(INSTANCE == null) INSTANCE = new Tags(defaultKey);
    return INSTANCE;
  }

  private int minScore = 5;
  private double minRate = 1.0;
  private double minDefaultRate = 4.0;

  private List<KWords> list = new ArrayList<KWords>();

  //"kinh-te"
  public Tags(String defaultKey) {
    try {
      init(UtilFile.getFolder("sources/type/tags/"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    setDefault(defaultKey);
    File folder = UtilFile.getFolder("sources/sources/ARTICLE/");
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(!files[i].isDirectory()) continue;
      String name = NameConverter.decode(files[i].getName());
      String ename = VietnameseConverter.toAlias(name.toLowerCase());
      for(int j = 0; j < list.size(); j++) {
        if(list.get(j).getName().equals(ename)) {
          list.get(j).setLabel(name);
        }
      }
    }
  }

  //default-key = "kinh-te"
  public Tags(File configFolder, String defaultKey) throws Exception {
    init(configFolder);
    setDefault(defaultKey);
  }

  public boolean hasTag() {
    if(Application.LICENSE != Install.SEARCH_SYSTEM) return false;
    return list.size() > 0; 
  }

  private void init(File configFolder) throws Exception {
    File[] files = configFolder.listFiles();
    for(int i = 0; i < files.length; i++) {
      String value  = new String(RWData.getInstance().load(files[i]), Application.CHARSET);
      String name = files[i].getName();
      int idx = name.indexOf('.');
      if(idx > 0) name = name.substring(0, idx);
      KWords kWords = new KWords(value);
      kWords.setName(name);
      list.add(kWords);  
    }
  }

  public void setDefault(String name) {
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getName().equals(name)) {
        list.get(i).setDefault(true);
        break;
      }
    }
  }

  public String tag(String id, List<HTMLNode> textNodes) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < textNodes.size(); i++) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(textNodes.get(i).getValue());
    }

    String tag = tag(new Document(builder.toString()));

//    if(tag == null) {
//      File file = new File("D:\\Temp\\unit_test2\\"+ id +".txt");
//      try {
//        writer.save(file, builder.toString().getBytes(Application.CHARSET));
//      } catch (Exception e) {
//        LogService.getInstance().setThrowable(e);
//      }
//    }

    return tag;
  }


  public String tag(Document document) {
    //    List<String> tags = new ArrayList<String>();
    List<Tag> tags = new ArrayList<Tag>();
    //    System.out.println("==================== " + document.getId());
    //    List<Word> words = document.getWords();
    //    if(words.size() > 0) {
    //      words.get(0).print(document.getText());
    //      System.out.println();
    //    }

    int tempScore = 0;
    for(int i = 0; i < list.size(); i++) {
      KWords kWords = list.get(i);
      if(kWords.isDefault()) continue;
      if(Document.print) {
        System.out.println(" \n\n checking kwords "+ kWords.getName() + " : "+ kWords.getWords().size());
      }
      int score = document.score(kWords.getWords());
      tempScore += score;
      if(score < minScore) continue;
      if(i == 0) score += 5;
      double rate = ((double)score*100)/document.total();
//      System.out.println("rate " +rate +  " = "+ score);
      if(rate < minRate) continue;
      if(kWords.getLabel() != null) {
        tags.add(new Tag(kWords.getLabel(), score));
      } else {
        tags.add(new Tag(kWords.getName(), score));
      }
    }

//        System.out.println(" vao day "+ tags.size());

    if(tags.size() < 1 ) {
      if(Document.print) {
        System.out.println(" \n\n============= default ================");
      }
      KWords _default = null;
      for(int i = 0; i < list.size(); i++) {
        KWords kWords = list.get(i);
        if(!kWords.isDefault()) continue;
        _default = kWords;
        tempScore += document.score(_default.getWords());
        break;
      }
      if(_default == null) return null;
      double rate = (tempScore*100)/document.total();
      if(Document.print) {
        System.out.println( " default : " 
            + tempScore + " : "+ document.total() + " = %: " + rate);
      }

      if(rate < minDefaultRate) return null;
      if(_default.getLabel() != null) return _default.getLabel();

      //      System.out.println(_default.getLabel()+ "  : "+ _default.getName());
      return _default.getName();
    }

    Collections.sort(tags, new Comparator<Tag>() {
      @Override
      public int compare(Tag o1, Tag o2) {
        return o2.score - o1.score;
      }
    });

    if(Document.print) {
      for(int i = 0; i < tags.size(); i++) {
        System.out.println( tags.get(i).name + " : " + tags.get(i).score + " : "+ document.total()
            + " = %: " + ((tags.get(i).score*100)/document.total()));
      }
    }
    return tags.get(0).name;
  }
  
  
  public int getMinScore() { return minScore; }
  public void setMinScore(int minScore) { this.minScore = minScore; }
  
  public double getMinRate() { return minRate; }
  public void setMinRate(double minRate) { this.minRate = minRate; }
  
  public double getMinDefaultRate() { return minDefaultRate; }
  public void setMinDefaultRate(double minDefaultRate) { 
    this.minDefaultRate = minDefaultRate;
  }

  private static class Tag {

    private String name;
    private int score;

    private Tag(String name, int score) {
      this.name = name;
      this.score = score;
      //      System.out.println(" ===  > "+ this.rate);
    }
  }

}
