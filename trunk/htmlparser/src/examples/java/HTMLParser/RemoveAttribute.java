

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 25, 2006
 */
public class RemoveAttribute {

  static void clean(HTMLNode node, Map<String, String[]> map){
    Iterator<String> iter = map.keySet().iterator();
    String key, value [];
    Attributes attributes = node.getAttributes(); 
    while(iter.hasNext()){
      key = iter.next();
      if(key.equals("*") || node.isNode(key)){
        value = map.get(key);
        for(String ele : value){
          if(attributes.contains(ele)){
            attributes.remove(ele);
          }
        }
      }
    }
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    for(HTMLNode child : children){
      clean(child, map);
    }
  }

  public static void main(String[] args) {
    try{
      URL url = new URL("http://www.java.net");
//      System.out.println(URLDecoder.decode("video_id=http%3A%2F%2Fliveu-80.vo.llnwd.net%2Fflurl%2Fmb53%2Fnew_media3%2F2006%2F8%2F29%2F174380_media_flash8.flv&homeurl=http%3A%2F%2Fwww.flurl.com%2F&endmovies=http%3A%2F%2Fwww.flurl.com%2Fthumbs.php%3Fid%3D174380&embed=%3Ctable%20border%3D%270%27%20bgcolor%3D%27ffffff%27%20cellpadding%3D%270%27%20cellspacing%3D%270%27%3E%3Ctr%3E%3Ctd%3E%3Cembed%20id%3D%27flurl_media%27%20name%3D%27flurl_media%27%20width%3D%27519%27%20height%3D%27438%27%20src%3D%27http%3A%2F%2Fwww.flurl.com%2Fflvplayer2.swf%3Fvideo%3Dhttp%3A%2F%2Fwww.flurl.com%2Fflash_player_info.php%3Fid%3D174380%26flash%3D8%27%20quality%3D%27high%27%20bgcolor%3D%27white%27%20play%3D%27true%27%20loop%3D%27false%27%20allowScriptAccess%3D%27sameDomain%27%20type%3D%27application%2Fx-shockwave-flash%27%20pluginspage%3D%27http%3A%2F%2Fwww.macromedia.com%2Fgo%2Fgetflashplayer%27%3E%3C%2Fembed%3E%3C%2Ftd%3E%3C%2Ftr%3E%3Ctr%3E%3Ctd%20align%3D%27right%27%3E%3Cstrong%3E%3Ca%20href%3D%27http%3A%2F%2Fwww.flurl.com%2F%27%3EHosted%20on%20Flurl%20Video%20Search%3C%2Fa%3E%20-%20%3Ca%20href%3D%27http%3A%2F%2Fwww.flurl.com%2Fmedia%27%3EWatch%20More%20Videos%3C%2Fa%3E%20%3C%2Fstrong%3E%3C%2Ftd%3E%3C%2Ftr%3E%3C%2Ftable%3E"));
      HTMLParser2 parser2 = new HTMLParser2();
      HTMLDocument document = parser2.createDocument(url.openStream(), "utf-8");

      NodePathParser pathParser = new NodePathParser();
      NodePath nodePath = pathParser.toPath("BODY[0].DIV[0].TABLE[0].TBODY[0].TR[1].TD[3].DIV[10]");
      HTMLExtractor htmlExtractor = new HTMLExtractor();
      HTMLNode node = htmlExtractor.extract(document, new NodePath[]{nodePath}).getRoot();

      System.out.println(node.getTextValue());

      System.out.println("=================================================================");

      Map<String, String []> map = new HashMap<String, String[]>();
      map.put("a", new String[]{"href"});
      map.put("*", new String[]{"class"});

      clean(node, map);

      System.out.println(node.getTextValue());
    }catch(Exception exp){
      exp.printStackTrace();
    }
  }

}