/*
 * Licensed to the Apache Software Foundation (ASF)
 * This file is revised from uimaj-example provided by apache uima
 */

package yanchenl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import yanchenl.types.Sentence;

/**
 * A simple CAS consumer that writes the CAS to XMI format.
 * <p>
 * This CAS Consumer takes one parameter:
 * <ul>
 * <li><code>OutputDirectory</code> - path to directory into which output files will be written</li>
 * </ul>
 */
public class GeneCasConsumer extends CasConsumer_ImplBase {
  /**
   * Name of configuration parameter that must be set to the path of a directory into which the
   * output files will be written.
   */
  public static final String PARAM_OUTPUTDIR = "OutputDirectory";

  private File mOutputDir;

  private int mDocNum;

  public void initialize() throws ResourceInitializationException {
    mDocNum = 0;
    mOutputDir = new File((String) getConfigParameterValue(PARAM_OUTPUTDIR));
    if (!mOutputDir.exists()) {
      mOutputDir.mkdirs();
    }
  }

  /**
   * Processes the CAS which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS is converted to XMI and written into the output file .
   * 
   * @param aCAS
   *          a CAS which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
   */
  public void processCas(CAS aCAS) throws ResourceProcessException {
    String modelFileName = null;

    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    // retreive the filename of the input file from the CAS
    FSIterator it = jcas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
    File outFile = null;
    if (it.hasNext()) {
      SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
      File inFile;
      try {
        inFile = new File(new URL(fileLoc.getUri()).getPath());
        String outFileName = inFile.getName();
        if (fileLoc.getOffsetInSource() > 0) {
          outFileName += ("_" + fileLoc.getOffsetInSource());
        }
        // outFileName = outFileName.substring(0,outFileName.lastIndexOf(".")); //delete suffix
        outFileName = "hw1-yanchenl.out";
        outFile = new File(mOutputDir, outFileName);
        modelFileName = mOutputDir.getAbsolutePath() + "/" + inFile.getName() + ".ecore";
      } catch (MalformedURLException e1) {
        // invalid URL, use default processing below
      }
    }
    if (outFile == null) {
      outFile = new File(mOutputDir, "doc" + mDocNum++ + ".out");
    }

    FSIterator<Annotation> fs = jcas.getAnnotationIndex(Sentence.type).iterator();
    String mytext = null;

    while (fs.hasNext()) {
      Annotation annotation = fs.next();
      int begin = annotation.getBegin();
      int end = annotation.getEnd();
      String id = ((Sentence) annotation).getId();
      String text = ((Sentence) annotation).getText();
      mytext = id + "|" + begin + " " + end + "|" + text + "\n";
      try {
        writeFile(jcas.getCas(), outFile, modelFileName, mytext);
      } catch (IOException e) {
        throw new ResourceProcessException(e);
      } catch (SAXException e) {
        throw new ResourceProcessException(e);
      }
    }
  }

  /**
   * Write a CAS to a file
   * 
   * @param name
   *          output file
   * @throws SAXException
   * @throws Exception
   * 
   * @throws ResourceProcessException
   */
  private void writeFile(CAS aCas, File name, String modelFileName, String mytext)
          throws IOException, SAXException {
    FileOutputStream out = null;

    try {
      out = new FileOutputStream(name, true);
      out.write(mytext.getBytes());
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
}
