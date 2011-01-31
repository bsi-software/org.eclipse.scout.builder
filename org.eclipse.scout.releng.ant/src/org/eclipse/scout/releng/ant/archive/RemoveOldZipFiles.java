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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.util.DropInZip;
import org.eclipse.scout.releng.ant.util.DropInZipFilter;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>TrunkateRepository</h4>
 * 
 * @author aho
 * @since 1.1.0 (29.01.2011)
 */
public class RemoveOldZipFiles extends Task {

  private File dir;
  private int keep = 3;

  /**
   * @return the dir
   */
  public File getDir() {
    return dir;
  }

  /**
   * @param dir
   *          the dir to set
   */
  public void setDir(File dir) {
    this.dir = dir;
  }

  /**
   * @return the keep
   */
  public int getKeep() {
    return keep;
  }

  /**
   * @param keep
   *          the keep to set
   */
  public void setKeep(int keep) {
    this.keep = keep;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    DropInZipFilter filter = new DropInZipFilter();
    getDir().list(filter);
    if (filter.size() > keep) {
      DropInZip[] toRemove = filter.getOrderedZipFiles();
      for (int i = keep; i < toRemove.length; i++) {
        if (toRemove[i].getZipFile().exists()) {
          FileUtility.deleteFile(toRemove[i].getZipFile());
        }
      }
    }
  }

  private void validate() throws BuildException {
    if (getDir() == null) {
      throw new BuildException("parameter dir missing.");
    }
  }

}
