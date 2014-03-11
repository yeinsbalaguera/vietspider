/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2009  
 */
public class TestNodeRendererSplit {
  
  private static String address = null;
  
  private static void proceess(File file, int [] values) throws Exception  {
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    
    HTMLNode body = ContentRendererFactory.searchBody(document);
    ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, address);
    
    int length = renderer.getTextValue().length();
    List<HTMLNode> renderNodes = renderer.getNodePositions(0, length);
    NodeRenderer nodeRenderer = new NodeRenderer(renderer, renderNodes, 0, length);
    
    String pattern = nodeRenderer.getMaxPatternValue();
    NodeRenderer [] nodeRenderers = nodeRenderer.split(pattern);
    
    for(int i = 0; i < values.length; i++) {
      System.out.println("/////////////////////////////////////////////////////////\n\n");
      nodeRenderer = nodeRenderers[values[i]];
      pattern = nodeRenderer.getMaxPatternValue();
      nodeRenderers = nodeRenderer.split(pattern);
    }
    
//    List<HTMLNode> htmlNodes = nodeRenderer.getNodes();
//    HTMLNode root = HTMLParser.clone(document.getRoot());
//    root.clearChildren(); 
//    HTMLNode parent = new HTMLParentUtils().getUpParent(htmlNodes);
//    root.addChild(parent);
    
//    file  = new File("F:\\Temp2\\web\\output\\xyz.html");
//    org.vietspider.common.io.RWData.getInstance().save(file, root.getTextValue().getBytes(Application.CHARSET));
    
    for(int i = 0; i < nodeRenderers.length; i++) {
      file  = new File("F:\\Temp2\\web\\output\\"+String.valueOf(i)+".txt");
      byte [] bytes = nodeRenderers[i].getText().getBytes(Application.CHARSET);
      org.vietspider.common.io.RWData.getInstance().save(file, bytes);
      ScoreCalculator.printNode(nodeRenderers[i]);
      
      file  = new File("F:\\Temp2\\web\\output\\"+String.valueOf(i)+".htm");
      bytes = nodeRenderers[i].getParent().getTextValue().getBytes(Application.CHARSET);
      org.vietspider.common.io.RWData.getInstance().save(file, bytes);
    }
    
    
//    System.out.println("bi tra ve roi na "+nodeRenderers[0].compare(nodeRenderers[1]));
  }
  
  public static void main(String[] args) throws Exception {
    UtilFile.deleteFolder(new File("F:\\Temp2\\web\\output\\"), false);
    File file = new File("F:\\Temp2\\web\\output\\");
    file.mkdirs();
    
//    int [] values = {0,1,2};
//    file  = new File("F:\\Temp2\\web\\vietnamnet.htm");
    
//    int [] values = {1,0,0};
//    file  = new File("F:\\Temp2\\web\\blogger1.html");
    
//    int [] values = {};
//    file  = new File("F:\\Temp2\\web\\cand.htm");
    
//    int [] values = {};
//    file  = new File("F:\\Temp2\\web\\thuytop.htm");
    
//    int [] values = {0};
//    file  = new File("F:\\Temp2\\web\\thuytop2.htm");
    
//    int [] values = {0,0,1};
//    file  = new File("F:\\Temp2\\web\\vnexpress2.htm");
    
//    int [] values = {};
//    file  = new File("F:\\Temp2\\web\\laodong1.htm");
    
//    int [] values = {1,1, 0};
//    file  = new File("F:\\Temp2\\web\\thanhnien.htm");
    
//    int [] values = {0,1,0};
//    file  = new File("F:\\Temp2\\web\\kenh14_1.htm");
    
//    int [] values = {};
//    file  = new File("F:\\Temp2\\web\\vneconomy.htm");
    
//    int [] values = {};
//    address = "http://3conference.wordpress.com/2008/12/15/eedesign-staircase/";
//    file  = new File("F:\\Temp2\\web\\wordpress8.htm");
    
    int [] values = {};
    file  = new File("F:\\Temp2\\web\\ngoisaoblog8.htm");
    
    proceess(file, values);
    
//    File folder  = new File("F:\\Temp2\\web\\");
//    File [] files = folder.listFiles();
//    for(int i = 0; i < files.length; i++) {
//      if(files[i].getName().endsWith(".htm") 
//          || files[i].getName().endsWith(".html")) {
//        proceess(files[i]);
//      }
//    }
  }
}
