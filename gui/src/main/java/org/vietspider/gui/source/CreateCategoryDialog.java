/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.source;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.TextVerifier;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.action.LoadGroupCategorySource;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 21, 2007  
 */
public class CreateCategoryDialog {
  
  private Shell shell;
  private Text txtName;

  private SourcesHandler sourcesHandler;

  public CreateCategoryDialog(SourcesHandler sourcesHandler) {
    this.sourcesHandler = sourcesHandler;

    shell = new Shell(sourcesHandler.getShell(), SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(3, false));

    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", getClass().getName());
    factory.createLabel("lblName");  
    txtName = factory.createText();
    txtName.setFont(UIDATA.FONT_10);
    GridData gridData = new GridData();     
    gridData.widthHint = 200;
    txtName.setLayoutData(gridData);
    txtName.addKeyListener(new KeyAdapter(){
      public void keyReleased(KeyEvent event) {
        if(event.keyCode == SWT.CR) createCategory();
      }
    });

    factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) { 
        createCategory();
      }      
    }, factory.loadImage("block.gif"));

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    shell.setImage(sourcesHandler.getShell().getImage());
    shell.setLocation(x, y);
    shell.pack();
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }

  public void createCategory() {
    Worker excutor = new Worker() {
      
      private String message = "";
      
      private String category;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        category = txtName.getText().trim();
      }

      public void execute() {
        ClientRM resource = new ClientRM("Creator");
        if(category.isEmpty()) {
          message = resource.getLabel("CreateCategoryDialog.msgErrorEmptyName");
          return ;
        }
        
        if(!isName(category) ) {
          message = resource.getLabel("CreateCategoryDialog.msgErrorName");
          return ;
        }
        
        try {
          String group  = sourcesHandler.getGroup();
          SourcesClientHandler handler = new SourcesClientHandler(group);
          String [] categories = handler.loadCategories();
          for(String ele : categories) {
            if(ele.trim().equalsIgnoreCase(category)) {
              message = resource.getLabel("CreateCategoryDialog.msgErrorExistsName");
              return;
            }
          }
          File file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + group);
          file.delete();
          handler.createCategory(category);
        } catch(Exception e) {
          message = e.toString();
        }
      }
      
      protected boolean isName(String name){
        if(name.length() == 0) return false;
        TextVerifier checker  = new TextVerifier();
        String [] collection = {".","-",":","?","*","\\","/","|","\"","<",">"};
        return !checker.existIn( name, collection);
      } 

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        } else {
          new LoadGroupCategorySource(sourcesHandler, new Worker[0], sourcesHandler.getGroup(), null);
          shell.dispose();
        }
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }

}
