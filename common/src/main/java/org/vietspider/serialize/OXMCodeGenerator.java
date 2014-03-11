/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class OXMCodeGenerator {
  
  public static boolean generate(Class<?> clazz) throws Exception {
    URI uri = clazz.getResource("/").toURI();
    File folder = new File(uri);
    
    if(!folder.isDirectory()) folder = folder.getParentFile();

    return generate(clazz, folder, folder);
  }

  public static boolean generate(Class<?> clazz, File codeFolder, File clazzFolder) throws Exception {
    StringBuilder codeBuilder = new StringBuilder();
    String fileName = clazz.getSimpleName() + "_MappingImpl";
    
    File file = new File(codeFolder, fileName+".java");
    
    if(file.exists()) {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      if(text.indexOf("code=" + String.valueOf(clazz.hashCode())) > -1) return false;
//      System.out.println(text);
//      System.out.println(text.indexOf("code=" + String.valueOf(clazz.hashCode())));
    }
    
    
    List<String> imports = new ArrayList<String>();

    codeBuilder.append("public class ").append(fileName);
    codeBuilder.append(" implements SerializableMapping<").append(clazz.getSimpleName()).append("> {");
    codeBuilder.append('\n');
    codeBuilder.append('\n');
    
    codeBuilder.append("\t");
    codeBuilder.append("private final static int code=").append(clazz.hashCode()).append(';');
    codeBuilder.append('\n');
    codeBuilder.append('\n');
    
    codeBuilder.append("\t");
    codeBuilder.append("public ").append(clazz.getSimpleName()).append(" create() {");
    codeBuilder.append('\n');
    
    codeBuilder.append("\t\t");
    codeBuilder.append("return new ").append(clazz.getSimpleName()).append("();");
    codeBuilder.append('\n');
    
    codeBuilder.append("\t");
    codeBuilder.append("}");
    codeBuilder.append('\n');
    

    codeBuilder.append('\n');
    ToObjectCode.generate(clazz, imports, codeBuilder);

    codeBuilder.append('\n');
    ToNodeCode.generate(clazz, imports, codeBuilder); 

    codeBuilder.append('}').append('\n');
    
    
    StringBuilder builder = new StringBuilder();
    
    builder.append("package ").append(clazz.getPackage().getName()).append(";").append('\n');
    builder.append('\n').append('\n');

    
//    builder.append("import ").append(clazz.getName()).append(";").append('\n');
//    builder.append("import java.util.*").append(";").append('\n');
    if(clazz.isMemberClass()) {
      imports.add("import " + clazz.getEnclosingClass().getName() + "." + clazz.getSimpleName() + ";");
    } else if(!ReflectUtil.isPrimitive(clazz)) {
      builder.append("import ").append(clazz.getName()).append(";").append('\n');
    } 
    builder.append("import org.vietspider.token.attribute.*").append(";").append('\n');
    builder.append("import org.vietspider.parser.xml.XMLNode;").append('\n');
    builder.append("import ").append(Object2XML.class.getName()).append(";").append('\n');
    builder.append("import ").append(XML2Object.class.getName()).append(";").append('\n');
    builder.append("import ").append(SerializableMapping.class.getName()).append(";").append('\n');
//    if(clazz.isMemberClass()) {
//      builder.append("import static ");
//      builder.append(clazz.getEnclosingClass().getName()).append(".");
//      builder.append(clazz.getSimpleName()).append(';');
//      builder.append('\n');
//    }
    
    for(int i = 0; i < imports.size(); i++) {
      String line = imports.get(i);
      builder.append(line).append('\n');
    }
    
    builder.append('\n').append('\n');
    
    builder.append(codeBuilder);

//    try {
    RWData.getInstance().save(file, builder.toString().getBytes());
//    } catch (Exception e) {
//      System.err.println(file.getAbsolutePath());
//      throw e;
//    }

    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    //    String directory  = clazz.getPackage().getName();
    //    directory = folder.getAbsolutePath() + String.valueOf(File.separatorChar) 
    //              + directory.replace('.', File.separatorChar);
    //    System.out.println(directory);
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    /*int compilationResult = */compiler.run(null, null, errorStream,
        "-d", clazzFolder.getAbsolutePath(), file.getAbsolutePath());

    //    System.out.println("hehe " + clazzFolder);

    String error  = new String(errorStream.toByteArray());
    if(error.length() > 0) {
      throw new Exception("Compile: " + error);
    }

    return true;
    //    NodeMap map = clazz.getAnnotation(NodeMap.class);
    //    if(map == null) throw new Exception("Not found NodeMap in "+ clazz);
    //    XMLNode node = new XMLNode(map.value().toCharArray(), map.value(), TypeToken.TAG);
    //    toXML(bean, node);
    //    return new XMLDocument(node);
  }
 
}
