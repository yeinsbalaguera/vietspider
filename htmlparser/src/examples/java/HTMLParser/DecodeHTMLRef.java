
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 31, 2006
 */
public class DecodeHTMLRef {

  private static RefsDecoder decoder = new RefsDecoder();

  private static void build(StringBuilder builder, HTMLNode node){
    if(node.isNode(Name.SCRIPT) || node.isNode(Name.COMMENT)) return;
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return;
    for(HTMLNode child : children){
      if(child.isNode(Name.CONTENT)){
        builder.append('\n').append(decoder.decode(child.getValue()));
        continue;
      }
      build(builder, child);
    }
  }

  public static void main(String[] args)  throws Exception {
    URL url = new URL("http://vnexpress.net/Vietnam/Xa-hoi/2006/10/3B9EFB66/");
    HTMLDocument document = new HTMLParser2().createDocument(url.openStream(), null);
    StringBuilder builder = new StringBuilder();
    build(builder, document.getRoot());
    File file = new File("E:\\Temp\\a.txt");
    if(!file.exists()) file.createNewFile();
    FileOutputStream stream = new FileOutputStream(file);
    stream.write(builder.toString().getBytes("utf-8"));
    stream.close();
    PrintStream print = new PrintStream(System.out, true, "utf-8");
    print.print(builder);
  }
}
