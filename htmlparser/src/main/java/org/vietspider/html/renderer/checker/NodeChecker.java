/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer.checker;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.Name;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2009  
 */
public abstract class NodeChecker {
  
  protected Name name = Name.A;
  protected LinkBlockChecker linkBlockChecker;
  protected ContentChecker contentChecker;
  protected FormChecker formChecker;
  
  protected int level = 0; 
  
  @SuppressWarnings("unused")
  private NodeChecker() {
  }
  
  public NodeChecker(Name name, int level) {
    this.name = name;
    this.level = level;
    formChecker = new FormChecker();
    contentChecker = new ContentChecker();
    linkBlockChecker = new LinkBlockChecker(contentChecker);
  }
  
  public boolean isValid(CheckModel model, int lvl) {
    if(lvl != level) return true;
    if(model.getNode() == null) return true;
    if(!model.getNode().isNode(name)) return true;
    if(model.getRawStatus() == CheckModel.UNCHECK) {
      boolean raw = contentChecker.isRawBlock(model.getNode());
      model.setRawData(raw ? CheckModel.RIGHT : CheckModel.NOT);
    }
    return check(model);
  }
  
  abstract boolean check(CheckModel model);
  
  public static List<NodeChecker> createDefaultCheckers(String url) {
    List<NodeChecker> checkers = new ArrayList<NodeChecker>();
    LinkNodeChecker linkNodeChecker = new LinkNodeChecker(url, 0);
    checkers.add(linkNodeChecker);
    return createDefaultCheckers(checkers);
  }
  
  public static List<NodeChecker> createDefaultCheckers() {
    List<NodeChecker> checkers = new ArrayList<NodeChecker>();
    return createDefaultCheckers(checkers);
  }
  
  private static List<NodeChecker> createDefaultCheckers(List<NodeChecker> checkers) {
    int level = 0;
    checkers.add(new SingleNodeChecker(Name.SCRIPT, level));
    checkers.add(new SingleNodeChecker(Name.COMMENT, level));
    checkers.add(new SingleNodeChecker(Name.MARQUEE, level));
    checkers.add(new SingleNodeChecker(Name.SELECT, level));
    checkers.add(new SingleNodeChecker(Name.NOSCRIPT, level));
    checkers.add(new SingleNodeChecker(Name.IFRAME, level));
    checkers.add(new SingleNodeChecker(Name.HR, level));
    
    level++;
    checkers.add(new BlockNodeChecker(Name.SPAN, level));
    checkers.add(new DIVNodeChecker(level));
    checkers.add(new TABLENodeChecker(level));
//    checkers.add(new TRNodeChecker(level));
    checkers.add(new ULNodeChecker(level));
    
    level++;
    checkers.add(new SingleNodeChecker(Name.INPUT, level));
    checkers.add(new EmptyNodeChecker(level));
    return checkers;
  }

  public int getLevel() { return level;  }
  
}
