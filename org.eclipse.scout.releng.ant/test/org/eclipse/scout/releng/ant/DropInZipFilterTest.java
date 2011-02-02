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
package org.eclipse.scout.releng.ant;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.scout.releng.ant.util.DropInZip;
import org.eclipse.scout.releng.ant.util.DropInZipFilter;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** <h4> DropInZipFilterTest </h4>
 *
 * @author aho
 * @since 1.1.0 (31.01.2011)
 *
 */
public class DropInZipFilterTest extends AbstractTestCase {
  

  private String m_workingDir;

  public DropInZipFilterTest(){
    m_workingDir = getTestDataDir() + "/dropInZipFilter";
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
  public void testDropInZipFilter(){
    File inputDir = new File(m_workingDir + "/input");
    DropInZipFilter filter = new DropInZipFilter();
    inputDir.list(filter);
    DropInZip[] zips = filter.getOrderedZipFiles();
    Assert.assertEquals(zips.length, 4);
    Assert.assertEquals(zips[0].getZipFile().getName(), "S-scout-3.7.1-20111020-0313.zip");
    Assert.assertEquals(zips[1].getZipFile().getName(), "R-scout-3.7.0-20110512-1520-Incubation.zip");
    Assert.assertEquals(zips[2].getZipFile().getName(), "S-scout-3.7.0M5-20110131-1230-Incubation.zip");
    Assert.assertEquals(zips[3].getZipFile().getName(), "N-scout-3.7.0M5-20110130-1256-Incubation.zip");
//    for(DropInZip zip : filter.getOrderedZipFiles()){
//      
//    }
    
  }
}
