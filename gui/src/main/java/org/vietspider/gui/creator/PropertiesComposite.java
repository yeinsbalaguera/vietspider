/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.net.client.HttpClientFactory;
import org.vietspider.ui.htmlexplorer.URLTemplateUtils;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 9, 2007  
 */
public class PropertiesComposite extends Composite {

  private List lstName;
  private Text txtValue;
  
  private Object menu;

  private Properties properties;
  
//  private LoginWebsite loginWebsite;

  public PropertiesComposite( ApplicationFactory factory, 
      Composite composite, /*LoginWebsite loginWebsite_,*/ boolean isSimple){
    super(composite, SWT.NONE);
    
//    this.loginWebsite = loginWebsite_;

    setLayout(new GridLayout(2, false));

    factory.setComposite(this);

    Group group = factory.createGroup("groupPropertiesName", null, new GridLayout(1, false));
    group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    factory.setComposite(group);

    lstName = factory.createList( new String[]{}, new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        loadProperty();
      }      
    });

    lstName.setFont(UIDATA.FONT_10);
    if(isSimple) {
      lstName.setItems(new String[]{"ContentFilter", "Referer", "Proxy"});
      GridData gridData = new GridData(GridData.FILL_BOTH);
      lstName.setLayoutData(gridData);
    } else {
      lstName.setItems(loadNames());
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = 120;
      lstName.setLayoutData(gridData);
    }
    if(lstName.getItemCount() > 0) lstName.select(0);

//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(lstName, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//      lstName.setMenu(new Menu(getShell(), SWT.POP_UP));
//    } else {
      menu = new Menu(getShell(), SWT.POP_UP);
      lstName.setMenu((Menu)menu);
//    }
    
//    factory.createMenuItem( menu, "itemPropertiesLogin", new SelectionAdapter(){
//      @SuppressWarnings("unused")
//      public void widgetSelected(SelectionEvent evt) {
//        try {
//          loginWebsite.login(properties);
//        } catch (Exception e) {
//          ClientLog.getInstance().setException(null, e);
//        }
//      }
//    });
    
    factory.createStyleMenuItem( menu, "itemPropertiesRemoveValue", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeProperty();
      }
    });
    
    factory.createStyleMenuItem( menu, "itemPropertiesRemoveValues", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeProperties();
      }
    });
    
//    menu.addMenuListener(new MenuAdapter(){
//      @SuppressWarnings("unused")
//      public void menuShown(MenuEvent e){
//        String [] values = lstName.getSelection();
//        if(values.length < 1) {
//          menu.getItem(0).setEnabled(false);
//          return;
//        }
//        menu.getItem(2).setEnabled(values[0].equals("Login"));
//      }
//    });

    lstName.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent evt) {
        saveProperties();
      }
    });

    factory.setComposite(this);

    group = factory.createGroup("groupPropertiesValue", null, new FillLayout());
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 150;
    group.setLayoutData(gridData);
    factory.setComposite(group);
    txtValue = factory.createText(SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER);
    txtValue.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent arg0) {
        saveProperties();
      }
    });
    new TextPopupMenu(factory, txtValue, false);

    if(isSimple) return;
    txtValue.setDoubleClickEnabled(false);
    txtValue.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e) {
        new URLTemplateUtils.HandleAction().handle(txtValue);
      }
    });
  }

  void reset() {
    properties = null;
    txtValue.setText("");
  }

  private void loadProperty() {
    int idx = lstName.getSelectionIndex();
    if(idx < 0 || properties == null) return;
    String name = lstName.getItem(idx).trim();
    if(properties.containsKey(name)) {
      txtValue.setText(properties.get(name).toString());
    } else {
      txtValue.setText("");
    }
  }

  private void removeProperties() {
    properties.clear();
    txtValue.setText("");
  }

  private int removeProperty() {
    int idx = lstName.getSelectionIndex();
    if(idx < 0 || properties == null) return -1;
    String name = lstName.getItem(idx).trim();
    if(properties.containsKey(name)) properties.remove(name);
    txtValue.setText("");
    return idx;
  }

  void saveProperties() {
    int idx = lstName.getSelectionIndex();
    if(idx < 0 || properties == null) return ;
    String name = lstName.getItem(idx).trim();
    String value = txtValue.getText().trim();
    if(value.isEmpty()) {
      if(properties.containsKey(name)) properties.remove(name);
    } else {
      if(name.equalsIgnoreCase("UserAgent")) {
        if(value.equals("google") || value.equals("googlebot")) {
          value = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
        } else if(value.equals("yahoo") || value.equals("yahooslurp")) {
          value = "Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)";
        } if(value.equals("firefox") || value.equals("mozilla")) {
          value = HttpClientFactory.USER_AGENT_VALUE;
        }
      } else if(name.equalsIgnoreCase("LinkGenerator")
          || name.equalsIgnoreCase("JSOnclickPattern")
          || name.equalsIgnoreCase("HomepageTemplate")) {
        value = new LinkGeneratorBuilder().buildLinkGenerator(properties, value);
      }
      if(value.indexOf('>') > -1 
          && value.indexOf('<') > -1) {
        RefsEncoder encoder = new RefsEncoder();
        value = new String(encoder.encode(value.toCharArray()));
      }
      properties.put(name, value);
    }
  }

  private String[] loadNames() {
    File folder = ClientConnector2.getCacheFolder("sources/type"); 
    File file = new File(folder, "property_name");
    if(!file.exists()) {
      try {
        RWData.getInstance().save(file, new SimpleSourceClientHandler().loadPropertiesType());
      } catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
        try {
          RWData.getInstance().copy(UtilFile.getFile("sources/type", "property_name"), file);
        } catch (Exception e2) {
          ClientLog.getInstance().setException(null, e2);
        }
      }
    }
    
    try {
      String value = new String(RWData.getInstance().load(file), Application.CHARSET);
      return value.split("\n");
    }catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return new String[]{};
    }
  }

  public void setProperties(Properties properties) { 
    this.properties = properties;  
    loadProperty();
  }

  void clearWorkingProperties() {
    if(properties == null) return;
    Iterator<Object> iterator = properties.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next().toString();
      if(key.endsWith("working")) iterator.remove();
    }
  }

}
