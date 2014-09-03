/*
 * Licensed to the Apache Software Foundation (ASF)
 * This file is revised from uimaj-example provided by apache uima
 */

package yanchenl;

import java.util.*;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import yanchenl.types.Sentence;

public class GeneNameAnnotator extends JCasAnnotator_ImplBase {

  public void process(JCas aJCas) {
    // get document text
    String docText = aJCas.getDocumentText();
    // split the document text for processing
    String[] splitText = docText.split("\n");
    int fileLength = splitText.length;

    PosTagNamedEntityRecognizer recognizer = null;
    Sentence annotation = new Sentence(aJCas);
    try {
      recognizer = new PosTagNamedEntityRecognizer();
    } catch (ResourceInitializationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    for (int n = 0; n < fileLength; n++) {
      // for each line of the document, create map to store the key and value
      Map<Integer, Integer> map = recognizer.getGeneSpans(splitText[n].substring(15));
      Iterator iterator = map.entrySet().iterator();
      while (iterator.hasNext()) {
        annotation = new Sentence(aJCas);
        Map.Entry entry = (Entry) iterator.next();
        // get the key
        Integer key = (Integer) entry.getKey();
        // get the value
        Integer value = (Integer) entry.getValue();
        // set the begin position
        annotation.setBegin(key);
        // set the end position
        annotation.setEnd(value);
        // id represents the sentence identifier
        annotation.setId(splitText[n].substring(0, 14));
        // text represents the gene and gene products' name
        annotation.setText(splitText[n].substring(15).substring(key, value));
        annotation.addToIndexes();
      }
    }
  }
}
