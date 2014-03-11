package org.vietspider.gui.creator.test;

import org.vietspider.html.HTMLDocument;
import org.vietspider.model.Source;

public interface TestPlugin {
  
	public Object process(Source source, HTMLDocument doc) ;
	
}
