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

import junit.framework.Assert;

import org.apache.tools.ant.Project;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** <h4> TestMirror </h4>
 *
 * @author jgu
 * @since 1.1.0 (01.05.2012)
 *
 */
public class TestMirror  extends AbstractTestCase{


  private String m_workingDir;

  public TestMirror() {
    m_workingDir = getTestDataDir() + "/mirror";
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
    MirrorTask task = new MirrorTask();
    Project project = new Project();
    task.setProject(project);
    File artifactsFile = new File(m_workingDir+"/input/artifacts.xml");
    task.setArtifactsFile(artifactsFile);
    File outFile = new File(m_workingDir+"/output/artifacts.xml");
    task.setOutputAtrifactsFile(outFile);
    task.setMirrorsURL("http://www.eclipse.org/downloads/download.php?format=xml&file=/scout/nightly&format=xml");
    task.execute();
    Assert.assertTrue(outFile.exists());
  }


}
