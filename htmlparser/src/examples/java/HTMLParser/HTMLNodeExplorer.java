import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 9, 2006
 */
public class HTMLNodeExplorer {

  private File file;

  public HTMLNodeExplorer() throws Exception {
    URL url = getClass().getResource("/");
    String path  = url.toString();
    path  = path + File.separator+"resources"+File.separator+"complexshapes.html";
    url = new URL(path);
    file = new File(url.toURI());

    System.out.println(file.getPath());
    System.out.println("*********************************************************");
  }

  public void viewNode(String path) throws Exception {
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(file, null);
    NodePathParser pathParser = new NodePathParser();
    NodePath nodePath = pathParser.toPath(path);
    HTMLExtractor htmlExtractor = new HTMLExtractor();
    HTMLNode node = htmlExtractor.extract(document, new NodePath[]{nodePath}).getRoot();
    System.out.println(node.getTextValue());
  }

  public HTMLDocument removeNode(String name)  throws Exception {
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(file, null);
    removeNode(name, document.getRoot());
    return document;
  }

  private void removeNode(String name, HTMLNode root){
    List<HTMLNode> children = root.getChildren();
    if(children == null) return;
    Iterator<HTMLNode> iter = children.iterator();
    while(iter.hasNext()){
      HTMLNode node  = iter.next();
      if(node.isNode(name)){
        iter.remove();
      }else{
        removeNode(name, node);
      }
    }
  }

  public List<HTMLNode> getAllNode(String name) throws Exception {
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(file, null);
    List<HTMLNode> list = new ArrayList<HTMLNode>();
    getAllNode(name, document.getRoot(), list);
    return list;
  }

  private void getAllNode(String name, HTMLNode root, List<HTMLNode> list) {
    if(root.isNode(name)) list.add(root);
    List<HTMLNode> children = root.getChildren();
    if(children == null) return;
    for (HTMLNode child : children) {
      getAllNode(name, child, list);
    }
  }

  public static void main(String[] args) throws Exception {
    HTMLNodeExplorer explorer = new HTMLNodeExplorer();
    explorer.viewNode("HTML.BODY.FONT[0].B");

    System.out.println("*********************************************************");
    explorer.viewNode("BODY[0].TABLE[1].TBODY[0].TR[0].TD[1].FONT");

    System.out.println("*********************************************************");
	explorer.viewNode("BODY[0].TABLE[1].TBODY[0].TR[0].TD[i>1].FONT");

	System.out.println("*********************************************************");
	explorer.viewNode("BODY[0].TABLE[1].TBODY[0].TR[0].TD[*].FONT");



    System.out.println("*********************************************************");
    List<HTMLNode> list = explorer.getAllNode("a");
    System.out.println("Total nodes "+list.size());
    System.out.println("\n"+list.get(15).getTextValue());
    System.out.println("\n"+list.get(15).getParent().getTextValue());
    System.out.println("\n"+list.get(15).getParent().getChildren().size());

    System.out.println("*********************************************************");
    HTMLDocument document = explorer.removeNode("a");
    list = new ArrayList<HTMLNode>();
    explorer.getAllNode("a", document.getRoot(), list);
    System.out.println(list.size());

  }
}
