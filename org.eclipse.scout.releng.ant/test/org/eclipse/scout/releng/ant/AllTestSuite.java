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

import org.eclipse.scout.releng.ant.archive.ArchiveSuite;
import org.eclipse.scout.releng.ant.category.TestCategoryTask;
import org.eclipse.scout.releng.ant.incubation.TestIncubation;
import org.eclipse.scout.releng.ant.p2.TestTrunkateRepository;
import org.eclipse.scout.releng.ant.pack200.Pack200Suite;
import org.eclipse.scout.releng.ant.undo.TestCreateUndoScript;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * <h4>AllTestSuite</h4>
 * 
 * @author aho
 * @since 1.1.0 (27.01.2011)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ArchiveSuite.class,
    Pack200Suite.class, TestCategoryTask.class, TestIncubation.class, TestCreateUndoScript.class, TestTrunkateRepository.class,
    TestCreateRepositoryOverview.class, DropInZipFilterTest.class})
public class AllTestSuite {

}
