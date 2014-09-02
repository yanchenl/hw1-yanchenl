/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.examples;

import java.util.*;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.examples.types.RoomNumber;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class RoomNumberAnnotator extends JCasAnnotator_ImplBase {

  public void process(JCas aJCas) {
    // get document text
    String docText = aJCas.getDocumentText();

    PosTagNamedEntityRecognizer recognizer = null;
    RoomNumber gene = new RoomNumber(aJCas);
    try {
      recognizer = new PosTagNamedEntityRecognizer();
    } catch (ResourceInitializationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Map<Integer, Integer> map = recognizer.getGeneSpans(docText);
    Iterator iterator = map.entrySet().iterator(); 
    while (iterator.hasNext()) {
      // found one - create annotation
//      Map.Entry entry = (Map.Entry) iter.next(); 
      Map.Entry entry = (Entry) iterator.next();
      Integer key = (Integer) entry.getKey();
      Integer value = (Integer) entry.getValue();
      gene.setBegin(key);
      gene.setEnd(value);
      gene.addToIndexes();
    }
  }
}
