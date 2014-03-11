/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.wizard;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Jan 6, 2012  
 */
import java.awt.Desktop;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.UIDATA;

/*
 * Open an OLE Windows Media Player.
 * 
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.3
 */
public class WizardHelp {
  
  private OleClientSite clientSite;

  public WizardHelp(Shell parent, 
      final String youtube, 
      final String url) {
    final Shell shell = new Shell(parent, SWT.MAX);
    shell.addShellListener(new ShellAdapter(){       
      public void shellClosed(ShellEvent e){
        e.doit = false; 
      }
    });
    shell.setText("VietSpider - Guide");
    
    GridLayout gridLayout = new GridLayout(1, true);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 0;
    shell.setLayout(gridLayout);
    
    Hyperlink youtubeLink = new Hyperlink(shell, SWT.NONE);
    youtubeLink.setText(youtube);
    youtubeLink.setUnderlined(true);
    youtubeLink.addHyperlinkListener(new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        try {
          Desktop.getDesktop().browse(new URI(youtube));
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }
    });
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    youtubeLink.setLayoutData(gridData);
    youtubeLink.setFont(UIDATA.FONT_9);
    
    Hyperlink videoLink = new Hyperlink(shell, SWT.NONE);
    videoLink.setText(url);
    videoLink.setUnderlined(true);
    videoLink.addHyperlinkListener(new HyperlinkAdapter() {
      @SuppressWarnings("unused")
       public void linkActivated(HyperlinkEvent evt) {
        try {
          Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }
    });
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    videoLink.setLayoutData(gridData);
    videoLink.setFont(UIDATA.FONT_9);
    
    try {
      OleFrame frame = new OleFrame(shell, SWT.NONE);
      clientSite = new OleClientSite(frame, SWT.NONE, "WMPlayer.OCX");
      clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
      
      gridData = new GridData(GridData.FILL_BOTH);
      frame.setLayoutData(gridData);
    } catch (SWTError e) {
    }
    
    Button butClose = new Button(shell, SWT.PUSH);
    butClose.setText("Close");
    butClose.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        shell.close();
        shell.dispose();
      }
      
    });
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    butClose.setLayoutData(gridData);
    
    shell.setSize(parent.getSize());
    shell.setLocation(parent.getLocation());
    shell.open();
    
    
    if(clientSite == null)  return;
  
    OleAutomation player = new OleAutomation(clientSite);
    int playURL[] = player.getIDsOfNames(new String[] { "URL" });
    if (playURL != null) {
      Variant theFile = new Variant(url);
      player.setProperty(playURL[0], theFile);
    }
    player.dispose();
  }

}