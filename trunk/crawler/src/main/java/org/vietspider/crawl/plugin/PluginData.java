/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin;

import java.io.Serializable;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.crawl.link.Link;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.io.model.GroupIO;
import org.vietspider.locale.vn.Word;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 18, 2009  
 */
public class PluginData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  //  private Meta meta;
  
  private Article article;
  private Link link;
  
  //for save to database
  
  private transient Group group;

  private transient  List<HTMLNode> textNodes;

  private transient  boolean isWebPage  = false;
  
  private TpWorkingData tpData;
//  protected LinkedList<Image> images = new LinkedList<Image>();
  private String tag;
  private List<Word> words ;
//  private boolean isTag = false;

  //for mining
  private transient List<HTMLNode> cloneTextNodes = null;

  public PluginData(Link link, Meta meta, Group group) {
    this.link = link;
    this.article = new Article();
    this.article.setMeta(meta);
    this.group = group;
  }
  
  public PluginData(Link link, Article article, Group group) {
    this.link = link;
    this.article = article;
    this.group = group;
  }

  public Meta getMeta() { return article.getMeta(); }
  public Article getArticle() { return article; }
  
  public Link getLink() { return link; }
  
  public List<HTMLNode> getTextNodes() { return textNodes; }

  public void setTextNodes(List<HTMLNode> textNodes) {
    this.textNodes = textNodes; 
    if(CrawlerConfig.INDEX_CONTENT) {
      NodeHandler nodeHandler = new NodeHandler();
      cloneTextNodes = nodeHandler.clone(textNodes);
      return;
    }
//    if(TopicTrackingServices.getInstance() == null || group.getMinPercentRelation() < 1) return ;
    NodeHandler nodeHandler = new NodeHandler();
    cloneTextNodes = nodeHandler.clone(textNodes);  
  }

  public Group getGroup() {
    if(group == null) {
      Groups groups = GroupIO.getInstance().loadGroups();
      this.group = groups.getGroup(article.getDomain().getGroup());
    }
    return group; 
  }

  public List<HTMLNode> getCloneTextNodes() { return cloneTextNodes; }

  public boolean isWebPage() { return isWebPage; }
  public void setWebPage(boolean isWebpage) { this.isWebPage = isWebpage; }
  
  public TpWorkingData getTpData() { return tpData; }
  public void setTpData(TpWorkingData tpData) { this.tpData = tpData; }

//  public LinkedList<Image> getImages() { return images; }
//  public void setImages(LinkedList<Image> images) { this.images = images; }
  
  public List<Word> getWords() { return words; }
  public void setWords(List<Word> list) { words = list; }

//  public boolean isTag() { return isTag;  }
//  public void setTag(boolean isTag) { this.isTag = isTag;  }

  public String getTag() { return tag; }
  public void setTag(String tag) { this.tag = tag; }
  
}
