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
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <h4>TestCreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */

public class TestRemoveUnpackSuffix extends AbstractTestCase {

  private String m_workingDir;

  public TestRemoveUnpackSuffix() {
    m_workingDir = getTestDataDir() + "/archive/removeUnpackSuffix";
  }

  @Before
  public void setUp() {
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
  public void testTask() throws Exception {
    File inputDir = new File(m_workingDir + "/input");
    File outputDir = new File(m_workingDir + "/output");
    outputDir.mkdirs();
    for (File f : inputDir.listFiles()) {
      FileUtility.copyToDir(f, outputDir);
    }
    RemoveUnpackSuffix task = new RemoveUnpackSuffix();
    Project project2 = new Project();
    task.setProject(project2);
    task.setDir(outputDir);
    task.execute();

    Assert.assertTrue(outputDir.exists());
    Assert.assertTrue(outputDir.isDirectory());
    HashMap<String, File> files = new HashMap<String, File>();

    for (File f : outputDir.listFiles()) {
      files.put(f.getName(), f);
    }

    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.jdbc.derby_10.5.3.201101211855.jar"));
    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.shared_3.5.5.201101211855.jar"));

  }

  @Test
  public void testTaskFileset() throws Exception {
    File inputDir = new File(m_workingDir + "/input");
    File outputDir = new File(m_workingDir + "/output");
    outputDir.mkdirs();
    for (File f : inputDir.listFiles()) {
      FileUtility.copyToDir(f, outputDir);
    }
    RemoveUnpackSuffix task = new RemoveUnpackSuffix();
    Project project2 = new Project();
    task.setProject(project2);
    FileSet set = new FileSet();
    set.setDir(outputDir);
    set.createInclude().setName("**/*.jar");
    task.addFileset(set);
    task.execute();

    Assert.assertTrue(outputDir.exists());
    Assert.assertTrue(outputDir.isDirectory());
    HashMap<String, File> files = new HashMap<String, File>();

    for (File f : outputDir.listFiles()) {
      files.put(f.getName(), f);
    }

    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.jdbc.derby_10.5.3.201101211855.jar"));
    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.shared_3.5.5.201101211855.jar"));

  }
  
}
