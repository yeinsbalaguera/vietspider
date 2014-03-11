/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap(value = "relations")
public class Relations {
  
  @NodeMap("id")
  private String metaId;
  
  public Relations() {
  }
  
  public Relations(String id) {
    this.metaId = id;
  }
  
  @NodesMap(value="items", item="item")
  private List<Relation> relations = new ArrayList<Relation>();

  public List<Relation> getRelations() { return relations; }
  public void setRelations(List<Relation> relations) { this.relations = relations; }
  
  public String getMetaId() { return metaId; }
  public void setMetaId(String metaId) { this.metaId = metaId; }
  
  public void addRelation(Relation relation) {
    if(relation.getMeta() == null || relation.getRelation() == null) return;
    String id =  relation.getRelation();
    if(relation.getMeta().equals(id)) return;
    for(int i = 0; i < relations.size(); i++) {
      if(id.equals(relations.get(i).getRelation())) return;
    }
    relations.add(relation);
  }
  
}
