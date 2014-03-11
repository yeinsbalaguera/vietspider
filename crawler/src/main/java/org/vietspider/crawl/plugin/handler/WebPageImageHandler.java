/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2008  
 */
public final class WebPageImageHandler {
  
 /* private ImageHandler imageLoader;
  
  private WebPageMetaExtractor metaExtractor;
  
  public WebPageImageHandler(WebPageMetaExtractor extractor) {
    this.metaExtractor = extractor;
//    this.imageLoader = imgLoader;
  }

  public boolean handle(PluginData pluginData, HTMLNode root, List<HTMLNode> contentNodes) {
//  //download images
    List<HTMLNode> imageNodes = searchImageNodes(root);
    Group group  = pluginData.getGroup();
    if(group.isDownloadImage()) {
      boolean force = group.isForceDownloadImage();
//      if(!imageLoader.download(imageNodes, pluginData, force)) return false;
      return true;
    } 
    
    if(!group.isSetImageToMeta()) return true;
    
    HTMLNode imgNode = metaExtractor.extractImage(contentNodes);
    Attributes attributes = AttributeParser.getInstance().get(imgNode);
    if(attributes == null) return true; 
    Attribute attr = attributes.get("src");
    if(attr == null) return true;
    String srcValue  = attr.getValue();
    if(srcValue == null || srcValue.length() < 1) return true;

    Link link = pluginData.getLink();
    ImageData imageData = new ImageData();
    imageData.setLink(imageLoader.createLink(link, srcValue));
    
    Meta meta = pluginData.getMeta();
    try {
//      imageLoader.read(imageData, link.getAddress());
    } catch (Exception e) {
      LogService.getInstance().setMessage(link.getSource(), e, null);
      return true;
    }
    imageLoader.setImage2Meta(imageData, meta);
    return true;
  }
  
  private List<HTMLNode> searchImageNodes(HTMLNode node) {
    List<HTMLNode>list = new ArrayList<HTMLNode>();
    searchImageNodes(list, node);
    return list;
  }
  
  private void searchImageNodes(List<HTMLNode>list, HTMLNode node) {
    if(node.isNode(Name.IMG)) {
      list.add(node);
      return ;
    }
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return ;
    for(int i = 0; i < children.size(); i++) {
      searchImageNodes(list, children.get(i));
    }
  }*/
}
