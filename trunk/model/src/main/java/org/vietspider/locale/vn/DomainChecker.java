/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.locale.vn;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2009  
 */
public class DomainChecker {

  private final static DomainChecker INSTANCE = new DomainChecker();

  public static DomainChecker getDomainChecker() { return INSTANCE; }

  private VietnameseSingleWords dict = new VietnameseSingleWords();

  private DomainChecker() {
  }

  public double calculate(String host) {
    if(host.startsWith("www")) {
      int idx = host.indexOf('.');
      host = host.substring(idx+1);
    }
    int index = host.lastIndexOf('.');
    Score score = new Score();
    if(index < 0) {
      calculateScrore3(score, host);
    } else {
      String ext = host.substring(index+1);
      if(ext.length() < 5) {
        host  = host.substring(0, index);
      }
      calculateScrore3(score, host);
      if("vn".equalsIgnoreCase(ext)) score.value += 20;
      host = deleteExt(host) ;
      if(host.startsWith("vn")) score.value += 5;
      if(host.endsWith("vn")) score.value += 7;
    }
    int rate = (score.total*100)/host.length();
    if(rate < 30) {
      score.value = Math.max(0, score.value - 5);
    } else if(rate >= 30 && rate < 50) {
      score.value = Math.max(0, score.value - 2);
    } else if(rate >= 50 && rate < 75) {
      score.value += 2;
    } else if(rate >= 75 && rate < 100) {
      score.value += 3;
    } else if(rate == 100) {
      score.value += 5;
    } 
//    System.out.println("ti le co " + host+ " : "+ rate);
    return score.value;
  }
  
  private String deleteExt(String host) {
    int index = host.lastIndexOf('.');
    if(index < 0) return host;
    String ext = host.substring(index+1);
    if(ext.length() <= 3
        || "mobi".equalsIgnoreCase(ext) 
        || "info".equalsIgnoreCase(ext)) {
      return deleteExt(host.substring(0, index));
    }
    return host;
  }
  
  private void calculateScrore3(Score score, String text) {
    String[] elements = text.split("\\.");
    for(int i = 0; i < elements.length; i++) {
      calculateScrore2(score, elements[i]);
    }
  }
  
  private void calculateScrore2(Score score, String text) {
    String domain = text;
    int time  = 0;
    while(domain.length() > 0) {
      calculateScrore(score, domain);
      if(score.value > 0) break;
      if(domain.length() > 0) domain = domain.substring(1);
      time++;
    }
    score.value = score.value - time;
  }

  private void calculateScrore(Score score, String text) {
    int index = text.length();
    int start = 0;
    while(index > 0) {
      String w = text.substring(start, index);
//      System.out.println("word is "+ w);
      if(dict.contains(w)) {
        if(w.length() == 2) {
          score.value += 0.5;
          score.total += w.length();
        } else if(w.length() == 3) {
          score.value += 1;
          score.total += w.length();
        } else if(w.length() == 4) {
          score.value += 2;
          score.total += w.length();
        } else if(w.length() == 5) {
          score.value += 3;
          score.total += w.length();
        } else if(w.length() == 6) {
          score.value += 4;
          score.total += w.length();
        } else if(w.length() > 6) {
          score.value += 5;
          score.total += w.length();
        }
        calculateScrore(score, text.substring(index, text.length()));
        break;
      } else if(start + 2 >= index) {
//        calculateScrore(score, text.substring(1, text.length()));
        break;
      } 
      index--;
    }
  }
  
  private static class Score  {
    private double value = 0.0;
    private int total = 0;
  }

  /* public void split(List<Word> list, String text) {
    System.out.println(text);
    int index = text.length();
    int start = 0;
    while(index > 0) {
      String w = text.substring(start, index);
      if(dict.contains(w)) {
        List<Word> nexts = new ArrayList<Word>();
        split(nexts, text.substring(index, text.length()));

        Word word = new Word();
        word.value = w;
        word.nexts = nexts;

        list.add(word);
        break;
      } else if( start + 2 >= index) {
        split(list, text.substring(1, text.length()));
        break;
      } 
      index--;
    }
  }

  public static void print(Word word) {
    System.out.print(word.value);
    System.out.print(" - ");
    for(int i = 0; i < word.nexts.size(); i++) {
      print(word.nexts.get(i));
      System.out.println();
    }
  }

  private static class Word {
    private String value;
    private List<Word> nexts = new ArrayList<Word>();
  }*/

  public static void main(String[] args) {
    DomainChecker checker = new DomainChecker();
    String domain = "www.dzone";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "linkhay";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "vietnamnet.vn";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "vnexpress";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "dangthanh";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "vanhoc";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    domain = "danasachs";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    
    domain = "ida.first.fraunhofer.de";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n");
    
    domain = "chinhduyen.demo.to";
    System.out.println(domain + " : " + checker.calculate(domain)+ "\n");
    
    domain = "galaxy.uci.agh.edu.pl";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "titleinsuranceny.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "pichunter.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "www.exploringchild.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "333tourthai.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "www.mightyriverpower.co.nz";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "kingkong.ugo.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "www.caledonian.org";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain  = "moe.gov.bw";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "mightyriverpower.co.nz";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );  
    
    domain = "mapllc.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "music.loitraitim.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    domain = "www.ezbatdongsan.com";
    System.out.println(domain + " : " + checker.calculate(domain) + "\n" );
    
    /*List<Word> words = new ArrayList<Word>();
    checker.split(words, domain);
    for(int i = 0; i < words.size(); i++) {
      print(words.get(i));
    }*/
  }
}
