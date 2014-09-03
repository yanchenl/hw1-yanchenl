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
      Map<Integer, Integer> map = recognizer.getGeneSpans(splitText[n].substring(15));
      Iterator iterator = map.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Entry) iterator.next();
        Integer key = (Integer) entry.getKey();
        Integer value = (Integer) entry.getValue();
        annotation.setBegin(key);
        annotation.setEnd(value);
        annotation.setId(splitText[n].substring(0, 14));
        annotation.setText(splitText[n].substring(15));
        annotation.addToIndexes();
      }
    }
  }
}
