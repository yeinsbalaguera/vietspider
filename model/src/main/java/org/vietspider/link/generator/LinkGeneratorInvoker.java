/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLDocument;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 17, 2009  
 */
public class LinkGeneratorInvoker {

  public static String[] invoke(List<Object> generators, HTMLDocument doc, int type) {
    List<String> values = new ArrayList<String>();
    invoke(generators, type, doc, values);
    return values.toArray(new String[values.size()]);
  }

  public static boolean invoke(List<Object> generators, int type, Object...params) {
    //HTMLDocument doc, List<String> values,
    boolean successfull = false;
    //    Source source = null;
    for(int i = 0; i < generators.size(); i++) {
      Object generator = generators.get(i);

      //      if(generator instanceof Generator) {
      //        Generator iGenerator = (Generator) generator;
      //        iGenerator.generate(values);
      //        continue;
      //      }
      short gType = -1; 
      Class<?> clazz = generator.getClass();
      try {
        Method method = clazz.getDeclaredMethod("getType", new Class[0]);
        gType = (Short)method.invoke(generator, new Object[0]);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(clazz.getName(), e);
      }

      if(gType != type) continue;

      /*try {
        Method method = clazz.getDeclaredMethod("getSource", new Class[0]);
        source = (Source) method.invoke(generator, new Object[0]);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(clazz.getName(), e);
      }*/
      
//      System.out.println(" for testing "+ getSource(clazz, generator));

      Method [] methods = clazz.getDeclaredMethods();
      for(int k = 0; k < methods.length; k++) {
        if(!methods[k].getName().equalsIgnoreCase("generate")) continue;
        Class<?> [] classes = methods[k].getParameterTypes();
        //        System.out.println(" chuan bin invoke " + clazz + " : "+ classes.length+ " : "+ params.length);
        if(classes.length != params.length || !isParam(classes, params)) continue;
        try {
          methods[k].invoke(generator, params);
          if(!successfull) successfull = true;
        } catch (Exception e1) {
          LogService.getInstance().setThrowable(getSource(clazz, generator), e1, clazz.getName());
        }
      }
    }
    return successfull;
  }

  private static String getSource(Class<?> clazz, Object generator) {
    String _default = clazz.getSimpleName().toLowerCase();
    Exception exp = null;
    while(clazz != Object.class) {
      try {
        Field field = clazz.getDeclaredField("sourceFullName");
        field.setAccessible(true);
//        System.out.println("test clazz " + clazz.getSimpleName() + " : "+ field + " : "+ (String)field.get(generator));
        return (String)field.get(generator);
      } catch (Exception e) {
        if(exp == null) exp = e;
      } 
      clazz = clazz.getSuperclass();
    }
    
    if(exp != null) {
      LogService.getInstance().setMessage(exp, clazz.getName()); 
    }

    return _default;
  }

  private static boolean isParam(Class<?> [] classes, Object [] values) {
    for(int i = 0; i < classes.length; i++) {
      //      System.out.println("compare "+values[i].getClass() +  " :  "+ classes[i] + " : " + classes[i].isInstance(values[i]));
      if(!classes[i].isInstance(values[i])) return false;
    }
    return true;
  }


  public static boolean constainGenerator(List<Object> generators, int type) {
    for(int i = 0; i < generators.size(); i++) {
      Object generator = generators.get(i);
      short gType = -1; 
      Class<?> clazz = generator.getClass();
      try {
        Method method = clazz.getDeclaredMethod("getType", new Class[0]);
        gType = (Short)method.invoke(generator, new Object[0]);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(clazz.getName(), e);
      }

      if(gType == type) return true;;
    }
    return false;
  }

  public static void invokeEndSession(List<Object> generators) {
    for(int i = 0; i < generators.size(); i++) {
      Object generator = generators.get(i);
      Class<?> clazz = generator.getClass();
      Method method = null;
      try {
        method = clazz.getDeclaredMethod("endSession", new Class[0]);
      } catch (Exception e) {
      }

      try { 
        if(method != null) method.invoke(generator, new Object[0]);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(clazz.getName(), e);
      }
    }
  }

  /* public static void main(String[] args) {
    Class clazz = UpdateDocumentGenerator.class;
    Method [] methods = clazz.getDeclaredMethods();
    for(int k = 0; k < methods.length; k++) {
      if(!methods[k].getName().equalsIgnoreCase("generate")) continue;
      Class<?> [] classes = methods[k].getParameterTypes();
      System.out.println(" chuan bin invoke " + clazz + " : "+ classes.length+ " : ");
      if(classes.length == 3) {
        for(Class cl : classes) {
          System.out.println(cl);
        }
      }
    }
  }*/

}
