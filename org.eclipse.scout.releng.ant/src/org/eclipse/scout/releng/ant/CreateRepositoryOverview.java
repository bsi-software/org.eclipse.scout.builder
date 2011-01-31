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
package org.eclipse.scout.releng.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.util.DropInZip;
import org.eclipse.scout.releng.ant.util.DropInZipFilter;
import org.eclipse.scout.releng.ant.util.ReverseStringComparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <h4>CreateRepositoryOverview</h4>
 * 
 * @author aho
 * @since 1.1.0 (31.01.2011)
 */
public class CreateRepositoryOverview extends Task {

  private String rootUrl;
  private File repositoryDir;
  private File overviewFile;

  private SimpleDateFormat dateFormat ;
  
  public CreateRepositoryOverview(){
    dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
  }
  /**
   * @param rootUrl the rootUrl to set
   */
  public void setRootUrl(String rootUrl) {
    this.rootUrl = rootUrl.replaceAll("/$", "");
  }

  /**
   * @return the rootUrl
   */
  public String getRootUrl() {
    return rootUrl;
  }

  /**
   * @param repositoryDir
   *          the repositoryDir to set
   */
  public void setRepositoryDir(File repositoryDir) {
    this.repositoryDir = repositoryDir;
  }

  /**
   * @return the repositoryDir
   */
  public File getRepositoryDir() {
    return repositoryDir;
  }

  /**
   * @param overviewFile
   *          the overviewFile to set
   */
  public void setOverviewFile(File overviewFile) {
    this.overviewFile = overviewFile;
  }

  /**
   * @return the overviewFile
   */
  public File getOverviewFile() {
    return overviewFile;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element rootElement = document.createElement("repository");
      rootElement.setAttribute("name", "Scout");
      document.appendChild(rootElement);
      findReleases(document, rootElement);
      // write xml
      writeXmlFile(document);
    }
    catch (Exception e) {
      throw new BuildException("could not create xml document.", e);
    }
  }

  private void findReleases(Document doc, Element repository) {
    // nightly
    File nightlyDir = new File(getRepositoryDir().getAbsolutePath()+File.separator+"nightly");
    if(nightlyDir.exists()){
      addRelease(nightlyDir, doc, repository);
    }
    P_ReleaseFilter releseFilter = new P_ReleaseFilter();
    getRepositoryDir().list(releseFilter);
    for (File releaseDir : releseFilter.getReleses()) {
      addRelease(releaseDir, doc, repository);
    }
  }
  
  private void addRelease(File releaseDir, Document doc,Element repository){
    Element releaseElement = doc.createElement("release");
    File updateDir = new File(releaseDir.getAbsolutePath()+File.separator+"update");
    if(updateDir.exists()){
      URI uri = getRepositoryDir().getAbsoluteFile().toURI().relativize(updateDir.getAbsoluteFile().toURI());
      releaseElement.setAttribute("url", rootUrl+"/"+uri.toString());
      releaseElement.setAttribute("version", releaseDir.getName());
    }
    File zipDir = new File(releaseDir.getAbsolutePath()+File.separator+"zip");
    if(zipDir.exists()){
      findZips(zipDir, doc, releaseElement);
    }
    
    if(releaseElement.hasChildNodes() || releaseElement.hasAttributes()){
      repository.appendChild(releaseElement);
    }
  
  }

  private void findZips(File zipFileDir, Document doc, Element releaseElement) {
    String attVersion = releaseElement.getAttribute("version");
    DropInZipFilter filter = new DropInZipFilter();
    zipFileDir.list(filter);
    for(DropInZip zip : filter.getOrderedZipFiles()){
      if(attVersion == null || attVersion.length()==0){
        attVersion = zip.getVersionMajor()+"."+zip.getVersionMinor();
        releaseElement.setAttribute("version", attVersion);
      }
      Element zipElement = doc.createElement("zip");
      zipElement.setAttribute("date", dateFormat.format(zip.getBuildDate()));
      URI uri = getRepositoryDir().getAbsoluteFile().toURI().relativize(zip.getZipFile().getAbsoluteFile().toURI());
      zipElement.setAttribute("url", rootUrl+"/"+uri.toString());
      releaseElement.appendChild(zipElement);
    }
  }
  
  private void writeXmlFile(Document document) throws Exception {
    OutputStream fos = null;
    try {
      if(!getOverviewFile().exists()){
        getOverviewFile().getParentFile().mkdirs();
      }
      fos = new FileOutputStream(getOverviewFile());
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
  
  private void validate() throws BuildException {
    if (getRepositoryDir() == null) {
      throw new BuildException("parameter repositoryDir (Folder) must be specified.");
    }
    if(getRootUrl() == null){
      throw new BuildException("parameter rootUrl (URL) must be specified.");
    }
  }

  private class P_ReleaseFilter implements FilenameFilter {
    TreeMap<String, File> releses = new TreeMap<String, File>(new ReverseStringComparator());

    @Override
    public boolean accept(File dir, String name) {
      if (name.matches("[0-9]{1,2}\\.[0-9]{1,2}")) {
        releses.put(name, new File(dir.getAbsoluteFile() + File.separator + name));
      }
      return false;
    }

    /**
     * @return the releses
     */
    public File[] getReleses() {
      return releses.values().toArray(new File[releses.size()]);
    }
  }

  

}

