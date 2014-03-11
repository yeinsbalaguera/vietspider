/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2009  
 */
public class BlockNodeChecker extends NodeChecker {
  
  public BlockNodeChecker(Name name, int level) {
    super(name, level);
  }
  
  @SuppressWarnings("unused")
  boolean check(CheckModel model) {
//    System.out.println("\n\n================================================");
//    System.out.println(node.getTextValue());
    HTMLNode node = model.getNode();
    if(model.hasRawData()) return true;
    
    if(model.getTextBlockStatus() == CheckModel.UNCHECK) {
      boolean isTextBlock = contentChecker.isTextBlock(node, true, 50, 5);
      model.setTextBlockStatus( isTextBlock ? CheckModel.RIGHT : CheckModel.NOT);
    }
    
    if(model.hasTextBlock()) return true;
    
    if(linkBlockChecker.isLink(model)) {
//      if(node.getTextValue().indexOf("u tượng của phần mền trong control panel") > -1) { 
//      System.out.println("===================================================");
//      System.out.println(node.getTextValue());
//      }
      return false;
    }
    
    if(contentChecker.isEmptyBlock(node, model.getTotalOfLink() > 1)) {
//        System.out.println("===================================================");
//        System.out.println(node.getTextValue());
      return false;
    }
    
    if(formChecker.hasForm(node)) {
      if(linkBlockChecker.hasParagraph(model.getNode(), 1)) return true;
      return false;
    }
    
    return true;
  }
  

}
