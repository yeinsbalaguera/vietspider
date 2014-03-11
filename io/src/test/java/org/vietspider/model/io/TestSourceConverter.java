package org.vietspider.model.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.NameConverter;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SetterMap;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 7, 2007  
 */
public class TestSourceConverter {

  @NodeMap("source")
  public static class Source3 {

    @NodeMap(value = "category")
    protected String category;
    public void setCategory(String d){ this.category = d; }
    public String getCategory(){ return this.category; }

    @NodeMap(value = "name")
    protected String name ;
    public void setName(String n){ this.name = n; } 
    public String getName(){ return this.name; }  

    @NodeMap(value = "home")
    protected String home;
    public void setHome(String value){ home = value; }
    public String getHome(){ return this.home; }
    
    @NodesMap(value = "url_exts", item = "url")
    protected String [] urlExts;
    @SetterMap("url_exts")
    public void setURLExts(String [] values){ urlExts = values; }
    @GetterMap("url_exts")
    public String [] getURLExts(){ return urlExts; }

    @NodeMap(value = "pattern")
    protected String pattern;
    public void setPattern(String p){
      this.pattern = p.trim();//.replaceAll( "\\&amp;", "\\&").trim();    
    }
    public String getPattern(){ return this.pattern; }  

    @NodeMap(value = "charset")
    protected String charset;
    public void setCharset( String c){ this.charset = c; }
    public String getCharset(){ return this.charset; }
    
//    @NodeMap(value = "normalize")
//    protected boolean normalize = false;
//    @SetterMap("normalize")
//    public void setNormalize(boolean value){ normalize = value; }
//    public boolean getNormalize(){ return normalize; }

    @NodeMap(value = "depth")
    protected int depth = 1 ;
    public void setDepth(int d){ this.depth = d; }
    public int getDepth(){ return this.depth; }

    @NodesMap(value = "detached_region", item = "path")
    protected String[] detachedRegion;
    @SetterMap("detached_region")
    public void setDetachedRegion(String[] value){ this.detachedRegion = value; }
    @GetterMap("detached_region")
    public String[] getDetachedRegion(){ return this.detachedRegion; }

    @NodesMap(value = "clean_region", item="path")
    protected String[] cleanRegion;
    @SetterMap("clean_region")
    public void setCleanRegion(String [] r){ this.cleanRegion = r; }
    @GetterMap("clean_region")
    public String[] getCleanRegion(){ return this.cleanRegion; }
    
    @NodeMap(value = "clean_from")
    private boolean cleanFrom = false;
    @GetterMap("clean_from")
    public boolean  isCleanFrom(){ return cleanFrom; }
    @SetterMap("clean_from")
    public void setCleanFrom(boolean value){ cleanFrom = value; }

    @NodesMap(value = "access_region", item="path")
    protected String[] accessRegion;
    @SetterMap("access_region")
    public void setAccessRegion(String[] value){ this.accessRegion = value; }
    @GetterMap("access_region")
    public String[] getAccessRegion(){ return this.accessRegion; }
    
    
    @NodesMap(value = "title_region", item="path")
    protected String[] titleRegion;
    @SetterMap("title_region")
    public void setTitleRegion(String[] value){ this.titleRegion = value; }
    @GetterMap("title_region")
    public String[] getTitleRegion(){ return this.titleRegion; }
    
    @NodesMap(value = "des_region", item="path")
    protected String[] desRegion;
    @SetterMap("des_region")
    public void setDesRegion(String[] value){ this.desRegion = value; }
    @GetterMap("des_region")
    public String[] getDesRegion(){ return this.desRegion; }
    
//    @NodeMap(value = "date_format")
//    protected String dateFormat;
//    public void setDateFormat(String d){ this.dateFormat = d; }
//    public String getDateFormat(){ return this.dateFormat; }

  }
  
  private static Source3 load(File file) throws Exception {
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
    if(xml == null || xml.trim().length() < 1) return null;
    XMLDocument document = XMLParser.createDocument(xml, null);
    return XML2Object.getInstance().toObject(Source3.class, document);
  }  
  
  public static void save(Source source, File folder) throws Exception {
    NameConverter convert = new NameConverter();
    String group = convert.encode(source.getCategory());
    File file  = new File(folder+"/"+group);
    if(!file.exists()) file.mkdirs();
    file  = new File(file, group +"."+convert.encode(source.getName()));
    String xml = Object2XML.getInstance().toXMLDocument(source).getTextValue();
    RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
  }
  
  private static void listFile(List<File> list, File folder)  throws Exception {
    File [] files = folder.listFiles();
    for(File ele  : files) {
      if(ele.isFile()) list.add(ele);
      else listFile(list, ele);
    }
  }
  
  private static Source convertSource(Source3 source) {
    Source source2 = new Source();
    source2.setCategory(source.getCategory());
    source2.setEncoding(source.getCharset());
    source2.setDepth(source.getDepth());
    source2.setExtractType(ExtractType.NORMAL);
    
    source2.setUpdateRegion(new Region(Region.UPDATE, source.getAccessRegion()));
    
    Region [] paths = new Region[2];
    paths[0] = new Region(Region.EXTRACT, source.getDetachedRegion());
    if(source.getCleanRegion() != null  && source.getCleanRegion().length > 0) {
      paths[1] = new Region(Region.CLEAN, source.getCleanRegion());
      if(source.isCleanFrom()) {
        paths[1].getProperties().put(Region.CLEAN_FROM, "true");
      }
    }
    source2.setExtractRegion(paths);
    
    paths = new Region[2];
    if(source.getTitleRegion() != null && source.getTitleRegion().length > 0) {
      paths[0] = new Region(Region.ARTICLE_TITLE, source.getTitleRegion());
    }
    if(source.getDesRegion() != null && source.getDesRegion().length > 0) {
      paths[1] = new Region(Region.ARTICLE_DESCRIPTION, source.getDesRegion());
    }
    source2.setProcessRegion(paths);
    
    
    List<String> urls = new ArrayList<String>();
    urls.add(source.getHome());
    String [] exts = source.getURLExts();
    if(exts != null) Collections.addAll(urls, exts);
    source2.setHome(urls.toArray(new String[urls.size()]));
    
    source2.setName(source.getName());
    source2.setPattern(source.getPattern());
    source2.setPriority(1);
    source2.setGroup(Group.ARTICLE);
    return source2;
  }
  
  public static void main(String[] args)throws Exception  {
    List<File> list = new ArrayList<File>();
    listFile(list, new File("F:\\Temp\\data_convert\\input"));
    File output = new File("F:\\Temp\\data_convert\\output");
//    int counter = 0;
    for(File ele : list) {
      try {
        Source3 source = load(ele);
        if(source == null) continue;
        Source source2 = convertSource(source);
        save(source2, output);
//        counter++;
//        if(counter == 1) System.exit(0);
      }catch (Exception e) {
        e.printStackTrace();
      }
    }
    
  } 
  
}
