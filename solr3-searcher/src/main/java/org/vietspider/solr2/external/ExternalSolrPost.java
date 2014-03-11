/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.external;

import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.chars.refs.CharRef;
import org.vietspider.chars.refs.CharRefs;
import org.vietspider.chars.refs.CharsSequence;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.common.Application;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.RequestManager;
import org.vietspider.net.client.WebClient;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 19, 2011  
 */
public class ExternalSolrPost {

  public static final String DEFAULT_POST_URL = "http://localhost:8983/solr/update";
  public static final String VERSION_OF_THIS_TOOL = "1.3";
  private static final String SOLR_OK_RESPONSE_EXCERPT = "<int name=\"status\">0</int>";

  private static final String DEFAULT_COMMIT = "yes";
  private static final String DEFAULT_OUT = "no";

  private static final String DEFAULT_DATA_TYPE = "application/xml";

  private static final String DATA_MODE_FILES = "files";
  private static final String DATA_MODE_ARGS = "args";
  private static final String DATA_MODE_STDIN = "stdin";
  private static final String DEFAULT_DATA_MODE = DATA_MODE_FILES;

  private static final Set<String> DATA_MODES = new HashSet<String>();
  static {
    DATA_MODES.add(DATA_MODE_FILES);
    DATA_MODES.add(DATA_MODE_ARGS);
    DATA_MODES.add(DATA_MODE_STDIN);
  }

  protected URL solrUrl;
  protected WebClient webClient;
  private String postURL = "http://localhost:8080/solr/update/"; 

  protected HttpMethodHandler handler;
  private RefsEncoder encoder;

  public ExternalSolrPost(String address) throws Exception {
    this.solrUrl = new URL(address);
    webClient = new WebClient();
    webClient.setURL(address, new URL(address));
    handler = new HttpMethodHandler(webClient);
    
    encoder = new RefsEncoder() {
      public char[] encode (char[] chars) {
        CharRefs charRefs = new CharRefs();
        if(!charRefs.isSorted()) charRefs.sort(new Comparator<CharRef>() {
          public int compare(CharRef o1, CharRef o2){
            if(o1.getValue() == o2.getValue()) return 0;
            if(o1.getValue() > o2.getValue()) return 1;
            return -1;
          }
        });
        CharsSequence refValue = new CharsSequence(chars.length * 6);
        char c;
        CharRef ref;
        int i = 0;
        while (i < chars.length){
          c = chars[i];
          ref = charRefs.searchByValue(c, new Comparator<CharRef>(){
            public int compare(CharRef o1, CharRef o2){
              if(o1.getValue() == o2.getValue()) return 0;
              if(o1.getValue() > o2.getValue()) return 1;
              return -1;
            }
          });
          if (ref != null){
            refValue.append ("&#");
            refValue.append (String.valueOf(ref.getValue()));
            refValue.append (';');
          }else if (!(c < 0x007F)){
            refValue.append ("&#");
            if (hexadecimal) {          
              refValue.append ('x');
              refValue.append (Integer.toHexString (c));
            }else{          
              refValue.append(String.valueOf((int)c));
            }
            refValue.append (';');
          } else if(c == '[') {
            refValue.append ("&#91;");
          } else if(c == ']') {
            refValue.append ("&#93;");
          } else {
            refValue.append (c);
          }
          i++;
        }
        return refValue.getValues();
      }
    };
  }

  public byte[] postData(TempDocument temp) throws Exception {
    XMLDocument document = XMLParser.createDocument("<add><doc></doc></add>", null);
    XMLNode root = document.getRoot().getChild(0).getChild(0);
    
    Article article = temp.getArticle();

    Meta meta = article.getMeta();
    Content  content = article.getContent();
    Domain domain  = article.getDomain();

    addField(root, "id", article.getId());

    addField(root, "title", meta.getTitle());
    String desc = new String(encoder.encode(meta.getDesc().toCharArray()));
    addField(root, "desc", "<![CDATA[" + desc + "]]>");
    if(content.getContent().trim().length() < 1) {
      addField(root, "content", "[empty]");
    } else {
      String text = new String(encoder.encode(content.getContent().toCharArray()));
      
      addField(root, "content", "<![CDATA[" + text + "]]>");
    }

    addField(root, "source_name", domain.getName());
    addField(root, "category", domain.getCategory());
    addField(root, "url", "<![CDATA[" + meta.getSource() + "]]>");
    addField(root, "crawl-time", meta.getTime());
    
    List<Image> images = temp.getImages();
    for(int i = 0; i < images.size(); i++) {
      Image image = images.get(i);
      addField(root, "resource_id", image.getId());
      addField(root, "resource_name", "<![CDATA[" + image.getName()+ "]]>");
      addField(root, "resource_type", image.getType());
//      System.out.println(image.getId() +  " : " + image.getImage().length);
//      if(image.getImage() == null || image.getImage().length < 1) continue;
//      Calendar calendar = Calendar.getInstance();
//      calendar.setTime(CalendarUtils.getDateTimeFormat().parse(meta.getTime()));
//      new ImageIO("content/solr2/external/images/").saveImage(calendar, image);
    }
    
    String xml = document.getRoot().getChild(0).getTextValue();
//    System.out.println(xml);
    byte [] bytes = xml.getBytes(Application.CHARSET);
    
//    System.out.println(" chuan bi post to external solr "+ article.getId());
    
    bytes = postData(bytes);
//    String text = new String(bytes, Application.CHARSET);
    //  System.out.println(text);
//    if(text.toLowerCase().indexOf("error report") > -1) {
//      System.out.println(xml);
//      System.out.println("\n\n\n\n\n");
//      HTMLDocument doc = new HTMLParser2().createDocument(text);
//      TextRenderer2 renderer2 = new TextRenderer2(doc.getRoot(), TextRenderer2.RENDERER);
//      LogService.getInstance().setMessage(null, renderer2.getTextValue().toString());
//      System.exit(0);
//    }

//    System.out.println(new String(bytes));

    return bytes;
  }

  private void addField(XMLNode root, String name, String value) {
    XMLNode node =  root.addTextChild("field", value);//root.addChild("field");
    Attributes attributes = AttributeParser.getInstance().get(node);
    attributes.add(new Attribute("name", name));
  }

  public byte[] postData(byte [] bytes) throws Exception {
//    final String type = System.getProperty("type", DEFAULT_DATA_TYPE);
//
//    ByteArrayOutputStream output = new ByteArrayOutputStream();
//    ByteArrayInputStream input = new ByteArrayInputStream(bytes);

    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, postURL);
    httpPost.addHeader(new BasicHeader("Content-Type", "application/xml; charset=utf-8"));

//    int length = bytes.length;
    httpPost.setEntity(new ByteArrayEntity(bytes));

    HttpHost httpHost = webClient.createHttpHost(postURL);
    HttpResponse httpResponse = null;
    httpResponse = webClient.execute(httpHost, httpPost);
    if(httpResponse == null) return new byte[0];
    return handler.readBody(httpResponse);

    //    HttpURLConnection urlc = null;
    //    try {
    //      urlc = (HttpURLConnection) solrUrl.openConnection();
    //      urlc.setRequestMethod("POST");
    //
    //      urlc.setDoOutput(true);
    //      urlc.setDoInput(true);
    //      urlc.setUseCaches(false);
    //      urlc.setAllowUserInteraction(false);
    //      urlc.setRequestProperty("Content-type", type);
    //
    //      urlc.setFixedLengthStreamingMode(bytes.length);
    //
    //      OutputStream out = null;
    //      try {
    //        out = urlc.getOutputStream();
    //        pipe(input, out);
    //      } finally {
    //        if(out!=null) out.close();  
    //      }
    //
    //      InputStream in = null;
    //      try {
    //        in = urlc.getInputStream();
    //        pipe(in, output);
    //      } finally {
    //        if(in!=null) in.close(); 
    //      }
    //
    //    } finally {
    //      if(urlc!=null) urlc.disconnect();
    //    }

    //    return null;//''output.toByteArray();
  }

//  private static void pipe(InputStream source, OutputStream dest) throws IOException {
//    byte[] buf = new byte[1024];
//    int read = 0;
//    while ( (read = source.read(buf) ) >= 0) {
//      if (null != dest) dest.write(buf, 0, read);
//    }
//    if (null != dest) dest.flush();
//  }

}
