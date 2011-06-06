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
import java.io.FileInputStream;
import java.util.Arrays;

import org.apache.tools.ant.Project;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <h4>TestCreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */

public class TestCategoryTask extends AbstractTestCase {

  private String m_workingDir;

  public TestCategoryTask() {
    m_workingDir = getTestDataDir() + "/category";
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
  public void testTask()throws Exception{
    CategoryTask task = new CategoryTask();
    Project project2 = new Project();
    task.setProject(project2);
    File categoryFile = new File(m_workingDir+"/output/scoutCategory.xml");
    task.setCategoryFile(categoryFile);
    task.setFeaturesFolder(new File(m_workingDir+"/input/features"));
    task.execute();
    
    Assert.assertTrue(categoryFile.exists());
    byte[] contentNew = FileUtility.getContent(new FileInputStream(categoryFile));
    byte[] contentRef = FileUtility.getContent(m_workingDir+"/input/scoutCategory.xml");
    Assert.assertTrue(Arrays.equals(contentNew, contentRef));
  }

}
