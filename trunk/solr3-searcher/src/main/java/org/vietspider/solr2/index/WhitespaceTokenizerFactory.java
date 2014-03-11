/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vietspider.solr2.index;

import java.io.Reader;

import org.apache.solr.analysis.BaseTokenizerFactory;
import org.vietspider.common.io.LogService;

/**
 * @version $Id: WhitespaceTokenizerFactory.java 591158 2007-11-01 22:37:42Z hossman $
 */
public class WhitespaceTokenizerFactory extends BaseTokenizerFactory {
  
  public WhitespaceTokenizer create(Reader input) {
    StringBuilder builder = new StringBuilder();
    int index = -1;
    char [] chars = new char[10000];
    try {
      while((index = input.read(chars)) != -1) {
        builder.append(chars, 0, index);
      }
//      System.out.println(builder);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
//    System.out.println(input.getClass());
////    String lang = null;
//    try {
//      Detector detector = DetectorFactory.create();
//      detector.append(input);
//      lang = detector.detect();
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//    }
//    System.out.println(" ===================> " + lang);
    return new WhitespaceTokenizer(input);
  }
}
