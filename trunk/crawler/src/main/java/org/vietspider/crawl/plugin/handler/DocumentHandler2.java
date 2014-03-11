/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.handler.XMLHandler3;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.NodePath;
import org.vietspider.js.JsUtils;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 5, 2010  
 */
public class DocumentHandler2 extends XMLDataHandler2 {
  
    public DocumentHandler2(Source source) {
      super(source);
    }

    public XMLDocument buildDocument(PluginData pluginData) {
      this.resources = new ArrayList();
      this.documentId = pluginData.getMeta().getId();

      HTMLDocument document = (HTMLDocument)pluginData.getLink().getDocument();
      Meta meta = pluginData.getMeta();
      this.link = pluginData.getLink();

      XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
      XMLDocument xmlDocument = new XMLDocument(xmlRoot);

      XMLNode xmlContentNode = new XMLNode("content", TypeToken.TAG);
      xmlRoot.addChild(xmlContentNode);

      HTMLNode root = document.getRoot();
      Region[] regions = this.source.getProcessRegion();

      for (int i = 0; i < regions.length; i++) {
        String key = regions[i].getName().toLowerCase();

        if (key.endsWith("đính kèm")) {
          searchFileNode(root, regions[i]);
        }
        else
        {
          String content = createNode(root, regions[i], xmlRoot);

          if (key.endsWith("số ký hiệu"))
            meta.setTitle(content.trim());
          else if (key.toLowerCase().endsWith("trích yếu")) {
            meta.setDesc(content.trim());
          }
        }
      }
      createSourceNode(xmlRoot, pluginData.getLink().getAddress());

      xmlRoot.setIsOpen(false);

      if ((meta.getTitle() == null) || (meta.getTitle().isEmpty())) return null;

      return xmlDocument;
    }

    protected XMLNode createPropertyNode(String name, String value)
    {
      String lower = name.toLowerCase();
      if (lower.endsWith("ngôn ngữ")) {
        lower = value.toLowerCase().trim();
        if ((lower.indexOf("việt") > -1) || ("vietnamese".equals(lower)))
        {
          value = "vi";
        } else if ((lower.indexOf("trung") > -1) || ("chinese".equals(lower)))
        {
          value = "zh";
        } else if ((lower.indexOf("anh") > -1) || ("english".equals(lower)))
        {
          value = "en";
        }
        else value = "vi";

        return super.createPropertyNode("Ngôn ngữ", value);
      }if (lower.endsWith("phân loại")) {
        return super.createPropertyNode(name, buildCategories(value));
      }
      return super.createPropertyNode(name, value);
    }

    private String buildCategories(String value) {
      String[] elements = value.split("»");
      StringBuilder builder = new StringBuilder();
      for (String element : elements)
        if (!element.trim().isEmpty()) {
          if (builder.length() > 0) builder.append('/');
          builder.append(element.trim());
        }
      return builder.toString();
    }

    protected String createNode(HTMLNode root, Region region, XMLNode xmlRoot) {
      String content = "";
      try {
        NodePath[] nodePaths = region.getNodePaths();
        int type = region.getType();
        if (type == 3) type = 1;
        content = lookupTextValue2(root, nodePaths, type, this.link.getAddress());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(this.source, e);
        content = "";
      }

      xmlRoot.addChild(createPropertyNode(region.getName(), content));

      return content;
    }

    protected void searchFileNode(HTMLNode root, Region region) {
      try {
        NodePath[] nodePaths = region.getNodePaths();
        if (nodePaths == null) return;
        List nodes = this.extractor.matchNodes(root, nodePaths);
        for (int k = 0; k < nodes.size(); k++) {
          searchResources((HTMLNode)nodes.get(k));
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(this.source, e);
      }
    }

    protected void searchResources(HTMLNode root) {
      NodeIterator iterator = root.iterator();
      while (iterator.hasNext()) {
        HTMLNode n = iterator.next();

        if (n.isNode(Name.A)) {
          Attributes attributes = n.getAttributes();
          Attribute attr;
          if ((attr = attributes.get("href")) != null) {
            String address = attr.getValue();
            if ((address == null) || (address.length() < 1))
              continue;
            if (address.toLowerCase().startsWith("javascript")) {
              String[] params = JsUtils.getParams(address);
              for (String param : params) {
                String id = buildResourceId();
                String name = getName(n, param);
                if (name == null) name = id;
                this.resources.add(new XMLHandler3.Resource(id, name, param));
              }
            } else {
              String id = buildResourceId();
              String name = getName(n, address);
              if (name == null) name = id;
              this.resources.add(new XMLHandler3.Resource(id, name, address));
            }
          }
        }
      }
    }
}
