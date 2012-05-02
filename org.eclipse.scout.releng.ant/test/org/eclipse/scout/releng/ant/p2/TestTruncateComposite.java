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
public class TestTruncateComposite  extends AbstractTestCase{


  private String m_workingDir;

  public TestTruncateComposite() {
    m_workingDir = getTestDataDir() + "/truncateComposite";
  }

  @Before
  public void setUp() throws Exception{
    removeOutputDir();
  }

  @After
  public void cleanUp() {
//    removeOutputDir();
  }

  private void removeOutputDir() {
    File outputDir = new File(m_workingDir + "/output");
    if (outputDir.exists()) {
      FileUtility.deleteFile(outputDir);
    }
  }

  @Test
  public void testTask()throws Exception{
    TruncateCompositeTask task = new TruncateCompositeTask();
    Project project = new Project();
    task.setProject(project);
    File in = new File(m_workingDir+"/input/compositeContent.xml");
    task.setSrcFile(in);
    File out = new File(m_workingDir+"/output/compositeContent.xml");
    task.setDestFile(out);
    task.setSize(4);
    task.execute();
    Assert.assertTrue(out.exists());
  }


}
