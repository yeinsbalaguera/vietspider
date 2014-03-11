/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 21, 2007  
 */
public class ChangeRegionNameDialog {
  
  private Shell shell;
  private Text txtName;
  
  private Combo cboRegionName;

  private String group;
  
  private String oldName;

  public ChangeRegionNameDialog(Shell parent, String group, Combo cboRegionName) {
    this.group = group;
    this.cboRegionName = cboRegionName;
    this.oldName = cboRegionName.getText();

    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(3, false));

    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", getClass().getName());
    factory.createLabel("lblName");  
    txtName = factory.createText();
    txtName.setFont(UIDATA.FONT_10);
    GridData gridData = new GridData();     
    gridData.widthHint = 200;
    txtName.setLayoutData(gridData);

    factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) { 
        shell.setVisible(false);
        changeName();
      }      
    }, factory.loadImage("block.gif"));

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.pack();
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }

  public void changeName() {
    Worker excutor = new Worker() {
      
      private String message = "";
      
      private String newName;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        newName = txtName.getText();
      }

      public void execute() {
        if(newName == null || newName.trim().isEmpty()) return;
        try {
          SourcesClientHandler client = new SourcesClientHandler(group);
          String [] categories = client.loadCategories();
          for(String category : categories) {
            String [] names = client.loadSources(category);
            for(String name : names) {
              Source source = client.loadSource(category, name);
              if(source == null) continue;
              Region[] detachPaths = source.getProcessRegion();
              if(detachPaths == null) continue;
              for(Region path : detachPaths) {
                if(path.getName().equals(oldName)) path.setName(newName);
              }
              client.saveSource(source);
            } 
          }

          org.vietspider.model.Groups groups = client.loadGroups();
          for(Group ele : groups.getGroups()) {
            if(ele.getType().equals(group)) {
              List<Region> regions = ele.getProcessRegions();
              for(int i = 0; i < regions.size(); i++) {
                if(regions.get(i).getName().equals(oldName)) {
                  regions.get(i).setName(newName);
                }
              }
            }
          }
          client.saveGroups(groups);
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        int idx = cboRegionName.getSelectionIndex();
        cboRegionName.add(newName, idx);
        cboRegionName.remove(cboRegionName.getText());
        cboRegionName.select(idx);
        shell.dispose();
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }

  public Shell getShell() { return shell; }

}
