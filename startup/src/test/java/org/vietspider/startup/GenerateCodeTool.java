/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.startup;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.model.plugin.joomla.XMLJoomlaConfig;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.OXMCodeGenerator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class GenerateCodeTool {
  
//  private static Set<Class<?>> generated = new HashSet<Class<?>>();
  
  static void generateClazz(Class<?> clazz, boolean print) throws Exception {
//  if(generated.contains(clazz)) return;
    URL url = null;
//    try {
    if(clazz.isMemberClass()) {
      url = clazz.newInstance().getClass().getResource(clazz.getEnclosingClass().getSimpleName()+".class");
    } else {
      url = clazz.newInstance().getClass().getResource(clazz.getSimpleName()+".class");
    }
//    } catch (Throwable e) {
//      System.out.println(" hiihihi  "+ clazz);
//      System.err.println(e.toString());
//      return;
//    }
    URI uri = url.toURI();
    if(print) System.out.println(uri.toString());
    File folder = new File(uri);
    folder = folder.getParentFile();

    //    System.out.println(folder.getAbsolutePath());

    String sp = String.valueOf(File.separatorChar);

    String path = folder.getAbsolutePath();
    int index = path.indexOf(sp + "target" + sp);
    String javaPath = null;
    String clazzPath = null;
    String packagePath = null;
    
    Package package1 = clazz.getPackage();
    
    packagePath = package1.getName().replace('.', File.separatorChar);
    //    if(path.indexOf(sp + "test-classes" + sp) > -1) {
    if(path.indexOf("test-classes") > -1) {
      javaPath = path.substring(0, index+1) + "src" + sp + "test" + sp + "java" + sp + packagePath;
      clazzPath = path.substring(0, index+1) + "target" + sp + "test-classes" ;
    } else {
      javaPath = path.substring(0, index+1) + "src" + sp + "main" + sp + "java" + sp + packagePath;
      clazzPath = path.substring(0, index+1) + "target" + sp + "classes" ;
    }


    if(print) System.out.println("Java Folder: " +javaPath);
    if(print) System.out.println("Clazz Folder: " + clazzPath);

    if(!OXMCodeGenerator.generate(
        clazz, new File(javaPath), new File(clazzPath))) return;
    if(print) System.out.println(path);
    if(print) System.out.println("\n\n");
    
    Field[] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      Class<?> _clazz = (Class<?>)field.getType();
      if(_clazz.isArray()) {
        _clazz = field.getType().getComponentType();
      }
      
      if((List.class.isAssignableFrom(_clazz)
          || Map.class.isAssignableFrom(_clazz)
          || Set.class.isAssignableFrom(_clazz) )
         && !Class.class.isInstance(field.getGenericType())) {
        ParameterizedType paramType = (ParameterizedType)field.getGenericType();
        for(int i = 0; i < paramType.getActualTypeArguments().length; i++) {
         Class<?> tclazz = (Class<?>)paramType.getActualTypeArguments()[i];
          NodeMap nodeMap = tclazz.getAnnotation(NodeMap.class);
          //      System.out.println(field.getName() +  " : "+ nodeMap);
          if(nodeMap != null && tclazz.isMemberClass()) generate(tclazz);
        }
      }
      NodeMap nodeMap = _clazz.getAnnotation(NodeMap.class);
      //      System.out.println(field.getName() +  " : "+ nodeMap);
      if(nodeMap != null && _clazz.isMemberClass()) generate(_clazz);
    }
    
  }

  static void generate(Class<?> clazz) throws Exception {
    generateClazz(clazz, true);
//    generated.add(clazz);

    Field[] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      Class<?> _clazz = (Class<?>)field.getType();
      if(_clazz.isArray()) {
        _clazz = field.getType().getComponentType();
      }
      
      if((List.class.isAssignableFrom(_clazz)
          || Map.class.isAssignableFrom(_clazz)
          || Set.class.isAssignableFrom(_clazz) )
         && !Class.class.isInstance(field.getGenericType())) {
        ParameterizedType paramType = (ParameterizedType)field.getGenericType();
        for(int i = 0; i < paramType.getActualTypeArguments().length; i++) {
         Class<?> tclazz = (Class<?>)paramType.getActualTypeArguments()[i];
         if(tclazz == clazz) continue;
          NodeMap nodeMap = tclazz.getAnnotation(NodeMap.class);
          //      System.out.println(field.getName() +  " : "+ nodeMap);
          if(nodeMap != null) generate(tclazz);
        }
      }
      if(_clazz == clazz) continue;
      NodeMap nodeMap = _clazz.getAnnotation(NodeMap.class);
      //      System.out.println(field.getName() +  " : "+ nodeMap);
      if(nodeMap != null) generate(_clazz);
    }
  }

  public static void main(String[] args) throws Exception {
//    generate(Article.class);
//    generate(Link.class);
//    generate(SearchResult.class);
//    generate(Category.class);
//    generate(Source.class);
//    generate(DrupalCategoriesConfig.class);
//    generate(XMLTradeKeyConfig.class);
    
//    generate(LinkExplorer.class);
    
//    generate(ArticleCleaner.class);
    
//    generate(XMLWordPressConfig.class);
//    generate(XArticles.class);
    
//    generate(JoomlaSyncData.class);
    generate(XMLJoomlaConfig.class);
//    generate(XMLBdsConfig.Category.class);
//    generate(XMLBdsConfig.Region.class);
//    generate(BdsSyncData.class);
    
//    generate(XMLVBulletinConfig.class);
//    generate(VBulletinSyncData.class);
    
//    generate(DatabaseConfig.class);
    
//    generate(Advertise.class);
    
//    generate(org.vietspider.server.plugin.cvs.CSVModel.class);
//    generate(Groups.class);
//    generate(MetaLink.class);
//    generate(CrawlerStatus.class);
//    generate(SyncDatabaseConfig.class);
//    generate(ExcelModel.class);
//    generate(ArticleCollection.class);
    //    System.out.println(new Source().getClass().getResource("Source.class"));
//    generate(org.vietspider.bean.Article.class, false);
    
//    generate(XMLWordPressConfig.class);
//    generate(WordPressSyncData.class);
    
//    generate(TopArticleCollection.class);
  }

}
