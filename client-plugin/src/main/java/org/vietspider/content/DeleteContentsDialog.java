/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.content;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 4, 2011
 */
public class DeleteContentsDialog {
  
  private Shell shell;
  private Button [] butTitles;
  private String [] ids;
  
  private Browser browser;
  private String domain;
  
  public DeleteContentsDialog(Browser _browser,
                              String domain,
                              String[] _ids, String[] titles) {
    this.domain = domain;
    this.browser = _browser;
    this.ids = _ids;
    shell = new Shell(browser.getShell(), SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ClientRM clientRM = DeleteDomainPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(1, false));

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        new ShellSetter(DeleteContentsDialog.class, shell);
        shell.dispose();
      }
    });

    factory.setComposite(shell);

    RefsDecoder decoder = new RefsDecoder();

    butTitles = new Button[titles.length];
//    selectors = new DeleteSingleArticleSelector[10];
    for (int i = 0; i < butTitles.length; i++) {
      titles[i] = new String(decoder.decode(titles[i].toCharArray()));
      butTitles[i] = new Button(shell, SWT.CHECK);
      butTitles[i].setSelection(true);
      butTitles[i].setToolTipText(clientRM.getLabel("itemDeleteTooltip"));
      butTitles[i].setText(titles[i]);
      butTitles[i].setLayoutData(new GridData());
    }

    Composite bottom = new Composite(shell, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    // gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottom);
    
    factory.createButton("butDeletDomain", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        deleteDomain();
      }
    });

    SelectionAdapter syncListener = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < ids.length; i++) {
          if(!butTitles[i].getSelection()) continue;
          if(builder.length() > 0) builder.append('\n');
          builder.append(ids[i]);
        }
        DeleteContentPlugin.delete(browser, builder.toString());
        shell.dispose();
      }
    };

    factory.createButton("butOk", syncListener);

    factory.createButton("butClose", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(DeleteContentsDialog.class, shell);
        shell.dispose();
      }
    });

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300) / 2;
    shell.setImage(browser.getShell().getImage());
    new ShellGetter(DeleteContentsDialog.class, shell, 550, 350, x, y);
    shell.open();
  }
  
  private void deleteDomain() {
    final String elements [] = domain.split("/");
    if(elements.length < 1) return;

    Worker excutor = new Worker() {

      private String error = null;
      final StringBuilder builder = new StringBuilder();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        builder.append(elements[0]);
        if(elements.length > 1) {
          if(elements[1].indexOf('.') > -1) {
            String [] categories = elements[1].split("\\.");
            builder.append('|').append(categories[0]).append('|').append(categories[1]);
          } else {
            builder.append('|').append(Application.GROUPS[0]).append('|').append(elements[1]);
          }
        }
        if(elements.length > 2) builder.append('|').append(elements[2]);
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          handler.send("delete.data.plugin", "delete.domain", builder.toString());
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        
        if(elements.length > 0) {
          try {
            Date instance = CalendarUtils.getParamFormat().parse(elements[0]);
            String date = CalendarUtils.getDateFormat().format(instance);
            DataClientHandler.getInstance().clearCached("track.id." + date);
          } catch (Exception e) {
            ClientLog.getInstance().setException(null, e);
          }
        }
        if(browser == null) return;
        
        browser.back();
        browser.refresh();

        new ShellSetter(DeleteContentsDialog.class, shell);
        shell.dispose();
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }
  
  
}
