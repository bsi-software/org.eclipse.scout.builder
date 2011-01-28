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
package org.eclipse.scout.releng.ant.incubation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <h4>TestCreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */

public class TestIncubation extends AbstractTestCase {

  private String m_workingDir;

  public TestIncubation() {
    m_workingDir = getTestDataDir() + "/incubation";
  }

  @Before
  public void setUp() throws Exception {
    removeOutputDir();
    File inputDir = new File(m_workingDir + "/input");
    File outputDir = new File(m_workingDir + "/output");
    FileUtility.copy(inputDir, outputDir);
  }

  @After
  public void cleanUp() {
    removeOutputDir();
  }

  private void removeOutputDir() {
    File outputDir = new File(m_workingDir + "/output");
    if (outputDir.exists()) {
      FileUtility.deleteFile(outputDir);
    }
  }

  @Test
  public void testTask() {
    File inputDir = new File(m_workingDir + "/output");
    MarkIncubation task = new MarkIncubation();
    Project project2 = new Project();
    task.setProject(project2);
    FileSet set = new FileSet();
    set.setDir(inputDir);
    set.createInclude().setName("**/**.jar");
    task.addFileset(set);
    task.execute();

    File rtFeatureJar = new File(m_workingDir + "/output/rtFeature/features/org.eclipse.scout.rt.feature_3.5.5.201112280312_unpack.jar");
    try {
      expectFeatureProperties(rtFeatureJar);
    }
    catch (Exception e) {
      Assert.fail();
    }
    try {
      expectFeatureXml(new File(m_workingDir + "/output/sdkFeature/features/org.eclipse.scout.sdk.feature_3.5.5.201150280650_unpack.jar"));
    }
    catch (Exception e) {
      Assert.fail();
    }
    
    try {
      expectManifestMf(new File(m_workingDir + "/output/rtFeature/plugins/org.eclipse.scout.commons_3.5.5.201112280312.jar"));
    }
    catch (Exception e) {
      Assert.fail();
    }
    
    try {
      expectPluginProps(new File(m_workingDir + "/output/rtFeature/plugins/org.eclipse.scout.commons.source_3.5.5.201112280312.jar"));
    }
    catch (Exception e) {
      Assert.fail();
    }
    
  }

  @SuppressWarnings("unused")
  private void expectFeatureXml(File feature) throws Exception {
    Assert.assertTrue(feature.exists());
    JarFile jarFile = new JarFile(feature);
    try {
      JarEntry featureXml = jarFile.getJarEntry("feature.xml");
      if (featureXml != null) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        Document doc = factory.newDocumentBuilder().parse(jarFile.getInputStream(featureXml));
        NodeList nodeList = doc.getElementsByTagName("feature");
        if (nodeList.getLength() == 1) {
          Element originalFeatureElement = (Element) nodeList.item(0);
          String label = originalFeatureElement.getAttribute("label");
          Assert.assertTrue(label.endsWith(MarkIncubation.INCUBATION_APPENDIX));
        }
      }
    }
    finally {
      jarFile.close();
    }
  }

  private void expectFeatureProperties(File feature) throws Exception {
    Assert.assertTrue(feature.exists());
    JarFile jarFile = new JarFile(feature);
    try {
      JarEntry propEntry = jarFile.getJarEntry("feature.properties");
      if (propEntry != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(propEntry)));
        try{
          String line = reader.readLine();
          while(line != null){
            if(line.startsWith("featureName")){
              Assert.assertTrue(line.endsWith(MarkIncubation.INCUBATION_APPENDIX));
              break;
            }
            line = reader.readLine();
          }
        }finally{
          reader.close();
        }
      }
    }finally{
      jarFile.close();
    }
  }
  
  private void expectManifestMf(File file) throws Exception {
    Assert.assertTrue(file.exists());
    JarFile jarFile = new JarFile(file);
    try {
      JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
      if (entry != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)));
        try{
          String line = reader.readLine();
          while(line != null){
            if(line.startsWith("Bundle-Name:")){
              Assert.assertTrue(line.endsWith(MarkIncubation.INCUBATION_APPENDIX));
              break;
            }
            line = reader.readLine();
          }
        }finally{
          reader.close();
        }
      }
    }finally{
      jarFile.close();
    }
  }
  
  private void expectPluginProps(File file) throws Exception {
    Assert.assertTrue(file.exists());
    JarFile jarFile = new JarFile(file);
    try {
      JarEntry entry = jarFile.getJarEntry("plugin.properties");
      if (entry != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)));
        try{
          String line = reader.readLine();
          while(line != null){
            if(line.startsWith("pluginName=")){
              Assert.assertTrue(line.endsWith(MarkIncubation.INCUBATION_APPENDIX));
              break;
            }
            line = reader.readLine();
          }
        }finally{
          reader.close();
        }
      }
    }finally{
      jarFile.close();
    }
  }
}


