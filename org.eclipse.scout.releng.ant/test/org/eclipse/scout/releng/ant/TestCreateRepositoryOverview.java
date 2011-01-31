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
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.TreeSet;

import junit.framework.Assert;

import org.eclipse.scout.releng.ant.util.FileUtility;
import org.eclipse.scout.releng.ant.util.ReverseStringComparator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** <h4> CreateRepositoryOverviewTest </h4>
 *
 * @author aho
 * @since 1.1.0 (31.01.2011)
 *
 */
public class TestCreateRepositoryOverview extends AbstractTestCase {

  private String m_workingDir;

  public TestCreateRepositoryOverview(){
    m_workingDir = getTestDataDir() + "/testRepositoryOverview";
  }

  @Before
  public void setUp() throws Exception{
    removeOutputDir();
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
  public void testReverseComparator(){
    TreeSet<String> set = new TreeSet<String>(new ReverseStringComparator());
    set.add("3.5");
    set.add(null);
    set.add("3.9");
    set.add("3.6");
    String[] arr = set.toArray(new String[set.size()]);
    Assert.assertEquals(arr[0], "3.9");
    Assert.assertEquals(arr[1], "3.6");
    Assert.assertEquals(arr[2], "3.5");
    Assert.assertEquals(arr[3], null);
  }
  
  @Test
  public void testRepositoryOverview() throws Exception{
    File inputDir = new File(m_workingDir + "/input");
    File outputDir = new File(m_workingDir + "/output");
    File overviewFile = new File(outputDir.getAbsolutePath()+"/repositoryOverview.xml");
    CreateRepositoryOverview task = new CreateRepositoryOverview();
    task.setRootUrl("http://dowload.eclipse.org/scout");
    task.setOverviewFile(overviewFile);
    task.setRepositoryDir(inputDir);
    task.execute();
    
    Assert.assertTrue(overviewFile.exists());
    byte[] contentNew = FileUtility.getContent(new FileInputStream(overviewFile));
    byte[] contentRef = FileUtility.getContent(m_workingDir+"/input/repositoryOverview.xml");
    Assert.assertTrue(Arrays.equals(contentNew, contentRef));
  }
}
