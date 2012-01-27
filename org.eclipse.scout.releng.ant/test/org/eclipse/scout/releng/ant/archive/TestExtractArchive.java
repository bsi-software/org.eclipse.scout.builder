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

public class TestExtractArchive extends AbstractTestCase {

  private String m_workingDir;

  public TestExtractArchive() {
    m_workingDir = getTestDataDir() + "/archive/extractArchive";
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
  public void testTask() {
    File outputDir = new File(m_workingDir + "/output");

    ExtractArchive task = new ExtractArchive();
    task.setInputDir(new File(m_workingDir + "/input"));
    task.setOutputDir(outputDir);
    task.execute();

    Assert.assertTrue(outputDir.exists());
    Assert.assertTrue(outputDir.isDirectory());
    HashMap<String, File> files = new HashMap<String, File>();

    for (File f : outputDir.listFiles()) {
      files.put(f.getName(), f);
    }

    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.jdbc.derby_10.5.3.201101211855"));
    Assert.assertTrue(files.containsKey("org.eclipse.scout.rt.shared_3.5.5.201101211855.jar"));
    Assert.assertTrue(files.get("org.eclipse.scout.rt.jdbc.derby_10.5.3.201101211855").isDirectory());
  }

}
