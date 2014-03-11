/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.remote;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.Header;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relations;
import org.vietspider.common.io.LogService;
import org.vietspider.db.sync.SyncHandler;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 10, 2009  
 */
public class SyncArticleData extends SyncHandler<Article> {

  //sync.data.server=localhost:9245
  //action  = add.article
  //cached folder = article
  public SyncArticleData() {
    super("article", "sync.data.server", "add.article", URLPath.REMOTE_DATA_HANDLER);
    dataCollection = new ArticleCollection();
  }

  /*public Object load(String id, String action, Header...headers) {
    Iterator<Article> iterator = temp.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();

      if(!article.getId().equals(id)) continue;

      if("load.article".equals(action)
          && article.getSaveType() == Article.SAVE_NEW)  
        return article;

      if("load.meta".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_META) 
            ) return article.getMeta();
      if("load.content".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_CONTENT) 
            ) return article.getContent();
      if("load.relations".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_RELATION) 
            ) return article.getRelations();
      if("load.domain".equals(action)
              && article.getSaveType() == Article.SAVE_NEW)  
        return article.getDomain();
    }

    return super.load(id, action, headers);
  }*/

  public Object load(String id, String action, Header...headers) {
    Iterator<Article> iterator = queue.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();

      if(!article.getId().equals(id)) continue;

      if("load.article".equals(action)
          && article.getSaveType() == Article.SAVE_NEW)  
        return article;

      if("load.meta".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_META) 
      ) return article.getMeta();
      if("load.content".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_CONTENT) 
      ) return article.getContent();
      if("load.relations".equals(action)
          && (article.getSaveType() == Article.SAVE_NEW 
              || article.getSaveType() == Article.SAVE_RELATION) 
      ) return article.getRelations();
      if("load.domain".equals(action)
          && article.getSaveType() == Article.SAVE_NEW)  
        return article.getDomain();
    }

    try {

      if("load.meta".equals(action)) return super.<Meta>load(id, action, Meta.class, headers);
      if("load.content".equals(action)) return super.<Content>load(id, action, Content.class, headers);
      if("load.domain".equals(action)) return super.<Domain>load(id, action, Domain.class, headers);

      if("load.relations".equals(action)) {
        return super.<Relations>load(id, action, Relations.class, headers).getRelations();
      }

      return super.load(id, action, Article.class, headers);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    return null;
  }

  public void removeSuccessfullValue(ArrayList<Article> list, String id) {
    for(int i = 0; i < list.size(); i++) {
      if(list.get(i).getId().equals(id)) {
        list.remove(i);
        //        System.out.println(" remove "+ list.get(i).getId()+ " : "+ list.size());
        return;
      }
    }
    return;
  }

}
