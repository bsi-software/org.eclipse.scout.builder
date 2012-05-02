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
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** <h4> TruncateCompositeTask </h4>
 *
 * @author jgu
 * @since 1.1.0 (02.05.2012)
 *
 */
public class TruncateCompositeTask extends Task{

  private File srcFile;
  private File destFile;
  private int size;

  public File getSrcFile() {
    return srcFile;
  }

  public void setSrcFile(File srcFile) {
    this.srcFile = srcFile;
  }

  public File getDestFile() {
    return destFile;
  }

  public void setDestFile(File destFile) {
    this.destFile = destFile;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }


  @Override
  public void execute() throws BuildException {
    validate();
    try {
      Document doc = readDocument();
      Node children = doc.getElementsByTagName("children").item(0);
      List<Node> childNodes = getChildNodesAsList(children);
      int deleteNum = childNodes.size()- getSize();
      for(int i=0;i<deleteNum;i++){
        children.removeChild(childNodes.get(i));
      }
      children.getAttributes().getNamedItem("size").setNodeValue(Integer.toString(getSize()));
      writeDocument(doc);
    }
    catch (Exception e) {
      throw new BuildException("Could not truncate composite repository",e);
    }
  }


  private List<Node> getChildNodesAsList(Node children) {
    List<Node> nodes = new ArrayList<Node>();
    NodeList childNodes = children.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      if (node.getNodeName().equals("child")) {
        nodes.add(node);
      }
    }
    return nodes;
  }


  protected Document readDocument() throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    return docBuilder.parse(getSrcFile());
  }

  protected void writeDocument(Document doc) throws IOException, TransformerException {
    //create out file
    File out = getDestFile();
    if (!out.exists()) {
      out.getParentFile().mkdirs();
      out.createNewFile();
    }

    // write the content into xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(getDestFile());
    transformer.transform(source, result);
  }

  protected void validate() throws BuildException {
    if (getSrcFile() == null) {
      throw new BuildException("srcFile must be set.");
    }
    if (getDestFile() == null) {
      throw new BuildException("destFile must be set.");
    }
  }



}
