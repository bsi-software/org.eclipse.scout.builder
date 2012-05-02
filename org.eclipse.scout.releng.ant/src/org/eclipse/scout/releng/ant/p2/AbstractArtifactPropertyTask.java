/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.releng.ant.p2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** <h4> AbstractArtifactPropertyTask </h4>
 *
 * @author jgu
 * @since 1.1.0 (01.05.2012)
 *
 */
public abstract class AbstractArtifactPropertyTask extends Task{

  private File artifactsFile;
  private File outputAtrifactsFile;

  public File getArtifactsFile() {
    return artifactsFile;
  }


  public void setArtifactsFile(File artifactsFile) {
    this.artifactsFile = artifactsFile;
  }


  public File getOutputAtrifactsFile() {
    return outputAtrifactsFile;
  }


  public void setOutputAtrifactsFile(File outputAtrifactsFile) {
    this.outputAtrifactsFile = outputAtrifactsFile;
  }

  protected Node getRepoProperties(Document doc){
    Node repo = doc.getElementsByTagName("repository").item(0);
    NodeList childNodes = repo.getChildNodes();
    for(int i=0;i<childNodes.getLength();i++){
      Node repoChild = childNodes.item(i);
      if(repoChild.getNodeName().equals("properties")){
        return repoChild;
      }
    }
    return null;
  }

  protected void increaseSize(Node repoProperties) throws BuildException {
    NamedNodeMap attributes = repoProperties.getAttributes();
    Node namedItem = attributes.getNamedItem("size");
    if (namedItem != null) {
      String nodeValue = namedItem.getNodeValue();
      namedItem.setNodeValue(String.valueOf(Integer.valueOf(nodeValue) + 1));
    }
    else {
      throw new BuildException("size attribute not found");
    }
  }

  protected void validate() throws BuildException {
    if(getArtifactsFile() == null){
      throw new BuildException("Artifacts File must be set.");
    }
    if(!getArtifactsFile().exists()){
      throw new BuildException("Artifacts File does not exist. " + getArtifactsFile().getAbsolutePath());
    }
    if(getOutputAtrifactsFile() == null){
      throw new BuildException("OutputAtrifactsFile must be set.");
    }
  }

  protected Document readArtifactsDocument() throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    return docBuilder.parse(getArtifactsFile());
  }

  protected void writeArtifactsDocument(Document doc) throws IOException, TransformerException {
    //create out file
    File out = getOutputAtrifactsFile();
    if (!out.exists()) {
      out.getParentFile().mkdirs();
      out.createNewFile();
    }

    // write the content into xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(getOutputAtrifactsFile());
    transformer.transform(source, result);
  }

}
