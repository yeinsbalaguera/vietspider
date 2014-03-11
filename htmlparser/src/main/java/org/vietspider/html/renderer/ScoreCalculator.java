/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 18, 2009  
 */
public class ScoreCalculator {

//  private int lastScore = 0; 

  private static int word1 = 1;
  private static int word2 = 2;
  private static int word3 = 3;
  private static int word4 = 4;

  private static int sentence1 = 3;
  private static int sentence2 = 4;
  private static int sentence3 = 5;

  private static int pattern1 = 4;
  private static int pattern2 = 5;
  private static int pattern3 = 6;
  
  public int calculate(int pattern, int sentence, int word) {
    int score = calculate1(pattern, sentence, word);
//    lastScore = score;
    return score;
  }


  private int calculate1(int pattern, int sentence, int word) {
    if(pattern >= 30) {
      if(sentence >= 5) {
        if(word >= 100) {
          return sentence*word*word4*sentence3*pattern1;
        }

        if(word < 100 && word >= 20) {
          return sentence*word*word3*sentence3*pattern1;
        }

        if(word < 20 && word >= 10)  {
          return sentence*word*word2*sentence3*pattern1;
        }

        //word < 10          
        return sentence*word*word1*sentence3*pattern1;
      } 

      if(sentence >= 2 && sentence < 5) {
        if(word >= 100) {
          return sentence*word*word4*sentence2*pattern1;
        }

        if(word < 100 && word >= 20) {
          return sentence*word*word3*sentence2*pattern1;
        }

        if(word < 20 && word >= 10)  {
          return sentence*word*word2*sentence2*pattern1;
        }

        //word < 10          
        return sentence*word*word1*sentence2*pattern1;
      }

      //sentence == 1

      if(word >= 100) {
        return sentence*word*word4*sentence1*pattern1;
      }

      if(word < 100 && word >= 20) {
        return sentence*word*word3*sentence1*pattern1;
      }

      if(word < 20 && word >= 10)  {
        return sentence*word*word2*sentence1*pattern1;
      }

      //word < 10          
      return 0;

    }

    if(pattern >= 7 && pattern < 30) {
      if(sentence >= 5) {
        if(word >= 100) {
          return sentence*word*word4*sentence3*pattern2;
        }

        if(word < 100 && word >= 20) {
          return sentence*word*word3*sentence3*pattern2;
        }

        if(word < 20 && word >= 10)  {
          return sentence*word*word2*sentence3*pattern2;
        }

        //word < 10          
        return sentence*word*word1*sentence3*pattern2;
      } 

      if(sentence >= 2 && sentence < 5) {
        if(word >= 100) {
          return sentence*word*word4*sentence2*pattern2;
        }

        if(word < 100 && word >= 20) {
          return sentence*word*word3*sentence2*pattern2;
        }

        if(word < 20 && word >= 10)  {
          return sentence*word*word2*sentence2*pattern2;
        }

        //word < 10          
        return sentence*word*word1*sentence2*pattern2;
      }

      //sentence == 1

      if(word >= 100) {
        return sentence*word*word4*sentence1*pattern2;
      }

      if(word < 100 && word >= 20) {
        return sentence*word*word3*sentence1*pattern2;
      }

      if(word < 20 && word >= 10)  {
        return sentence*word*word2*sentence1*pattern2;
      }

      //word < 10          
      return 0;
    }

    //pattern < 7
    if(sentence >= 5) {
      if(word >= 100) {
        return sentence*word*word4*sentence2*pattern3;
      }

      if(word < 100 && word >= 20) {
        return sentence*word*word3*sentence2*pattern3;
      }

      if(word < 20 && word >= 10)  {
        return sentence*word*word2*sentence2*pattern3;
      }

      //word < 10          
      return sentence*word*word1*sentence2*pattern3;
    }

    //sentence == 1

    if(word >= 100) {
      return sentence*word*word4*sentence1*pattern3;
    }

    if(word < 100 && word >= 20) {
      return sentence*word*word3*sentence1*pattern3;
    }

    if(word < 20 && word >= 10)  {
      return sentence*word*word2*sentence1*pattern3;
    }

    //word < 10          
    return sentence*word*word1*sentence1*pattern3;
  }

  // pattern >= 30 -> 1 --> sentence --> word

  /* private int calculateWord(int counter) {
    if(counter >= 100) return counter*10;
    if(counter < 100 && counter >= 20) return counter*5;
    if(counter < 20 && counter >= 10) return counter*2;
    return counter;
  }

  private int calculateSentence(int counter) {
    if(counter >= 5) return counter*2*10;
    if(counter < 5 && counter >= 2) return counter*2*2;
    return counter*2;
  }

  private int calculatePattern(int pattern) {
    if(pattern < 10) return (30 - pattern + 1)*2;
    if(pattern >= 10 && pattern < 30) return 30 - pattern + 1;
    return 1;
  }*/
  
  public static  void printNode( NodeRenderer nodeRenderer) {
    int max = nodeRenderer.getMaxPattern();
    int min = nodeRenderer.getMinPattern();
    int mid = (max + min)/2;
    int total = nodeRenderer.getTotalPattern();
    int time = nodeRenderer.getTimePattern();
    int overage = 0;
    if(time > 0) overage = total/time;
//    System.out.println("score: " + nodeRenderer.getScore() 
//        + ", min : " + min
//        + " , max : "+ max
//         + " , mid : "+ mid
//        + " , overage : "+ overage
//        + " , time pattern: "+ time
//        + " , total pattern: "+ total
//        + " , total link : "+ countLink(nodeRenderer.getParent())
//        );
  }
  
  private static int countLink(HTMLNode node) {
    if(node == null) return 0;
    NodeIterator iterator = node.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(Name.A)) counter++;
    }
//    System.out.println(" thay co cai na "+ counter);
    return counter;
  }

  public static void main(String[] args) {
    ScoreCalculator calculator = new ScoreCalculator();
    System.out.println(calculator.calculate(22, 1, 10));
    System.out.println(calculator.calculate(30, 1, 10));
    System.out.println(calculator.calculate(30, 2, 4));
    System.out.println(calculator.calculate(30, 3, 4));
  }

}
