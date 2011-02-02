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
package org.eclipse.scout.releng.ant.archive;

import java.io.File;

import junit.framework.Assert;

import org.apache.tools.ant.Project;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <h4>TestCreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */

public class TestRemoveOldZipFiles extends AbstractTestCase {

  private String m_workingDir;

  public TestRemoveOldZipFiles() {
    m_workingDir = getTestDataDir() + "/archive/removeOldZipFiles";
  }

  @Before
  public void setUp() throws Exception{
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
    File outputDir = new File(m_workingDir + "/output");
    RemoveOldZipFiles task = new RemoveOldZipFiles();
    Project project2 = new Project();
    task.setProject(project2);
    task.setKeep(3);
    task.setDir(outputDir);
    task.execute();
    
    Assert.assertTrue(outputDir.exists());
    Assert.assertFalse(new File(outputDir.getAbsolutePath()+"/N-scout-3.5.0M4-20110120-0901-Incubation.zip").exists());
    Assert.assertTrue(new File(outputDir.getAbsolutePath()+"/N-scout-3.5.0M4-20110121-0901-Incubation.zip").exists());
    Assert.assertTrue(new File(outputDir.getAbsolutePath()+"/N-scout-3.5.0M4-20110129-0901.zip").exists());
    Assert.assertTrue(new File(outputDir.getAbsolutePath()+"/N-scout-3.5.0M4-20110130-0901-Incubation.zip").exists());
    
  }

}

