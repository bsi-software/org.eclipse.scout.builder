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
package org.eclipse.scout.releng.ant.undo;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DirSet;
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

public class TestCreateUndoScript extends AbstractTestCase {

  private String m_workingDir;

  public TestCreateUndoScript() {
    m_workingDir = getTestDataDir() + "/undo";
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
    File undoScript = new File(m_workingDir + "/output/undo.sh");
    File inputDir = new File(m_workingDir + "/input");
    CreateUndoScript task = new CreateUndoScript();
    Project project2 = new Project();
    task.setProject(project2);
    DirSet set = new DirSet();
    set.setDir(inputDir);
    set.createInclude().setName("plugins");
    set.createInclude().setName("features");
    task.addDirset(set);
    task.setUndoScript(undoScript);
    task.execute();
    
  }

}

