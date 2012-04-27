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
package org.eclipse.scout.releng.ant.category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <h4>CategoryTask</h4>
 *
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */
public class CategoryTask extends Task {

  private File categoryFile;
  private File featuresFolder;
  private String label = "Scout Application Development";
  private String name = "Scout";


  /**
   * @param categoryFile
   *          the categoryFile to set
   */
  public void setCategoryFile(File categoryFile) {
    this.categoryFile = categoryFile;
  }

  /**
   * @return the categoryFile
   */
  public File getCategoryFile() {
    return categoryFile;
  }

  /**
   * @param featuresFolder
   *          the featuresFolder to set
   */
  public void setFeaturesFolder(File featuresFolder) {
    this.featuresFolder = featuresFolder;
  }

  /**
   * @return the featuresFolder
   */
  public File getFeaturesFolder() {
    return featuresFolder;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element rootElement = document.createElement("site");
      document.appendChild(rootElement);
      for(File feature : getFeaturesFolder().listFiles()){
        if(feature.getName().endsWith(".jar")){
          addFeature(document, rootElement, feature);
        }
      }
      Element categoryDef = document.createElement("category-def");
      categoryDef.setAttribute("name", getName());
      categoryDef.setAttribute("label", getLabel());
      rootElement.appendChild(categoryDef);

      // write document
      writeXmlFile(document);
    }
    catch (Exception e) {
      throw new BuildException("could not create category file.", e);
    }
  }

  private void validate() throws BuildException {
    if(getCategoryFile() == null){
      throw new BuildException("categoryFile must be set.");
    }
    if(getFeaturesFolder() == null){
      throw new BuildException("featureFolder must be set");
    }
  }

  private void addFeature(Document doc, Element site, File feature) throws Exception{
    Element featureElement = doc.createElement("feature");
    featureElement.setAttribute("url", "features/"+feature.getName());
    fillFeatureIdVersion(feature, featureElement);
    site.appendChild(featureElement);
    // category

    Element categoryElement = doc.createElement("category");
    categoryElement.setAttribute("name", getName());
    featureElement.appendChild(categoryElement);
  }


  private void fillFeatureIdVersion(File feature, Element featureElement) throws Exception{
    JarFile jar = null;
    try{
      jar = new JarFile(feature);
      JarEntry featureXml = jar.getJarEntry("feature.xml");
      InputStream is = jar.getInputStream(featureXml);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setExpandEntityReferences(false);
      Document doc = factory.newDocumentBuilder().parse(is);
      NodeList nodeList = doc.getElementsByTagName("feature");
      if(nodeList.getLength() ==1){
        Element originalFeatureElement = (Element)nodeList.item(0);
        featureElement.setAttribute("id", originalFeatureElement.getAttribute("id"));
        featureElement.setAttribute("version", originalFeatureElement.getAttribute("version"));
      }
    }finally{
      if(jar != null){
        jar.close();
      }
    }
  }
  


  private void writeXmlFile(Document document) throws Exception {
    OutputStream fos = null;
    try {
      if(!getCategoryFile().exists()){
        getCategoryFile().getParentFile().mkdirs();
      }
      fos = new FileOutputStream(getCategoryFile());
//      fos = System.out;
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(fos);
      transformer.transform(source, result);
    }
    finally {
      fos.close();
    }
  }
}
