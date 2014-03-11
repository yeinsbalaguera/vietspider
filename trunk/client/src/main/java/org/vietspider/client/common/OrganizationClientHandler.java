/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.common.text.NameConverter;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.user.AccessChecker;
import org.vietspider.user.Group;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2008  
 */
public class OrganizationClientHandler {
  
  public String [] listUsers() throws Exception {
    return list(new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "system/user/user/")
    });
  }
  
  public String [] listGroups() throws Exception {
    return list(new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "system/user/group/")
    });
  }
  
  private String[] list(Header [] headers) throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    for(int i = 0; i < elements.length; i++) {
      if(elements[i].trim().isEmpty()) continue;
      elements[i] = NameConverter.decode(elements[i]);
    }
    return elements;
  }
  
  public void save(User user) throws Exception {
    save(user, new Header[] {
        new BasicHeader("action", "save"),
        new BasicHeader("file", "system/user/user/" + NameConverter.encode(user.getUserName()))
    });
  }
  
  public void save(Group group) throws Exception {
    save(group, new Header[] {
        new BasicHeader("action", "save"),
        new BasicHeader("file", "system/user/group/" + NameConverter.encode(group.getGroupName()))
    });
  }
  
  private void save(Object obj, Header[] headers) throws Exception {
    String xml = Object2XML.getInstance().toXMLDocument(obj).getTextValue();
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    connector.post(URLPath.FILE_HANDLER, bytes, headers);
  }
  
  public Group loadGroup(String groupName) throws Exception {
    return load(Group.class, new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "system/user/group/" + NameConverter.encode(groupName))
    });
  }
  
  public User loadUser(String username) throws Exception {
    return load(User.class, new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "system/user/user/"+NameConverter.encode(username))
    });
  }
  
  private <T> T  load(Class<T> clazz, Header [] headers) throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    String xml = new String(bytes, Application.CHARSET);
    if(xml.trim().length() < 1 || xml.trim().equals("-1"))  return null;
    XMLDocument document = XMLParser.createDocument(xml, null);
    return XML2Object.getInstance().toObject(clazz, document);
  }
  
  public void deleteUser(String username) throws Exception {
    delete(new Header[] {
        new BasicHeader("action", "delete"), 
        new BasicHeader("file", "system/user/user/"+NameConverter.encode(username))
    });
  }
  
  public void deleteGroup(String groupname) throws Exception {
    delete(new Header[] {
        new BasicHeader("action", "delete"), 
        new BasicHeader("file", "system/user/group/" + NameConverter.encode(groupname))
    });
  }
  
  private void delete(Header[] headers) throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
  }
  
  public AccessChecker loadAccessChecker() throws Exception {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    String username = connector.getUserHeader().getValue();
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.access.checker"),
        new BasicHeader("username", username)
    };
    byte [] bytes = connector.post(URLPath.APPLICATION_HANDLER, new byte[0], headers);
    ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
    AccessChecker accessChecker = (AccessChecker) objectInput.readObject();
    objectInput.close();
    return accessChecker;
  }
}
