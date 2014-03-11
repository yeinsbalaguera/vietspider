/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
@NodeMap(value = "meta-relations")
public class MetaRelations {
  
  @NodeMap("id")
  private String metaId;
  
  public MetaRelations() {
  }
  
  public MetaRelations(String id) {
    this.metaId = id;
  }
  
  @NodesMap(value="items", item="item")
  private List<MetaRelation> relations = new ArrayList<MetaRelation>();

  @GetterMap("items")
  public List<MetaRelation> getMetaRelations() { return relations; }
  @SetterMap("items")
  public void setMetaRelations(List<MetaRelation> relations) { this.relations = relations; }
  
  public String getMetaId() { return metaId; }
  public void setMetaId(String metaId) { this.metaId = metaId; }
  
//  public void addMetaRelation(MetaRelation relation) {
//    if(relation.getMeta() == null || relation.getRelation() == null) return;
//    String id =  relation.getRelation();
//    if(relation.getMeta().equals(id)) return;
//    for(int i = 0; i < relations.size(); i++) {
//      if(id.equals(relations.get(i).getRelation())) return;
//    }
//    relations.add(relation);
//  }
  
}
