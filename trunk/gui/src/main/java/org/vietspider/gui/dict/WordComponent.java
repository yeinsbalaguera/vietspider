/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.dict;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.locale.Html2Text;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 21, 2009  
 */
public class WordComponent  extends Composite {
  
  private Text text;
  
  public WordComponent(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    
    text = new Text(this, SWT.V_SCROLL | SWT.MULTI | SWT.WRAP | SWT.BORDER);
    text.addKeyListener(new KeyAdapter() {

      public void keyReleased(KeyEvent e) {
        if ((e.stateMask & SWT.CTRL) != 0) {
          char key = (char)e.keyCode;
          if(key == 's' || key == 'S') {
            String content = getWord();
            MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
            msg.setMessage("Do you want to save \'"+ content +"\' to dictionary?");
            if(msg.open() != SWT.YES) return;  
            saveWord(content);
          }
          if(key == 'a' || key == 'A') {
            String [] words = getWords();
            MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
            msg.setMessage("Do you want to save all to dictionary?");
            if(msg.open() != SWT.YES) return;
            for(int i = 0; i < words.length; i++) {
              words[i] = words[i].toLowerCase().trim();
              if(words[i].isEmpty()) continue;
              saveWord(words[i]);
            }
          }
          if(key == 'r' || key == 'R') removeWord();
          
        }
      }
    });
    text.setFont(UIDATA.FONT_10);
  }
  
  public void setHtml(final String html) {
    Worker excutor = new Worker() {

      private String content;
      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          RefsDecoder decoder = new RefsDecoder();
          List<NodeImpl> tokens = new HTMLParser2().createTokens(html.toCharArray());
          content = Html2Text.toText(tokens);
          content = new String(decoder.decode(content.toCharArray()));
          content = DataClientHandler.getInstance().analyze(content);
        } catch(Exception exp) {
          error = exp.toString();
        } 
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error));
          return;
        }

        text.setText(content);

      }
    };
    new ThreadExecutor(excutor, text).start();
  }
  
  public void saveWord(final String content) {
    Worker excutor = new Worker() {

      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          DataClientHandler.getInstance().saveWordToDict(content);
        } catch(Exception exp) {
          error = exp.toString();
        } 
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error));
          return;
        }
        
        Clipboard clipboard = new Clipboard(getDisplay());
        TextTransfer transfer = TextTransfer.getInstance();
        clipboard.setContents( 
            new Object[] { content +"; " }, 
            new Transfer[]{ transfer }
        );
        
        text.paste();
      }
    };
    new ThreadExecutor(excutor, text).start();
  }
  
  public void removeWord() {
    final String content = getWord();
    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    msg.setMessage("Do you want to remove \'"+ content +"\' to dictionary?");
    if(msg.open() != SWT.YES) return;  
    
    Worker excutor = new Worker() {

      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          DataClientHandler.getInstance().removeWordToDict(content);
        } catch(Exception exp) {
          error = exp.toString();
        } 
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error));
          return;
        }
      }
    };
    new ThreadExecutor(excutor, text).start();
  }
  
  private String getWord() {
    String value = text.getSelectionText();
    value = value.trim();
    String [] elements = value.split(";");
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < elements.length; i++) {
      if(builder.length() > 0) builder.append(' ');
      builder.append(elements[i].trim());
    }
    return builder.toString().toLowerCase();
  }
  
  private String[] getWords() {
    String value = text.getText();
    value = value.trim();
    return value.split("\n");
  }
  
}
