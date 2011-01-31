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

public class TestTrunkateRepository extends AbstractTestCase {

  private String m_workingDir;

  public TestTrunkateRepository() {
    m_workingDir = getTestDataDir() + "/p2";
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
    TrunkateRepository task = new TrunkateRepository();
    Project project2 = new Project();
    task.setProject(project2);
    task.setKeep(1);
    task.setRepositoryLocation(new File(m_workingDir + "/output"));
    task.execute();
    
  }

}

