/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.CCombo;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.action.IHyperlinkListener;
import org.vietspider.ui.widget.images.ToolbarResource;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2006
 */
abstract public class UIToolbar extends Composite {

  protected CCombo cbo;
//  private HistoryLoader loader;

  protected ImageHyperlink butStop, butRefresh; 
  private ImageHyperlink butGo, butBack, butForward;

  protected Composite parent;

  private String lastPattern;

  private TabBrowser tabBrowser;
  private CreateMenuHandler createHandler;

  public UIToolbar(Composite _parent, TabBrowser tab) {
    super(_parent, SWT.NONE);

    this.parent = _parent;
    this.tabBrowser = tab;

    this.createHandler = new CreateMenuHandler(tabBrowser);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 2;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;    
    if(parent instanceof  BrowserWidget) {
      gridLayout.numColumns = 5;  
    } else {
      gridLayout.numColumns = 2;
    }
    setLayout(gridLayout);

    GridData gridData = new GridData();
    final ToolbarResource resources = ToolbarResource.getInstance();
    if(parent instanceof  BrowserWidget) {
      butBack = createIcon(this,
          resources.getImageBack(), resources.getTextBack(), new HyperlinkAdapter(){  
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          butBack.setImage(resources.getImageBack());
          butBack.redraw();
        }
        @SuppressWarnings("unused")
        public void linkExited(HyperlinkEvent e) {
          butBack.setImage(resources.getImageBack());
          butBack.redraw();
        }
        @SuppressWarnings("unused")
        public void linkEntered(HyperlinkEvent e) {
          butBack.setImage(resources.getImageBack());
          butBack.redraw();
        }
      });

      butBack.addMouseListener(new MouseAdapter() {
        @SuppressWarnings("unused")
        public void mouseUp(MouseEvent e) {
          if(parent instanceof BrowserWidget) {
            ((BrowserWidget)parent).backPage();  
          }
        }

        @SuppressWarnings("unused")
        public void mouseDown(MouseEvent e) {
          butBack.setImage(resources.getImageBack1());
          butBack.redraw();
        }
      });

      gridData = new GridData(); 
      butForward = createIcon(this, 
          resources.getImageForward(), resources.getTextForward(), new HyperlinkAdapter(){  
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          butForward.setImage(resources.getImageForward());
          butForward.redraw();
        }
        @SuppressWarnings("unused")
        public void linkExited(HyperlinkEvent e) {
          butForward.setImage(resources.getImageForward());
          butForward.redraw();
        }
        @SuppressWarnings("unused")
        public void linkEntered(HyperlinkEvent e) {
          butForward.setImage(resources.getImageForward());
          butForward.redraw();
        }
      });
      butForward.addMouseListener(new MouseAdapter() {
        @SuppressWarnings("unused")
        public void mouseUp(MouseEvent e) {
          if(parent instanceof BrowserWidget) {
            ((BrowserWidget)parent).forwardPage();
          }
        }

        @SuppressWarnings("unused")
        public void mouseDown(MouseEvent e) {
          butForward.setImage(resources.getImageForward1());
          butForward.redraw();
        }
      });
      butForward.setLayoutData(gridData);
    }

    cbo = new CCombo(this, SWT.BORDER | SWT.DROP_DOWN | SWT.V_SCROLL | SWT.H_SCROLL);   
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    //    gridData.verticalIndent = -31;
    //    gridData.heightHint = 20;
    cbo.setForeground(UIDATA.FCOLOR); 
    cbo.setFont(UIDATA.FONT_10);
    cbo.setBackground(UIDATA.ADDRESS_BCOLOR);
    if(UIDATA.isMacOS) {
      gridData.heightHint = 30;
      cbo.setFont(UIDATA.FONT_11);
    }
    cbo.setLayoutData(gridData);
    cbo.setVisibleItemCount(15);

    ApplicationFactory factory = new ApplicationFactory(this, "VietSpider", getClass().getName());

    Object menu;
    final Text text = cbo.text; 
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(text, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//      text.setMenu(new Menu(text.getShell(), SWT.POP_UP));
//    } else {
      menu = new Menu(text.getShell(), SWT.POP_UP);
      text.setMenu((Menu)menu);
//    }
    //    menu = new Menu(text.getTextComponent().getShell(), SWT.POP_UP);

    factory.createStyleMenuItem(menu, "menuPaste", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        text.paste();
      }
    });

    factory.createStyleMenuItem(menu, "menuCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getSelectionText() == null 
            || text.getSelectionText().isEmpty()) {
          text.selectAll();
        }
        text.copy();
      }
    });

    factory.createStyleMenuItem(menu, "menuClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        text.setText("");
      }
    });

    factory.createStyleMenuItem(menu, "menuCut", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getSelectionText() == null 
            || text.getSelectionText().isEmpty()) {
          text.selectAll();
        }
        text.cut(); 
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem(menu, "addHomepage", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getText().trim().isEmpty()) return;
        String [] links = new String[]{text.getText().trim()};
        createHandler.putHomepage(links);
      }
    });

    factory.createStyleMenuItem(menu, "putTemplateVisitLink", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getText().trim().isEmpty()) return;
        String [] links = new String[]{text.getText().trim()};
        createHandler.putTemplatePage(links, "templateVisitLink");
      }
    });

    factory.createStyleMenuItem(menu, "putSamplePage", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getText().trim().isEmpty()) return;
        String [] links = new String[]{text.getText().trim()};
        createHandler.putSamplePage(links);
      }
    });

    factory.createStyleMenuItem(menu, "putTemplateDataLink", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getText().trim().isEmpty()) return;
        String [] links = new String[]{text.getText().trim()};
        createHandler.putTemplatePage(links, "templateDataLink");
      }
    });

    factory.createStyleMenuItem(menu, "testSamplePage", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getText().trim().isEmpty()) return;
        String [] links = new String[]{text.getText().trim()};
        createHandler.testSamplePage(links);
      }
    });
    
    text.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode  == SWT.CR) browse();
//          String url = text.getText();
//          BrowserWidget browser = tabBrowser.getSelected();
//          if(browser == null) {
//            browser = tabBrowser.createItem();
//          }
//          browser.setUrl(url);
//        }
      }
      
    });


//    loader = new HistoryLoader(this, cbo);

    cbo.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent evt) {
        if(Character.isIdentifierIgnorable(evt.character)) return;
        if(cbo.getText().trim().equalsIgnoreCase(lastPattern)) return;
        lastPattern = cbo.getText().trim(); 
       
//        new ThreadExecutor(loader, cbo).start();
      }

      @SuppressWarnings("unused")
      public void keyReleased(KeyEvent evt) {
      }
    });

    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    DropTarget target = new DropTarget(text, operations);
    target.setTransfer(new Transfer[] {URLTransfer.getInstance()});
    target.addDropListener (new DropTargetAdapter() {

      public void dragEnter(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE)
          e.detail = DND.DROP_LINK;
      }
      public void dragOperationChanged(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE) e.detail = DND.DROP_LINK;
      }

      public void drop(DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        text.setText(((String) event.data));
        browse();
      }
    });

    factory.setComposite(cbo);
    butGo = factory.createIcon("go", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        if(parent instanceof BrowserWidget) {
          ((BrowserWidget)parent).setUrl(cbo.getText());
        } else {
          String url = cbo.getText();
          BrowserWidget browser = tabBrowser.createItem();
          browser.setUrl(url);
        }
      }
    });
    cbo.setIcon(butGo);

    if(parent instanceof  BrowserWidget) {
      butRefresh = createIcon(this, 
          resources.getImageRefesh(), resources.getTextRefesh(), new HyperlinkAdapter(){    
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          butRefresh.setImage(resources.getImageRefesh());
          butRefresh.redraw();
        }
        @SuppressWarnings("unused")
        public void linkExited(HyperlinkEvent e) {
          butRefresh.setImage(resources.getImageRefesh());
          butRefresh.redraw();
        }
        @SuppressWarnings("unused")
        public void linkEntered(HyperlinkEvent e) {
          butRefresh.setImage(resources.getImageRefesh());
          butRefresh.redraw();
        }
      });
      butRefresh.addMouseListener(new MouseAdapter() {
        @SuppressWarnings("unused")
        public void mouseUp(MouseEvent e) {
          if(parent instanceof BrowserWidget) {
            ((BrowserWidget)parent).setUrl(cbo.getText());
          } else {
            String url = cbo.getText();
            BrowserWidget browser = tabBrowser.createItem();
            browser.setUrl(url);
          }
        }

        @SuppressWarnings("unused")
        public void mouseDown(MouseEvent e) {
          butRefresh.setImage(resources.getImageRefesh1());
          butRefresh.redraw();
        }
      });
      gridData = new GridData(); 
      butRefresh.setLayoutData(gridData);  

      butStop = createIcon(this, 
          resources.getImageStop(), resources.getTextStop(), new HyperlinkAdapter(){ 
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          butStop.setImage(resources.getImageStop());
          butStop.redraw();
        }
        @SuppressWarnings("unused")
        public void linkExited(HyperlinkEvent e) {
          butStop.setImage(resources.getImageStop());
          butStop.redraw();
        }
        @SuppressWarnings("unused")
        public void linkEntered(HyperlinkEvent e) {
          butStop.setImage(resources.getImageStop());
          butStop.redraw();
        }
      }); 
      butStop.addMouseListener(new MouseAdapter() {
        @SuppressWarnings("unused")
        public void mouseUp(MouseEvent e) {
          if(parent instanceof BrowserWidget) {
            BrowserWidget browser = ((BrowserWidget)parent);
            browser.stopPage();
            browser.getStatusBar().setProgressValue("", 0);
          }
        }

        @SuppressWarnings("unused")
        public void mouseDown(MouseEvent e) {
          butStop.setImage(resources.getImageStop1());
          butStop.redraw();
        }
      });
      gridData = new GridData(); 
      butStop.setLayoutData(gridData); 
    }
  }

  private ImageHyperlink createIcon(Composite com, Image img, String tip, IHyperlinkListener listener) {    
    ImageHyperlink icon = new ImageHyperlink(com , SWT.CENTER);
    icon.setBackground(com.getBackground());
    icon.setImage(img);
    if(listener != null) icon.addHyperlinkListener(listener);
    icon.setToolTipText(tip);
    return icon;    
  } 

  public void browse() {
    if(parent instanceof BrowserWidget) {
      ((BrowserWidget)parent).setUrl(cbo.getText());
    } else {
      String url = cbo.getText();
      BrowserWidget browser = tabBrowser.createItem();
      browser.setUrl(url);
    }
//    loader.dropped();
  }

//  public HistoryLoader getLoader() {  return loader; }

}