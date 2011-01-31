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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
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

public class TestCreateDropInZip extends AbstractTestCase {

  private String m_workingDir;

  public TestCreateDropInZip() {
    m_workingDir = getTestDataDir() + "/archive/createDropInZip";
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
    CreateDropInZip task = new CreateDropInZip();
    task.setOutputDir(new File(m_workingDir + "/output"));
    Project p = new Project();
    task.setProject(p);
    // filesets
    FileSet set1 = new FileSet();
    set1.setDir(new File(m_workingDir + "/input/sdkFeature"));
    set1.createInclude().setName("**/**");
    task.addFileset(set1);
    FileSet set2 = new FileSet();
    set2.setDir(new File(m_workingDir + "/input/rtFeature"));
    set2.createInclude().setName("**/**");
    task.addFileset(set2);
    task.setZipName("blubber");
    task.setMilestone("M4");
    task.setTimestamp("201001211203");
    task.setVersionMajor("3");
    task.setVersionMinor("5");
    task.setVersionMicro("0");
    task.execute();

    File file = new File(m_workingDir + "/output/blubber-3.5.0M4-201001211203.zip");
    Assert.assertTrue(file.exists());
    Assert.assertTrue(file.isFile());
    ZipFile zipFile = new ZipFile(file);
    int i = 0;
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      entries.nextElement();
      i++;
    }
    Assert.assertEquals(i, 20);
  }
  
  @Test
  public void testTaskIncubation() throws Exception {
    CreateDropInZip task = new CreateDropInZip();
    task.setOutputDir(new File(m_workingDir + "/output"));
    Project p = new Project();
    task.setProject(p);
    // filesets
    FileSet set1 = new FileSet();
    set1.setDir(new File(m_workingDir + "/input/sdkFeature"));
    set1.createInclude().setName("**/**");
    task.addFileset(set1);
    FileSet set2 = new FileSet();
    set2.setDir(new File(m_workingDir + "/input/rtFeature"));
    set2.createInclude().setName("**/**");
    task.addFileset(set2);
    task.setZipName("blubber");
    task.setMilestone("M4");
    task.setTimestamp("201001211203");
    task.setVersionMajor("3");
    task.setVersionMinor("5");
    task.setVersionMicro("0");
    task.setIncubation(true);
    task.execute();

    File file = new File(m_workingDir + "/output/blubber-Incubation-3.5.0M4-201001211203.zip");
    Assert.assertTrue(file.exists());
    Assert.assertTrue(file.isFile());
    ZipFile zipFile = new ZipFile(file);
    int i = 0;
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      entries.nextElement();
      i++;
    }
    Assert.assertEquals(i, 20);
  }

}
