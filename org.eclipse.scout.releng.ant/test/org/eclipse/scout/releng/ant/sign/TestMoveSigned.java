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
package org.eclipse.scout.releng.ant.sign;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.AbstractTestCase;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <h4>TestMoveSigned</h4>
 * @author jgu
 * @since 1.1.0 (08.04.2012)
 */

public class TestMoveSigned extends AbstractTestCase {

  private String m_workingDir;
  private  String m_tempInOutDir;

  public TestMoveSigned() throws IOException {
    String testDataDir = getTestDataDir() + "/sign/moveSigned";
    m_tempInOutDir = getTestDataDir() + "/sign/moveSignedTest";
    m_workingDir = m_tempInOutDir + "/moveSigned";
    FileUtility.copyToDir(new File(testDataDir),new File(m_tempInOutDir));
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
    File outputDir = new File(m_tempInOutDir);
    if (outputDir.exists()) {
      FileUtility.deleteFile(outputDir);
    }
  }

  @Test
  public void testTask() {
    File inputDir = new File(m_workingDir + "/input");
    MoveSignedFiles task = new MoveSignedFiles();
    Project project2 = new Project();
    task.setProject(project2);
    FileSet set = new FileSet();
    set.setDir(inputDir);
    set.createInclude().setName("**/*.jar");
    task.addFileset(set);
    task.setOutputDir(new File(m_workingDir + "/output"));
    task.execute();
  }

}
