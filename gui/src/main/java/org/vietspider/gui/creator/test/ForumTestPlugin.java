package org.vietspider.gui.creator.test;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.handler.CompleteDocHandler;
import org.vietspider.handler.PostForumHandler;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.model.Source;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.services.ClientLog;

public class ForumTestPlugin implements TestPlugin {
	
  private PostForumHandler postHandler;
  
  public ForumTestPlugin() {
    postHandler = new PostForumHandler(new CompleteDocHandler());
  }
	
	public Object process(Source source, HTMLDocument doc)  {
	  if(source.getProcessRegion().length < 1 
	      || source.getProcessRegion()[0].getPaths().length < 1)  return doc;
	  
	  HTMLExtractor extractor = new HTMLExtractor();
	  NodePathParser pathParser = new NodePathParser();
    HTMLNode root  = doc.getRoot();
    
    NodePath [] titlePaths = null;
    NodePath [] userPaths = null;
    NodePath [] contentPaths = null;
    NodePath [] pagePaths = null;
    try {
      titlePaths = pathParser.toNodePath(source.getProcessRegion()[0].getPaths());
      userPaths = pathParser.toNodePath( source.getProcessRegion()[1].getPaths());
      contentPaths = pathParser.toNodePath( source.getProcessRegion()[2].getPaths());
      if(source.getProcessRegion().length > 3 
          && source.getProcessRegion()[3].getPaths() != null) {
        pagePaths = pathParser.toNodePath(source.getProcessRegion()[3].getPaths());
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
      return null;
    }
    
    List<HTMLNode> titleNodes = extractor.matchNodes(root, titlePaths);
		List<HTMLNode> userNodes = extractor.matchNodes(root, userPaths);
		List<HTMLNode> contentNodes = extractor.matchNodes(root, contentPaths);
		List<HTMLNode> pageNodes = new ArrayList<HTMLNode>();
		if(pagePaths != null) pageNodes = extractor.matchNodes(root, pagePaths);
		
		if(titleNodes == null || userNodes == null || contentNodes == null)  return doc;
		
		HTMLNode html = HTMLParser2.clone(root);
		 html.addChild(buildMetaData(userNodes, contentNodes, pageNodes));
		
		for(int i = 0; i < titleNodes.size(); i++) {
		  if(titleNodes.get(i) == null) continue;
      html.addChild(titleNodes.get(i));
//      titleNodes.get(i).setParent(html);
		}
		
		postHandler.build(html, userNodes, contentNodes);
		
   /* int post = Math.min(userNodes.size(), contentNodes.size());
//    System.out.println("title nodes "+ titleNodes.size());
//    System.out.println("content user "+ contentNodes.size());
  
    
    for(int i = 0; i < post; i++) {
      if(i > 0) {
				HTMLNode hrNode = new NodeImpl(hrNodeValue, Name.HR, TypeToken.SINGLE); 
        html.addChild(hrNode);
//        hrNode.setParent(html);
      }
      
      HTMLNode userNode = userNodes.get(i);
      if(userNode != null) {
        userNode =  completeTable(userNode);
        html.addChild(userNode);
//        userNode.setParent(html);
      }

			HTMLNode brNode = new NodeImpl(brNodeValue, Name.BR, TypeToken.SINGLE); 
      html.addChild(brNode);
//      brNode.setParent(html);
      
      HTMLNode contentNode = contentNodes.get(i);
//      System.out.println(contentNode.getTextValue());
      if(contentNode != null) {
        contentNode =  completeTable(contentNode);
        html.addChild(contentNode);
//        contentNode.setParent(html);
      }
    }*/
	  return new HTMLDocument(html);
  }
	
  public HTMLNode completeTable(HTMLNode root){
    if(root.isNode(Name.TD) || root.isNode(Name.TH)){
      Attributes attributes = root.getAttributes();
      Attribute attr = attributes.get("width");
      if(attr != null) attributes.remove(attr);
      
      NodeImpl tr =  new NodeImpl("tr".toCharArray(), Name.TR, TypeToken.TAG);
      tr.setIsOpen(false);
      tr.addChild(root);
      root = tr;
    }

    if(root.isNode(Name.TR) || root.isNode(Name.TBODY) 
        || root.isNode(Name.THEAD) || root.isNode(Name.TFOOT)) {
      
      Attributes attributes = root.getAttributes();
      Attribute attr = attributes.get("width");
      if(attr != null) attributes.remove(attr);
      
      NodeImpl table =  new NodeImpl("table".toCharArray(), Name.TABLE, TypeToken.TAG);
      table.setIsOpen(false);
      table.addChild(root);
      root = table;
    }
    
    return root;
  }
  
  private HTMLNode buildMetaData(List<HTMLNode> userNodes,
      List<HTMLNode> contentNodes, List<HTMLNode> pageNodes) {
    String metaValue = "font color=\"red\"";
    NodeImpl metaNode = new NodeImpl(metaValue.toCharArray(), Name.FONT, TypeToken.TAG);
    metaNode.setIsOpen(false);
    StringBuilder metaBuilder = new StringBuilder();
    metaBuilder.append("Total of Users: ").append(userNodes.size());
    metaBuilder.append(", Total of Posts: ").append(contentNodes.size());
    NodeImpl metaContent = new NodeImpl(metaBuilder.toString().toCharArray(), Name.CONTENT, TypeToken.CONTENT);
    metaNode.addChild(metaContent);
    
    NodeImpl brNode = new NodeImpl("BR".toCharArray(), Name.BR, TypeToken.SINGLE);
    metaNode.addChild(brNode);
    
    String pageValue = buildPageValue(pageNodes);
    NodeImpl pageContent = new NodeImpl(pageValue.toCharArray(), Name.CONTENT, TypeToken.CONTENT);
    metaNode.addChild(pageContent);
    
    brNode = new NodeImpl("HR".toCharArray(), Name.BR, TypeToken.SINGLE);
    metaNode.addChild(brNode);
    
    return metaNode;
  }
  
  private String buildPageValue(List<HTMLNode> pageNodes) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < pageNodes.size(); i++) {
      buildPageValue(builder, pageNodes.get(i));
    }
    return "Page Value: " + builder.toString();
  }
  
  private void buildPageValue(StringBuilder builder, HTMLNode pageNode) {
    if(pageNode == null) return;
    NodeIterator iterator = pageNode.iterator(); 
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.CONTENT)) {
        if(builder.length() > 0) builder.append(", ");
        builder.append(node.getValue());
      }
    }
  }
  
//  private void extract(String Path) {
//    NodeHandler nodeHandler = worker.getResource(NodeHandler.class);
//    for(NodePath path : titleThreadPaths) {
//      HTMLNode node = extractor.lookNode(root, path);
//      if(node == null) continue;
//      List<HTMLNode> nodes  = new ArrayList<HTMLNode>(); 
//      nodeHandler.searchTextNode(node, nodes);
//      for(HTMLNode ele : nodes) {
//        builder.append(ele.getValue()).append(' ');
//      }
//      nodeHandler.removeNode(node);
//    }
//  }
  
}
